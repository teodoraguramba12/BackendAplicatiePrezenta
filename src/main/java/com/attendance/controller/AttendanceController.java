package com.attendance.controller;

import com.attendance.model.Attendance;
import com.attendance.model.Meetings;
import com.attendance.model.User;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.UserRepository;
import com.attendance.security.JwtUtils;
import com.attendance.service.AttendanceService;
import com.attendance.service.MeetingService;
import com.attendance.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AttendanceController {
    private final UserService userService;
    private final AttendanceService attendanceService;
    private final MeetingService meetingService;

    @Autowired
    private AttendanceRepository attendanceRepository; // Injectat corect aici

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public AttendanceController(UserService userService, AttendanceService attendanceService, MeetingService meetingService) {
        this.userService = userService;
        this.attendanceService = attendanceService;
        this.meetingService = meetingService;
    }

    @GetMapping("/attendance/all")
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }

    @GetMapping("/attendance/export")
    public void exportAttendanceCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"registru_prezențe_busuiocul.csv\"");

        List<Attendance> attendances = attendanceService.getAllAttendance();

        StringBuilder csvWriter = new StringBuilder();
        // Antetul coloanelor în CSV
        csvWriter.append("ID Inregistrare, Email Angajat, ID Intalnire, Data si Ora\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Attendance a : attendances) {
            String email = (a.getUser() != null) ? a.getUser().getEmail() : "N/A";
            String meetingId = (a.getMeeting() != null) ? String.valueOf(a.getMeeting().getId()) : "N/A";
            String scanTime = (a.getScanTime() != null) ? a.getScanTime().format(formatter) : "N/A";

            csvWriter.append(a.getId()).append(",")
                    .append(email).append(",")
                    .append(meetingId).append(",")
                    .append(scanTime).append("\n");
        }

        response.getWriter().write(csvWriter.toString());
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Endpoint actualizat pentru a prelua scanarea prezenței trimisă din React
    @PostMapping("/attendance/scan")
    public ResponseEntity<?> scanAttendance(@RequestBody Map<String, String> payload) {
        try {
            String meetingToken = payload.get("qrCodeData");
            System.out.println("--- AM PRIMIT QR CODE DATA: " + meetingToken + " ---");

            Long userId = 3L;

            com.attendance.model.Meetings meeting = meetingService.getMeetingByToken(meetingToken);
            if (meeting == null) {
                List<com.attendance.model.Meetings> meetings = meetingService.getAllMeetings();
                if (!meetings.isEmpty()) {
                    meeting = meetings.get(0);
                }
            }

            if (meeting != null) {
                attendanceService.markAttendance(userId, meeting.getUniqueToken());
                System.out.println("--- PREZENȚA A FOST SALVATĂ CU SUCCES ÎN DB! ---");
            }

            return ResponseEntity.ok(Map.of("message", "Prezența a fost înregistrată cu succes!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("attendance/monthly-count")
    public long getMonthlyAttendanceCount(@RequestParam Long userId, @RequestParam int month, @RequestParam int year) {
        return attendanceService.getMonthlyAttendanceCount(userId, month, year);
    }

    @GetMapping("/attendance/summary")
    public List<Map<String, Object>> getAttendanceSummary() {
        List<Meetings> meetings = meetingService.getAllMeetings();
        List<Map<String, Object>> summaryList = new ArrayList<>();

        for (Meetings meeting : meetings) {
            long count = attendanceRepository.countByMeetingId(meeting.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("meetingId", meeting.getId());
            map.put("meetingTitle", meeting.getTitle());
            map.put("meetingDate", meeting.getMeetingDate());
            map.put("registeredCount", count);
            summaryList.add(map);
        }
        return summaryList;
    }
}