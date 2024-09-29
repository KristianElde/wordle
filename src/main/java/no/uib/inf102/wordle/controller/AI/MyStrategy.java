package no.uib.inf102.wordle.controller.AI;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.CandidateGuess;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class MyStrategy implements IStrategy {

    private final Dictionary dictionary;
    private WordleWordList guesses;
    private String firstGuessWord = null;

    public MyStrategy(Dictionary dict) {
        this.dictionary = dict;
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) {
        if (feedback == null && firstGuessWord != null) {
            return firstGuessWord;
        }

        if (feedback != null)
            guesses.eliminateWords(feedback);

        if (guesses.possibleAnswers().size() == 1) {
            return guesses.possibleAnswers().get(0);
        }

        double bestInfoGain = Double.MIN_VALUE;
        String bestGuess = null;

        for (String guess : dictionary.getGuessWordsList()) {
            CandidateGuess candidateGuess = EntropyUtility.informationGain(guess, guesses.possibleAnswers());
            if (candidateGuess.getInfoGain() > bestInfoGain) {
                bestGuess = candidateGuess.getGuess();
                bestInfoGain = candidateGuess.getInfoGain();
            }
        }

        if (firstGuessWord == null) {
            System.out.println(bestGuess);
            firstGuessWord = bestGuess;
        }

        return bestGuess;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
    }

}
