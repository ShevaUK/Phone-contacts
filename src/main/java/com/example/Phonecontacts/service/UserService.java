package com.example.Phonecontacts.service;

import com.example.Phonecontacts.dto.UserDTO;
import com.example.Phonecontacts.utils.BaseResponseDTO;

public interface UserService {
    BaseResponseDTO registerAccount(UserDTO userDTO);
}
