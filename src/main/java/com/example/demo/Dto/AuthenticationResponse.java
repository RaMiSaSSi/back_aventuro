package com.example.demo.Dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthenticationResponse {
    private String jwt;



    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }


}
