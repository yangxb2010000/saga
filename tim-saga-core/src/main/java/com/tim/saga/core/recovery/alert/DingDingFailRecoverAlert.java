package com.tim.saga.core.recovery.alert;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;
import com.tim.saga.core.recovery.FailRecoverAlert;
import com.tim.saga.core.transaction.SagaTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * 钉钉报警事务回滚失败
 * @author xiaobing-notebook
 */
public class DingDingFailRecoverAlert implements FailRecoverAlert {
    private static Logger logger = LoggerFactory.getLogger(DingDingFailRecoverAlert.class);

    private DingTalkClient client;
    private MessageFormat messageFormat;

    public DingDingFailRecoverAlert(String webHookUrl){
        this.client = new DefaultDingTalkClient(webHookUrl);
        this.messageFormat = new MessageFormat("Saga事务(ID：{0}){1}重试次数达到上限({2}次)，不会再重试，请人工介入！！！最后一次异常：{3}");
    }

    @Override
    public void alertCompleteFail(SagaTransaction transaction, String lastError, int maxRetries) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");

        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(this.messageFormat.format(new Object[]{transaction.getId(), transaction.getName(), maxRetries, lastError} ));
        request.setText(text);

        try {
            client.execute(request);
        } catch (ApiException e) {
            logger.error("failed to send ding ding alert for transaction: {}, ex: {}", transaction.getId(), e);
        }
    }
}
