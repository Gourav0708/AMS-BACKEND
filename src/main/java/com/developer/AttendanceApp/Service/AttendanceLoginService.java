package com.developer.AttendanceApp.Service;

import com.developer.AttendanceApp.Entity.Attendance;
import com.developer.AttendanceApp.Entity.Employee;
import com.developer.AttendanceApp.Repository.AttendanceRepo;
import com.developer.AttendanceApp.Repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.*;

@Component
public class AttendanceLoginService {

    @Autowired
    private AttendanceRepo attendanceRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    public String saveLoginDetail(Employee employee, Attendance attendance) {
        if (employee == null) {
            return "Employee cannot be null";
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"); // Example: "2025-02-14 10:30 PM"

        Optional<Attendance> existingRecord = attendanceRepo.findByEmployeeAndRecordDate(employee, today);

        if (existingRecord.isPresent()) {
            Attendance existingAttendance = existingRecord.get();

            if (existingAttendance.getMarkInTime() != null) {
                return "Already logged in today at: " + existingAttendance.getMarkInTime().format(formatter);
            }

            // Update mark-in time
            existingAttendance.setMarkInTime(LocalDateTime.now());
            attendanceRepo.save(existingAttendance);
            return "Mark-in time updated: " + existingAttendance.getMarkInTime().format(formatter);
        }

        // Create new attendance record
        Attendance newAttendance = new Attendance();
        newAttendance.setMarkInTime(LocalDateTime.now());
        newAttendance.setRecordDate(today);
        newAttendance.setEmployee(employee);
        attendanceRepo.save(newAttendance);

        return "Mark-in time recorded: " + newAttendance.getMarkInTime().format(formatter);
    }

    public ResponseEntity<?> getLoginDetail(Employee employee, Attendance logout) {
        if (employee == null) {
            return new ResponseEntity<>(Collections.singletonMap("message", "Employee cannot be null"), HttpStatus.BAD_REQUEST);
        }

        LocalDate today = LocalDate.now();
        Optional<Attendance> attendanceOpt = attendanceRepo.findByEmployeeAndRecordDate(employee, today);

        if (attendanceOpt.isPresent()) {
            Attendance attendance = attendanceOpt.get();

            if (attendance.getMarkOutTime() != null) {
                return new ResponseEntity<>(Collections.singletonMap("message", "Already logged out today"), HttpStatus.OK);
            }

            // Set mark-out time
            attendance.setMarkOutTime(LocalDateTime.now());

            // Calculate working hours
            Duration duration = Duration.between(attendance.getMarkInTime(), attendance.getMarkOutTime());
            long hoursWorked = duration.toHours();
            long minutesWorked = duration.toMinutes() % 60;
            String formattedDuration = String.format("%d hours %d minutes", hoursWorked, minutesWorked);

            attendance.setTotalWorkingHours(formattedDuration);
            attendanceRepo.save(attendance);

            // Format logoutTime (this is the fix)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            String formattedLogoutTime = attendance.getMarkOutTime().format(formatter);

            // Returning JSON response
            Map<String, String> response = new HashMap<>();
            response.put("logoutTime", formattedLogoutTime);  // FIXED!
            response.put("totalWorkingHours", formattedDuration);

            return ResponseEntity.ok(response);
        }

        return new ResponseEntity<>(Collections.singletonMap("message", "No mark-in record found for today."), HttpStatus.NOT_FOUND);
    }

}
