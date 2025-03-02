package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.moonshot.MoonshotClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoonshotKeyUpdater implements KeyUpdater {
    @Autowired
    private MoonshotClient moonshotClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.MOONSHOT.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        moonshotClient.setApiKey(key);
    }
}