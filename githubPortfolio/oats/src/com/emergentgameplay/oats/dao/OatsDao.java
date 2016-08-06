package com.emergentgameplay.oats.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.emergentgameplay.oats.model.FileInfo;
import com.emergentgameplay.oats.model.FrequencyContext;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * Singleton DAO for Google App Engine to handle all game data Not a fan of
 * mapping DB frameworks, do the object DS mapping by hand. API is simple
 * compared to JDBC by hand
 * 
 * @author Chad
 */
public class OatsDao {

    private static final Logger log = Logger.getLogger(OatsDao.class.getName());
    private static OatsDao oatsDao = new OatsDao();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Somewhat simplistic datamodel, can get away with keeping the GAE kinds
    // and properties within the DAO
    enum KINDS {
        FILE_INFO, FREQUENCY_CONTEXT
    };

    enum FREQUENCY_CONTEXT_PROPERTIES {
        FILE_ID, TARGET_FREQUENCY, ACCURACY, SAMPLED_FREQUENCIES, INPUT_FILE_BLOB_KEY, ANALYZED_MAGNITUDE_BLOB_KEY, NAME, MAX_VALUE
    };

    enum FILE_INFO_PROPERTIES {
        INPUT_FILE_BLOB_KEY, SAMPLE_RATE, NUMBER_OF_FRAMES, USER_ID, FILE_NAME
    };

    /**
     * Singleton, don't allow instantiation
     */
    protected OatsDao() {

    }

    public static OatsDao getOatsDao() {
        return oatsDao;
    }
    
    public List<FrequencyContext> getFrequencyContexts(long fileId) {
        List<FrequencyContext> resultList = new ArrayList<FrequencyContext>();
        //Create the Filters
        Filter fileIdFilter = new FilterPredicate(FREQUENCY_CONTEXT_PROPERTIES.FILE_ID.name(), FilterOperator.EQUAL, fileId);

        //Prepare the Query
        Query q = new Query(KINDS.FREQUENCY_CONTEXT.name()).setFilter(fileIdFilter);
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> entityList = pq.asList(FetchOptions.Builder.withDefaults());
        
        for (Entity entity : entityList) {
            resultList.add(resolveFrequencyContext(entity));
        }
        
        return resultList;
    }

