package com.connecteamed.server.domain.retrospective.service;

import com.connecteamed.server.domain.retrospective.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiProvider {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestTemplate restTemplate;

    public String getAnalysis(String userResult, List<String> taskNames) {
        String url = API_URL + "?key=" + apiKey;

            String prompt = String.format(
                    "당신은 프로젝트 관리 전문가입니다. 사용자의 성과 요약과 완료된 업무 목록을 분석하여" +
                            "업무와 성과에 대한 요약과 향후 발전 방향을 제시하는 전문적인 회고문을 작성하세요.\n\n" +
                            "성과 요약: %s\n업무 목록: %s",
                    userResult, String.join(", ", taskNames)
            );
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            // 3. API 호출
            GeminiResponse response = restTemplate.postForObject(url, requestBody, GeminiResponse.class);

            if (response != null && !response.candidates().isEmpty()) {
                return response.candidates().get(0).content().parts().get(0).text();
            }
            return "분석 결과를 가져오지 못했습니다.";

        } catch (Exception e) {
            return "AI 분석 결과를 생성하는 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
