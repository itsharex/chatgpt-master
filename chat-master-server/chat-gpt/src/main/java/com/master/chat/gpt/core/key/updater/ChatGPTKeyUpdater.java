package com.master.chat.gpt.core.key.updater;

import com.master.chat.llm.openai.OpenAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ChatGPTKeyUpdater implements KeyUpdater {
    @Autowired
    private OpenAiClient openAiClient;
    
    @Override
    public String supportModel() {
        return "ChatGPT";
    }
    
    @Override
    public void updateKey(String key) {
        openAiClient.setApiKey(Collections.singletonList(key));
    }
}