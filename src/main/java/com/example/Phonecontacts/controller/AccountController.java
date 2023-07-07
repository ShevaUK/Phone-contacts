package com.example.Phonecontacts.controller;

import com.example.Phonecontacts.dto.UserDTO;
import com.example.Phonecontacts.service.UserService;
import com.example.Phonecontacts.utils.BaseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<BaseResponseDTO> register(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.registerAccount(userDTO));
    }
}
