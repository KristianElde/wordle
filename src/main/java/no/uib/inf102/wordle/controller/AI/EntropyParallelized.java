package no.uib.inf102.wordle.controller.AI;

import java.util.ArrayList;
import java.util.List;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.CandidateGuess;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class EntropyParallelized implements IStrategy {

    private final Dictionary dictionary;
    private WordleWordList guesses;
    private String firstGuessWord = null;
    private int nGuesses = 0;

    private final int N_THREADS = 1;
    private volatile ArrayList<CandidateGuess> bestGuesses;

    public EntropyParallelized(Dictionary dict) {
        this.dictionary = dict;
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) {
        if (feedback == null && firstGuessWord != null) {
            nGuesses++;
            return firstGuessWord;
        }

        if (feedback != null)
            guesses.eliminateWords(feedback);

        if (guesses.possibleAnswers().size() == 1) {
            nGuesses++;
            return guesses.possibleAnswers().get(0);
        }

        List<String> guessWordsList = dictionary.getGuessWordsList();
        int totalGuesses = guessWordsList.size();
        int chunkSize = totalGuesses / N_THREADS;

        bestGuesses = new ArrayList<>();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < N_THREADS; i++) {
            int start = chunkSize * i;
            int stop = i == N_THREADS - 1 ? totalGuesses : chunkSize * i + 1;
            threads.add(new Thread(
                    new InformationGainTask(guessWordsList.subList(start, stop), guesses.possibleAnswers())));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CandidateGuess bestGuess = new CandidateGuess("", Double.MIN_VALUE, null);
        for (CandidateGuess guess : bestGuesses) {
            if (guess.getInfoGain() > bestGuess.getInfoGain())
                bestGuess = guess;
        }

        if (firstGuessWord == null) {
            System.out.println(bestGuess);
            firstGuessWord = bestGuess.getGuess();
        }

        nGuesses++;
        return bestGuess.getGuess();
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
        nGuesses = 0;
    }

    private class InformationGainTask implements Runnable {
        private final List<String> guesses;
        private final List<String> possibleAnswers;

        InformationGainTask(List<String> guesses, List<String> possibleAnswers) {
            this.guesses = guesses;
            this.possibleAnswers = possibleAnswers;
        }

        @Override
        public void run() {
            CandidateGuess localBestGuess = new CandidateGuess("", Double.MIN_VALUE, null);

            for (String guess : guesses) {
                CandidateGuess candidateGuess = entropyUtilityy.informationGain(guess, possibleAnswers);
                if (candidateGuess.getInfoGain() > localBestGuess.getInfoGain()) {
                    localBestGuess = candidateGuess;
                }
            }

            updateBestGuess(localBestGuess);
        }

        private synchronized void updateBestGuess(CandidateGuess candidateGuess) {
            bestGuesses.add(candidateGuess);
        }
    }

}
