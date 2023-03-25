package com.inlym.lifehelper.extern.chatgpt.service;

import com.inlym.lifehelper.extern.chatgpt.config.ChatGPTProperties;
import com.inlym.lifehelper.extern.chatgpt.exception.ChatGPTCommonException;
import com.inlym.lifehelper.extern.chatgpt.pojo.CreateCompletionRequestData;
import com.inlym.lifehelper.extern.chatgpt.pojo.CreateCompletionResponse;
import com.inlym.lifehelper.extern.chatgpt.pojo.ProxyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * ChatGPT 的 HTTP 请求服务
 *
 * <h2>主要用途
 * <p>封装 HTTP 请求
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2023/3/25
 * @since 1.9.5
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatGPTHttpService {
    private final ChatGPTProperties properties;

    private final RestTemplate restTemplate;

    private String getKey() {
        String[] keys = properties.getKeys();
        // 每隔100秒换一个 key
        int seq = ((int) Math.floor(System.currentTimeMillis() / 100000.0)) % keys.length;

        return keys[seq];
    }

    /**
     * 会话补全
     *
     * @param prompt 提示语
     *
     * @see <a href="https://platform.openai.com/docs/api-reference/completions/create">Create completion</a>
     * @since 1.9.5
     */
    public CreateCompletionResponse createCompletion(String prompt) {
        String key = getKey();
        Map<String, String> headers = new HashMap<>(16);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + key);

        CreateCompletionRequestData requestData = CreateCompletionRequestData
            .builder()
            .model("text-davinci-003")
            .prompt(prompt)
            .maxTokens(1000)
            .suffix("")
            .build();

        ProxyRequest request = ProxyRequest
            .builder()
            .method("POST")
            .url("https://api.openai.com/v1/completions")
            .headers(headers)
            .data(requestData)
            .build();

        String proxyUrl = properties.getProxyUrl();
        CreateCompletionResponse responseData = restTemplate.postForObject(proxyUrl, request, CreateCompletionResponse.class);

        assert responseData != null;
        if (responseData.getError() != null) {
            throw new ChatGPTCommonException(responseData
                .getError()
                .getMessage());
        }

        return responseData;
    }
}
