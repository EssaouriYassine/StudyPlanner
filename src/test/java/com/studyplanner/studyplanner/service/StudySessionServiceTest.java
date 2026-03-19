package com.studyplanner.studyplanner.service;

import com.studyplanner.studyplanner.entity.StudySession;
import com.studyplanner.studyplanner.exception.BusinessException;
import com.studyplanner.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.studyplanner.repository.StudySessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudySessionServiceTest {

    @Mock
    private StudySessionRepository repository;

    @InjectMocks
    private StudySessionService service;

    private StudySession session;

    @BeforeEach
    void setUp() {
        session = new StudySession();
        session.setSubject("Java");
        session.setStartTime(LocalDateTime.now().plusDays(1));
        session.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
    }

    @Test
    void shouldCreateSessionSuccessfully() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudySession result = service.createSession(session, "alice");

        assertThat(result.getStudentName()).isEqualTo("alice");
        assertThat(result.getSubject()).isEqualTo("Java");
        verify(repository, times(1)).save(session);
    }

    @Test
    void shouldRejectSessionWhenEndTimeBeforeStartTime() {
        session.setEndTime(LocalDateTime.now().plusDays(1).minusHours(1)); // endTime avant startTime

        assertThatThrownBy(() -> service.createSession(session, "alice"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("date de fin");
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSessionById(99L, "alice"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenSessionBelongsToAnotherUser() {
        StudySession bobSession = new StudySession();
        bobSession.setStudentName("bob");
        when(repository.findById(1L)).thenReturn(Optional.of(bobSession));

        assertThatThrownBy(() -> service.getSessionById(1L, "alice"))
                .isInstanceOf(ResourceNotFoundException.class);
    }


}
