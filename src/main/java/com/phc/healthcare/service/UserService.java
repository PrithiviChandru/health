package com.phc.healthcare.service;

import com.phc.healthcare.dao.UserDAO;
import com.phc.healthcare.model.*;
import com.phc.healthcare.utils.TokenManager;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public ResponseEntity<BaseResponse> signup(User user) {

        ResponseEntity<BaseResponse> response;
        BaseResponse<UserWrapper> bR = new BaseResponse<>();

        try {

            String email = user.getEmail();
            String password = user.getPassword();
            String firstName = user.getFirstName();

            if (null == email || email.isEmpty()) throw new IllegalArgumentException("Email Required");
            if (null == password || password.isEmpty()) throw new IllegalArgumentException("Password Required");
            if (null == firstName || firstName.isEmpty()) throw new IllegalArgumentException("First Name Required");

            User prevUser = userDAO.findByEmail(user.getEmail());
            if (null != prevUser) throw new IllegalArgumentException("Email Id Exists");

            String encryptPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(encryptPassword);

            userDAO.save(user);
            UserWrapper uw = new UserWrapper(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhone());

            bR.setValue(uw);
            bR.setApiStatus(true);

        } catch (IllegalArgumentException e) {
            bR.setApiStatus(false);
            bR.setMessage(e.getMessage());
        } catch (Exception e) {
            bR.setApiStatus(false);
            bR.setMessage(e.getMessage());
            bR.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(bR, HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<BaseResponse> login(Login login) {

        ResponseEntity<BaseResponse> response;
        LoginResponse lR = new LoginResponse();

        try {

            String email = login.getEmail();
            String password = login.getPassword();

            if (null == email || email.isEmpty()) throw new IllegalArgumentException("Email Required");
            if (null == password || password.isEmpty()) throw new IllegalArgumentException("Password required");

            User dbUser = userDAO.findByEmail(email);
            if (null == dbUser) throw new IllegalArgumentException("User Not Found");

            if (BCrypt.checkpw(password, dbUser.getPassword())) {
                lR.setApiStatus(true);
                lR.setToken(TokenManager.generateAuthToken(dbUser.getEmail()));
                lR.setUser(new UserWrapper(dbUser.getId(), dbUser.getEmail(), dbUser.getFirstName(), dbUser.getLastName(), dbUser.getPhone()));
            } else {
                throw new IllegalArgumentException("Incorrect Password");
            }

        } catch (IllegalArgumentException e) {
            lR.setApiStatus(false);
            lR.setMessage(e.getMessage());
        } catch (Exception e) {
            lR.setApiStatus(false);
            lR.setMessage(e.getMessage());
            lR.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(lR, HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<BaseResponse> getAllUsers() {

        ResponseEntity<BaseResponse> response;
        BaseResponse<UserWrapper> bR = new BaseResponse<>();

        try {

            List<User> dbUsers = userDAO.findAll();
            List<UserWrapper> users = new ArrayList<>();
            for (User u : dbUsers) {
                UserWrapper uw = new UserWrapper(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getPhone());
                users.add(uw);
            }

            bR.setValues(users);
            bR.setApiStatus(true);

        } catch (Exception e) {
            bR.setApiStatus(false);
            bR.setMessage(e.getMessage());
            bR.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(bR, HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity<String> userIdByToken(String token) {

        ResponseEntity<String> response;
        String userId = "";

        try {
            userId = TokenManager.getUserIdFromToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response = new ResponseEntity<>(userId, HttpStatus.OK);
        }

        return response;
    }

    public ResponseEntity isTokenValid(String token) {
        ResponseEntity<String> response;
        HttpStatus status = HttpStatus.ACCEPTED;

        try {

            if (TokenManager.isTokenValid(token))
                status = HttpStatus.OK;
            else
                status = HttpStatus.UNAUTHORIZED;

        } catch (Exception e) {
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } finally {
            response = new ResponseEntity<>(status);
        }

        return response;
    }

}
