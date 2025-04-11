// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";
import {firebaseConfig} from "./api/config/firebase.js"
// Initialize Firebase
const app = initializeApp(firebaseConfig);
const fbmessaging = getMessaging(app);
getToken(fbmessaging, { vapidKey: 'BIIxnIF86ntYUwC6KM49ka7r2QRZh3puA8SSQFQywtBcLtXM7WhE1pmP9DF66KwGa2r0_SoPY-p2fs1cktS1iHY' }).then((currentToken) => {
    if (currentToken) {
        console.log("registration token is: ",currentToken)
      // Send the token to your server and update the UI if necessary
      // ...
    } else {
      // Show permission request UI
      console.log('No registration token available. Request permission to generate one.');
      // ...
    }
  }).catch((err) => {
    console.log('An error occurred while retrieving token. ', err);
    // ...
  });
onMessage(fbmessaging, (payload) => {
    console.log('Message received. ', payload);
    // ...
  });