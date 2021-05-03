package com.mapgoblin.service;

import com.mapgoblin.api.dto.mail.MailDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {

    private JavaMailSender javaMailSender;

    @Value("${mail.username}")
    private static String FROM_ADDRESS;

    public void send(MailDto mailDto){

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(mailDto.getEmail());
        simpleMailMessage.setFrom(FROM_ADDRESS);
        simpleMailMessage.setSubject(mailDto.getTitle());
        simpleMailMessage.setText(mailDto.getContent());

        javaMailSender.send(simpleMailMessage);
    }
}
