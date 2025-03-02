package com.master.chat.gpt.core.key.updater;

import com.master.chat.llm.wenxin.WenXinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WenXinKeyUpdater implements KeyUpdater {
    @Autowired
    private WenXinClient wenXinClient;
    
    @Override
    public String supportModel() {
        return "WENXIN";
    }
    
    @Override
    public void updateKey(String key) {
        wenXinClient.setApiKey(key);
    }
}