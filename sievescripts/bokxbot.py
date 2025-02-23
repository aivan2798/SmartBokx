import sieve,os

@sieve.Model(name="bokxbot",environment_variables=[sieve.Env(name="open_ai_key", description="open ai key from tune ai"),sieve.Env(name="pinecone_key", description="Pinecone Key", default="")],python_packages=["pinecone","transformers","sentence-transformers","openai","langchain","langchainhub","langchain-community",""])
class BokxBot():
    
    def __setup__(self):
        
        from sentence_transformers import SentenceTransformer
        
       
        self.open_ai_key = None #os.environ["open_ai_key"]
        
        #os.environ["LANGCHAIN_API_KEY"]=open_ai_key
        self.pinecone_key = None#os.environ["pinecone_key"]
        #print("using open ai: ",open_ai_key," with pinecone: ",pinecone_key)
        self.sentenceTransformer = SentenceTransformer('all-MiniLM-L6-v2')
        #self.pinecone_object = Pinecone(api_key=pinecone_key)
        #self.smart_bokx_index = self.pinecone_object.Index(name="smartbokx2")
        #self.prompt = hub.pull("pwoc517/crafted_prompt_for_rag")
        #self.chat_model = ChatOpenAI(
        #        openai_api_key=open_ai_key,
        #        openai_api_base="https://proxy.tune.app/",
        #        model_name="meta/llama-3.1-405b-instruct"
        #)

    def autoGen(self,plain_url):

        
        xurl = {"url":plain_url}
        bokx_url_analyzer = sieve.function.get("cot2we-gmail-com/bokx_url_analyzer")
        output = bokx_url_analyzer.push(xurl)
        print('This is printing while a job is running in the background!')
        result = output.result()
        print(result)
        return result

    

    def __predict__(self,text):
        from pinecone import Pinecone
        from langchain import hub
        from langchain.chat_models import ChatOpenAI
        if self.pinecone_key == None:
                self.pinecone_key = os.environ["pinecone_key"]
                
                self.pinecone_object = Pinecone(api_key=self.pinecone_key)
                self.smart_bokx_index = self.pinecone_object.Index(name="smartbokx2")
        
        if self.open_ai_key == None:
            self.open_ai_key = os.environ["open_ai_key"]
            os.environ["LANGCHAIN_API_KEY"]=self.open_ai_key
            self.prompt = hub.pull("pwoc517/crafted_prompt_for_rag")
            self.chat_model = ChatOpenAI(
                openai_api_key=self.open_ai_key,
                openai_api_base="https://proxy.tune.app/",
                model_name="meta/llama-3.1-405b-instruct"
            )
        print("using open ai: ",self.open_ai_key," with pinecone: ",self.pinecone_key)
        raw_text = text["content"]
        cmd = text["route"]
        user_id = text["user_id"]
        if cmd == "digest":
            default_index = 0
            user_namespace = self.smart_bokx_index.describe_index_stats().get("namespaces").get(user_id)
            if user_namespace!=None:
                default_index = user_namespace.vector_count
                all_db_notes = self.getAllNotes(user_id,default_index)
                to_db, to_user = self.findMissingNotes(user_id,raw_text,all_db_notes)

                if((len(to_db)>0)):
                    made_vectors = self.genVectors(to_db,default_index)
                    self.addIndex(user_id,made_vectors)
                    return {
                                "message":"sync okay",
                                "data":to_user
                        }
                else:
                    return {
                                "message":"no data to sync",
                                "data":to_user
                        }
            else:
                if(len(raw_text)>0):
                    made_vectors = self.genVectors(raw_text,default_index)
                    self.addIndex(user_id,made_vectors)
                    return {
                        "message":"sync okay",
                        "data":[]
                    }
                else:
                    return {
                                "message":"no data to sync",
                                "data":[]
                        }
        elif cmd == "query":
            query_answer = self.getAnswer(user_id,raw_text)
            return query_answer
        elif cmd == "auto_gen":
            url_text = self.autoGen(raw_text)
            return url_text
        else:
            return {
                "message":"unknown command"
            }
    def xgetAllNotes(self,user_id,notes_count):
        all_ids = []
        all_vectors_metadata = []
        total_ids = 0
        for ids in self.smart_bokx_index.list(namespace=user_id):
                for id in ids:
                    all_ids.append(id)
        total_ids = len(all_ids)
        all_vectors = self.smart_bokx_index.fetch(ids=all_ids, namespace=user_id)
        vector_data = all_vectors.vectors
        for an_id in all_ids:
            vector_datum = vector_data[an_id].metadata
            vector_datum["id"] = an_id
            all_vectors_metadata.append(vector_datum)
        return all_vectors_metadata

    def getAllNotes(self,user_id,notes_count):
        all_ids = []
        all_vectors_metadata = []
        total_ids = 0
        for ids in self.smart_bokx_index.list(namespace=user_id):
                total_ids = len(ids)
                all_vectors = self.smart_bokx_index.fetch(ids=ids, namespace=user_id)
                vector_data = all_vectors.vectors
                for an_id in ids:
                    vector_datum = vector_data[an_id].metadata
                    vector_datum["id"] = an_id
                    all_vectors_metadata.append(vector_datum)
        return all_vectors_metadata

    def findMissingNotes(self,user_id,user_notes,db_notes):
        missing_in_db = user_notes
        missing_in_user = db_notes

        xmissing_in_db_values = []
        xmissing_in_user_values = []


        user_notes_len = len(user_notes)
        db_notes_len = len(db_notes)

        if(user_notes_len<db_notes_len):
            user_index = 0
            for user_note in user_notes:
                db_index = 0
                for db_note in db_notes:
                    if (user_note["note_title"] == db_note["title"]):# and (user_note["note_description"] == db_note["content"]):
                        missing_in_user.remove(db_note)
                        missing_in_db.remove(user_note)
                        db_index = db_index + 1
                        break
            user_index = user_index + 1
        else:
            db_index = 0
            for db_note in db_notes:
                user_index = 0
                for user_note in user_notes:
                    if (db_note["title"] == user_note["note_title"]):# and (db_note["content"] == user_note["note_description"]):
                        
                        #missing_in_user.remove(db_index)
                        #missing_in_db.remove(user_index)
                        missing_in_user.remove(db_note)
                        missing_in_db.remove(user_note)
                        user_index = user_index + 1
                        break
                db_index = db_index + 1
        return missing_in_db, missing_in_user


    def genVectors(self,notes_pack: list,default_index=0):
        vector_list = []
        last_point = default_index+len(notes_pack)
        for note_index in range(default_index,last_point):
            note_pack = notes_pack[(note_index-default_index)]
            note_title = note_pack["note_title"]
            note_link = note_pack["note_link"]
            note_content = note_pack["note_description"]
            note_embeddings = self.sentenceTransformer.encode(note_content, convert_to_tensor=True)
            note_embeddings_numpy = note_embeddings.numpy()

            note_id = str(note_index)
    

            metadata = {
                    "title": note_title,
                    "link": note_link,
                    "content": note_content
            }
            vector = {
                    "id": note_id,
                    "values": note_embeddings_numpy,
                    "metadata": metadata
            }
            vector_list.append(vector)
        return vector_list

    def genQueryVector(self,query):
        query_embedding = self.sentenceTransformer.encode(query, convert_to_tensor=True)
        query_embedding_numpy = query_embedding.numpy().tolist()
        return query_embedding_numpy

    def addIndex(self,user_id,note_vectors):
        print(user_id)
        self.smart_bokx_index.upsert(vectors=note_vectors,namespace= user_id)

    def searchPinecone(self,user_id,query_embeddings,top_k):
        
        result = self.smart_bokx_index.query(
                    namespace=user_id,
                    vector=query_embeddings,
                    top_k=top_k,
                    include_metadata=True
                )

        return result
        #return str(self.smart_bokx_index)
    
    def genPrompt(self,context_active, query_active):
        
        example_messages = self.prompt.invoke(
                                    {"context": context_active, "question": query_active}
            ).to_messages()

        return example_messages
    
    def genContext(self,db_results):
        context_text = ""
        for db_match in db_results["matches"]:
            content = db_match["metadata"]["content"]
            title = db_match["metadata"]["title"]
            link = db_match["metadata"]["link"]
            context_text +=  f"Title:{title}\tLink:{link}\tContent:{content}\t\t"
        return context_text

    def getAnswer(self,user_id,query):
        query_vec = self.genQueryVector(query)
        #print(query_vec)
        query_results = self.searchPinecone(user_id,query_vec,5)
        print(query_results)
        context_text = self.genContext(query_results)
        print(context_text)
        active_prompt = self.genPrompt(context_text,query)[0].content
        print(active_prompt)
        out = self.chat_model.predict(active_prompt)
        print(out)
        bot_msg = {
            "answer":out
        }
        return bot_msg