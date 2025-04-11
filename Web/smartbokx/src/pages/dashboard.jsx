import {useEffect, useRef, useState} from "react"
import { Form, NavbarBrand} from "react-bootstrap";
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Tab from 'react-bootstrap/Tab';
import Tabs from 'react-bootstrap/Tabs';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import "./styles/dashboard.css"
import { Fab, Button, Dialog, DialogTitle, DialogContent, DialogActions, IconButton, TextField} from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import { Psychology, SearchRounded } from "@mui/icons-material";
import PersonIcon from '@mui/icons-material/Person';
import { Navigate, useNavigate } from "react-router-dom";
import { Bokxman } from "../api/bokx_api";
// Import the functions you need from the SDKs you need

import { CircleLoader } from "react-spinners";

//import {SxProps} from "@mui/system";
const endpoint = "https://u7pyeg.buildship.run"//"https://ty051i.buildship.run/bokx/model"
var bokx_man = null
let msg_channel = null;
//let bokx_man = null
function CategoryCard(){

    return(
        <>
            <nav className="navbar-light bg-light">
                <div className="navbar-brand-md">
                    Categories
                </div>
            </nav>
            <div className="container">
                <div className="bokx-category">
                    21 Monkeys
                </div>
                <div className="bokx-category">
                    Altered Carbon
                </div>
            </div>
        </>
    )
}

const fab_buttom_style = {
    position: "fixed",
    
    bottom: 50,
    marginLeft:50
    /*right: 10,*/
}

function SearchBody({user_notes}){
    
    const note_title = useRef()
    const note_content = useRef()
    const [search_results,addResults] = useState([])
    const renderedOutput = search_results.map(item => <NoteBox note_data={item}/>)
    const [add_note_dialog_state,showAddNoteDialog] = useState(false)
    return(
        <>
            <div className="main_dashboard_body">
                <div className="container search_body">
                    <form>
                            <div className="form-group row">
                                <div className="col-sm-2"></div>
                                <div className="col-sm-6">
                                    <div id="search_id" className="input-container">
                                        
                                        <input id="search_input" ref={note_title} type="text" placeholder="text to search" className="form-control"/>
                                        <Button className="col-sm-2" variant="contained" onClick={async()=>{
                                                const text_to_search = note_title.current.value
                                                const made_search = user_notes.filter(item=>item["content"].includes(text_to_search))

                                                console.log(made_search)
                                                addResults(made_search)
                                        }}><SearchRounded/></Button>
                                    </div>
                                </div>
                                
                            </div>

                    </form>

                </div>
                {renderedOutput}
            </div>
        </>
    );
}


function BokxAIBody({bokx_results,addResults}){
    
    console.log("bokx_body ready")
    const note_title = useRef()
    const note_content = useRef()

    const [add_note_dialog_state,showAddNoteDialog] = useState(false)
    
    
    
    const [new_style,setNewStyle] = useState()
    /*const renderedOutput = bokx_results.map((item) =>{
        //setNewStyle();
        console.log("making new input")
        return <QnBox note_data={item}/>})
    */
    /*useEffect(()=>{
        console.log("bokx effect up")

        if(channel_route!=null){
            channel_route.on("broadcast",{"event":"bokx_qa"},
                (payload)=>{
                    console.log("bokx_ai_reply: ",payload)
                    setNewStyle({})
                }
            )
        }
        
    },channel_route)
    */
                        
    return(
        <>
            <div className="main_dashboard_body">
                <div className="container">
                        <form>
                                <div className="form-group row">
                                    <div className="col-sm-2"></div>
                                    <div className="col-sm-6">
                                        <div id="search_id" className="input-container">
                                            
                                            <input id="search_input" ref={note_title} type="text" placeholder="ask bokx" className="form-control"/>
                                            <Button variant="contained" onClick={
                                                async()=>{
                                                    setNewStyle({animation:"psycho_think 1s infinite alternate-reverse"})
                                                const bokx_qnt = note_title.current.value
                                                //alert(bokx_qnt)
                                                const qtag = <QnBox note_data={bokx_qnt}/>
                                                console.log("bokx results: ",bokx_results)
                                                addResults([...bokx_results,qtag])
                                                const bokx_ans = await bokx_man.askBokx(bokx_qnt)
                                                //addResults([...bokx_results,bokx_ans])
                                                
                                                }
                                            }><Psychology className="psycho_logo" style={new_style}/></Button>
                                        </div>
                                    </div>
                                    
                                </div>

                        </form>
                        
                </div>
                {bokx_results}
            </div>
            
        </>
    );
}

