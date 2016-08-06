package com.emergentgameplay.oats.model;

import java.io.InputStream;

/**
 * This class provides the context on a requested frequency analysis.
 * It has the id of the file to be analyzed, and a status field to indicate the status of the analysis
 * It contains the parameters needed to perform the analysis (frequency, accuracy)
 * It contains the plain text results once analysis is done. Limited to 1MB
 * @author Chad
 *
 */
public class FrequencyContext {
    
    private Long fileId;
    private double targetFrequency;
    private Long contextId;
    private String inputFileBlobKeyString;
    private String analyzedMagnitudeBlobKeyString;
    //The input stream 
    private InputStream analyzedMagnitude;
    private String name; //The given name
    private double maxValue; //The maximum value seen producing the context
    
    /**
     * Accuracy might not be the best label, used to calculate bin width as a percentage of the targetFrequency
     * Samples = (int)Math.ceil(sampleRate/(accuracy*targetFrequency)))
     */
    double accuracy;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileInfoId) {
        this.fileId = fileInfoId;
    }
    


    public String getInputFileBlobKeyString() {
        return inputFileBlobKeyString;
    }

    public void setInputFileBlobKeyString(String inputFileBlobKeyString) {
        this.inputFileBlobKeyString = inputFileBlobKeyString;
    }

    public double getTargetFrequency() {
        return targetFrequency;
    }

    public void setTargetFrequency(double targetFrequency) {
        this.targetFrequency = targetFrequency;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
    

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public String getAnalyzedMagnitudeBlobKeyString() {
        return analyzedMagnitudeBlobKeyString;
    }

    public void setAnalyzedMagnitudeBlobKeyString(String analyzedMagnitudeBlobKeyString) {
        this.analyzedMagnitudeBlobKeyString = analyzedMagnitudeBlobKeyString;
    }

    public InputStream getAnalyzedMagnitude() {
        return analyzedMagnitude;
    }

    public void setAnalyzedMagnitude(InputStream analyzedMagnitude) {
        this.analyzedMagnitude = analyzedMagnitude;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
    
    
}
