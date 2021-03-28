package com.mapgoblin.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapgoblin.api.dto.member.CreateMemberRequest;
import com.mapgoblin.api.dto.member.FindMemberRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Rollback(value = false)
class MemberApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @PersistenceContext
    EntityManager em;

    @Test
    public void signup() throws Exception {
        //given
        CreateMemberRequest request = new CreateMemberRequest("testId", "testName", "test@email.com", "test123");

        //when
        ResultActions actions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        em.flush();

        //then
        actions.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void signin() throws Exception {
        //given
        FindMemberRequest request = new FindMemberRequest("testId", "test123");

        //when
        ResultActions actions = mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        actions.andExpect(status().isOk())
                .andDo(print());
    }

}