function NoteBox({note_data}){
    const note_content = note_data.content
    const note_link = note_data.link
    return(
        <div className="container">
                
                <div className="note-bokx">
                    <div className="note_title"><Psychology/>{note_link}</div>
                    <div className="note_content">{note_content}</div>
                </div>
                
            
        </div>
    )
}

function QnBox({note_data}){
    
    return(
        <div className="container">
                
                <div className="note-bokx">
                    <div className="note_title"><PersonIcon/>User</div>
                    <div className="note_content">{note_data}</div>
                </div>
                
            
        </div>
    )
}

function AnsBox({note_data}){
    
    return(
        <div className="container">
                
                <div className="note-bokx">
                    <div className="note_title">Bokx AI</div>
                    <div className="note_content">{note_data}</div>
                </div>
                
            
        </div>
    )
}

function DashboardBody({all_notes,notesSetter,bokxman_instance}){
    //console.log(all_notes)
    const note_title = useRef()
    const note_content = useRef()
    var renderedOutput = all_notes.map(item => <NoteBox note_data={item}/>)
    const [add_note_dialog_state,showAddNoteDialog] = useState(false)
    return(
        <>
            <div className="main_dashboard_body">
                <Dialog
                    open={add_note_dialog_state}>
                    <DialogTitle>Add Note</DialogTitle>
                    <DialogContent>
                        <form>
                            <div className="form-group row">
                                <label className="col-sm-4">Note Title</label>
                                <div className="col-sm-8">
                                    <input ref={note_title} type="text" className="form-control"/>
                                </div>
                            </div>

                            <div className="form-group row">
                                <label className="col-sm-4">Note Content</label>
                                <div className="col-sm-8">
                                    <textarea ref={note_content} type="text" className="form-control"/>
                                </div>
                            </div>
                        </form>
                    </DialogContent>
                    <DialogActions>
                        <Button variant="contained" onClick={async()=>{
                            notesSetter([])
                            const all_new_notes = await bokx_man.addNote(note_title.current.value,note_content.current.value)
                            console.log(all_new_notes)
                            notesSetter(all_new_notes)
                        }}>Add</Button>
                        <Button variant="outlined"  onClick={
                    ()=>{
                        showAddNoteDialog(false)
                    }
                }>Cancel</Button>
                    </DialogActions>
                </Dialog>
                <Fab className="add_note_fab" color="primary" aria-label="add" style={fab_buttom_style} onClick={
                    ()=>{
                        showAddNoteDialog(true)
                    }
                }>
                    
                    <AddIcon/>
                </Fab>
                {renderedOutput}
            </div>
        </>
    );
}

function messageReceived(payload) {
    console.log("RECEIVED WS PAYLOAD: ",payload)
    console.log("receieved_data: ",payload.payload.message)
  }


