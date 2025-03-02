package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.chatglm.ChatGLMClient;
import com.master.chat.llm.deepseek.DeepSeekStreamClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DeepSeekKeyUpdater implements KeyUpdater {
    @Autowired
    private DeepSeekStreamClient deepSeekStreamClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.DEEPSEEK.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        deepSeekStreamClient.setApiKey(Collections.singletonList(key));
    }
}