package com.master.chat.gpt.core.key.updater;

import com.master.chat.llm.locallm.LocalLMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalLMKeyUpdater implements KeyUpdater {
    @Autowired
    private LocalLMClient localLMClient;
    
    @Override
    public String supportModel() {
        return "LocalLM";
    }
    
    @Override
    public void updateKey(String key) {
        localLMClient.setApiKey(key);
    }
}