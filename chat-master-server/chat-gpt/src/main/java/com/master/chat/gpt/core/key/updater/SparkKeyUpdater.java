package com.master.chat.gpt.core.key.updater;

import com.master.chat.llm.spark.SparkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SparkKeyUpdater implements KeyUpdater {
    @Autowired
    private SparkClient sparkClient;
    
    @Override
    public String supportModel() {
        return "SPARK";
    }
    
    @Override
    public void updateKey(String key) {
        sparkClient.setApiKey(key);
    }
}