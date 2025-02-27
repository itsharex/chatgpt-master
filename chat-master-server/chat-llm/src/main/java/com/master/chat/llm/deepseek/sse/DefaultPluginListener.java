package com.master.chat.llm.deepseek.sse;

import com.master.chat.llm.deepseek.DeepSeekStreamClient;
import com.master.chat.llm.openai.entity.chat.ChatCompletion;
import com.master.chat.llm.openai.plugin.PluginAbstract;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSourceListener;

/**
 * 描述： 插件开发返回信息收集sse监听器
 *
 */
@Slf4j
public class DefaultPluginListener extends PluginListener {

    public DefaultPluginListener(DeepSeekStreamClient client, EventSourceListener eventSourceListener, PluginAbstract plugin, ChatCompletion chatCompletion) {
        super(client, eventSourceListener, plugin, chatCompletion);
    }
}
