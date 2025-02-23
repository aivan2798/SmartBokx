import React, { useState, Suspense, useEffect } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import {BrowserRouter as Router, Routes, Route, useNavigate} from "react-router-dom"
import { createClient, SupabaseClient } from '@supabase/supabase-js'
//import {LoginPage} from "./pages/LoginPage"
//import 'bootstrap'
export const supabase = createClient('https://bjzhvayyfwkhmymmpuqf.supabase.co', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJqemh2YXl5ZndraG15bW1wdXFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjI1NDk3MTMsImV4cCI6MjAzODEyNTcxM30.PdBzt2HO0jCVlfpG1F7pG8OceezOO-1VRK8MerUS6a4')


const Login = React.lazy(()=>import("./pages/LoginPage"))
const Reset = React.lazy(()=>import("./pages/PasswordReset"))
const Dashboard = React.lazy(()=>import("./pages/dashboard"))
function App() {
  const [count, setCount] = useState(0)
  //const navigator = useNavigate()
  const [client,setClient] = useState()
  

  return (
    <>
      
        <Router>
            <Suspense fallback={<div>Please Wait .... </div>}>
              <Routes>
                  <Route path="/" element={<Login/>}/>
                  <Route path="/dashboard" element={<Dashboard active_session={supabase} client={client}/>}/>
                  <Route path="/reset" element={<Reset/>}/>
              </Routes>
            </Suspense>
        </Router>
      
    </>
  );
}

export default App
