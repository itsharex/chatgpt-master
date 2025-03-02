package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.chatglm.ChatGLMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatGLMKeyUpdater implements KeyUpdater {
    @Autowired
    private ChatGLMClient chatGLMClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.CHATGLM.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        chatGLMClient.setAppKey(key);
    }
}