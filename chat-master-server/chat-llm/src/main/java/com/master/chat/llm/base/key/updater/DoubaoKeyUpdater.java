package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.deepseek.DeepSeekStreamClient;
import com.master.chat.llm.doubao.DouBaoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DoubaoKeyUpdater implements KeyUpdater {
    @Autowired
    private DouBaoClient douBaoClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.DOUBAO.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        douBaoClient.setApiKey(key);
    }
}