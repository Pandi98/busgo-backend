package com.busticketbooking.service.impl;

import com.busticketbooking.dto.request.UserUpdateRequest;
import com.busticketbooking.dto.response.UserResponse;
import com.busticketbooking.entity.Role;
import com.busticketbooking.entity.User;
import com.busticketbooking.exception.ResourceNotFoundException;
import com.busticketbooking.repository.BookingRepository;
import com.busticketbooking.repository.UserRepository;
import com.busticketbooking.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "id", id));

        return toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id,
                                   UserUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "id", id));

        user.setName(request.name());
        user.setPhone(request.phone());

        userRepository.save(user);

        return toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "id", id));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Admin cannot be deleted."
            );
        }

        if (!bookingRepository.findByUser(user).isEmpty()) {
            throw new IllegalArgumentException(
                    "User has bookings. Cannot delete."
            );
        }

        userRepository.delete(user);
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name()
        );
    }

}
