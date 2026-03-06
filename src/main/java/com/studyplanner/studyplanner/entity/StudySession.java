package com.studyplanner.studyplanner.entity;

import jakarta.persistence.*;

@Entity
@Table(name="study_session")
public class StudySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
