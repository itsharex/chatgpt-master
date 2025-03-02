package com.master.chat.llm.base.key.updater;

import com.master.chat.client.enums.ChatModelEnum;
import com.master.chat.llm.wenxin.WenXinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WenXinKeyUpdater implements KeyUpdater {
    @Autowired
    private WenXinClient wenXinClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.WENXIN.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        wenXinClient.setApiKey(key);
    }
}