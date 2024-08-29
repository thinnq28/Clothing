package com.datn.shop_app.service;

import com.datn.shop_app.DTO.ChangePasswordDTO;
import com.datn.shop_app.DTO.UserDTO;
import com.datn.shop_app.DTO.UserLoginDTO;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.exception.InvalidParamException;
import com.datn.shop_app.response.user.UpdateUserDTO;
import com.datn.shop_app.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UserService {
    UserResponse save(UserDTO userDTO) throws DataNotFoundException;

    List<String> validUser(BindingResult result, UserDTO userDTO);

    String login(UserLoginDTO userLoginDTO) throws DataNotFoundException, InvalidParamException;

    List<String> validLogin(BindingResult result, UserLoginDTO userLoginDTO);

    User getUserDetailsFromToken(String token) throws Exception;

    Page<UserResponse> getAllUsers(String name, String phoneNumber, String email, Boolean active, Pageable pageable);

    User updateUser(Integer userId, UpdateUserDTO updateUserDTO) throws DataNotFoundException;

    List<String> validUpdateUser(BindingResult result, UpdateUserDTO updateUserDTO, Integer userId) throws DataNotFoundException;

    void deleteUser(Integer userId);

    User updateUserPassword(User user, ChangePasswordDTO changePasswordDTO);

    List<String> validateChangePassword(User user, BindingResult result, ChangePasswordDTO changePasswordDTO);
}
