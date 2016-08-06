package com.emergentgameplay.oats.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.*;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;


/**
 * Each upload to the blobstore requires a dynamically generated URL. This servlet generates and returns one
 * @author Chad
 *
 */
@SuppressWarnings("serial")
public class GenerateUploadUrlServlet extends HttpServlet {
    // Get a blobstore service
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private static final Logger log = Logger.getLogger(GenerateUploadUrlServlet.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(blobstoreService.createUploadUrl("/upload"));
        resp.getWriter().flush();

    }
}
