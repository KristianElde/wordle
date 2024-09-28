package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.WordleAnswer;
import no.uib.inf102.wordle.model.word.WordleCharacter;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class EntropyStrategy implements IStrategy {

    private final Dictionary dictionary;
    private WordleWordList guesses;
    private boolean firstGuess = true;
    private String firstGuessWord = null;

    public EntropyStrategy(Dictionary dict) {
        this.dictionary = dict;
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) {
        if (feedback != null)
            guesses.eliminateWords(feedback);

        if (firstGuessWord != null && firstGuess) {
            firstGuess = false;
            return firstGuessWord;
        }

        if (guesses.possibleAnswers().size() < 10)
            return guesses.possibleAnswers().get(0);

        double bestInfoGain = Integer.MAX_VALUE;
        String bestGuess = null;

        for (String guess : dictionary.getGuessWordsList()) {
            double infoGain = informationGain(guess);
            if (infoGain < bestInfoGain) {
                bestGuess = guess;
                bestInfoGain = infoGain;
            }
        }

        if (firstGuessWord == null) {
            firstGuessWord = bestGuess;
            System.out.println(firstGuessWord);
        }

        return bestGuess;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
        firstGuess = true;
    }

    private String feedbackString(WordleWord feedback) {
        String feedbackString = "";
        for (WordleCharacter wordleCharacter : feedback) {
            switch (wordleCharacter.answerType) {
                case WRONG -> feedbackString += 'W';
                case MISPLACED -> feedbackString += 'M';
                case CORRECT -> feedbackString += 'C';
                default -> throw new IllegalArgumentException();
            }
        }
        return feedbackString;
    }

    private double informationGain(String guess) {
        HashMap<String, Integer> outcomeFrequencies = getOutcomeFrequencies(guess);

        double informationGain = 0;
        for (String outcome : outcomeFrequencies.keySet()) {
            double p = ((double) outcomeFrequencies.get(outcome)) / guesses.possibleAnswers().size();
            informationGain -= (p * log2(1 / p));
        }
        return informationGain;
    }

    private HashMap<String, Integer> getOutcomeFrequencies(String guess) {
        HashMap<String, Integer> outcomeFrequencies = new HashMap<>();

        for (String answer : guesses.possibleAnswers()) {
            WordleWord feedback = WordleAnswer.matchWord(guess, answer);
            String feedbackString = feedbackString(feedback);
            outcomeFrequencies.put(feedbackString, outcomeFrequencies.getOrDefault(feedbackString, 0) + 1);
        }
        return outcomeFrequencies;
    }

    private double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

}
