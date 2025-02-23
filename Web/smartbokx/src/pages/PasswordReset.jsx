import { Alert, Button } from "@mui/material";
import { SupabaseClient, createClient } from "@supabase/supabase-js";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {supabase} from "../App"
import { CircleLoader } from "react-spinners";
//const supabase = createClient('https://bjzhvayyfwkhmymmpuqf.supabase.co', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJqemh2YXl5ZndraG15bW1wdXFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjI1NDk3MTMsImV4cCI6MjAzODEyNTcxM30.PdBzt2HO0jCVlfpG1F7pG8OceezOO-1VRK8MerUS6a4')

function LoginButton({uname,pword,show_alert,setAlert,setServiceMsg,setSeverinity}){
    const navigator = useNavigate()
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
            setServiceMsg("");
            //console.log("username: "+uname.current.value)
            //console.log("password: "+pword.current.value)
            const {data,error} = await supabase.auth.updateUser({
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
                setAlert(false)
                console.log("no message")
                setServiceMsg("password changed well");
                setSeverinity("success")
                setLoading(false)
                navigator("/")
            }
    
        }} variant="contained">
            RESET PASSWORD
        </Button>
        );
    }
}

function ResetCard(){

    let password = useRef("");
    let password1 = useRef("");
    let showPassword = useRef()
    let [password_type,setPasswordType] = useState("password")
    let [password1_type,setPassword1Type] = useState("password")
    //const [isloading, setLoading] = useState(false)
    const [show_forgotten_dialog,showPasswordDialog] = useState(false)
    const [show_alert,setAlert] = useState(true)
    const [service_msg,setServiceMsg] = useState("")
    const [severe,setSeverinity] = useState("error")
    const [submit_visibility,setSubmitVisibility] = useState(false)
    const[match_severinity,setMatchSeverinity] = useState("info")
    const[match_alert_txt, setMatchAlertText] = useState("I will be checking Your Passwords")
    /*login("cot2we@gmail.com","testman").then((result)=>{
        console.log(result)
    }).catch((error_msg)=>{
        console.log(error_msg)
    });
    */
   const onPassword = (e)=>{
    const pw_1 = e.target.value
    const pw = password1.current.value

    if(pw_1.length>0){
        if(pw==pw_1)
        {
                setMatchSeverinity("success")
                setMatchAlertText("Passwords match, continue")
                setSubmitVisibility(true)
        }
        else{
            setMatchSeverinity("error")
            setMatchAlertText("Passwords don't match")
            setSubmitVisibility(false)
        }
    }
    else{
        setMatchSeverinity("info")
        setMatchAlertText("Please Input Password, I will be checking them")
        setSubmitVisibility(false)
    }
    }
    const onPassword1 = (e)=>{
        const pw_1 = e.target.value
        const pw = password.current.value

        if(pw_1.length>0){
            if(pw==pw_1)
            {
                    setMatchSeverinity("success")
                    setMatchAlertText("Passwords match, continue")
                    setSubmitVisibility(true)
            }
            else{
                setMatchSeverinity("error")
                setMatchAlertText("Passwords don't match")
                setSubmitVisibility(false)
            }
        }
        else{
            setMatchSeverinity("info")
            setMatchAlertText("Please Input Password, I will be checking them")
            setSubmitVisibility(false)
        }
    }
    return(
        <div className="container">
            <div className="card">
                
                <div className="card-body">
                    <div className="card-title"><b>RESET YOUR SMARTBOKX PASSWORD</b></div>
                    <Alert hidden={show_alert} severity={severe}>
                        {service_msg}
                    </Alert>
                    <form>
                        <div className="form-group row">
                            <label className="col-sm-4">Password</label>
                            <div className="col-sm-8">
                                <input ref={password} type={password_type} className="form-control" onInput={onPassword} onMouseLeave={onPassword}/>
                            </div>
                        </div>
                        <div className="form-group row">
                            <div className="col-sm-4">
                            </div>
                            <div className="col-sm-8">
                                <input type="checkbox" onChange={(e)=> {
                                    const pw_type = e.target.checked;
                                    if(pw_type===true){
                                        console.log(pw_type)
                                        setPasswordType("text")
                                    }
                                    else{
                                        console.log(pw_type)
                                        setPasswordType("password")
                                    }
                                }}
                                /> &nbsp; Show Password
                            </div>
                        </div>
                        <div className="form-group row">
                            <label className="col-sm-4">Repeat Password</label>
                            <div className="col-sm-8">
                                <input ref={password1} type={password1_type} onInput={onPassword1} onMouseLeave={onPassword1} className="form-control"/>
                            </div>
                        </div>

                        <div className="form-group row">
                            <div className="col-sm-4">
                            </div>
                            <div className="col-sm-8">
                                <input type="checkbox" onChange={(e)=> {
                                    const pw_type = e.target.checked;
                                    if(pw_type===true){
                                        console.log(pw_type)
                                        setPassword1Type("text")
                                    }
                                    else{
                                        console.log(pw_type)
                                        setPassword1Type("password")
                                    }
                                }}
                                /> &nbsp; Show Password
                            </div>
                        </div>
                        
                        <div className="form-group row">
                            <div className="col-sm-4">
                            </div>
                            <div className="col-sm-8">
                                <Alert severity={match_severinity}>
                                    {match_alert_txt}
                                </Alert>
                            </div>
                        </div>
                        {
                            (submit_visibility)=>{
                                if(submit_visibility==true){
                                    console.log("showing submit")
                                }
                                else{
                                    console.log("not showing submit")
                                }
                            }
                        }
                        <div className="form-group row" style={{display: submit_visibility?"block":"none"}}>
                            
                            <LoginButton uname={password} pword={password} show_alert={show_alert} setAlert={setAlert} setSeverinity={setSeverinity} setServiceMsg = {setServiceMsg}/>
                        </div>

    
                        
                        
                    </form>
                </div>
            </div>
            
        </div>
    );
}

function PasswordResetPage(){

    const navigator = useNavigate()
    useEffect(()=>{
        supabase.auth.getSession().then(({data,error})=>{
            console.log(data)
            console.log(error)
            if(data.session==null){
                console.log("no session")
                navigator("/")
            }
            else{
                console.log(data)

            }
           
        }).finally(()=>{

        })
    },[])
    return(
        <>
            <nav className="nav navbar-dark bg-dark flex">
                <div className="navbar-brand">
                    SmartBokx
                </div>
            </nav>
            <div className="container">
                <div className="row" style={{paddingTop:50}}>
            
                    <div className="col-sm-3">
                    </div>
                    <div className="col-sm-9">
                        <nav className="navbar-dark bg-dark">
                            <div className="navbar-brand">Reset Password</div>
                                <ResetCard/>
                        </nav>
                    </div>
        
                </div>
            </div>
            
        </>

    )
}

export default PasswordResetPage;