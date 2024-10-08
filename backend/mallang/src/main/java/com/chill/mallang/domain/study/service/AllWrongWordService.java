package com.chill.mallang.domain.study.service;

import com.chill.mallang.domain.study.dto.user.WrongWordDto;
import com.chill.mallang.domain.study.model.StudyGameLog;
import com.chill.mallang.domain.study.repository.StudyGameLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AllWrongWordService {

    private static final Logger logger = LoggerFactory.getLogger(StudiedWordService.class);

    private final  StudyGameLogRepository studyGameLogRepository;

    public Map<String, Object> getWrongWord(Long userId){
        logger.info(userId.toString());
        List<StudyGameLog> studyGameLogs = studyGameLogRepository.getWrongStudyGameLogByUserId(userId);

        ArrayList<WrongWordDto> wrongWords = new ArrayList<>();
        for (StudyGameLog studyGameLog : studyGameLogs) {
            WrongWordDto wrongWord = WrongWordDto.builder()
                    .StudyId(studyGameLog.getStudyGame().getId())
                    .word(studyGameLog.getStudyGame().getQuestionText())
                    .build();

            wrongWords.add(wrongWord);
        }


        return new HashMap<>(){{
            put("data",wrongWords);
        }};
    }
}
