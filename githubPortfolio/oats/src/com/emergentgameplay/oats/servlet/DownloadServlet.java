package com.emergentgameplay.oats.servlet;

import java.io.IOException;
import javax.servlet.http.*;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * This servlet will download a resynthesized file.
 * @author Chad
 *
 */
@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter("blobkey"));
        blobstoreService.serve(blobKey, resp);
    }
}
