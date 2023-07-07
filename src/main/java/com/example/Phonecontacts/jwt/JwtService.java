package com.example.Phonecontacts.jwt;

import com.example.Phonecontacts.service.security.UserDetailsCustom;
import io.jsonwebtoken.Claims;


import java.security.Key;

public interface JwtService {

    Claims extractClaims(String token);

    Key getKey();

    String generateToken(UserDetailsCustom userDetailsCustom);

    boolean isValidToken(String token);
}
