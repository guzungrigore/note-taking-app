import express from 'express';
import {AuthUser} from "../schemas/userSchema.js";
import jwt from "jsonwebtoken";
import axios from "axios";
const router = express.Router();

router.post('/register', async function (req, res, next) {
    const username = req.body.username
    const password = req.body.password

    if (!username || !password) {
        return res.status(400).json({error: "Username and password are required"})
    }
    try {
        await AuthUser.create({username, password});
        res.status(201).json({message: "User registered in successfully"})
    } catch (error) {
        if (error.code === 11000) {
            return res.status(409).json({ error: "Username already exists" })
        }
        console.error("Error saving user:", error)
        res.status(500).json({error: error.message})
    }
})

router.post('/login', async (req, res, next) => {
    const {username, password} = req.body
    if (!username || !password) {
        return res.status(400).json({error: "Username and password are required"})
    }
    try {
        const user = await AuthUser.findOne({username})

        if (!user) {
            return res.status(400).json({error: "Invalid username or password"})
        }

        if (password !== user.password) {
            return res.status(400).json({error: "Invalid username or password"})
        }

        const accessToken = jwt.sign(
            {id: user._id, user: user.username},
            process.env.ACCESS_TOKEN_SECRET,
            {expiresIn: '24h'}
        )

        try {
            await axios.post('http://cache-service:5000/cache', {userId: user._id, token:accessToken})
            console.log(`Token stored in pad-cache for user: ${user._id}`)
        } catch (error) {
            console.error('Error storing token in pad-cache:', error.message)
        }
        res.status(200).json({
            message: "Login successful",
            accessToken: accessToken
        })
    } catch (error) {
        console.error("Error logging in user:", error)
        res.status(500).json({error: "Internal server error"})
    }
})

router.put('/resetPassword', async (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1]
    const password = req.body.password

    if (!token) {
        return res.status(401).json({ message: 'Invalid token' })
    }

    if (!password) {
        return res.status(400).json({ error: "Password is required" })
    }

    try {
        const decoded = jwt.verify(token, process.env.ACCESS_TOKEN_SECRET)

        const user = await AuthUser.findOne({username: decoded.user})

        if (!user) {
            return res.status(404).json({ error: "User not found" })
        }

        user.password = password
        await user.save()

        res.status(200).json({ message: "Password reset successfully" })
    } catch (error) {
        console.error("Error resetting password:", error)
        if (error.name === 'JsonWebTokenError' || error.name === 'TokenExpiredError') {
            return res.status(401).json({ message: 'Invalid or expired token' })
        }
        res.status(500).json({ error: "Internal server error" })
    }
})
export default router
