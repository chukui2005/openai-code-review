package chukui.openaicodereviewsdk.infrastructure.openai;

import chukui.openaicodereviewsdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import chukui.openaicodereviewsdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;

public interface IOpenAI {

    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;

}
