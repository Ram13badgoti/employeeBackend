package com.spring.boot.Example.Models;


import jakarta.validation.constraints.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employees")
public class Employee {

    @Id
    @NotNull(message = "Employee ID cannot be empty")
    private String id;
    @NotNull(message = "Employee ID cannot be empty")
    private String employeeName;
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;
    @Email(message = "Invalid email address")
    private String email;
    private String reportsTo;
    private String profileImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(String reportsTo) {
        this.reportsTo = reportsTo;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }



}
