package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.CandidateGuess;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class EntropyStrategy2 implements IStrategy {

    private final Dictionary dictionary;
    private WordleWordList guesses;
    private String firstGuessWord;
    private int n_guesses = 0;

    public EntropyStrategy2(Dictionary dict) {
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
        PriorityQueue<CandidateGuess> bestGuesses = new PriorityQueue<>();

        for (String guess : (n_guesses < 2 ? dictionary.getGuessWordsList() : guesses.possibleAnswers())) {
            CandidateGuess candidateGuess = informationGain(guess, guesses.possibleAnswers());
            if (bestGuesses.size() < 10)
                bestGuesses.add(candidateGuess);
            else if (candidateGuess.getInfoGain() > bestGuesses.peek().getInfoGain()) {
                bestGuesses.poll();
                bestGuesses.add(candidateGuess);
            }
        }
        double bestInfoGain = Double.MIN_VALUE;
        String bestGuess = null;

        for (CandidateGuess candidateGuess : bestGuesses) {
            Double infoGain = candidateGuess.getInfoGain();
            for (String outcome : candidateGuess.getOutcomeFrequencies().keySet()) {
                WordleWordList wordList = new WordleWordList(dictionary,
                        guesses.possibleAnswers());
                WordleWord word = entropyUtility.wordleWord(candidateGuess.getGuess(), outcome, dictionary.WORD_LENGTH);
                wordList.eliminateWords(word);
                double p = ((double) candidateGuess.getOutcomeFrequencies().get(outcome))
                        / guesses.possibleAnswers().size();

                for (String guess : wordList.possibleAnswers())
                    infoGain += p * (informationGain(guess, wordList.possibleAnswers()).getInfoGain());

            }
            if (infoGain > bestInfoGain) {
                bestGuess = candidateGuess.getGuess();
                bestInfoGain = infoGain;
            }
        }

        if (firstGuessWord == null) {
            firstGuessWord = bestGuess;
            System.out.println(firstGuessWord);
        }

        n_guesses++;
        return bestGuess;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
        n_guesses = 0;
    }

    private CandidateGuess informationGain(String guess, List<String> possibleAnswers) {
        HashMap<String, Integer> outcomeFrequencies = entropyUtility.getOutcomeFrequencies(guess, possibleAnswers);

        double informationGain = 0;
        for (String outcome : outcomeFrequencies.keySet()) {
            double p = ((double) outcomeFrequencies.get(outcome)) / guesses.possibleAnswers().size();
            informationGain += (p * entropyUtility.log2(1 / p));
        }
        return new CandidateGuess(guess, informationGain, outcomeFrequencies);
    }

}
