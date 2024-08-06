package com.project.serviceImpl;

import com.google.common.base.Strings;
import com.project.DTO.UserDTO;
import com.project.JWT.CustomerUsersDetailsService;
import com.project.JWT.JwtFilter;
import com.project.JWT.JwtUtil;
import com.project.POJO.User;
import com.project.constants.Constants;
import com.project.dao.UserDao;
import com.project.service.UserService;
import com.project.utils.EmailUtils;
import com.project.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    User newUser = getUserFromMap(requestMap);
                    userDao.save(newUser);
                    return Utils.getResponseEntity("Successfully registered.", HttpStatus.OK);
                } else {
                    return Utils.getResponseEntity("User with given email already exists.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
                && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                if (customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(), customerUsersDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("{\"message\":\" Wait for admin approval.\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>("{\"message\":\" Bad credentials.\"}", HttpStatus.BAD_REQUEST);

    }

    @Override
    public ResponseEntity<List<UserDTO>> getAll() {
        try {
            if (jwtFilter.isAdmin())
                return new ResponseEntity<>(userDao.getAll(), HttpStatus.OK);
            else return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> user = userDao.findById(Long.parseLong(requestMap.get("id")));
                if (user.isPresent()) {
                    String status = requestMap.get("status");
                    if (status.equalsIgnoreCase("true") || status.equalsIgnoreCase("false")) {
                        userDao.updateStatus(status, Long.parseLong(requestMap.get("id")));
                        sendMailToAllAdmin(status, user.get().getEmail(), userDao.getAllAdminsEmails());
                        return Utils.getResponseEntity("User status updated successfully.", HttpStatus.OK);
                    } else {
                        return Utils.getResponseEntity("Passed bad status type.", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return Utils.getResponseEntity("User id does not exist.", HttpStatus.OK);
                }
            } else
                return Utils.getResponseEntity(Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdminsEmails) {
        allAdminsEmails.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:-" + user + "\nwas approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdminsEmails);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER:-" + user + "\nwas disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdminsEmails);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return Utils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePasword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (user != null) {
                if (passwordEncoder.matches(requestMap.get("oldPassword"), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(requestMap.get("newPassword")));
                    userDao.save(user);
                    return Utils.getResponseEntity("Password updated successfully.", HttpStatus.OK);
                }
                return Utils.getResponseEntity("Incorrect old password.", HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                String temporaryPassword = Utils.generateTemporaryPassword();
                user.setPassword(passwordEncoder.encode(temporaryPassword));
                userDao.save(user);
                emailUtils.forgotMail(user.getEmail(), "Credentials Management System", temporaryPassword);
            }
            return Utils.getResponseEntity("Check yout mail for credentials.", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
