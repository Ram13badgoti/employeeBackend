package com.spring.boot.Example.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendNewEmployeeNotification(String managerEmail, String employeeName, String phoneNumber, String emailId) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(managerEmail);
        mailMessage.setSubject("New Employee Added");
        mailMessage.setText(
                employeeName + " will now work under you. Mobile number is " + phoneNumber + " and email is " + emailId
        );

        javaMailSender.send(mailMessage);
    }
}
