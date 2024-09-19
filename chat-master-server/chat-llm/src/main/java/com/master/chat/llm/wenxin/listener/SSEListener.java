package com.master.chat.llm.wenxin.listener;

import com.alibaba.fastjson.JSON;
import com.master.chat.client.enums.ChatContentEnum;
import com.master.chat.client.enums.ChatRoleEnum;
import com.master.chat.client.enums.ChatStatusEnum;
import com.master.chat.client.model.command.ChatMessageCommand;
import com.master.chat.client.service.GptService;
import com.master.chat.framework.util.ApplicationContextUtil;
import com.master.chat.llm.base.entity.ChatData;
import com.master.chat.llm.base.websocket.WebsocketServer;
import com.master.chat.llm.base.websocket.constant.FunctionCodeConstant;
import com.master.chat.llm.base.websocket.entity.WebSocketData;
import com.master.chat.llm.wenxin.entity.response.ChatResponse;
import com.master.chat.common.exception.ErrorException;
import com.master.chat.framework.validator.ValidatorUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 流式响应同步返回 监听
 *
 * @author: Yang
 * @date: 2023/9/7
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
@Slf4j
public class SSEListener extends EventSourceListener {
    private static final String FINISH = "[finish]";
    private long tokens;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private HttpServletResponse response;
    private SseEmitter sseEmitter;
    private StringBuffer output = new StringBuffer();
    private Long chatId;
    private String parentMessageId;
    private String conversationId;
    private String finishReason;
    private String model;
    private String version;
    private Boolean error;
    private String errTxt;
    private String uid;
    private Boolean isWs;

    public SSEListener(HttpServletResponse response, SseEmitter sseEmitter, Long chatId, String parentMessageId, String model, String version, String uid, Boolean isWs) {
        this.response = response;
        this.sseEmitter = sseEmitter;
        this.chatId = chatId;
        this.parentMessageId = parentMessageId;
        this.model = model;
        this.version = version;
        this.uid = uid;
        this.isWs = isWs;
        this.error = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response rp) {
        if (response == null) {
            log.error("客户端非sse推送");
            return;
        }
        if (!isWs) {
            response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        log.info("{}建立sse连接...", model);
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("SSE返回，模型：{}，ID：{}，TYPE：{}，数据：{}", model, id, type, data);
        ChatData chatData;
        try {
            String text = handleCompletionResponse(data);
            if (FINISH.equals(text)) {
                log.info("{}返回数据结束了", model);
                sseEmitter.complete();
                ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(conversationId).parentMessageId(parentMessageId)
                        .model(model).modelVersion(version)
                        .content(output.toString()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.ASSISTANT.getValue()).finishReason(finishReason)
                        .status(ChatStatusEnum.SUCCESS.getValue()).appKey("").usedTokens(tokens)
                        .build();
                ApplicationContextUtil.getBean(GptService.class).saveChatMessage(chatMessage);
                return;
            }
            chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
                    .parentMessageId(parentMessageId)
                    .role(ChatRoleEnum.ASSISTANT.getValue()).content(text).build();
            if (isWs) {
                WebSocketData wsData = WebSocketData.builder().functionCode(FunctionCodeConstant.MESSAGE).message(chatData).build();
                WebsocketServer.sendMessageByUserId(uid, JSON.toJSONString(wsData));
            } else {
                response.getWriter().write(ValidatorUtil.isNull(text) ? JSON.toJSONString(chatData) : "\n" + JSON.toJSONString(chatData));
                response.getWriter().flush();
            }
        } catch (Exception e) {
            log.error("消息错误", e);
            eventSource.cancel();
            countDownLatch.countDown();
            throw new ErrorException();
        }
    }

    /**
     * 处理流式返回
     *
     * @return
     */
    private String handleCompletionResponse(String data) {
        ChatResponse completionResponse = JSON.parseObject(data, ChatResponse.class);
        if (completionResponse.getIs_end()) {
            tokens = completionResponse.getUsage().getTotalTokens();
            return FINISH;
        }
        String content = completionResponse.getResult();
        if (ValidatorUtil.isNull(conversationId) && ValidatorUtil.isNotNull(completionResponse.getId())) {
            conversationId = completionResponse.getId();
        }
        finishReason = FINISH;
        output.append(content).toString();
        return output.toString();
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("{}关闭sse连接，流式输出返回值总共{}tokens", model, tokens() - 2);
        eventSource.cancel();
        countDownLatch.countDown();
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (ValidatorUtil.isNotNull(response) && Objects.nonNull(response.body())) {
            log.error("sse连接异常data.body：{}，异常：{}", response.body().string(), t);
        } else {
            log.error("sse连接异常data：{}，异常：{}", response, t);
        }
        ChatData chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
                .parentMessageId(parentMessageId)
                .role(ChatRoleEnum.ASSISTANT.getValue()).content("文心大模型接口请求失败，无法响应！").contentType(ChatContentEnum.TEXT.getValue()).build();
        this.error = true;
        this.errTxt = "文心大模型接口连接异常";
        this.response.getWriter().write(JSON.toJSONString(chatData));
        this.response.getWriter().flush();
        eventSource.cancel();
        countDownLatch.countDown();
    }

    public CountDownLatch getCountDownLatch() {
        return this.countDownLatch;
    }

    /**
     * tokens
     *
     * @return
     */
    public long tokens() {
        return tokens;
    }

    public Boolean getError() {
        return error;
    }

    public String getErrTxt() {
        return errTxt;
    }

}
