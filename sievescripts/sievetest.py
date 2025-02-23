import sieve,os

@sieve.Model(name="sieve_test",environment_variables=[sieve.Env(name="open_ai_key", description="open ai key from tune ai"),sieve.Env(name="OPENAI_API_KEY", description="Used to communication with the OpenAI API."),sieve.Env(name="OPTIONAL_CREDENTIALS", description="An optional credential used for a backup operation.", default="")],python_packages=["pinecone","transformers","sentence-transformers","openai","langchain","langchainhub","langchain-community",""])
class BokxBot:
    def __setup__(self):
        print("setting up")
    
    def __predict__(self, datum):
        op = os.environ["open_ai_key"]
        cus = os.environ["OPTIONAL_CREDENTIALS"]
        print("op is ",op)
        return {"op":op}