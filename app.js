import express from "express";
import authRouter from "./routes/auth.js";
import {config} from "dotenv";
import {connect} from "mongoose";
import cors from 'cors'
// import timeout from "connect-timeout";


config()
const app = express()
const MongodbUrl = 'mongodb://adelia:adelia27@mongodb:27017/authDB?authSource=admin'
// const MongodbUrl = 'mongodb://127.0.0.1:27017/pad'
// app.use(timeout('30s'))

connect(MongodbUrl)
    .then(() => console.log("Connected to MongoDB"))
    .catch(err => console.error("MongoDB connection error:", err))


app.use(express.urlencoded({extended: true}))
app.use(express.static("public"))

app.use(cors())
app.use(express.json());
app.use('/auth', authRouter)

// app.get('/health', (req, res) => {
//     if (!req.timedout) {
//         res.status(200).json({ message: 'OK' })
//     }
// })

app.get('/status', (req, res) => {
    res.status(200).send('note-Service is alive')
})

// app.use((err, req, res, next) => {
//     if (err.code === 'ETIMEDOUT') {
//         res.status(408).send('Request timed out')
//     } else {
//         next(err)
//     }
// })

app.listen(3000, () => {
    console.log("Server started on port 3000")
})

export default app
