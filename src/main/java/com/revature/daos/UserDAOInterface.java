package com.revature.daos;

import com.revature.models.UserDTO;

public interface UserDAOInterface {

    UserDTO login(String username);
}
