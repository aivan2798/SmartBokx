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
import { Navigate, useNavigate } from "react-router-dom";
import { Bokxman } from "../api/bokx_api";

//import {SxProps} from "@mui/system";
const endpoint = "https://ty051i.buildship.run/bokx/model"
var bokx_man = null
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


function BokxAIBody(client){
    
    const note_title = useRef()
    const note_content = useRef()

    const [add_note_dialog_state,showAddNoteDialog] = useState(false)
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
                                        <Button variant="contained"><Psychology/></Button>
                                    </div>
                                </div>
                                
                            </div>

                    </form>
                </div>
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
                    <div className="note_title">{note_link}</div>
                    <div className="note_content">{note_content}</div>
                </div>
                
            
        </div>
    )
}

function DashboardBody({all_notes,notesSetter,bokxman_instance}){
    console.log(all_notes)
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

function Dashboard({active_session}){

    const active_user = "active_session."
    
    
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

      useEffect(()=>{
        active_session.auth.getSession().then(async({data,error})=>{
            console.log(data)
            console.log(error)
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
                    console.log(data)
                    const all_user_notes = await bokx_man.getAllNotes()

                    setAllNotes(all_user_notes)
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
                        <BokxAIBody/>
                    </Tab>
            </Tabs>
                
            </div>
            <div className="col-sm-2">

            </div>
        </div>
        
        
        </>
    );
}

export default Dashboard;