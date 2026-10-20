package com.busticketbooking.service;




import com.busticketbooking.dto.request.UserUpdateRequest;
import com.busticketbooking.dto.response.UserResponse;

import java.util.List;

public interface UserManagementService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

}
