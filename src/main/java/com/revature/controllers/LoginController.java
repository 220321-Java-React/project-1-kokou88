package com.revature.controllers;

///import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revature.models.UserDTO;
import com.revature.services.LoginService;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller{

   LoginService loginService = new LoginService();
   Logger logger = LoggerFactory.getLogger("Login Controller Logger");

    public Handler loginAttempt = (ctx) -> {
        UserDTO user = ctx.bodyAsClass(UserDTO.class);
        UserDTO returnedUser = loginService.login(user.username, user.password);
        if(returnedUser != null){
            logger.info("Login attempt was successful");
            ctx.req.getSession();
            ctx.cookie("userRole", returnedUser.userRole, 1*24*60*60*1000);
            ctx.cookieStore("userID", returnedUser.userID);
            ctx.status(200);
        }else {
            logger.error("Login attempt was unsuccessful");
            ctx.req.getSession().invalidate();
            ctx.status(401);
        }
    };

    public Handler logout = (ctx) -> {
        if (ctx.req.getSession(false) != null){
            ctx.clearCookieStore();
            ctx.req.getSession().invalidate();
            logger.info("Logout successful");
            ctx.status(200);
        }else {
            logger.error("No session to log out of");
            ctx.status(401);
        }
    };

    @Override
    public void addRoutes(Javalin app) {
        app.post("/login", this.loginAttempt);
        app.get("/logout", this.logout);
    }

}