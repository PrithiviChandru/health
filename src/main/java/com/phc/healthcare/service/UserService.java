package com.phc.healthcare.service;

import com.phc.healthcare.dao.UserDAO;
import com.phc.healthcare.model.BaseResponse;
import com.phc.healthcare.model.Login;
import com.phc.healthcare.model.User;
import com.phc.healthcare.model.UserWrapper;
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
            bR.setTrace(e.toString());
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
        BaseResponse bR = new BaseResponse<>();

        try {

            String email = login.getEmail();
            String password = login.getPassword();

            if (null == email || email.isEmpty()) throw new IllegalArgumentException("Email Required");
            if (null == password || password.isEmpty()) throw new IllegalArgumentException("Password required");

            User dbUser = userDAO.findByEmail(email);
            if (null == dbUser) throw new IllegalArgumentException("User Not Found");

            if (BCrypt.checkpw(password, dbUser.getPassword()))
                bR.setApiStatus(true);
            else
                bR.setMessage("INVALID_CREDENTIALS");

        } catch (IllegalArgumentException e) {
            bR.setApiStatus(false);
            bR.setMessage(e.getMessage());
            bR.setTrace(e.toString());
        } catch (Exception e) {
            bR.setApiStatus(false);
            bR.setMessage(e.getMessage());
            bR.setTrace(e.toString());
        } finally {
            response = new ResponseEntity<>(bR, HttpStatus.OK);
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

}
