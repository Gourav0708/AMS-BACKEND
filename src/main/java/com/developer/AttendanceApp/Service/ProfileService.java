package com.developer.AttendanceApp.Service;

import com.developer.AttendanceApp.Entity.Employee;
import com.developer.AttendanceApp.Repository.EmployeeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ProfileService {

    @Value("${upload.directory}")
    private String uploadDir;

    @Autowired
    private EmployeeRepo employeeRepo;

    // Update Name, Address, Position, and Profile Picture in one method
    @Transactional
    public Employee updateProfile(String email, String name, String address, String position, MultipartFile file) {
        try {
            Employee user = employeeRepo.findByEmail(email);
            if (user == null) {
                System.out.println("User not found in DB!");
                return null;
            }

            // Debug logs
            System.out.println("Before update: " + user);

            // Update fields
            user.setName(name);
            user.setAddress(address);
            user.setPosition(position);

            if (file != null && !file.isEmpty()) {
                System.out.println("Uploading file: " + file.getOriginalFilename());
                String fileName = email + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                System.out.println("Saving file to: " + filePath);
                Files.write(filePath, file.getBytes());
                user.setProfilePicture("/images/" + fileName);
            }

            employeeRepo.save(user);
            System.out.println("After update: " + user);

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    // Fetch Profile Picture
    public Employee profilePicture(String email) {
        return employeeRepo.findByEmail(email);
    }
}
