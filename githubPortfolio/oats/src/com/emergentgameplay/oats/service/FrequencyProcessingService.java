package com.emergentgameplay.oats.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.logging.Logger;

import com.emergentgameplay.oats.WavFile;
import com.emergentgameplay.oats.WavFileException;
import com.emergentgameplay.oats.analysis.SlidingGoertzleCalculator;
import com.emergentgameplay.oats.dao.OatsDao;
import com.emergentgameplay.oats.model.FrequencyContext;
import com.emergentgameplay.oats.servlet.FrequencyProcessorServlet;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;


public class FrequencyProcessingService {
    private OatsDao dao = OatsDao.getOatsDao();
    private static final Logger log = Logger.getLogger(FrequencyProcessingService.class.getName());
    private BlobInfoFactory infoFactory = new BlobInfoFactory();
    // Get a file service for writting
    FileService fileService = FileServiceFactory.getFileService();
    
    public void process(String contextId) throws IOException {
        //Get the id of the context object

        log.info("Processing:"+contextId);
        //read the context object from the DAO
        FrequencyContext context = dao.getFrequencyContext(Long.valueOf(contextId));
        log.info("Read From DAO:"+contextId);
        BlobKey blobKey = new BlobKey(context.getInputFileBlobKeyString());
        log.info("Got Blob Key:"+contextId);
        //Use the analysis service to read the file, process it, and write the results back to the DS
        BlobInfo info =infoFactory.loadBlobInfo(blobKey);
        log.info("Got Blob info:"+contextId);
        
        
        BlobstoreInputStream inputStream = new BlobstoreInputStream(blobKey);
        log.info("Created inputStream:"+contextId);
        WavFile wav = null;
        try {
             wav = WavFile.openWavFile(inputStream, info.getSize());
        } catch (WavFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Display information about the wav file
        wav.display();
    
        // Get the number of audio channels in the wav file
        int numChannels = wav.getNumChannels();
        
        // Create a new Blob file for the output
        AppEngineFile file = fileService.createNewBlobFile("frequencyMagnitude");
        // Open a channel to write the analysis to
        boolean lock = true;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
        OutputStream os = Channels.newOutputStream(writeChannel);
    
        // Create a buffer of 100 frames
        double[] inputBuffer = new double[100 * numChannels];
        //Create a SlidingGoertzleCalculator
        SlidingGoertzleCalculator calc = new SlidingGoertzleCalculator(context.getTargetFrequency(), wav.getSampleRate(), 
                (int)Math.ceil(wav.getSampleRate()/(context.getAccuracy()*context.getTargetFrequency())));
        
        //Iterate over Wav input
        int framesRead = 0;
        try {
            framesRead = wav.readFrames(inputBuffer, 100);
        } catch (WavFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    
        System.out.println("Initializing complete");
        double maxValue = 0;
        //j represents how many buffers have been read
        for (int j = 0; framesRead != 0; j++) {
            if (j == 1) {
                log.info("Processed 1 buffer");
            }
            // prepare an output buffer 8 bytes per frame read (there are 8 bytes in a double)
            byte[] outputBuffer = new byte[framesRead*8];
            //wrap in a ByteBuffer for convenience
            ByteBuffer outputByteBuffer = ByteBuffer.wrap(outputBuffer);
            //Can't yet handle multiple channels, only look at the first channel
            for (int s = 0; s < framesRead * numChannels; s = s + numChannels) {
                calc.addSample(inputBuffer[s]);
                double value = calc.getResult();
                if (value > maxValue) {
                	maxValue = value;
                }
                outputByteBuffer.putDouble(value);
            }
            
            //write the outputBuffer
            os.write(outputBuffer);
            //Attempt to read more frames into the input buffer
            try {
                framesRead = wav.readFrames(inputBuffer, 100);
            } catch (WavFileException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // close the output file,
        os.close();
        writeChannel.closeFinally();
        
        //write the key to the frequency context
        BlobKey outputBlobKey = fileService.getBlobKey(file);
        context.setAnalyzedMagnitudeBlobKeyString(outputBlobKey.getKeyString());
        context.setMaxValue(maxValue);
        dao.writeFrequencyContext(context);
    }
}
