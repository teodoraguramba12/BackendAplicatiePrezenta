package com.attendance.repository;

import com.attendance.model.Meetings;
import com.attendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository <Meetings, Long>{
    Meetings findByUniqueToken(String uniqueToken);
}
