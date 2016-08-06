package com.emergentgameplay.oats.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.*;

import com.emergentgameplay.oats.WavFile;
import com.emergentgameplay.oats.WavFileException;
import com.emergentgameplay.oats.dao.OatsDao;
import com.emergentgameplay.oats.model.FileInfo;
import com.emergentgameplay.oats.model.FrequencyContext;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Transaction;


@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Queue queue = QueueFactory.getQueue("frequencyProcessing");
    BlobInfoFactory infoFactory = new BlobInfoFactory();
    private static final Logger log = Logger.getLogger(UploadServlet.class.getName());
    UserService userService = UserServiceFactory.getUserService();
    OatsDao dao = OatsDao.getOatsDao();
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private String[] names ={"E<sub>2</sub>",
            "F<sub>2</sub>",
            "F<sup>#</sup><sub>2</sub>/G<sup>b</sup><sub>2</sub>",
            "G<sub>2</sub>",
            "G<sup>#</sup><sub>2</sub>/A<sup>b</sup><sub>2</sub>",
            "A<sub>2</sub>",
            "A<sup>#</sup><sub>2</sub>/B<sup>b</sup><sub>2</sub>",
            "B<sub>2</sub>",
            "C<sub>3</sub>",
            "C<sup>#</sup><sub>3</sub>/D<sup>b</sup><sub>3</sub>",
            "D<sub>3</sub>",
            "D<sup>#</sup><sub>3</sub>/E<sup>b</sup><sub>3</sub>",
            "E<sub>3</sub>",
            "F<sub>3</sub>",
            "F<sup>#</sup><sub>3</sub>/G<sup>b</sup><sub>3</sub>",
            "G<sub>3</sub>",
            "G<sup>#</sup><sub>3</sub>/A<sup>b</sup><sub>3</sub>",
            "A<sub>3</sub>",
            "A<sup>#</sup><sub>3</sub>/B<sup>b</sup><sub>3</sub>",
            "B<sub>3</sub>",
            "C<sub>4</sub>",
            "C<sup>#</sup><sub>4</sub>/D<sup>b</sup><sub>4</sub>",
            "D<sub>4</sub>",
            "D<sup>#</sup><sub>4</sub>/E<sup>b</sup><sub>4</sub>",
            "E<sub>4</sub>",
            "F<sub>4</sub>",
            "F<sup>#</sup><sub>4</sub>/G<sup>b</sup><sub>4</sub>",
            "G<sub>4</sub>",
            "G<sup>#</sup><sub>4</sub>/A<sup>b</sup><sub>4</sub>",
            "A<sub>4</sub>",
            "A<sup>#</sup><sub>4</sub>/B<sup>b</sup><sub>4</sub>",
            "B<sub>4</sub>",
            "C<sub>5</sub>",
            "C<sup>#</sup><sub>5</sub>/D<sup>b</sup><sub>5</sub>",
            "D<sub>5</sub>",
            "D<sup>#</sup><sub>5</sub>/E<sup>b</sup><sub>5</sub>",
            "E<sub>5</sub>",
            "F<sub>5</sub>",
            "F<sup>#</sup><sub>5</sub>/G<sup>b</sup><sub>5</sub>",
            "G<sub>5</sub>",
            "G<sup>#</sup><sub>5</sub>/A<sup>b</sup><sub>5</sub>",
            "A<sub>5</sub>",
            "A<sup>#</sup><sub>5</sub>/B<sup>b</sup><sub>5</sub>",
            "B<sub>5</sub>",
            "C<sub>6</sub>"};
    private double[] frequencies = {82.41,
            87.31,
            92.5,
            98,
            103.83,
            110,
            116.54,
            123.47,
            130.81,
            138.59,
            146.83,
            155.56,
            164.81,
            174.61,
            185,
            196,
            207.65,
            220,
            233.08,
            246.94,
            261.63,
            277.18,
            293.66,
            311.13,
            329.63,
            349.23,
            369.99,
            392,
            415.3,
            440,
            466.16,
            493.88,
            523.25,
            554.37,
            587.33,
            622.25,
            659.26,
            698.46,
            739.99,
            783.99,
            830.61,
            880,
            932.33,
            987.77,
            1046.5};
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!userService.isUserLoggedIn()) {
            //unauthorized
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        User user = userService.getCurrentUser();
        String userId = user.getUserId();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        BlobKey blobKey = blobs.get("myFile").get(0);
        String blobKeyString = blobKey.getKeyString();
        //Create the file Info, with the owner ID, etc.
        FileInfo info = new FileInfo();
        info.setBlobKeyString(blobKeyString);
        info.setUserId(userId);
        
        BlobInfo blobInfo =infoFactory.loadBlobInfo(blobKey);
        info.setFileName(blobInfo.getFilename());
        
        BlobstoreInputStream inputStream = new BlobstoreInputStream(blobKey);
        WavFile wav = null;
        try {
             wav = WavFile.openWavFile(inputStream, blobInfo.getSize());
        } catch (WavFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        info.setNumberOfFrames(wav.getNumFrames());
        info.setSampleRate( wav.getSampleRate());
        Long fileInfoId = dao.writeFileInfo(info);
        
        //Iterate over each item, create it on the DB, push it to the queue for processing
        for (int i = 0; i < frequencies.length; i++) {
            FrequencyContext context = new FrequencyContext();
            context.setAccuracy(.05); //5%
            context.setInputFileBlobKeyString(blobKeyString);
            context.setFileId(fileInfoId);
            context.setTargetFrequency(frequencies[i]);
            context.setName(names[i]);

            Transaction txn = ds.beginTransaction();
            Long contextId = dao.writeFrequencyContext(context);
            log.info("Submitting:"+Long.toString(contextId));
            queue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
                    .payload(Long.toString(contextId)));
            /*
            TaskOptions task = TaskOptions.Builder.withUrl("/process");
            task.param("contextId", Long.toString(contextId));
            task.header("Host", BackendServiceFactory.getBackendService().getBackendAddress("frequency-processing-backend"));
            task.method(Method.PUT);
            queue.add(task);
            */
            txn.commit();
        }
        
        resp.setContentType("text/plain");
        resp.getWriter().println("Processing");
    }
}
