package chukui.openaicodereviewsdk.domain.service;

import chukui.openaicodereviewsdk.infrastructure.git.GitCommand;
import chukui.openaicodereviewsdk.infrastructure.openai.IOpenAI;
import chukui.openaicodereviewsdk.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Author: chukui
 * @CreateTime: 2026-05-26  14:57
 * @Description: TODO
 * @Version: 1.0
 */
public abstract class AbstractOpenAiCodeReviewService implements IOpenAiCodeReviewService {

    private final Logger logger = LoggerFactory.getLogger(AbstractOpenAiCodeReviewService.class);

    protected final GitCommand gitCommand;
    protected final IOpenAI openAI;
    protected final WeiXin weiXin;

    public AbstractOpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        this.gitCommand = gitCommand;
        this.openAI = openAI;
        this.weiXin = weiXin;
    }

    @Override
    public void exec() {
        try {
            // 1. 获取提交代码
            String diffCode = getDiffCode();
            // 2. 开始评审代码
            String recommend = codeReview(diffCode);
            // 3. 记录评审结果；返回日志地址
            String logUrl = recordCodeReview(recommend);
            // 4. 发送消息通知（如果配置了微信）
            if (weiXin != null) {
                pushMessage(logUrl);
            } else {
                logger.info("微信通知未配置，跳过推送消息");
            }
        } catch (Exception e) {
            logger.error("openai-code-review error", e);
        }
    }

    protected abstract String getDiffCode() throws IOException, InterruptedException;
    protected abstract String codeReview(String diffCode) throws Exception;
    protected abstract String recordCodeReview(String recommend) throws Exception;
    protected abstract void pushMessage(String logUrl) throws Exception;

}
