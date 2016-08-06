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
import com.emergentgameplay.oats.service.FrequencyProcessingService;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;

/**
 * This servelet will give back the list of context infos for a file
 * @author Chad
 *
 */
@SuppressWarnings("serial")
public class BackendInitServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(BackendInitServlet.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	log.info("ServletInitialized");
    	Thread thread = ThreadManager.createBackgroundThread(new Runnable() {
    		FrequencyProcessingService service = new FrequencyProcessingService();
    		Queue queue = QueueFactory.getQueue("frequencyProcessing");
    		  public void run() {
    		    try {
    		      while (true) {
    		    	  List<TaskHandle> tasks = queue.leaseTasks(1, TimeUnit.HOURS, 1);
    		    	  for (TaskHandle task : tasks) {
    		    		  try {
							service.process(new String(task.getPayload(), "UTF-8"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    		    		  queue.deleteTask(task.getName());
    		    	  }
    		        Thread.sleep(10);
    		      }
    		    } catch (InterruptedException ex) {
    		      throw new RuntimeException("Interrupted in loop:", ex);
    		    }
    		  }
    		});
    		thread.start();
    }
}
