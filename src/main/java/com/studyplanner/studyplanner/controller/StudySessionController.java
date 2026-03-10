package com.studyplanner.studyplanner.controller;

import com.studyplanner.studyplanner.entity.StudySession;
import com.studyplanner.studyplanner.service.StudySessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class StudySessionController {

    private final StudySessionService studySessionService;

    public StudySessionController(StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudySession createSession(@Valid @RequestBody StudySession session, Principal principal) {
        String studentName = principal.getName();
        return studySessionService.createSession(session, studentName);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StudySession> getAllSessions(Principal principal) {
        String studentName = principal.getName();
        return studySessionService.getAllSessions(studentName);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudySession getSessionById(@PathVariable Long id, Principal principal) {
        String studentName = principal.getName();
        return studySessionService.getSessionById(id, studentName);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudySession updateSession(@PathVariable Long id, @Valid @RequestBody StudySession updatedSession, Principal principal) {
        String studentName = principal.getName();
        return studySessionService.updateSession(id, updatedSession, studentName);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable Long id, Principal principal) {
        String studentName = principal.getName();
        studySessionService.deleteSession(id, studentName);
    }
}