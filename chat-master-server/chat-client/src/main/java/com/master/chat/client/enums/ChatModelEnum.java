package com.master.chat.client.enums;

import lombok.Getter;

/**
 * 聊天大模型枚举类
 *
 * @author: Yang
 * @date: 2023/01/31
 * @version: 1.0.0
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
@Getter
public enum ChatModelEnum {

    /**
     * CHAT_GPT
     */
    OPENAI("ChatGPT", "ChatGPT"),

    WENXIN("WENXIN", "文心一言"),

    CHATGLM("ChatGLM", "智谱清言"),

    TONGYI("QIANWEN", "通义千问"),

    SPARK("SPARK", "讯飞星火"),

    MOONSHOT("Moonshot", "月之暗面"),

    DEEPSEEK("DeepSeek", "深度求索"),

    DOUBAO("Doubao", "豆包"),

    INTERNLM("Internlm", "书生·浦语"),

    LOCALLM("LocalLM", "本地模型"),

    ;

    /**
     * 值
     */
    private final String value;

    /**
     * 标签
     */
    private final String label;

    ChatModelEnum(final String value, final String label) {
        this.label = label;
        this.value = value;
    }

    public static ChatModelEnum getEnum(String value) {
        for (ChatModelEnum chatModelEnum : ChatModelEnum.values()) {
            if (value.equals(chatModelEnum.value)) {
                return chatModelEnum;
            }
        }
        return null;
    }

}
