package com.attendance.service;

import com.attendance.model.Meetings;
import com.attendance.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;

    }
    public Meetings createMeetings(String title) {
        String uniqueToken = UUID.randomUUID().toString();
        Meetings meeting = new Meetings( title, uniqueToken);

        return meetingRepository.save(meeting);
    }
    public List<Meetings> getAllMeetings() {
        return meetingRepository.findAll();
    }
    public Meetings getMeetingByToken(String token) {
        return meetingRepository.findByUniqueToken(token);
    }



}
