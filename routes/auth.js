import express from 'express';
import {AuthUser} from "../schemas/userSchema.js";
import passport from "passport";

const router = express.Router();

router.post('/register', async function (req, res, next) {
    const username = req.body.username
    const password = req.body.password
    if (!username || !password) {
        return res.status(400).json({error: "Username and password are required"})
    }
    try {
        await AuthUser.register(new AuthUser({username}), password);
        res.status(201).json({message: "User registered in successfully"})
    } catch (error) {
        console.error("Error saving user:", error)
        res.status(500).json({error: error.message})
    }
});

router.post('/login', function (req, res, next) {
    passport.authenticate('local', function (err, user) {
        if (err) {
            return next(err)
        }
        if (!user) {
            return res.status(401).json({ message: 'Invalid username or password' })
        }
        req.logIn(user, function (err) {
            if (err) {
                return next(err)
            }
            req.session.isAuth = true
            return res.status(200).json({ message: 'User logged in successfully' })
        });
    })(req, res, next);
});



const checkAuth =  (req, res,next) => {
    if (req.session.isAuth) {
        next()
    } else {
        res.status(401).send('Unauthorized. Please log in.');
    }
}

export default router
