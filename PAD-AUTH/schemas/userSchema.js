import {model, Schema} from "mongoose";
import {v4 as uuidv4} from 'uuid'

const userSchema = new Schema({
    _id: {
        type: String,
        default: uuidv4,
    }, username: {
        type: String,
        required: true,
        unique: true
    }, password: {
        type: String,
        required: true
    }
}, {versionKey: false})

// userSchema.plugin(passportLocalMongoose)

export const AuthUser = new model('users', userSchema)

