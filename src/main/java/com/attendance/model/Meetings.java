package com.attendance.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Meetings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String uniqueToken;

    private LocalDate meetingDate=LocalDate.now();

    public Meetings() {
    }
    public Meetings(String title, String uniqueToken) {
        this.title = title;
        this.uniqueToken = uniqueToken;
        this.meetingDate = LocalDate.now();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUniqueToken() {
        return uniqueToken;
    }
    public void setUniqueToken(String uniqueToken) {
        this.uniqueToken = uniqueToken;
    }
    public LocalDate getMeetingDate() {
        return meetingDate;
    }
    public void setMeetingDate(LocalDate meetingDate) {
        this.meetingDate = meetingDate;
    }
}
