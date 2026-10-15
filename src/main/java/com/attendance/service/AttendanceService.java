package com.attendance.service;

import com.attendance.model.Attendance;
import com.attendance.model.Meetings;
import com.attendance.model.User;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.MeetingRepository;
import com.attendance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository, MeetingRepository meetingRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
    }

    public Attendance markAttendance(Long userId, String meetingToken) {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost gasit"));

        Meetings meeting= meetingRepository.findByUniqueToken(meetingToken);
        if(meeting==null){
            throw new RuntimeException("Întâlnirea nu a fost gasita");

        }
        Boolean alreadyMarked=attendanceRepository.existsByUserAndMeeting(user,meeting);
        if(alreadyMarked){
            throw new RuntimeException("Prezenta a fost deja marcata pentru acesta intalnire!");
        }
        Attendance attendance=new Attendance(meeting,user);
        return attendanceRepository.save(attendance);

    }
    public long getMonthlyAttendanceCount(Long userId, int month, int year){
        return attendanceRepository.countAttendanceByMonth(userId, month, year);
    }
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }


}
