package com.emergentgameplay.oats.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses multiple GoertzleCalculators to give a sliding result
 * @author Chad
 *
 */
public class SlidingGoertzleCalculator {
    List<GoertzleCalculator> calculators;
    int k; //The number of samples.
    int currentCalculator; //The calculator to be used to give a result, and then replaced
    double targetFrequency;
    long sampleRate;
    
    public SlidingGoertzleCalculator(double targetFrequency, long sampleRate, int k) {
        calculators = new ArrayList<GoertzleCalculator>(k);
        currentCalculator = -k;
        this.targetFrequency = targetFrequency;
        this.sampleRate = sampleRate;
        this.k = k;
        for (int i = 0; i < k; i++) {
            calculators.add(new GoertzleCalculator(targetFrequency, sampleRate));
        }
    }
    
    /**
     * Perform the calculations for adding one sample
     * @param sample
     */
    public void addSample(double sample) {
        if (currentCalculator > -1) {
            //Replace the calculator with info about the last element with a new calculator
            calculators.set(currentCalculator, new GoertzleCalculator(targetFrequency, sampleRate));
        }
        
        currentCalculator = (currentCalculator + 1) % k;
        
        for (GoertzleCalculator calc:calculators) {
            calc.addSample(sample);
        }
    }
    
    public double getResult() {
        if (currentCalculator > -1) {
            return calculators.get(currentCalculator).getResult();
        } else {
            return 0;
        }
    }
}
