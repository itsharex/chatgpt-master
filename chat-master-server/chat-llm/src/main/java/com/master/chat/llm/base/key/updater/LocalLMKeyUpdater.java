package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.locallm.LocalLMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalLMKeyUpdater implements KeyUpdater {
    @Autowired
    private LocalLMClient localLMClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.LOCALLM.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        localLMClient.setApiKey(key);
    }
}