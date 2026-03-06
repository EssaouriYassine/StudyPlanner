package com.studyplanner.studyplanner.service;

import com.studyplanner.studyplanner.entity.StudySession;
import com.studyplanner.studyplanner.exception.BusinessException;
import com.studyplanner.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.studyplanner.repository.StudySessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudySessionService {

    private final StudySessionRepository repository;

    public StudySessionService(StudySessionRepository repository) {
        this.repository = repository;
    }

    public StudySession createSession(StudySession session, String username) {
        session.setStudentName(username);

        if (!session.getEndTime().isAfter(session.getStartTime())) {
            throw new BusinessException("La date de fin doit être après la date de début");
        }

        return repository.save(session);
    }

    public List<StudySession> getAllSessions(String username) {
        return repository.findByStudentName(username);
    }

    public StudySession getSessionById(Long id, String username) {
        StudySession session = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable"));

        if (!session.getStudentName().equals(username)) {
            throw new ResourceNotFoundException("Session introuvable");
        }

        return session;
    }

    public StudySession updateSession(Long id, StudySession updatedSession, String username) {
        StudySession existingSession = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable"));

        if (!existingSession.getStudentName().equals(username)) {
            throw new ResourceNotFoundException("Session introuvable");
        }

        if (!updatedSession.getEndTime().isAfter(updatedSession.getStartTime())) {
            throw new BusinessException("La date de fin doit être après la date de début");
        }

        existingSession.setSubject(updatedSession.getSubject());
        existingSession.setDescription(updatedSession.getDescription());
        existingSession.setStartTime(updatedSession.getStartTime());
        existingSession.setEndTime(updatedSession.getEndTime());

        return repository.save(existingSession);
    }

    public void deleteSession(Long id, String username) {
        StudySession session = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable"));

        if (!session.getStudentName().equals(username)) {
            throw new ResourceNotFoundException("Session introuvable");
        }

        repository.delete(session);
    }
}