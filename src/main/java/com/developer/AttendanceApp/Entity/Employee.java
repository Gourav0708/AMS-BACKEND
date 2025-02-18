package com.developer.AttendanceApp.Entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import java.util.List;

@Entity
@Component
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment handled by DB
    @Column(name = "employee_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    @NonNull
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "position")
    private String position;

//    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "roles")
    private List<String> roles;


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // No need to set this manually

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
