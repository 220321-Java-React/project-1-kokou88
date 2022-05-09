package com.revature.services;

import com.revature.models.UserDTO;
import com.revature.daos.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class LoginService {

    private UserDAO userDAO;
    private Logger logger = LoggerFactory.getLogger("Login Service Logger");

    public LoginService() {
        userDAO = new UserDAO();
    }

    public LoginService(UserDAO mockedDAO) {
        this.userDAO = mockedDAO;
    }

    public UserDTO login(String username, String password){
        if (username != null && password != null) {
            UserDTO userFromDb = userDAO.login(username);
            if (userFromDb == null) {
                logger.debug("userFromDb is null; login failed");
            } else {
                String encryptedPass = LoginService.encodePassword(password);
                if (encryptedPass.equals(userFromDb.password)) {
                    logger.info("Input password and returned password match");
                    return userFromDb;
                } else {
                    logger.debug("The password provided did not match the password in the database");
                    logger.debug("Passed in password: " + encryptedPass);
                    logger.debug("Password in database: " + userFromDb.password);
                }
            }
        }else{
            logger.debug("The username and/or the password were not provided");
        }

        return null;
    }

    public static String encodePassword(String password){
        byte[] bytes = password.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    

}
