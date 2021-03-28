package com.mapgoblin.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapgoblin.api.dto.member.CreateMemberRequest;
import com.mapgoblin.api.dto.member.FindMemberRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Rollback(value = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    public void signup() throws Exception {
        //given
        CreateMemberRequest request = new CreateMemberRequest("gildong123", "홍길동", "gildong@gmail.com", "1q2w3e4r");

        //when
        ResultActions actions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        actions.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(2)
    public void signin() throws Exception {
        //given
        FindMemberRequest request = new FindMemberRequest("gildong123", "1q2w3e4r");

        //when
        ResultActions actions = mockMvc.perform(post("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        actions.andExpect(status().isOk())
                .andDo(print());
    }
}