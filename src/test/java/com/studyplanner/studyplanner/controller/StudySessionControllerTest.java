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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
    void getAllSessions() throws Exception {
        List<StudySession> sessions = Arrays.asList(session1, session2);
        when(studySessionService.getAllSessions("student")).thenReturn(sessions);

        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].subject").value("Math"))
                .andExpect(jsonPath("$[1].subject").value("Physics"));
    }

    @Test
    @WithMockUser(username = "student")
    void getSessionById() throws Exception {
        when(studySessionService.getSessionById(1L, "student")).thenReturn(session1);

        mockMvc.perform(get("/api/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.subject").value("Math"));
    }

    @Test
    @WithMockUser(username = "student")
    void updateSession() throws Exception {
        when(studySessionService.updateSession(eq(1L), any(StudySession.class), eq("student"))).thenReturn(session1);

        mockMvc.perform(put("/api/sessions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(session1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.subject").value("Math"));
    }

    @Test
    @WithMockUser(username = "student")
    void deleteSession() throws Exception {
        doNothing().when(studySessionService).deleteSession(anyLong(), anyString());

        mockMvc.perform(delete("/api/sessions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(studySessionService, times(1)).deleteSession(1L, "student");
    }
}
