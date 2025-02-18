package com.developer.AttendanceApp.Controller;
import com.developer.AttendanceApp.Entity.Attendance;
import com.developer.AttendanceApp.Entity.Employee;
import com.developer.AttendanceApp.Repository.AttendanceRepo;
import com.developer.AttendanceApp.Repository.EmployeeRepo;
import com.developer.AttendanceApp.Service.AttendanceLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/Attendance")
public class AttendanceController {
    @Autowired
    private AttendanceLoginService attendanceLoginService;
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private AttendanceRepo attendanceRepo;

    // login  api

    @PostMapping("/markIn")
    public ResponseEntity<?> loginTiming(@RequestBody Attendance attendance) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Employee employee = employeeRepo.findByEmail(email);
        attendance.setEmployee(employee);

        try {
            String responseMessage = attendanceLoginService.saveLoginDetail(employee, attendance);

            Map<String, String> response = new HashMap<>();

            // If the response says "Already logged in today", return that
            if (responseMessage.startsWith("Already logged in today")) {
                response.put("message", "Already logged in today");
                return ResponseEntity.ok(response);
            }

            // Otherwise, extract the formatted time
            String formattedTime = responseMessage.replaceAll(".*: ", "").trim();

            response.put("message", "Logged in successfully");
            response.put("loginTime", formattedTime);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("message", "Got Some Error: " + e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }



    @PostMapping("/markOut")
    public ResponseEntity<?> markOut(@RequestBody Attendance attendance) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Employee employee = employeeRepo.findByEmail(email);

        if (authentication.isAuthenticated()) {
            return attendanceLoginService.getLoginDetail(employee, attendance);
        }

        return new ResponseEntity<>(Collections.singletonMap("message", "No user found"), HttpStatus.NOT_FOUND);
    }


}
