package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.ChangePasswordDTO;
import com.datn.shop_app.DTO.UserDTO;
import com.datn.shop_app.DTO.UserLoginDTO;
import com.datn.shop_app.entity.Token;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.response.role.RoleResponse;
import com.datn.shop_app.response.user.ListUserResponse;
import com.datn.shop_app.response.user.LoginResponse;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.user.UpdateUserDTO;
import com.datn.shop_app.response.user.UserResponse;
import com.datn.shop_app.service.TokenService;
import com.datn.shop_app.service.UserService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/users/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TokenService tokenService;

    private final LocalizationUtils localizationUtils;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@Valid @RequestBody UserDTO userDTO,
                                                   BindingResult result) throws DataNotFoundException {

        List<String> errors = userService.validUser(result, userDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Register User is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        UserResponse userResponse = userService.save(userDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Register User successful")
                .status(HttpStatus.OK)
                .data(userResponse)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@Valid @RequestBody UserLoginDTO userLoginDTO,
                                                BindingResult result,
                                                HttpServletRequest request) throws Exception {
        //valid data login
        List<String> errors = userService.validLogin(result, userLoginDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors).build());
        }

        // Kiểm tra thông tin đăng nhập và sinh token
        String token = userService.login(userLoginDTO);
        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) //method reference
                .id(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(loginResponse).build());
    }

    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
        User user = userService.getUserDetailsFromToken(extractedToken);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Get user's detail successfully")
                        .data(UserResponse.fromUser(user))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getUsers(@RequestParam(defaultValue = "") String name,
                                                   @RequestParam(defaultValue = "", name = "phone_number") String phoneNumber,
                                                   @RequestParam(defaultValue = "", name = "email") String email,
                                                   @RequestParam(defaultValue = "", name = "role_id") Integer roleId,
                                                   @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                   @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<UserResponse> userPage = userService.getAllUsers(name, phoneNumber, email, active, pageRequest);
        totalPages = userPage.getTotalPages();
        List<UserResponse> content = userPage.getContent();
        List<UserResponse> userResponses = new ArrayList<>();

        if(roleId != null && roleId > 0) {
            for (UserResponse userResponse : content) {
                 List<RoleResponse> roleResponses = userResponse.getRoles().stream()
                         .filter(e -> e.getId().equals(roleId)).toList();
                if(!roleResponses.isEmpty()) {
                    userResponses.add(userResponse);
                }
            }
        } else{
            userResponses.addAll(content);
        }

        for (UserResponse user : userResponses) {
            user.setTotalPages(totalPages);
        }

        ListUserResponse listUserResponse = ListUserResponse.builder()
                .users(userResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get user successfully")
                .status(HttpStatus.OK)
                .data(listUserResponse)
                .build());
    }

    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> updateUser(@PathVariable("id") Integer userId,
                                                     @Valid @RequestBody UpdateUserDTO updateUserDTO,
                                                     BindingResult result,
                                                     @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractedToken);
        // Ensure that the user making the request matches the user being updated
        if (user.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> errors = userService.validUpdateUser(result, updateUserDTO, userId);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Update is not successful")
                            .data(errors)
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        User updatedUser = userService.updateUser(userId, updateUserDTO);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update user detail successfully")
                        .data(UserResponse.fromUser(updatedUser))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteUser(@PathVariable("id") Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Delete user successful")
                .status(HttpStatus.OK)
                .data("").build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO,
            BindingResult result,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);

            List<String> errors;
            errors = userService.validateChangePassword(user, result, changePasswordDTO);

            if(!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Change password is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            User newUser = userService.updateUserPassword(user, changePasswordDTO);

            UserResponse userResponse = UserResponse.fromUser(newUser);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Change password is successful")
                    .status(HttpStatus.OK)
                    .data(userResponse).build());
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Change password is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(ex.getMessage()).build());
        }
    }
}
