package com.lvwj.halo.rocketmq.producer;

import com.alibaba.fastjson.JSONObject;
import com.lvwj.halo.common.constants.SystemConstant;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.rocketmq.annotation.MessageMode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.apache.rocketmq.client.impl.CommunicationMode;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvweijie
 * @date 2023年12月05日 17:32
 */
@Slf4j
@AllArgsConstructor
public class RocketMQProducerHelper {

    private final RocketMQTemplate rocketMQTemplate;

    public void apply(Long msgPK, String msgKey, String topic, String tag, String body, Integer delayLevel,
                      MessageMode msgMode, CommunicationMode communicationMode, Long timeout, boolean bodyWithHeader, boolean isStoreMsg) {
        // 获取Destination
        String destination = getDestination(topic, tag);
        // 获取Message
        Message<String> message = getMsg(msgPK, body, msgKey, tag, delayLevel, bodyWithHeader);
        switch (communicationMode) {
            case SYNC:
                SendResult sendResult;
                if (msgMode == MessageMode.ORDER && StringUtils.hasText(msgKey)) {
                    sendResult = this.rocketMQTemplate.syncSendOrderly(destination, message, msgKey, timeout, delayLevel);
                } else {
                    sendResult = this.rocketMQTemplate.syncSend(destination, message, timeout, delayLevel);
                }
                String result = sendResult.getSendStatus().equals(SendStatus.SEND_OK) ? "成功" : "失败";
                log.info("[SYNC]MQ发送{}: [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [Msg:{}], [Result:{}]", result, topic, tag, sendResult.getMsgId(), msgKey, JsonUtil.toJson(message.getPayload()), sendResult);
                break;
            case ASYNC:
                SendCallback sendCallback = new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("[ASYNC]MQ发送成功: [Topic:{}], [Tag:{}], [Id:{}], [Key:{}], [Msg:{}], [Result:{}]", topic, tag, sendResult.getMsgId(), msgKey, JsonUtil.toJson(message.getPayload()), sendResult);
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("[ASYNC]MQ发送失败: [Topic:{}], [Tag:{}], [Key:{}], [Msg:{}]", topic, tag, msgKey, JsonUtil.toJson(message.getPayload()), e);
                    }
                };
                if (msgMode == MessageMode.ORDER && StringUtils.hasText(msgKey)) {
                    this.rocketMQTemplate.asyncSendOrderly(destination, message, msgKey, sendCallback, timeout, delayLevel);
                } else {
                    this.rocketMQTemplate.asyncSend(destination, message, sendCallback, timeout, delayLevel);
                }
                break;
            case ONEWAY:
                if (msgMode == MessageMode.ORDER && StringUtils.hasText(msgKey)) {
                    this.rocketMQTemplate.sendOneWayOrderly(destination, message, msgKey);
                } else {
                    this.rocketMQTemplate.sendOneWay(destination, message);
                }
                log.info("[ONEWAY]MQ发送成功: [Topic:{}], [Tag:{}], [Key:{}], [Msg:{}]", topic, tag, msgKey, JsonUtil.toJson(message.getPayload()));
                break;
        }
    }

    private String getDestination(String topic, String tag) {
        if (StringUtils.hasText(tag)) {
            return topic + StringPool.COLON + tag;
        } else {
            return topic;
        }
    }

    private Message<String> getMsg(Long msgPK, String body, String key, String tag, int delayLevel, boolean bodyWithHeader) {
        body = getBody(msgPK, body, key, tag, bodyWithHeader);
        MessageBuilder<String> builder = MessageBuilder.withPayload(body);
        String traceId = getTraceId();
        if (Func.isNotBlank(traceId)) {
            builder.setHeader(SystemConstant.TID, traceId);
        }
        if (null != msgPK && msgPK > 0) {
            builder.setHeader("msgPK", msgPK);
        }
        if (StringUtils.hasText(key)) {
            builder.setHeader(MessageConst.PROPERTY_KEYS, key);
        }
        if (StringUtils.hasText(tag)) {
            builder.setHeader(MessageConst.PROPERTY_TAGS, tag);
        }
        if (delayLevel > 0) {
            builder.setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, delayLevel);
        }
        return builder.build();
    }

    private String getTraceId() {
        String traceId = ThreadContext.get(SystemConstant.TRACE_ID);
        if (Func.isBlank(traceId) || SystemConstant.DEFAULT_TID.equals(traceId)) {
            traceId = TraceContext.traceId();
        }
        if (SystemConstant.DEFAULT_TID.equals(traceId)) {
            traceId = StringPool.EMPTY;
        }
        return traceId;
    }

    private String getBody(Long msgPK, String body, String key, String tag, boolean bodyWithHeader) {
        if (!bodyWithHeader) return body;
        Map<String, Object> headers = new HashMap<>(2);
        headers.put("keys", key);
        headers.put("tag", tag);
        if (null != msgPK) {
            headers.put("id", msgPK);
        }
        headers.put("timestamp", System.currentTimeMillis());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("headers", headers);
        jsonObject.put("payload", body);
        return jsonObject.toString();
    }
}
