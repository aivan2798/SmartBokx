import sieve

@sieve.Model(name="answer_bot", gpu=sieve.gpu.L4(),python_packages = ["transformers","torch","accelerate"])
class AnswerBot:
    def __setup__(self):
        from transformers import AutoTokenizer, AutoModelForCausalLM
        import transformers
        import torch
        self.model = "tiiuae/falcon-7b-instruct"

        self.tokenizer = AutoTokenizer.from_pretrained(self.model)
        self.pipeline = transformers.pipeline(
                        "text-generation",
                        model=self.model,
                    tokenizer=self.tokenizer,
                    torch_dtype=torch.bfloat16,
                    #trust_remote_code=True,
                    device_map="auto",
        )

    def inf(self,text):
        text_in = text["message"]
        sequences = self.pipeline(
                    text_in,
                    max_length=900,
                    do_sample=True,
                    top_k=10,
                    num_return_sequences=1,
                    eos_token_id=self.tokenizer.eos_token_id,
        )
        return sequences
        #for seq in sequences:
        #    print(f"Result: {seq['generated_text']}")
    
    def __predict__(self,corpus_txt):
        return self.inf(corpus_txt)