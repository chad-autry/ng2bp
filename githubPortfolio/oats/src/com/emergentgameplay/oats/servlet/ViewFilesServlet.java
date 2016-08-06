package com.emergentgameplay.oats.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.*;

import com.emergentgameplay.oats.dao.OatsDao;
import com.emergentgameplay.oats.model.FileInfo;
import com.emergentgameplay.oats.model.FrequencyContext;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This servlet provides a list of all files the user has uploaded
 * @author Chad
 *
 */
@SuppressWarnings("serial")
public class ViewFilesServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ViewFilesServlet.class.getName());
    UserService userService = UserServiceFactory.getUserService();
    OatsDao dao = OatsDao.getOatsDao();
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        //This servlet is used by asynch requests. The page calling this should check the status and re-direct the user to re-login
        if (!userService.isUserLoggedIn()) {
            //unauthorized
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        User user = userService.getCurrentUser();
        String userId = user.getUserId();
        
        //Pull the list of FileInfos this user has uploaded
        List<FileInfo> fileInfos = dao.getFileInfos(userId);
        //for each file info, create an entry in the response json array
        StringBuilder responseBuilder = new StringBuilder("{\"files\":[");
        String seperator ="";
        for (FileInfo fileInfo:fileInfos) {
            responseBuilder.append(seperator);
            responseBuilder.append("{\"fileName\":\"").append(fileInfo.getFileName()).append("\",\"fileInfoId\":").append(fileInfo.getFileInfoId()).append("}");
           
            seperator = ",";
        }
        


        responseBuilder.append("]}");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        //log.info(responseBuilder.toString());
        resp.getWriter().print(responseBuilder.toString());
        //resp.getWriter().print("{\"gravitationalObjects\":[{\"mass\":1,\"radius\":0,\"uPos\":0,\"vPos\":0,\"uVel\":0,\"vVel\":0},{\"mass\":5,\"radius\":1,\"uPos\":13,\"vPos\":6,\"uVel\":0,\"vVel\":0}]}");//responseBuilder.toString());
        resp.getWriter().flush();

    }
}
