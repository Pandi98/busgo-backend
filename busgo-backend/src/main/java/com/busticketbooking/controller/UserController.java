package com.busticketbooking.controller;

import com.busticketbooking.dto.request.ProfileUpdateRequest;
import com.busticketbooking.dto.response.ApiResponse;
import com.busticketbooking.entity.User;
import com.busticketbooking.exception.ResourceNotFoundException;
import com.busticketbooking.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;



import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var profile = Map.<String, Object>of(
                "id",    user.getId(),
                "name",  user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role",  user.getRole().name()
        );

        return ResponseEntity.ok(ApiResponse.success("Profile fetched", profile));
    }
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(request.name());
        user.setPhone(request.phone());

        userRepository.save(user);

        var profile = Map.<String, Object>of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role", user.getRole().name()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully", profile)
        );
    }

}
