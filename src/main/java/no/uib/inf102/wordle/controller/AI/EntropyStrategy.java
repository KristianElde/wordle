package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class EntropyStrategy implements IStrategy {

    private final Dictionary dictionary;
    private WordleWordList guesses;
    private String firstGuessWord = null;
    private int n_guesses = 0;

    public EntropyStrategy(Dictionary dict) {
        this.dictionary = dict;
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) {
        if (feedback == null && firstGuessWord != null) {
            n_guesses++;
            return firstGuessWord;
        }

        if (feedback != null)
            guesses.eliminateWords(feedback);

        if (guesses.possibleAnswers().size() == 1) {
            n_guesses++;
            return guesses.possibleAnswers().get(0);
        }

        double bestInfoGain = 0d;
        String bestGuess = null;

        for (String guess : (n_guesses < 2 ? dictionary.getGuessWordsList() : guesses.possibleAnswers())) {
            double infoGain = informationGain(guess);
            if (infoGain < bestInfoGain) {
                bestGuess = guess;
                bestInfoGain = infoGain;
            }
        }

        if (firstGuessWord == null) {
            System.out.println(bestGuess);
            firstGuessWord = bestGuess;
        }

        n_guesses++;
        return bestGuess;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
        n_guesses = 0;
    }

    private double informationGain(String guess) {
        HashMap<String, Integer> outcomeFrequencies = entropyUtility.getOutcomeFrequencies(guess,
                guesses.possibleAnswers());

        double informationGain = 0;
        for (String outcome : outcomeFrequencies.keySet()) {
            double p = ((double) outcomeFrequencies.get(outcome)) / guesses.possibleAnswers().size();
            informationGain -= (p * entropyUtility.log2(1 / p));
        }

        return informationGain;
    }

}
