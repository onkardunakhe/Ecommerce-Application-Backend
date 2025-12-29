package com.Crud.Crud.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerifyRequest {
    private String email;
    private String otp;
}
