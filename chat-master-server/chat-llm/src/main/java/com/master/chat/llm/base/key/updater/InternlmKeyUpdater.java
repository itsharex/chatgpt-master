package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.doubao.DouBaoClient;
import com.master.chat.llm.internlm.InternlmClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternlmKeyUpdater implements KeyUpdater {
    @Autowired
    private InternlmClient internlmClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.INTERNLM.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        internlmClient.setToken(key);
    }
}