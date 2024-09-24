package com.freelancers.Stream24.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.freelancers.Stream24.Entity.User;
import com.freelancers.Stream24.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public User registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public User loginUser(String email, String password) {
		User user = userRepository.findByEmail(email);
		if (user != null && passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}
		return null;
	}

	public void generateAndSendOtp(User user) {
		String otp = generateOtp();
		user.setOtp(otp);
		user.setOtpExpiry(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 minutes expiry
		userRepository.save(user);
		emailService.sendOtp(user.getEmail(), otp);
	}

	public boolean verifyOtp(String email, String otp) {
		User user = userRepository.findByEmail(email);
		if (user != null && user.getOtp().equals(otp) && user.getOtpExpiry().after(new Date())) {
			user.setOtpVerified(true);
			userRepository.save(user);
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

	private String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}



}
