// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getMessaging } from "firebase/messaging/sw";
import {firebaseConfig} from "./config/firebase.js"
// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const fbmessaging = getMessaging(app);
console.log("firebase up")
