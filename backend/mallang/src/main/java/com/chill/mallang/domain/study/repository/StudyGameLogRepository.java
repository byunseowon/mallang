package com.chill.mallang.domain.study.repository;

import com.chill.mallang.domain.study.model.StudyGameLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGameLogRepository extends JpaRepository<StudyGameLog, Long>{

    // user_id = userId + result가 1인 가장 최신의 데이터만 뽑기
    // 맞힌 문제 단어 뽑기
    @Query(value =  """
                WITH ranked_logs AS (
                    SELECT *,
                           ROW_NUMBER() OVER (PARTITION BY study_game_id ORDER BY id DESC) AS rn
                    FROM study_game_log
                    WHERE user_id = :userId
                )
                SELECT * 
                FROM ranked_logs 
                WHERE rn = 1 
                AND result = 1 
                ORDER BY id DESC
            """, nativeQuery = true)
    List<StudyGameLog> getStudyGameLogByUserId(@Param("userId") Long userId);

    // user_id = userId + result가 0인 '가장 최신의 데이터'만 뽑기
    // 오답노트 만들기
    @Query(value =  """
                WITH ranked_logs AS (
                    SELECT *,
                           ROW_NUMBER() OVER (PARTITION BY study_game_id ORDER BY id DESC) AS rn
                    FROM study_game_log
                    WHERE user_id = :userId
                )
                SELECT * 
                FROM ranked_logs 
                WHERE rn = 1 
                AND result = 0 
                ORDER BY id DESC
            """, nativeQuery = true)
    List<StudyGameLog> getWrongStudyGameLogByUserId(@Param("userId") Long userId);

    //(user_id = userId) + (study_game_id = studyId) + result = 0인 가장 최신의 데이터
    @Query(value = "SELECT * FROM study_game_log WHERE user_id = :userId AND study_game_id = :studyId AND result = 0 ORDER BY id DESC LIMIT 1 FOR UPDATE" ,nativeQuery = true)
    Optional<StudyGameLog> getOneWrongStudyGameLogByUserIdStudyId(@Param("userId") Long userId, @Param("studyId") Long studyId);

    //이미 푼 문제인지 아닌지 확인 용
    @Query(value = "SELECT * FROM study_game_log WHERE user_id = :userId AND study_game_id = :studyId ORDER BY id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    Optional<StudyGameLog> findByStudyGameAndUserForLastResult(@Param("userId") Long userId, @Param("studyId") Long studyId);
}
