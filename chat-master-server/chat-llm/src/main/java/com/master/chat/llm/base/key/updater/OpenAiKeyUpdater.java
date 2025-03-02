package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.openai.OpenAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class OpenAiKeyUpdater implements KeyUpdater {
    @Autowired
    private OpenAiClient openAiClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.OPENAI.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        openAiClient.setApiKey(Collections.singletonList(key));
    }
}