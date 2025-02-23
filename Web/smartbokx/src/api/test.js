import {Bokxman} from "./bokx_api.js"

console.log("\t\tSTARTED TEST")

const bokxman = new Bokxman("eyJhbGciOiJIUzI1NiIsImtpZCI6IlVpSWNwYkc3eU1NaHlRZlkiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2Jqemh2YXl5ZndraG15bW1wdXFmLnN1cGFiYXNlLmNvL2F1dGgvdjEiLCJzdWIiOiJhYjY4NjRmYy0yZjQ3LTQ1ODItYTVhNy0xNDdjZmQyM2M3ZmIiLCJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzM5MjU1MTAwLCJpYXQiOjE3MzkyNTE1MDAsImVtYWlsIjoiYXllYWxnb0BnbWFpbC5jb20iLCJwaG9uZSI6IiIsImFwcF9tZXRhZGF0YSI6eyJwcm92aWRlciI6ImVtYWlsIiwicHJvdmlkZXJzIjpbImVtYWlsIl19LCJ1c2VyX21ldGFkYXRhIjp7ImVtYWlsIjoiYXllYWxnb0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInBob25lX3ZlcmlmaWVkIjpmYWxzZSwic3ViIjoiYWI2ODY0ZmMtMmY0Ny00NTgyLWE1YTctMTQ3Y2ZkMjNjN2ZiIn0sInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiYWFsIjoiYWFsMSIsImFtciI6W3sibWV0aG9kIjoicGFzc3dvcmQiLCJ0aW1lc3RhbXAiOjE3MzkyNTE1MDB9XSwic2Vzc2lvbl9pZCI6Ijk0ZmFkMjIyLWY3ZGEtNDViOS05M2ZhLTQ2ZjU2NzFkYTU2ZiIsImlzX2Fub255bW91cyI6ZmFsc2V9.moQy86aj5kLWKXMEFah3MgknGJpiT64ofEqxJrejMqg")

const {data, error, error_message} = await bokxman.initBokxmanAsync()

if(error){
    console.log("\t\tRECEIVED BIG ERROR")
    console.log(error)
}
else if(data){
    console.log(data)
    await bokxman.getAllNotes()
}
else if(error_message){
    console.log("\t\t__CONNECTION ERROR: "+error_message)
}

