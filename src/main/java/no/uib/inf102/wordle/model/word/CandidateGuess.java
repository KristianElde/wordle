package no.uib.inf102.wordle.model.word;

import java.util.HashMap;

public class CandidateGuess implements Comparable<CandidateGuess> {

    private String guess;
    private double infoGain;
    private HashMap<String, Integer> outcomeFrequencies;

    public CandidateGuess(String guess, double infoGain, HashMap<String, Integer> outcomeFrequencies) {
        this.guess = guess;
        this.infoGain = infoGain;
        this.outcomeFrequencies = outcomeFrequencies;
    }

    public String getGuess() {
        return guess;
    }

    public double getInfoGain() {
        return infoGain;
    }

    public HashMap<String, Integer> getOutcomeFrequencies() {
        return outcomeFrequencies;
    }

    @Override
    public int compareTo(CandidateGuess other) {
        return Double.compare(this.infoGain, other.infoGain);
    }

    @Override
    public String toString() {
        return String.format("%s: %f", this.guess, this.infoGain);
    }

}