function Dashboard({active_session}){

    const active_user = "active_session."
    
    const [xbokx_results,xaddResults] = useState([])
    const navigate = useNavigate()
    
    const [all_notes,setAllNotes] = useState([{
        "content": "Guy pushes smart phone in trench to record a video but it disappears under a bridge, funny ",
        "link": "https://vm.tiktok.com/ZMr7hE2qa/ This post is shared via TikTok Lite. Download TikTok Lite to enjoy more posts:  https://www.tiktok.com/tiktoklite",
        "title": "Link Note",
        "id": "1006"
      },
      {
        "content": "Cool Martha's quote",
        "link": "We all are a little bit broken, but life won't stop anyway",
        "title": "Link Note",
        "id": "1007"
      },])
    
    const [global_loader,setGlobalLoader] = useState(false)

    const bokxman_qaboter = (payload)=>{
        console.log("gotten new boter data")
        const all_datum = payload.payload.message
        console.log("gotten new boter data: ",all_datum)
        const all_data = [Object.fromEntries(all_datum.map((x)=>[x.Key,x.Value]))]
        console.log("\t all notes wh: ",all_datum[0])
        console.log("data sample: ",all_data)
        const ans_tag = <AnsBox note_data={all_data[0].answer}/>
        console.log(xbokx_results)
        xaddResults([...xbokx_results,ans_tag])
        //setGlobalLoader(false)
    }
    
    const bokxman_boter = (payload)=>{
        console.log("gotten new boter data")
        const all_datum = payload.payload.message
        const all_data = all_datum.map((active_arr)=>Object.fromEntries(active_arr.map((x)=>[x.Key,x.Value])))
        console.log("\t all notes wh: ",all_datum[0])
        console.log("data sample: ",all_data[0])
        setAllNotes(all_data)
        setGlobalLoader(false)
    }

    const bokxman_qa = (payload)=>{
        const all_data = payload.payload.message
        setAllNotes(all_data)
    }
    
    useEffect(()=>{
        active_session.auth.getSession().then(async({data,error})=>{
            
            //console.log(data)
            //console.log(error)
            setAllNotes([])
            if(data.session!=null){
                console.log("session present")
                bokx_man = new Bokxman(data.session.access_token)

                const {initdata,initerror,error_message} = await bokx_man.initBokxmanAsync()

                if(initerror){
                    console.log("\t\tRECEIVED BIG ERROR")
                    console.log(error)
                }
                else if(initdata){
                    console.log(initdata)
                    const channel_name = initdata.channel_name
                    console.log("using channel: ",channel_name)
                    msg_channel = active_session.channel(channel_name,{
                        config: {
                          broadcast: { self: true },
                        }
                    })
                    
                    msg_channel.on("broadcast",{"event":"siever"},
                        (payload)=>messageReceived(payload)
                    )

                    msg_channel.on("broadcast",{"event":"all_notes"},
                        (payload)=>bokxman_boter(payload)
                    )

                    msg_channel.on("broadcast",{"event":"bokx_qa"},
                        (payload)=>{
                            console.log("xxbokx_ai_reply: ",payload)
                            //setNewStyle({})
                            bokxman_qaboter(payload)
                        }
                    )

                    

                    msg_channel.subscribe((status) => {
                        if (status !== 'SUBSCRIBED') { return }
                        
                      })
                        
                    //const all_user_notes = await bokx_man.getAllNotes()
                    //console.log("all user notes: ",all_user_notes)
                    //setAllNotes(all_user_notes)
                    
                }
                else if(error_message){
                    console.log("\t\t__CONNECTION ERROR: "+error_message)
                }
                            }
                            else{
                                console.log("session not present on start")
                                navigate("/")
                    
                            }
                        
                        }).finally(()=>{
                    
                        })
    },[])
    
    return (
        <>
        <Navbar collapseOnSelect expand="lg"
                className="bg-dark navbar-dark">
            <Navbar.Brand href="#">
                    SmartBokx
            </Navbar.Brand>
            <Container>
            
                
                <Navbar.Toggle
                    aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="ml-auto">
                        <NavDropdown title="Account"
                            id="collapsible-nav-dropdown">
                            <NavDropdown.Item href="reset">
                                Reset Password
                            </NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item href="#action/3.4">
                                User Details
                            </NavDropdown.Item>
                            <NavDropdown.Divider />
                            <NavDropdown.Item href="" onClick={
                                async()=>{
                                    alert("logging out")
                                    const { error } = await active_session.auth.signOut()
                                    console.log(error)
                                }
                            }>
                                Log Out
                            </NavDropdown.Item>
                        </NavDropdown>
                    </Nav>
                    <Nav>
                        <Nav.Link href="#contactus">
                            Contact Us
                        </Nav.Link>
                        <Nav.Link eventKey={2} href="#community">
                            Community
                        </Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
        <div className="dashboard_container row">
            <div className="col-sm-2">
                <CategoryCard/>
            </div>
            <div className="col-sm-9">
            <Tabs
                defaultActiveKey="all_notes"
                id="uncontrolled-tab-example"
                className="mb-3">
                    <Tab eventKey="all_notes" title="All Notes">
                        <DashboardBody all_notes={all_notes} notesSetter={setAllNotes} bokxman_instance={bokx_man}/>
                    </Tab>
                    <Tab eventKey="search_notes" title="Search">
                        <SearchBody user_notes={all_notes}/>
                    </Tab>
                    <Tab eventKey="ai_search" title="Bokx AI">
                        <BokxAIBody bokx_results={xbokx_results} addResults={xaddResults}/>
                    </Tab>
            </Tabs>
            <Dialog
                    open={global_loader}>
                <DialogTitle>FETCHING DATA</DialogTitle>
                <DialogContent>
                    <CircleLoader color="black" size={30}/>
                </DialogContent>
            
            </Dialog>
            
                
            </div>
            <div className="col-sm-2">

            </div>
        </div>
        
        
        </>
    );
}

export default Dashboard;