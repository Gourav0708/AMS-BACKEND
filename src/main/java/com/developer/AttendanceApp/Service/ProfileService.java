package com.developer.AttendanceApp.Service;

import com.developer.AttendanceApp.Entity.Employee;
import com.developer.AttendanceApp.Repository.EmployeeRepo;
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
    public Employee updateProfile(String email, String name, String address, String position, MultipartFile file) {
        try {
            Employee user = employeeRepo.findByEmail(email);
            if (user == null) {
                return null;
            }

            // Update user details
            user.setName(name);
            user.setAddress(address);
            user.setPosition(position);

            // Update profile picture if a new file is provided
            if (file != null && !file.isEmpty()) {
                System.out.println("Uploading file: " + file.getOriginalFilename());

                String fileName = email + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);

                System.out.println("Saving file to: " + filePath.toString());

                Files.write(filePath, file.getBytes());

                String newProfilePicturePath = "/images/" + fileName;
                System.out.println("Updated Profile Picture Path: " + newProfilePicturePath);

                user.setProfilePicture(newProfilePicturePath);
            }


            employeeRepo.save(user);
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
