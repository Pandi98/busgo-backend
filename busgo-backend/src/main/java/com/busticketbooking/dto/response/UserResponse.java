package com.busticketbooking.dto.response;



public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String role
)
{

}