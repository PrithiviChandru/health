package com.phc.healthcare.controller;

import com.phc.healthcare.model.BaseResponse;
import com.phc.healthcare.model.Login;
import com.phc.healthcare.model.User;
import com.phc.healthcare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("signup")
    public ResponseEntity<BaseResponse> signup(@RequestBody User user) {
        return userService.signup(user);
    }

    @PostMapping("login")
    public ResponseEntity<BaseResponse> login(@RequestBody Login login) {
        return userService.login(login);
    }

    @GetMapping("users")
    public ResponseEntity<BaseResponse> getAllUsers() {
        return userService.getAllUsers();
    }

}
