package com.example.recommend.llm;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
public interface LlmClient {
    /**
     * Send a chat completion request and return the assistant's text reply.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    String chat(String systemPrompt, List<ChatTurn> history) throws JsonMappingException, JsonProcessingException;
    
    class ChatTurn {

        private String role;
        private String content;

        public ChatTurn(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}