package com.emergentgameplay.oats.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.Channels;
import java.text.DecimalFormat;
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
 * This servelet will give back the list of context infos for a file
 * @author Chad
 *
 */
@SuppressWarnings("serial")
public class ContextListServlet extends HttpServlet {
    // Get a file service
    FileService fileService = FileServiceFactory.getFileService();
    private static final Logger log = Logger.getLogger(ContextListServlet.class.getName());
    OatsDao dao = OatsDao.getOatsDao();
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //Get the fileInfo ID from the parameter
        String fileInfoId = req.getParameter("fileInfoId");
        
        //Pull the file info to validate the user and get the duration/sampleRate
        FileInfo info = dao.getFileInfo(Long.valueOf(fileInfoId));
        log.info("Retrieving ContextList:"+fileInfoId);
        double duration = (1.0*info.getNumberOfFrames())/info.getSampleRate();
        //Prepare the array format for the frequency context data
        StringBuilder responseBuilder = new StringBuilder("{\"duration\":").append(duration).append(",\"frequencyContexts\":[");
        
        //TODO Create and use a separate method which does not create the stream to read the analyzed values from
        //Pull the FrequencyContexts with the analyzed magnitudes included
        List<FrequencyContext> frequencyContexts = dao.getFrequencyContexts(Long.valueOf(fileInfoId));
        for (FrequencyContext context : frequencyContexts) {
            responseBuilder.append("{\"targetFrequency\":").append(context.getTargetFrequency())
            .append(",\"contextId\":").append(context.getContextId())
            .append(",\"contextName\":").append("\""+context.getName()+"\"")
            .append(",\"maxValue\":").append(new DecimalFormat("#0.000000").format(context.getMaxValue()))
            .append("},");
        }
        responseBuilder.deleteCharAt(responseBuilder.lastIndexOf(","));
        responseBuilder.append("]}");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        log.info(responseBuilder.toString());
        resp.getWriter().print(responseBuilder.toString());
        //resp.getWriter().print("{\"gravitationalObjects\":[{\"mass\":1,\"radius\":0,\"uPos\":0,\"vPos\":0,\"uVel\":0,\"vVel\":0},{\"mass\":5,\"radius\":1,\"uPos\":13,\"vPos\":6,\"uVel\":0,\"vVel\":0}]}");//responseBuilder.toString());
        resp.getWriter().flush();

    }
}
