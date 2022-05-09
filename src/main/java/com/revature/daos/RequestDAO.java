package com.revature.daos;

import com.revature.models.Request;
import com.revature.models.RequestDTO;
import com.revature.models.ResolveDTO;
import com.revature.utils.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO implements RequestDAOInterface {

    private static final Logger logger = LoggerFactory.getLogger("RequestDAO Logger");
    private static final String connectionEstablished = "The connection was established";
    private static final String querySucceeded = "The query was run successfully";
    private static final String exceptionMessage = "Either the connection or the query failed";
    private static final String emptyResult = "The query succeeded, but no results were returned";
    private static final String getRequestsQuery = "SELECT r.reimb_id, r.reimb_amount, r.reimb_submitted, r.reimb_resolved, " +
            "r.reimb_description, r.reimb_author, r.reimb_resolver, t.reimb_type, s.reimb_status\n" +
            "FROM reimbursements AS r\n" +
            "JOIN reimbursement_type AS t ON r.reimb_type_id = t.reimb_type_id\n" +
            "JOIN reimbursement_status AS s ON r.reimb_status_id = s.reimb_status_id";

    @Override
    public List<Request> showAllRequests() {
        try (PreparedStatement statement = ConnectionUtil.getConnection().prepareStatement(getRequestsQuery + ";")){
            logger.info(connectionEstablished);
            ResultSet rs = statement.executeQuery();
            List<Request> requestList = populateRequestList(rs);

            if (!requestList.isEmpty()){
                logger.info(querySucceeded);
            }else{
                logger.info(emptyResult);
            }

            return requestList;
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(exceptionMessage);
        }

        return null;
    }

    @Override
    public List<Request> showUserRequests(int userId) {
        try (PreparedStatement statement = ConnectionUtil.getConnection()
                .prepareStatement(getRequestsQuery + " WHERE r.reimb_author = ?;")){
            logger.info(connectionEstablished);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            List<Request> requestList = populateRequestList(rs);

            if (!requestList.isEmpty()){
                logger.info(querySucceeded);
            }else{
                logger.info(emptyResult);
            }

            return requestList;
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(exceptionMessage);
        }

        return null;
    }

    @Override
    public List<Request> showByStatus(String status) {
        try (PreparedStatement statement = ConnectionUtil.getConnection()
                .prepareStatement(getRequestsQuery + " WHERE s.reimb_status = ?;")){
            logger.info(connectionEstablished);
            statement.setString(1, status);
            ResultSet rs = statement.executeQuery();
            List<Request> requestList = populateRequestList(rs);

            if (!requestList.isEmpty()){
                logger.info(querySucceeded);
            }else{
                logger.info(emptyResult);
            }

            return requestList;
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(exceptionMessage);
        }

        return null;
    }

    @Override
    public List<Request> showUserRequestsByStatus(String status, int userId) {
        try (PreparedStatement statement = ConnectionUtil.getConnection()
                .prepareStatement(getRequestsQuery + " WHERE s.reimb_status = ? AND reimb_author = ?;")){
            logger.info(connectionEstablished);
            statement.setString(1, status);
            statement.setInt(2, userId);
            ResultSet rs = statement.executeQuery();
            List<Request> requestList = populateRequestList(rs);

            if (!requestList.isEmpty()){
                logger.info(querySucceeded);
            }else{
                logger.info(emptyResult);
            }

            return requestList;
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(exceptionMessage);
        }

        return null;
    }

    private List<Request> populateRequestList(ResultSet rs) throws SQLException {
        List<Request> requestList = new ArrayList<>();

        while (rs.next()) {
            int requestId = rs.getInt("reimb_id");
            double amount = rs.getDouble("reimb_amount");
            String submitted = rs.getTimestamp("reimb_submitted").toString();
            Timestamp resolvedDate = rs.getTimestamp("reimb_resolved");
            String resolved = "";
            if (resolvedDate != null) {
                resolved = resolvedDate.toString();
            } else {
                resolved = "N/A";
            }
            String description = rs.getString("reimb_description");
            int author = rs.getInt("reimb_author");
            int resolver = rs.getInt("reimb_resolver");
            String type = rs.getString("reimb_type");
            String status = rs.getString("reimb_status");

            Request a = new Request(requestId, amount, submitted, resolved, description, author, resolver, status, type);
            requestList.add(a);
        }

        return requestList;
    }

    @Override
    public int addReimbStatus() {
        int requestStatusId = 0;

        try (Connection conn = ConnectionUtil.getConnection()){
            String insertQuery = "INSERT INTO reimbursement_status (reimb_status) VALUES ('Pending');";
            PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                logger.info(querySucceeded);
                requestStatusId = rs.getInt("reimb_status_id");
            }else{
                logger.debug("A statusId was not generated");
            }

            logger.info(querySucceeded);
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(exceptionMessage);
        }

        return requestStatusId;

    }

    @Override
    public int addReimbType(String type) {
        int requestTypeId = 0;

        try (Connection conn = ConnectionUtil.getConnection()){
            String insertQuery = "INSERT INTO reimbursement_type (reimb_type)\n" +
                    "VALUES (?);";

            PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, type);
            statement.execute();
            logger.info(connectionEstablished);
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                logger.info(querySucceeded);
                requestTypeId = rs.getInt("reimb_type_id");
            }else{
                logger.debug("A typeId was not generated");
            }

            logger.info(querySucceeded);
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(exceptionMessage);
        }

        return requestTypeId;
    }

    @Override
    public boolean addRequest(RequestDTO request) {
        int statusId = addReimbStatus();
        int typeId = addReimbType(request.type);

        if (statusId != 0 && typeId != 0) {
            try (Connection conn = ConnectionUtil.getConnection()) {
                logger.info(connectionEstablished);
                String sqlStatement = "INSERT INTO reimbursements (reimb_amount, reimb_description, reimb_author, "+
                "reimb_status_id, reimb_type_id, reimb_submitted)\n" +
                        "VALUES(?, ?, ?, ?, ?, ?);";

                PreparedStatement statement = conn.prepareStatement(sqlStatement);
                statement.setDouble(1, request.amount);
                statement.setString(2, request.description);
                statement.setInt(3, request.authorId);
                statement.setInt(4, statusId);
                statement.setInt(5, typeId);
                statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                statement.execute();

                logger.info(querySucceeded);
                return true;
            } catch (SQLException e) {
                logger.error(exceptionMessage);
            }
        }else{
            logger.debug("Either the statusId or the typeId were not retrieved");
        }

        return false;
    }

    @Override
    public boolean resolveRequest(ResolveDTO resolveDTO) {
        int statusId = getStatusId(resolveDTO.requestID);
        if (statusId != 0) {
            try (Connection conn = ConnectionUtil.getConnection()) {
                logger.info(connectionEstablished);
                String updateQuery = "UPDATE reimbursement_status SET reimb_status = ? WHERE reimb_status_id = ?;";
                String addResolveDate = "UPDATE reimbursements SET reimb_resolved = ?, reimb_resolver = ? WHERE reimb_id = ?;";

                PreparedStatement statement = conn.prepareStatement(updateQuery);
                statement.setString(1, resolveDTO.resolveChoice);
                statement.setInt(2, statusId);
                statement.execute();

                PreparedStatement statement2 = conn.prepareStatement(addResolveDate);
                statement2.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement2.setInt(2, resolveDTO.resolverId);
                statement2.setInt(3, resolveDTO.requestID);
                statement2.execute();

                logger.info(querySucceeded);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error(exceptionMessage);
            }
        }

        return false;
    }

    private int getStatusId(int requestID) {
        int statusId = 0;

        try (Connection conn = ConnectionUtil.getConnection()){
            logger.info(connectionEstablished);
            String updateQuery = "SELECT reimb_status_id FROM reimbursements WHERE reimb_id = ?;";

            PreparedStatement statement = conn.prepareStatement(updateQuery);
            statement.setInt(1, requestID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                logger.info(querySucceeded);
                statusId = rs.getInt("reimb_status_id");
            }else {
                logger.info("Did not get the statusId");
            }
        }catch (SQLException e){
            e.printStackTrace();
            logger.error(exceptionMessage);
        }

        return statusId;
    }

}