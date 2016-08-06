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
public class ResynthesisServlet extends HttpServlet {
    // Get a file service
    FileService fileService = FileServiceFactory.getFileService();
    private static final Logger log = Logger.getLogger(ResynthesisServlet.class.getName());
    OatsDao dao = OatsDao.getOatsDao();
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //Get the file ID from the parameter
        String fileInfoId = req.getParameter("fileId");
        
        //Pull the file info to validate the user and get the duration/sampleRate
        FileInfo info = dao.getFileInfo(Long.valueOf(fileInfoId));
        log.info("Resynthesizing:"+fileInfoId);

        
        //Pull the FrequencyContexts with the analyzed magnitudes included
        List<FrequencyContext> frequencyContexts = dao.getFrequencyContexts(Long.valueOf(fileInfoId));
        
        // Create a new Blob file with mime-type "audio/wav" to putput to
        AppEngineFile file = fileService.createNewBlobFile("audio/wav");

        // Open a channel to write to it
        boolean lock = true;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
        OutputStream os = Channels.newOutputStream(writeChannel);
        
        WavFile outWav = null;
        try {
            outWav = WavFile.newWavFile(os, 1, info.getNumberOfFrames(), 16, info.getSampleRate());
        } catch (WavFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //Iterate over the stuff and do the thinngnggngngngngngngn
        //TODO Optimize the code in these loops, they loop alot
        //Iterate over a set of output buffers of 100 doubles
        for (int i = 0; i < info.getNumberOfFrames(); i = i + 100) {
          //prepare an output buffer, of either 100 frames, or the remaining if this is the final loop
            int bufferFrames = 100;
            if (i + 100 >  info.getNumberOfFrames()) {
                bufferFrames = (int) (info.getNumberOfFrames() % 100);
            }
            double[] outBuffer = new double[bufferFrames];
            
            //iterate through each frequencyContext and calculate its contributions to the output buffer
            FrequencyContext context;
            for (Iterator<FrequencyContext> it = frequencyContexts.iterator(); it.hasNext(); ) {
                context = it.next();
                //Create an input buffer
                byte[] inputBuffer = new byte[8*bufferFrames];
                context.getAnalyzedMagnitude().read(inputBuffer);
                DoubleBuffer inputDoubleBuffer = ByteBuffer.wrap(inputBuffer).asDoubleBuffer();
                //calculate each element of the outputBuffer
                for (int j = 0; j < outBuffer.length; j++) {
                    //figure out the magnitude
                    double scale = inputDoubleBuffer.get();
                    scale = Math.abs(scale);
                    //TODO Why is this needed? scale = scale / 100000;
                    double intermeidate = scale*Math.sin((((double)(i+j))/(double)info.getSampleRate())*context.getTargetFrequency()*(2*Math.PI));

                    outBuffer[j] = outBuffer[j] + intermeidate;
                    if (i == 20*100 && j == 0) {
                        log.info("frequency:"+context.getTargetFrequency() + "scale: "+scale + " intermediate:"+intermeidate);
                        log.info("outbuffer:"+outBuffer[0]);
                    }
                }

            }
            if (i == 20*100 ) {
                log.info("outbuffer:"+outBuffer[0]);
            }
            
            //Write the buffer
            try {
                outWav.writeFrames(outBuffer, bufferFrames);
            } catch (WavFileException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        os.close();
        writeChannel.closeFinally();
        //Save the blob id to the FileInfo
    }
}
