package com.chill.mallang.domain.quiz.error;

import com.chill.mallang.errors.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuizErrorCode implements ErrorCode {
    INVALID_QUIZ_PK(HttpStatus.BAD_REQUEST, "Cannot find quiz"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Cannot find user"),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "Cannot find quiz"),
    AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "Cannot find area"),
    USER_ID_NULL(HttpStatus.BAD_REQUEST, "User ID must not be null"),
    AREA_ID_NULL(HttpStatus.BAD_REQUEST, "Area ID must not be null"),
    FACTION_ID_NULL(HttpStatus.BAD_REQUEST, "Faction ID must not be null"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
