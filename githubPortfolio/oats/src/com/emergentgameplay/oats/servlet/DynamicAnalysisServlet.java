package com.emergentgameplay.oats.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.Channels;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.*;

import com.emergentgameplay.oats.WavFile;
import com.emergentgameplay.oats.WavFileException;
import com.emergentgameplay.oats.dao.OatsDao;
import com.emergentgameplay.oats.model.FileInfo;
import com.emergentgameplay.oats.model.FrequencyContext;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

/**
 * This file will produce a re-synthesized file based on the various FrequencyContexts
 * @author Chad
 *
 */
@SuppressWarnings("serial")
public class DynamicAnalysisServlet extends HttpServlet {
    // Get a file service
    private static final Logger log = Logger.getLogger(DynamicAnalysisServlet.class.getName());
    OatsDao dao = OatsDao.getOatsDao();
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //Get the file ID from the parameter
        String fileInfoId = req.getParameter("fileId");
        //Get the relevant file info from the DB
        
        //get the frequency, from the parameter
        
        //get the accuracy, from the parameter
        
        //get the samples per second, from the parameter
        
        log.info("Dynamically analyzing:"+fileInfoId);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        //write out content as we go so it can stream to the client
        resp.getWriter().print("[");
        
        //Create the GoertzleCalculator
        
        //Loop for the required number of samples
        
        	//reset the GoertzleCalculator
        
        	//Select the appropriate sample bits from the DB
        
        	//Feed sample bits to calculator
        
        	//Write result to response

        resp.getWriter().print("]");
        resp.getWriter().flush();
    }
}
