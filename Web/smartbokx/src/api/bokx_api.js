function getAllNotes(supabase_token){
    
}

export class Bokxman{
    bokx_url = "https://ty051i.buildship.run";
    
    constructor(supabase_token){
        this.supabase_token = supabase_token
        console.log("using token: ",supabase_token)
    }

    initBokxman(){
        console.log("\n\nInitialising ")
        const fetch_data  = fetch(this.bokx_url+"/bokx/auth/signup",{
            method: "GET",
            headers: {
                "x-bokx-key": this.supabase_token,
                'Content-Type': 'application/json'
            }
        }).then((response)=>{
            //console.log(response)
            console.log("\n\t\t"+response.statusText)
            return response.json()
        }).then((data) => {
            // Handle the parsed response
            console.log('Status:', data.status);
            console.log('Available Questions:', data.available_questions);
            console.log('Available Answers:', data.available_answers);

            return data
        }).catch((error)=>{
            console.log(error)
        })

    }

    async initBokxmanAsync(){
        console.log("\n\nInitialising ")

        try{
            const response  = await fetch(this.bokx_url+"/bokx/auth/signup",{
                method: "GET",
                headers: {
                    "x-bokx-key": this.supabase_token,
                    'Content-Type': 'application/json'
                }
            })
    
    
            if (!response.ok)
            {
                console.log("\t\tconnection error")
                error_data = await response.json()
                return {data: null,error: error_data}
            }
            const bokx_data = await response.json()
            //console.log(error)
    
            return {initdata: bokx_data,initerror:null}
    
        }
        catch(error){
                console.log(error.message)

                return {data:null,error:null,error_message:error.message}
        }
    }


    async getAllNotes(){
        const all_notes = await fetch(this.bokx_url+"/bokx/model",{
            headers:{
                "x-bokx-key": this.supabase_token,
                'Content-Type': 'application/json'
            },
            method:"POST",
            body:JSON.stringify({
                content:"",
                route:"digest"
            })
        })

        const notes_reply = await all_notes.json()
        console.log(notes_reply)

        const poll_reply = await this.pollJob(notes_reply.job_id)
        const notes_data = poll_reply.outputs[0].data
        console.log(notes_data)

        return notes_data
    }

    async askBokx(query){
        const query_job = await fetch(this.bokx_url+"/bokx/model",{
            headers:{
                "x-bokx-key": this.supabase_token,
                'Content-Type': 'application/json'
            },
            method:"POST",
            body:JSON.stringify({
                content:query,
                route:"query"
            })
        })
        console.log(query_job)
        const notes_reply = await query_job.json()
        console.log(notes_reply)

        const poll_reply = await this.pollJob(notes_reply.job_id)
        const notes_data = poll_reply.outputs[0].answer
        console.log(notes_data)

        return notes_data
    }

    async addNote(this_note_link,this_note_content){
        const all_notes = await fetch(this.bokx_url+"/bokx/model",{
            headers:{
                "x-bokx-key": this.supabase_token,
                'Content-Type': 'application/json'
            },
            method:"POST",
            body:JSON.stringify({
                content:[{
                    note_link: this_note_link,
                    note_description: this_note_content,
                    note_title: this_note_link
                }],
                route:"digest"
            })
        })

        const notes_reply = await all_notes.json()
        //console.log(notes_reply)

        const poll_reply = await this.pollJob(notes_reply.job_id)
        const notes_data = poll_reply.outputs[0].data
        //console.log(notes_data)

        return notes_data
    }

    async pollJob(jobid){

        let job_status = 0;
        let job_msg_status = "";
        let job_result = null;
        console.log("\n\n\t\tSTARTED JOB POLLING")
        //console.log(job_status)
    
        do{
            const all_notes = await fetch(this.bokx_url+"/bokx/model/jobs",{
                headers:{
                    "x-bokx-key": this.supabase_token,
                    'Content-Type': 'application/json'
                },
                method:"POST",
                body:JSON.stringify({
                    
                    job_id:jobid
                })
            })

            const tmp_job_status = await all_notes.json()
            job_result = tmp_job_status
            job_status = tmp_job_status.status
            job_msg_status = tmp_job_status.job_status
            console.log(job_msg_status)
            
        }
        while(((job_msg_status==="processing")||((job_msg_status==="queued"))))
            //console.log(job_result)
            return job_result
        
    }
}
