package com.revature.daos;

import com.revature.models.Request;
import com.revature.models.RequestDTO;
import com.revature.models.ResolveDTO;

import java.util.List;

public interface RequestDAOInterface {
    public List<Request> showAllRequests();
    public List<Request> showByStatus(String status);
    public List<Request> showUserRequests(int userId);
    public List<Request> showUserRequestsByStatus(String status, int userId);

    public int addReimbStatus();
    public int addReimbType(String type);
    public boolean addRequest(RequestDTO requestDTO);
    public boolean resolveRequest(ResolveDTO resolveDTO);
}
