import sieve
import urllib 
import requests
import datetime
@sieve.Model(name="bokx_url_analyzer",gpu=sieve.gpu.T4(),python_version="3.9",python_packages=["supabase","MoviePy", "tiktok_downloader"])
class BokxURL():
    def __setup__(self):
        import supabase
        from moviepy.editor import VideoFileClip
        from tiktok_downloader import snaptik
        supabase_url = ""
        supabase_api_key = ""
        self.bucket_name = ""
        
        self.supabase_man = supabase.create_client(supabase_url,supabase_api_key)
        

    def __predict__(self,text):
        active_url = text["url"]
        status,condensed = self.startFlow(active_url)
        return {
            "status_code":status,
            "message":condensed
        }
    
    def genPrompt(self,context_active, query_active):
        
        example_messages = self.prompt.invoke(
                                    {"context": context_active, "question": query_active}
            ).to_messages()

        return example_messages
    #upload to bucket
    def uploadToBucket(self,file_name):
        filepath = "bokx"+file_name.split("/")[-1]
        with open(file_name, 'rb') as f:
            #filename = file_name
            print(file_name)
            self.supabase_man.storage.from_(id = self.bucket_name).upload(file=f,path=filepath, file_options={"content-type": "video/mp4"})
        public_url = self.supabase_man.storage.from_(id = self.bucket_name).get_public_url(path=filepath)
        print("uploaded to supabase: ",public_url)
        return public_url
    
    #tiktok downloader
    def tiktokDownload(self,tiktok_link):
        timestamp = datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
        print(timestamp)
        file_name = "bokx"+timestamp+".mp4"
        print(file_name)
        d=snaptik(tiktok_link)
        print(d)
        d[0].download(file_name)
        return file_name

    def getDuration(self,clip):
        
        # getDurationng duration of the video 
        duration = clip.duration 
 
        # printing duration 
        print("Duration : " + str(duration))
        return duration

    def clipVideo(self,clip):
       
        duration = clip.duration 
 
        # printing duration 
        print("Duration : " + str(duration))
        return duration

    def youtubeDowload(self,youtube_link):
        url = youtube_link
        resolution = "720p"
        include_audio = True
        youtube_to_mp4 = sieve.function.get("sieve/youtube_to_mp4")
        output = youtube_to_mp4.run(url, resolution, include_audio)
        print(output)
        return output

    def summerizeVid(self,vid_url):
        video = sieve.File(url=vid_url)
        conciseness = "concise"
        visual_detail = "high"
        spoken_context = True
        object_context = False
        detail_boost = False
        chunk_by_scene = False
        enable_references = False
        return_metadata = False
        image_only = False
        additional_instructions = ""
        llm_backend = "openai"
        minimum_scene_duration = -1
        describe = sieve.function.get("sieve/describe")
        output = describe.run(video, conciseness, visual_detail, spoken_context, object_context, detail_boost, chunk_by_scene, minimum_scene_duration, enable_references, return_metadata, image_only, additional_instructions, llm_backend)
        #print(output)
        return output

    def checkUrl(self,url):
        requests_response = requests.get(url, allow_redirects=False)
        status_code = requests_response.status_code
        master_url = ""
        if (status_code > 300) and (status_code < 309):
            master_url = requests_response.headers["Location"]
            print("red: ",master_url)
        elif(status_code == 200):
            master_url = url
            print(master_url)
        return master_url

    def processUrl(self,master_url):
        parsed_url = urllib.parse.urlparse(master_url.lower())
        split_url = parsed_url.netloc.split(".")
        file_name = ""
        try:
            if (split_url[0] == "youtube") or (split_url[1] == "youtube"):
                print("from youtube: ",master_url)
                output = self.youtubeDowload(master_url)
                filepath = output.path  
                print(filepath)
                print(output.url)
                file_name = filepath
            elif (split_url[0] == "tiktok") or (split_url[1] == "tiktok"):
                print("from tiktok: ",master_url)
                file_name = self.tiktokDownload(master_url)
                print("from tiktok")
            
            #clip = VideoFileClip(filepath)
            clip_len = 20#self.getDuration(clip)
            if(clip_len>600):
                sub_clip = clip.subclip(0, 10)
                clip_name = filepath+"cliped"+".mp4"
                sub_clip.write_videofile(clip_name)
                file_name = clip_name
        except Exception as e:
            print(e)
            file_name = ""
        return file_name

    def startFlow(self,flow_url):
        master_url = self.checkUrl(flow_url)
        file_path = self.processUrl(master_url)
        

        if (file_path != ""):
            public_url = self.uploadToBucket(file_path)
            summerised = self.summerizeVid(public_url)
            file_name = "bokx"+file_path.split("/")[-1]
            self.supabase_man.storage.from_(id = self.bucket_name).remove(file_name)
            return 200,summerised
        else:
            return 500,"url error"