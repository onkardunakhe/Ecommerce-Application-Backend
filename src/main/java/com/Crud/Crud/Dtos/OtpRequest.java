package com.Crud.Crud.Dtos;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {
    @Email
    private String email;
}
