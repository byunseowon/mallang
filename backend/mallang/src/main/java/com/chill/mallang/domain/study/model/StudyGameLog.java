package com.chill.mallang.domain.study.model;

import com.chill.mallang.domain.user.model.User;
import jakarta.persistence.*;

@Entity
public class StudyGameLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private StudyGame studyGame;

    private boolean result;
}
