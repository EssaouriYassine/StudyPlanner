package com.studyplanner.studyplanner.repository;

import com.studyplanner.studyplanner.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByStudentName(String studentName);

    long countByStudentName(String studentName);

}


// ajouter annotation @Repository