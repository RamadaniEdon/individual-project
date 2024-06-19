package com.database.federation.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.database.federation.configurations.Protected;
import com.database.federation.utils.JwtObject;
import com.database.federation.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {

  private final UserService userService;
  private final PasswordService passwordService;
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  public UserController(UserService userService, PasswordService passwordService) {
    this.userService = userService;
    this.passwordService = passwordService;
  }

    @PostMapping("/generateToken")
    public String generateToken() throws JsonProcessingException {
        JwtObject exampleObject = new JwtObject("12345678");
        long expirationTimeMillis = 36000000; // 1 hour
        return jwtUtils.generateToken(exampleObject, expirationTimeMillis);
    }
    
    @GetMapping("/parseToken")
    public JwtObject parseToken(@RequestParam String token) throws JsonProcessingException {
        return jwtUtils.parseToken(token, JwtObject.class);
    }


  
  @PostMapping("/users")
  public ResponseEntity<String> addDocument(@RequestBody UserModel user) {
    try {
      user.setPassword(passwordService.hashPassword(user.getPassword()));
      UserModel addedUser = userService.addUser(user);
      return new ResponseEntity<>("Document added successfully with id: " + addedUser.getId(), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to add document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Protected
  @GetMapping("/users/{id}")
  public ResponseEntity<UserModel> getUserById(@PathVariable String id)  {
    try {
      UserModel user = userService.findUserById(id);
      if (user != null) {
        return new ResponseEntity<>(user, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserModel>> getAllUsers() {
    try {
      List<UserModel> users = userService.getAllUsers();
      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<String> deleteUserById(@PathVariable String id) {
    try {
      userService.deleteUserById(id);
      return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to delete user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/users/{id}/data")
  public ResponseEntity<String> getMethodName(@PathVariable String id) {
    
    return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
  }

}
