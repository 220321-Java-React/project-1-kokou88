package com.revature.controllers;

import com.revature.models.Request;
import com.revature.models.RequestDTO;
import com.revature.models.ResolveDTO;
import com.revature.services.RequestService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequestController implements Controller{

    RequestService requestService = new RequestService();
    Logger logger = LoggerFactory.getLogger("Request Controller Logger");

    public Handler getAllRequests = ctx -> {
        if (ctx.req.getSession(false) != null){
            int userId = ctx.cookieStore("userID");
            String userRole = ctx.cookie("userRole");
            List<Request> requestList = requestService.showAllRequests(userId, userRole);
            if (requestList != null){
                if (requestList.isEmpty()){
                    logger.info("The returned list is empty");
                    ctx.status(204);
                }else{
                    logger.debug("The list of reimbursement requests was retrieved");
                    ctx.json(requestList);
                    ctx.status(200);
                }
            }else{
                logger.debug("The list of reimbursement requests was not retrieved");
                ctx.status(400);
            }
        }else{
            logger.debug("There isn't a session in progress");
            ctx.status(400);
        }
    };

    public Handler getByStatus = ctx -> {
        if(ctx.req.getSession(false)!=null){
            String reimbStatus = ctx.pathParam("reimbStatus");
            String userRole = ctx.cookie("userRole");
            int userID = ctx.cookieStore("userID");
            List<Request> requestList = requestService.showByStatus(reimbStatus, userRole, userID);
            if (requestList != null) {
                logger.debug("The list of reimbursement requests was retrieved");
                if (requestList.isEmpty()){
                    logger.info("The returned list is empty");
                    ctx.status(204);
                }else{
                    ctx.status(200);
                    ctx.json(requestList);
                }
            }else{
                logger.debug("There was a problem and the list was not retrieved");
            }
        }else {
            ctx.status(400);
        }
    };

    public Handler addRequest = (ctx) -> {
        if (ctx.req.getSession(false) != null) {
            RequestDTO requestDTO = ctx.bodyAsClass(RequestDTO.class);
            requestDTO.authorId = ctx.cookieStore("userID");

            if (requestService.addRequest(requestDTO)) {
                logger.info("The request was successfully added");
                ctx.status(201);
            } else {
                logger.error("The request could not be added");
                ctx.status(400);
            }
        }else{
            ctx.status(400);
            logger.debug("There isn't a session in progress");
        }
    };

    public Handler resolveRequest = ctx -> {
        if (ctx.req.getSession(false) != null) {
            ResolveDTO resolveDTO = ctx.bodyAsClass(ResolveDTO.class);
            resolveDTO.resolverId = ctx.cookieStore("userID");
            String userRole = ctx.cookie("userRole");
            if (requestService.resolveRequest(resolveDTO, userRole)) {
                logger.info("requestDTO was successfully created");
                ctx.status(200);
            }
            else {
                logger.error("There was a problem creating the DTO from the form input");
                ctx.status(304);
            }
        }else{
            logger.debug("There isn't a session in progress");
        }
    };

    @Override
    public void addRoutes(Javalin app) {
        app.get("/requests/all", getAllRequests);
        app.get("/requests/{reimbStatus}", getByStatus);
        app.post("/requests/add", addRequest);
        app.patch("/requests/resolve", resolveRequest);
    }
}