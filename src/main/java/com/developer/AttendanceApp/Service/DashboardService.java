package com.developer.AttendanceApp.Service;

import com.developer.AttendanceApp.Entity.Attendance;
import com.developer.AttendanceApp.Entity.Employee;
import com.developer.AttendanceApp.Repository.AttendanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    AttendanceRepo attendanceRepo;

    public Object getDashboard(Employee employee) {
        LocalDate today = LocalDate.now();
        Optional<Attendance> EmployeeAndRecordDate = attendanceRepo.findByEmployeeAndRecordDate(employee, today);
        Map<String, Object> response = new HashMap<>();

        // Adding employee details to the response
        Map<String, Object> employeeDetails = new HashMap<>();
        employeeDetails.put("id", employee.getId());
        employeeDetails.put("name", employee.getName());
        employeeDetails.put("email", employee.getEmail());
        employeeDetails.put("roles", employee.getRoles());
        employeeDetails.put("profile_picture", employee.getProfilePicture());

        response.put("employee", employeeDetails);
        response.put("loginTime", "--");
        response.put("logoutTime", "--");
        response.put("totalWorkingHours", "--");

        // If attendance record exists, populate the response with actual values
        if (EmployeeAndRecordDate.isPresent()) {
            Attendance attendance = EmployeeAndRecordDate.get();

            if (attendance.getMarkInTime() != null) {
                response.put("loginTime", attendance.getMarkInTime().toString());
            }

            if (attendance.getMarkOutTime() != null) {
                response.put("logoutTime", attendance.getMarkOutTime().toString());

                // If total working hours are available, format them as HH:mm
                if (attendance.getTotalWorkingHours() != null) {
                    response.put("totalWorkingHours", attendance.getTotalWorkingHours().toString());
                }
            }
        }

        return response;
    }
}
