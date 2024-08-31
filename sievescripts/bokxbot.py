import sieve

@sieve.Model(name="bokxbot",gpu=sieve.gpu.T4(),python_packages=["pinecone","transformers","sentence-transformers","openai","langchain","langchainhub","langchain-community",""])
class BokxBot():
    def __setup__(self):
        from pinecone import Pinecone
        from sentence_transformers import SentenceTransformer
        from langchain import hub
        from langchain.chat_models import ChatOpenAI
        import os
        open_ai_key = ""#os.getenv("open_ai_key")
        os.environ["LANGCHAIN_API_KEY"]=open_ai_key
        pinecone_key = ""#os.getenv("pinecone_key")
        print("using open ai: ",open_ai_key," with pinecone: ",pinecone_key)
        self.sentenceTransformer = SentenceTransformer('all-MiniLM-L6-v2')
        self.pinecone_object = Pinecone(api_key=pinecone_key)
        self.smart_bokx_index = self.pinecone_object.Index(name="smartbokx2")
        self.prompt = hub.pull("pwoc517/crafted_prompt_for_rag")
        self.chat_model = ChatOpenAI(
                openai_api_key=open_ai_key,
                openai_api_base="https://proxy.tune.app/",
                model_name="meta/llama-3.1-405b-instruct"
        )

    def autoGen(self,plain_url):
        xurl = {"url":plain_url}
        bokx_url_analyzer = sieve.function.get("aalg2798007-gmail-com/bokx_url_analyzer")
        output = bokx_url_analyzer.push(xurl)
        print('This is printing while a job is running in the background!')
        result = output.result()
        print(result)
        return result

    

    def __predict__(self,text):
        raw_text = text["content"]
        cmd = text["route"]
        user_id = text["user_id"]
        if cmd == "digest":
            made_vectors = self.genVectors(raw_text)
            self.addIndex(user_id,made_vectors)
            return {
                "message":"sync okay"
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
    
    def genVectors(self,notes_pack: list):
        vector_list = []
        for note_index in range(0,len(notes_pack)):
            note_pack = notes_pack[note_index]
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
        user_namespace = self.smart_bokx_index.describe_index_stats().get("namespaces").get(user_id)
        if user_namespace!=None:
            self.smart_bokx_index.delete(delete_all=True, namespace=user_id)
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