package com.emergentgameplay.oats.model;

/**
 * This class provides ownership information about a file.
 * Has the actual file blobstore key, has the owner id, has the creation date
 * @author Chad
 *
 */
public class FileInfo {
    
    public Long fileInfoId;
    public String blobKeyString;
    public Long sampleRate;
    public Long numberOfFrames;
    public String userId;
    public String fileName;

    public Long getFileInfoId() {
        return fileInfoId;
    }
    public void setFileInfoId(Long fileInfoId) {
        this.fileInfoId = fileInfoId;
    }
    public String getBlobKeyString() {
        return blobKeyString;
    }
    public void setBlobKeyString(String blobKeyString) {
        this.blobKeyString = blobKeyString;
    }
    public Long getSampleRate() {
        return sampleRate;
    }
    public void setSampleRate(Long sampleRate) {
        this.sampleRate = sampleRate;
    }
    public Long getNumberOfFrames() {
        return numberOfFrames;
    }
    public void setNumberOfFrames(Long numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    

}
