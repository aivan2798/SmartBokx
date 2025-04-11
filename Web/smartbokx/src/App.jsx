import React, { useState, Suspense, useEffect } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import {BrowserRouter as Router, Routes, Route, useNavigate} from "react-router-dom"
import { createClient, SupabaseClient } from '@supabase/supabase-js'
//import {LoginPage} from "./pages/LoginPage"
//import 'bootstrap'
export const supabase = createClient('https://bjzhvayyfwkhmymmpuqf.supabase.co', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJqemh2YXl5ZndraG15bW1wdXFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA5MDM3ODUsImV4cCI6MjA1NjQ3OTc4NX0._vPf-PnljXLUWMrPIj7iFwbimCnPupubUglK2qn5oZQ')


//navigator.serviceWorker.register('/firebase-messaging-sw.js', { type: 'module' });
const Login = React.lazy(()=>import("./pages/LoginPage"))
const Reset = React.lazy(()=>import("./pages/PasswordReset"))
const Dashboard = React.lazy(()=>import("./pages/dashboard"))
function App() {
  const [count, setCount] = useState(0)
  //const navigator = useNavigate()
  const [client,setClient] = useState()
  /*
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('firebase-messaging-sw.js')
      .then(function(registration) {
        console.log('Registration successful, scope is:', registration.scope);
      }).catch(function(err) {
        console.log('Service worker registration failed, error:', err);
      });
    }
    */
  
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
