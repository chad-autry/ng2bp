package com.emergentgameplay.oats.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
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
 * This servlet sends a response with the data of a single analyzed file frequency context
 * @author Chad
 *
 */
@SuppressWarnings("serial")
public class ViewContextServlet extends HttpServlet {
    // Get a file service
    FileService fileService = FileServiceFactory.getFileService();
    private static final Logger log = Logger.getLogger(ViewContextServlet.class.getName());
    OatsDao dao = OatsDao.getOatsDao();
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //Get the context ID from the parameter
        String frequencyContextId = req.getParameter("contextId");
        

        
        //Pull out the context
        FrequencyContext context = dao.getFrequencyContext(Long.valueOf(frequencyContextId));
        log.info("Retrieving:"+frequencyContextId);

        //Pull the file info
        FileInfo info = dao.getFileInfo(Long.valueOf(context.getFileId()));
        //Prepare the array format for the frequency context data
        StringBuilder responseBuilder = new StringBuilder("[");
        
        //Parse out the start and end times
        double startTime = 0;
        double endTime = Long.MAX_VALUE;
        try {
        	startTime = Double.parseDouble(req.getParameter("startTime"));
        } catch (NumberFormatException e) {
        	startTime = 0;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
        	endTime = Double.parseDouble(req.getParameter("endTime"));
        } catch (NumberFormatException e) {
        	endTime = Double.MAX_VALUE;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //Figure out the min and max frames shown
        //time * sampleRate = frameCount 
        long maxFrame = (long)Math.floor(endTime*info.getSampleRate());
        long minFrame = (long)Math.ceil(startTime*info.getSampleRate());

        
        //Get the max number of samples requested from the parameter
        long samplesPerSecond = Long.parseLong(req.getParameter("samplesPerSecond"));
        
        
        
        long everyXFrames = info.getSampleRate() / samplesPerSecond;
        
        int totalFrames = 0;

        //Round to a default of 3 sig figs
        MathContext roundingContext = new MathContext(2);
        BigDecimal runningAverage = null;
        int averagedValues = 1;
        outer:
        for (int i = 0; i < info.getNumberOfFrames(); i = i + 100) {
            //prepare an output buffer, of either 100 frames, or the remaining if this is the final loop
              int bufferFrames = 100;
              if (i + 100 >  info.getNumberOfFrames()) {
                  bufferFrames = (int) (info.getNumberOfFrames() % 100);
              }

             
              byte[] inputBuffer = new byte[8*bufferFrames];
              context.getAnalyzedMagnitude().read(inputBuffer);
              DoubleBuffer inputDoubleBuffer = ByteBuffer.wrap(inputBuffer).asDoubleBuffer();
              //calculate each element of the outputBuffer

              
              for (int j = 0; j < bufferFrames; j++) {
                  
                  //Make the final frame a 0 magnitude
                  if ((i + 100) >= info.getNumberOfFrames() && ( j + 1) == bufferFrames) {
                      responseBuilder.append("0");
                      continue;
                  }
                  
                  //figure out the magnitude
                  double value = inputDoubleBuffer.get();
                  //only add a value if it isn't identical to the previous rounded value
                  BigDecimal currentValue = new BigDecimal(value);
                  //currentValue = currentValue.round(roundingContext);
                  //if check to reduce the frames shown
                  if (totalFrames >= minFrame) {
                	  if ((totalFrames - 1) % everyXFrames == 0) {
                		  if (runningAverage == null) {
                			  runningAverage = currentValue;
                		  }
	                      runningAverage = runningAverage.divide(BigDecimal.valueOf(averagedValues), roundingContext);
	                      responseBuilder.append(runningAverage.toPlainString()+",");
	                      averagedValues = 1;
                	  } else {
                		  if (runningAverage == null) {
                			  runningAverage = currentValue;
                		  } else {
                			  runningAverage = runningAverage.add(currentValue);
                			  averagedValues++;
                			
                		  }
                	  }
                  }
                  totalFrames++;
                  if (totalFrames >= maxFrame) {
                      //TODO The maxFrame was added after display was implimented, integrate with the loops better and don't use a labeled break
                      responseBuilder.append("0");
                      break outer;
                  }
              }
        }

        //Don't need to delete this coma since have final case handling//responseBuilder.deleteCharAt(responseBuilder.lastIndexOf(","));
        responseBuilder.append("]");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        //log.info(responseBuilder.toString());
        resp.getWriter().print(responseBuilder.toString());
        //resp.getWriter().print("{\"gravitationalObjects\":[{\"mass\":1,\"radius\":0,\"uPos\":0,\"vPos\":0,\"uVel\":0,\"vVel\":0},{\"mass\":5,\"radius\":1,\"uPos\":13,\"vPos\":6,\"uVel\":0,\"vVel\":0}]}");//responseBuilder.toString());
        resp.getWriter().flush();

    }
}
