import React, { useEffect, useRef, useState } from "react";
import {Button, Dialog, Alert, DialogTitle, DialogContent, DialogActions} from "@mui/material"

import { CircleLoader } from "react-spinners";
import SmartLogo from "../assets/smartbocx_dark.png"

import { createClient } from '@supabase/supabase-js'
import { useNavigate } from "react-router-dom";

import {supabase} from "../App"
// Create a single supabase client for interacting with your database




async function login(username,password){
    const authman = await supabase.auth.signInWithPassword({
        username,password
    });

    console.log(authman)
}

function LoginButton({uname,pword,show_alert,setAlert,setServiceMsg,setSeverinity}){

    const [isloading, setLoading] = useState(false)
    //const [show_alert,setAlert] = useState(true)
    //const [service_msg,setServiceMsg] = useState("")
    //const [severe,setSeverinity] = useState("error")
    //const uname = props.xusername;
    //const pword = props.xpassword;
    //console.log("uname: "+uname.current.value)
    //console.log("pname: "+uname.current.value)
    if(isloading==true){
        return(<Button className="col"><CircleLoader color="black" size={30}/></Button>) 
    }
    else{
        return (<Button className="col" onClick={async()=>{
            //const uname = username;
            //const pword = password;
            setLoading(true)
            //console.log("username: "+uname.current.value)
            //console.log("password: "+pword.current.value)
            const {data,error} = await supabase.auth.signInWithPassword({
                email: uname.current.value,
                password: pword.current.value
            });
            if (error!=null){
                //console.log(error.message)
                setAlert(false)
                setServiceMsg(error.message);
                setSeverinity("error")
                setLoading(false)
            }
            else{
                console.log("no message")
                setLoading(false)
            }
    
        }} variant="contained">
            LOGIN
        </Button>
        );
    }
}

function PasswordResetBox({showPasswordDialog,show_forgotten_dialog}){
    let username = useRef("");
    const [show_reset_alert, showResetAlert] = useState(false)
    const [reset_msg,setResetMsg] = useState("")
    const [reset_dialog_severity,setSeverinity] = useState("")
    return (
            <>
            <DialogTitle><nav className="bg-dark navbar-dark"><div className='navbar-brand' style={{paddingLeft:50}}>
                Reset Password
            </div></nav></DialogTitle>
            <DialogContent>
            <Alert hidden={show_reset_alert} severity={reset_dialog_severity}>
                {reset_msg}
            </Alert>
            <div className="form-group row">
                <label className="col-sm-4">Email Address</label>
                <div className="col-sm-8">
                    <input ref={username} type="text" className="form-control"/>
                </div>
            </div>
            </DialogContent>

            <DialogActions>
                <Button variant="outlined" onClick={()=>{
                    showPasswordDialog(false)
                }}>Close</Button>
                <Button variant="contained" onClick={async()=>{
                    setSeverinity("info")
                    setResetMsg("Please wait")
                    showResetAlert(false)
                    const {data, error} = await supabase.auth.resetPasswordForEmail(username.current.value, {redirectTo: "http://localhost:5173/reset"})
                    //const {data, error} = await supabase.auth.signInWithOtp( {email: username.current.value,options:{emailRedirectTo: "http://localhost:5173/passwordreset"}})
                    //showResetAlert(false)
                    if(error!=null){
                        
                        setSeverinity("error")
                        setResetMsg(error.message)
                        //showResetAlert(true)
                    }
                    else{
                        console.log("error message set")
                        setSeverinity("success")
                        setResetMsg("Check your email for reset link")
                        //showResetAlert(true)
                    }
                }}>Reset</Button>
            </DialogActions>
            </>
    );
}

function LoginCard(){

    let username = useRef("");
    let password = useRef("");

    //const [isloading, setLoading] = useState(false)
    const [show_forgotten_dialog,showPasswordDialog] = useState(false)
    const [show_alert,setAlert] = useState(true)
    const [service_msg,setServiceMsg] = useState("")
    const [severe,setSeverinity] = useState("error")

    
    /*login("cot2we@gmail.com","testman").then((result)=>{
        console.log(result)
    }).catch((error_msg)=>{
        console.log(error_msg)
    });
    */

    return(
        <div className="container">
            <div className="card">
                <img className="card-img-top" src={SmartLogo} style={{ width: '50px', height: 'auto'}}></img>
                <div className="card-body">
                    <div className="card-title"><b>LOGIN TO SMARTBOKX</b></div>
                    <Alert hidden={show_alert} severity={severe}>
                        {service_msg}
                    </Alert>
                    <form>
                        <div className="form-group row">
                            <label className="col-sm-4">Email Address</label>
                            <div className="col-sm-8">
                                <input ref={username} type="text" className="form-control"/>
                            </div>
                        </div>

                        <div className="form-group row">
                            <label className="col-sm-4">Password</label>
                            <div className="col-sm-8">
                                <input ref={password} type="password" className="form-control"/>
                            </div>
                        </div>
                        <div className="form-group row">
                            
                            <LoginButton uname={username} pword={password} show_alert={show_alert} setAlert={setAlert} setSeverinity={setSeverinity} setServiceMsg = {setServiceMsg}/>
                        </div>

                        <div className="form-group row">
                            <div className="col-sm-4"></div>
                            <a href="#" className="col-sm-8" onClick={()=>{
                                showPasswordDialog(true)
                            }}>Forgot Password!!!</a>
                        </div>
                        <Dialog open={show_forgotten_dialog}>
                                <PasswordResetBox showPasswordDialog={showPasswordDialog} show_forgotten_dialog={show_forgotten_dialog}/>
                        </Dialog>
                        
                    </form>
                </div>
            </div>
            
        </div>
    );
}

function LoginPage(){

    const navigate = useNavigate()

    useEffect(()=>{
        supabase.auth.getSession().then(({data,error})=>{
            console.log(data)
            console.log(error)
            if(data.session!=null){
                console.log("session present")
                navigate("/dashboard")
            }
            else{
                console.log("session not present on start")
                
    
            }
           
        }).finally(()=>{
    
        })
    },[])

    return(
    <>
        <nav className='nav navbar-dark bg-dark flex'>
            <div className='navbar-brand'>
                 SmartBokx
            </div>
        </nav>
        <div className="container">
            <div className="row">
                <div className="col-sm-2">
                </div>
                <div className="col-sm-9">
                
                    <LoginCard/>
                </div>
                <div className="col-sm-1">
                </div>
            </div>
        </div>
        
    </>
    );
}


export default LoginPage;