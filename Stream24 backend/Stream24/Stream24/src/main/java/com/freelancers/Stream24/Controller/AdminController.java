package com.freelancers.Stream24.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.freelancers.Stream24.Entity.Admin;
import com.freelancers.Stream24.Entity.LoginRequest;
import com.freelancers.Stream24.Entity.OtpRequest;
import com.freelancers.Stream24.Entity.User;
import com.freelancers.Stream24.Service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

	@Autowired
	private AdminService adminService;

	

	@PostMapping("/register")
	public String registerAdmin(@RequestBody Admin admin) {
		adminService.registerAdmin(admin);
		adminService.generateAndSendOtp(admin);
		return "Admin registration successful, OTP sent to your email.";
	}

	@PostMapping("/login")
	public String loginAdmin(@RequestBody LoginRequest loginRequest) {
		Admin admin = adminService.loginAdmin(loginRequest.getEmail(), loginRequest.getPassword());
		if (admin != null && admin.isOtpVerified()) {
			return "Admin login successful";
		} else if (admin != null) {
			return "OTP not verified. Please verify your OTP.";
		} else {
			return "Invalid credentials";
		}
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestBody OtpRequest otpRequest) {
		if (adminService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp())) {
			return "OTP verified successfully";
		} else {
			return "Invalid OTP or OTP expired";
		}
	}

	// Update user details
	@PutMapping("/update-user")
	public String updateUserDetails(@RequestBody User updatedUser) {
		boolean isUpdated = adminService.updateUserDetails(updatedUser);
		if (isUpdated) {
			return "User details updated successfully.";
		} else {
			return "User not found.";
		}
	}

	// Delete a user
	@DeleteMapping("/delete-user/{userId}")
	public String deleteUser(@PathVariable String userId) {
		boolean isDeleted = adminService.deleteUser(userId);
		if (isDeleted) {
			return "User deleted successfully.";
		} else {
			return "User not found.";
		}
	}

	@GetMapping("/users")
	public List<User> getAllUsers() {
		return adminService.getAllUsers();
	}

//  @GetMapping("/users")
//  public List<User> getAllUsers() throws Exception {
//      List<User> users = adminService.getAllUsers();
//      for (User user : users) {
//          // Assuming User has a getPassword() method
//          String decryptedPassword = passwordDecryptor.decrypt(user.getPassword());
//          user.setPassword(decryptedPassword);
//      }
//      return users;
//  }

//  @PostMapping("/verify-otp")
//  public String verifyOtp(@RequestParam String email, @RequestParam String otp) {
//      boolean isVerified = adminService.verifyOtp(email, otp);
//      if (isVerified) {
//          return "OTP verified successfully.";
//      } else {
//          return "Invalid OTP or OTP expired.";
//      }
//  }

}
