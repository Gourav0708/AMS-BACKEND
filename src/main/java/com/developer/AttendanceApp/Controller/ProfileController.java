package com.developer.AttendanceApp.Controller;

import com.developer.AttendanceApp.Entity.Employee;
import com.developer.AttendanceApp.Service.EmployeeService;
import com.developer.AttendanceApp.Service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/get-profile")
    public ResponseEntity<?> getProfile() {
        // Extract email from the authenticated JWT user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT extracts email as username

        Employee employee = employeeService.getEmployeeByUserName(email); // Ensure this method exists

        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Employee not found"));
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", employee.getName());
        profileData.put("email", employee.getEmail());
        profileData.put("position", employee.getPosition());
        profileData.put("address", employee.getAddress());
        profileData.put("profile_picture", employee.getProfilePicture());

        return ResponseEntity.ok(profileData);
    }


    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("position") String position,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        System.out.println("Received Update Profile Request: " + name + ", " + address + ", " + position);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // Extract email from JWT
            System.out.println("Extracted Email: " + email);

            Employee updatedEmployee = profileService.updateProfile(email, name, address, position, file);

            if (updatedEmployee != null) {
                return ResponseEntity.ok(Map.of(
                        "message", "Profile updated successfully",
                        "updatedUser", updatedEmployee
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Failed to update profile"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An error occurred while updating the profile"));
        }
    }

}
