package com.attendance.controller;

import com.attendance.model.Meetings;
import com.attendance.service.MeetingService;
import com.attendance.service.QRcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "http://localhost:3000")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // Transformat în POST ca să primească titlul trimis prin JSON din React
    @PostMapping("/create")
    public Meetings createNewMeeting(@RequestBody(required = false) Map<String, String> payload,
                                     @RequestParam(required = false) String title) {
        String meetingTitle = (payload != null && payload.containsKey("title"))
                ? payload.get("title")
                : (title != null ? title : "Întâlnire Nouă");

        return meetingService.createMeetings(meetingTitle);
    }

    @GetMapping
    public List<Meetings> getAllMeetings() {
        return meetingService.getAllMeetings();
    }

    @Autowired
    private QRcodeService qrcodeService;

    @GetMapping(value = "/{token}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getMeetingQRCode(@PathVariable String token) {
        int width = 300;
        int height = 300;
        return qrcodeService.generateQRCodeImage(token, width, height);
    }
}