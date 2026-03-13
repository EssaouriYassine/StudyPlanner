package com.studyplanner.studyplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyplanner.studyplanner.entity.StudySession;
import com.studyplanner.studyplanner.service.StudySessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudySessionController.class)
public class StudySessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudySessionService studySessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudySession session1;
    private StudySession session2;

    @BeforeEach
    void setUp() {
        session1 = new StudySession();
        session1.setId(1L);
        session1.setStudentName("student");
        session1.setSubject("Math");
        session1.setStartTime(LocalDateTime.now().plusHours(1));
        session1.setEndTime(LocalDateTime.now().plusHours(2));

        session2 = new StudySession();
        session2.setId(2L);
        session2.setStudentName("student");
        session2.setSubject("Physics");
        session2.setStartTime(LocalDateTime.now().plusDays(1));
        session2.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
    }

    @Test
    @WithMockUser(username = "student")
    void createSession() throws Exception {
        when(studySessionService.createSession(any(StudySession.class), eq("student"))).thenReturn(session1);

        mockMvc.perform(post("/api/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.subject").value("Math"));
    }

    @Test
    @WithMockUser(username = "student")
    void shouldReturn400WhenSubjectIsBlank() throws Exception {
        StudySession invalid = new StudySession();
        invalid.setSubject("");
        invalid.setStartTime(LocalDateTime.now().plusDays(1));
        invalid.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        mockMvc.perform(post("/api/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isUnauthorized());
    }
}
