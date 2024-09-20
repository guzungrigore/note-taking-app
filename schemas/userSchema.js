import mongoose, {model, Schema} from "mongoose";
import passportLocalMongoose from "passport-local-mongoose";

const userSchema = new Schema({}, {versionKey: false})

userSchema.plugin(passportLocalMongoose)

export const AuthUser = new model('users', userSchema)

