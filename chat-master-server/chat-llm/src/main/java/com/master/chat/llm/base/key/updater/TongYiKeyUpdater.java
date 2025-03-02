package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.tongyi.TongYiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TongYiKeyUpdater implements KeyUpdater {
    @Autowired
    private TongYiClient tongYiClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.TONGYI.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        tongYiClient.setAppKey(key);
    }
}