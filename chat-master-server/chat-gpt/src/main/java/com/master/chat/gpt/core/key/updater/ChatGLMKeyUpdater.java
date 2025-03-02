package com.master.chat.gpt.core.key.updater;

import com.master.chat.llm.chatglm.ChatGLMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatGLMKeyUpdater implements KeyUpdater {
    @Autowired
    private ChatGLMClient chatGLMClient;
    
    @Override
    public String supportModel() {
        return "ChatGLM";
    }
    
    @Override
    public void updateKey(String key) {
        chatGLMClient.setAppKey(key);
    }
}