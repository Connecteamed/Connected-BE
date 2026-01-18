package com.connecteamed.server.domain.retrospective.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class GeminiProvider {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public String getAnalysis(String userResult, List<String> taskNames) {
        RestTemplate restTemplate = new RestTemplate();
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
            Map response = restTemplate.postForObject(url, requestBody, Map.class);

            // 4. 응답 데이터에서 결과 텍스트만 추출 (JSON 파싱)
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);

            return firstPart.get("text").toString();

        } catch (Exception e) {
            return "AI 분석 결과를 생성하는 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
