package com.revature.daos;

import com.revature.models.UserDTO;
import com.revature.utils.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO implements UserDAOInterface {

    private static final Logger logger = LoggerFactory.getLogger("UserDao logger");

    @Override
    public UserDTO login(String username) {
        try(Connection conn = ConnectionUtil.getConnection()){
            String sql = "SELECT username, passwrd, user_role, user_id FROM users AS users JOIN users_roles AS roles ON users.user_role_id = roles.users_role_id WHERE users.username = ?; "; 
                    //ON users.user_role_id = roles.users_role_id " +
                  //  "WHERE users.username = ?;";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                logger.info("The query executed and returned a record");
                UserDTO user = new UserDTO();
                user.username = rs.getString("username");
                user.password = rs.getString("passwrd");
                user.userRole = rs.getString("user_role");
                user.userID = rs.getInt("user_id");
                return user;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        logger.debug("No UserDTO object was created");
        return null;
    }
}