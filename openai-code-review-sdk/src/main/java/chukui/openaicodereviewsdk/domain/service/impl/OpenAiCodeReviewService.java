package chukui.openaicodereviewsdk.domain.service.impl;

import chukui.openaicodereviewsdk.domain.model.Model;
import chukui.openaicodereviewsdk.domain.service.AbstractOpenAiCodeReviewService;
import chukui.openaicodereviewsdk.infrastructure.git.GitCommand;
import chukui.openaicodereviewsdk.infrastructure.openai.IOpenAI;
import chukui.openaicodereviewsdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import chukui.openaicodereviewsdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;
import chukui.openaicodereviewsdk.infrastructure.weixin.WeiXin;
import chukui.openaicodereviewsdk.infrastructure.weixin.dto.TemplateMessageDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenAiCodeReviewService extends AbstractOpenAiCodeReviewService {

    public OpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        super(gitCommand, openAI, weiXin);
    }

    @Override
    protected String getDiffCode() throws IOException, InterruptedException {
        return gitCommand.diff();
    }

    @Override
    protected String codeReview(String diffCode) throws Exception {
        ChatCompletionRequestDTO request = new ChatCompletionRequestDTO();
        request.setModel(Model.DEEPSEEK_V4_FLASH.getCode());
        request.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequestDTO.Prompt("user",
                        "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言，请您根据git diff记录，对代码做出评审。代码如下:"));
                add(new ChatCompletionRequestDTO.Prompt("user", diffCode));
            }
        });

        ChatCompletionSyncResponseDTO response = openAI.completions(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    @Override
    protected String recordCodeReview(String recommend) throws Exception {
        return gitCommand.commitAndPush(recommend);
    }

    @Override
    protected void pushMessage(String logUrl) throws Exception {
        Map<String, Map<String, String>> data = new HashMap<>();
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.REPO_NAME, gitCommand.getProject());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.BRANCH_NAME, gitCommand.getBranch());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_AUTHOR, gitCommand.getAuthor());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_MESSAGE, gitCommand.getMessage());
        weiXin.sendTemplateMessage(logUrl, data);
    }
}
