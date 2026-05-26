package chukui.openaicodereviewsdk;

import chukui.openaicodereviewsdk.domain.service.impl.OpenAiCodeReviewService;
import chukui.openaicodereviewsdk.infrastructure.git.GitCommand;
import chukui.openaicodereviewsdk.infrastructure.openai.IOpenAI;
import chukui.openaicodereviewsdk.infrastructure.openai.impl.DeepSeek;
import chukui.openaicodereviewsdk.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAiCodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiCodeReview.class);

    public static void main(String[] args) throws Exception {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        // 微信通知是可选的：只有配置了 WEIXIN_APPID 才启用
        WeiXin weiXin = null;
        String wxAppid = getEnvOptional("WEIXIN_APPID");
        if (wxAppid != null && !wxAppid.isEmpty()) {
            weiXin = new WeiXin(
                    wxAppid,
                    getEnv("WEIXIN_SECRET"),
                    getEnv("WEIXIN_TOUSER"),
                    getEnv("WEIXIN_TEMPLATE_ID")
            );
        }

        IOpenAI openAI = new DeepSeek(
                getEnv("DEEPSEEK_API_KEY"),
                getEnv("DEEPSEEK_API_HOST")
        );

        OpenAiCodeReviewService service = new OpenAiCodeReviewService(gitCommand, openAI, weiXin);
        service.exec();

        logger.info("openai-code-review done!");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException(key + " is not configured");
        }
        return value;
    }

    private static String getEnvOptional(String key) {
        return System.getenv(key);
    }

}
