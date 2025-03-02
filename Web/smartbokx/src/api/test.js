import {Bokxman} from "./bokx_api.js"

console.log("\t\tSTARTED TEST")

const bokxman = new Bokxman("eyJhbGciOiJIUzI1NiIsImtpZCI6IlVpSWNwYkc3eU1NaHlRZlkiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2Jqemh2YXl5ZndraG15bW1wdXFmLnN1cGFiYXNlLmNvL2F1dGgvdjEiLCJzdWIiOiJhYjY4NjRmYy0yZjQ3LTQ1ODItYTVhNy0xNDdjZmQyM2M3ZmIiLCJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzQwMzI5NTI0LCJpYXQiOjE3NDAzMjU5MjQsImVtYWlsIjoiYXllYWxnb0BnbWFpbC5jb20iLCJwaG9uZSI6IiIsImFwcF9tZXRhZGF0YSI6eyJwcm92aWRlciI6ImVtYWlsIiwicHJvdmlkZXJzIjpbImVtYWlsIl19LCJ1c2VyX21ldGFkYXRhIjp7ImVtYWlsIjoiYXllYWxnb0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInBob25lX3ZlcmlmaWVkIjpmYWxzZSwic3ViIjoiYWI2ODY0ZmMtMmY0Ny00NTgyLWE1YTctMTQ3Y2ZkMjNjN2ZiIn0sInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiYWFsIjoiYWFsMSIsImFtciI6W3sibWV0aG9kIjoicGFzc3dvcmQiLCJ0aW1lc3RhbXAiOjE3NDAzMjU5MjR9XSwic2Vzc2lvbl9pZCI6IjkyOTRhMWE5LWMxNzgtNGE5MC1hMTFlLWY3NGIyNzhlODgxNCIsImlzX2Fub255bW91cyI6ZmFsc2V9.HYUfwIAWo9gnzuwcqZSvni88yzI8ZaZXu4dsdBgJo1g")

const {initdata, error, error_message} = await bokxman.initBokxmanAsync()

if(error){
    console.log("\t\tRECEIVED BIG ERROR")
    console.log(error)
}
else if(initdata){
    console.log(initdata)
    await bokxman.askBokx("what do you know about naruto?")
    //await bokxman.getAllNotes()
}
else if(error_message){
    console.log("\t\t__CONNECTION ERROR: "+error_message)
}

