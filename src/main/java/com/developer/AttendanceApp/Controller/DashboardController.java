package com.developer.AttendanceApp.Controller;

import com.developer.AttendanceApp.Entity.Employee;
import com.developer.AttendanceApp.Repository.EmployeeRepo;
import com.developer.AttendanceApp.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    DashboardService dashboardService;

    @GetMapping("/user-details")
    public ResponseEntity<?> getDashboardDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Employee employee = employeeRepo.findByEmail(email);
        if (employee == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Fetch dashboard data
        Map<String, Object> dashboardData = (Map<String, Object>) dashboardService.getDashboard(employee);

        // Construct a simplified response
        Map<String, Object> response = new HashMap<>();
        response.put("name", employee.getName());
        response.put("email", employee.getEmail());
        response.put("address", employee.getAddress());
        response.put("profilePicture", employee.getProfilePicture());
        response.put("position", employee.getPosition());
        response.put("loginTime", dashboardData.get("loginTime"));
        response.put("logoutTime", dashboardData.get("logoutTime"));
        response.put("totalWorkingHours", dashboardData.get("totalWorkingHours"));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}