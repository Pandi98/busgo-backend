package com.busticketbooking.service;

import com.busticketbooking.dto.request.AuthRequest;
import com.busticketbooking.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest.Register request);
    AuthResponse login(AuthRequest.Login request);
}
