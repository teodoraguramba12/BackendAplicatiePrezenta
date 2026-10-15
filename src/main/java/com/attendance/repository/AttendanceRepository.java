package com.attendance.repository;

import com.attendance.model.Attendance;
import com.attendance.model.Meetings;
import com.attendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; // Asigură-te că ai și importul pentru List

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND EXTRACT(MONTH FROM a.scanTime) = :month AND EXTRACT(YEAR FROM a.scanTime) = :year")
    long countAttendanceByMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    boolean existsByUserAndMeeting(User user, Meetings meetings);

    long countByMeetingId(Long meetingId);

    List<Attendance> findByMeetingId(Long meetingId);

}