package com.chill.mallang.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScoreResponse {
    // ai 모범 답안
    private String answer;
    // ai 채점 점수
    private String score;
    // 1줄 피드백
    private String feedback;
}
