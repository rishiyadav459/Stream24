package com.freelancers.Stream24.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.freelancers.Stream24.Entity.Admin;
import com.freelancers.Stream24.Entity.User;
import com.freelancers.Stream24.Repository.AdminRepository;
import com.freelancers.Stream24.Repository.UserRepository;

@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public Admin registerAdmin(Admin admin) {
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		return adminRepository.save(admin);
	}

	public Admin loginAdmin(String email, String password) {
		Admin admin = adminRepository.findByEmail(email);
		if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
			return admin;
		}
		return null;
	}

	public void generateAndSendOtp(Admin admin) {
		String otp = generateOtp();
		admin.setOtp(otp);
		admin.setOtpExpiry(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 minutes expiry
		adminRepository.save(admin);
		emailService.sendOtp(admin.getEmail(), otp);
	}

	public boolean verifyOtp(String email, String otp) {
		Admin admin = adminRepository.findByEmail(email);
		if (admin != null && admin.getOtp().equals(otp) && admin.getOtpExpiry().after(new Date())) {
			admin.setOtpVerified(true);
			adminRepository.save(admin);
			return true;
		}
		return false;
	}

	public boolean updateUserDetails(User updatedUser) {
		Optional<User> existingUserOpt = userRepository.findById(updatedUser.getId());

		if (existingUserOpt.isPresent()) {
			User existingUser = existingUserOpt.get();

			// Update fields
			existingUser.setUsername(updatedUser.getUsername());
			existingUser.setEmail(updatedUser.getEmail());
			existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
			// Add other fields as needed

			// Check if the password needs to be updated
			if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
				// Compare the current password with the new one to check if it's different
				if (!passwordEncoder.matches(updatedUser.getPassword(), existingUser.getPassword())) {
					// Encode the new password before saving
					existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
				}
			}

			// Save the updated user
			userRepository.save(existingUser);
			return true;
		} else {
			return false; // User not found
		}
	}

	public boolean deleteUser(String userId) {
		Optional<User> existingUserOpt = userRepository.findById(userId);

		if (existingUserOpt.isPresent()) {
			userRepository.deleteById(userId);
			return true;
		} else {
			return false; // User not found
		}
	}

	private String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

}