    public FrequencyContext getFrequencyContext(long contextId) {
        FrequencyContext result = null;
        try {
            Entity context = datastore.get(KeyFactory.createKey(KINDS.FREQUENCY_CONTEXT.name(), contextId));
            result = resolveFrequencyContext(context);
        } catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
    private FrequencyContext resolveFrequencyContext(Entity context) {
        FrequencyContext result = null;
        result = new FrequencyContext();
        result.setAccuracy((Double) context.getProperty(FREQUENCY_CONTEXT_PROPERTIES.ACCURACY.name()));
        result.setInputFileBlobKeyString((String) context.getProperty(FREQUENCY_CONTEXT_PROPERTIES.INPUT_FILE_BLOB_KEY.name()));
        result.setContextId(context.getKey().getId());
        result.setFileId((Long) context.getProperty(FREQUENCY_CONTEXT_PROPERTIES.FILE_ID.name()));
        result.setTargetFrequency((Double) context.getProperty(FREQUENCY_CONTEXT_PROPERTIES.TARGET_FREQUENCY.name()));
        result.setAnalyzedMagnitudeBlobKeyString((String) context.getProperty(FREQUENCY_CONTEXT_PROPERTIES.ANALYZED_MAGNITUDE_BLOB_KEY.name()));
        result.setName((String) context.getProperty(FREQUENCY_CONTEXT_PROPERTIES.NAME.name()));
        result.setMaxValue((Double) context.getProperty(FREQUENCY_CONTEXT_PROPERTIES.MAX_VALUE.name()));
        
        //Get and set the inputStream for the analyzed magnitudes
        if (result.getAnalyzedMagnitudeBlobKeyString() != null) {
            BlobKey blobKey = new BlobKey(result.getAnalyzedMagnitudeBlobKeyString());
            try {
                BlobstoreInputStream inputStream = new BlobstoreInputStream(blobKey);
                result.setAnalyzedMagnitude(inputStream);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    public long writeFrequencyContext(FrequencyContext context) {
        Entity entity = null;

        if (context.getContextId() == null) {
            entity = new Entity(KINDS.FREQUENCY_CONTEXT.name());
        } else {
            entity = new Entity(KINDS.FREQUENCY_CONTEXT.name(), context.getContextId());
        }

        entity.setProperty(FREQUENCY_CONTEXT_PROPERTIES.FILE_ID.name(), context.getFileId());
        entity.setProperty(FREQUENCY_CONTEXT_PROPERTIES.ACCURACY.name(), context.getAccuracy());
        entity.setProperty(FREQUENCY_CONTEXT_PROPERTIES.INPUT_FILE_BLOB_KEY.name(),context.getInputFileBlobKeyString());
        entity.setProperty(FREQUENCY_CONTEXT_PROPERTIES.TARGET_FREQUENCY.name(),context.getTargetFrequency());
        entity.setProperty(FREQUENCY_CONTEXT_PROPERTIES.ANALYZED_MAGNITUDE_BLOB_KEY.name(),context.getAnalyzedMagnitudeBlobKeyString());
        entity.setProperty(FREQUENCY_CONTEXT_PROPERTIES.NAME.name(), context.getName());
        entity.setProperty(FREQUENCY_CONTEXT_PROPERTIES.MAX_VALUE.name(), context.getMaxValue());
        return datastore.put(entity).getId();
    }
    
    /**
     * Private helper method to get a FileInfo from an entity
     * @param context
     * @return
     */
    private FileInfo resolveFileInfo(Entity context) {
        FileInfo result = null;
        result = new FileInfo();
        result.setSampleRate((Long) context.getProperty(FILE_INFO_PROPERTIES.SAMPLE_RATE.name()));
        result.setNumberOfFrames((Long) context.getProperty(FILE_INFO_PROPERTIES.NUMBER_OF_FRAMES.name()));
        result.setBlobKeyString((String) context.getProperty(FILE_INFO_PROPERTIES.INPUT_FILE_BLOB_KEY.name()));
        result.setUserId((String) context.getProperty(FILE_INFO_PROPERTIES.USER_ID.name()));
        result.setFileName((String) context.getProperty(FILE_INFO_PROPERTIES.FILE_NAME.name()));
        result.setFileInfoId(context.getKey().getId());

        return result;
    }

    public FileInfo getFileInfo(long fileId) {
        FileInfo result = null;
        try {
            Entity context = datastore.get(KeyFactory.createKey(KINDS.FILE_INFO.name(), fileId));
            result = resolveFileInfo(context);
            
        } catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Gets all the file info objects attached to this user
     * @param userId
     * @return
     */
    public List<FileInfo> getFileInfos(String userId) {
        
        List<FileInfo> resultList = new ArrayList<FileInfo>();
        //Create the Filters
        Filter fileIdFilter = new FilterPredicate(FILE_INFO_PROPERTIES.USER_ID.name(), FilterOperator.EQUAL, userId);

        //Prepare the Query
        Query q = new Query(KINDS.FILE_INFO.name()).setFilter(fileIdFilter);
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> entityList = pq.asList(FetchOptions.Builder.withDefaults());
        
        for (Entity entity : entityList) {
            resultList.add(resolveFileInfo(entity));
        }
        
        return resultList;
    }
    
    
    
    public long writeFileInfo(FileInfo info) {
        Entity entity = null;

        if (info.getFileInfoId() == null) {
            entity = new Entity(KINDS.FILE_INFO.name());
        } else {
            entity = new Entity(KINDS.FILE_INFO.name(), info.getFileInfoId());
        }

        entity.setProperty(FILE_INFO_PROPERTIES.INPUT_FILE_BLOB_KEY.name(), info.getBlobKeyString());
        entity.setProperty(FILE_INFO_PROPERTIES.SAMPLE_RATE.name(), info.getSampleRate());
        entity.setProperty(FILE_INFO_PROPERTIES.NUMBER_OF_FRAMES.name(), info.getNumberOfFrames());
        entity.setProperty(FILE_INFO_PROPERTIES.USER_ID.name(), info.getUserId());
        entity.setProperty(FILE_INFO_PROPERTIES.FILE_NAME.name(), info.getFileName());

        return datastore.put(entity).getId();
    }
}
