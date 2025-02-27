package com.master.chat.gpt.core.key.updater;

import com.master.chat.llm.tongyi.TongYiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QianWenKeyUpdater implements KeyUpdater {
    @Autowired
    private TongYiClient tongYiClient;
    
    @Override
    public String supportModel() {
        return "QIANWEN";
    }
    
    @Override
    public void updateKey(String key) {
        tongYiClient.setAppKey(key);
    }
}