package com.master.chat.gpt.core.key.updater;

import com.master.chat.llm.moonshot.MoonshotClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoonshotKeyUpdater implements KeyUpdater {
    @Autowired
    private MoonshotClient moonshotClient;
    
    @Override
    public String supportModel() {
        return "Moonshot";
    }
    
    @Override
    public void updateKey(String key) {
        moonshotClient.setApiKey(key);
    }
}