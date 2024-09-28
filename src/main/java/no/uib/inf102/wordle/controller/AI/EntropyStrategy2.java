package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.AnswerType;
import no.uib.inf102.wordle.model.word.CandidateGuess;
import no.uib.inf102.wordle.model.word.WordleAnswer;
import no.uib.inf102.wordle.model.word.WordleCharacter;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class EntropyStrategy2 implements IStrategy {

    private final Dictionary dictionary;
    private WordleWordList guesses;
    private boolean firstGuess = true;
    private String firstGuessWord;

    public EntropyStrategy2(Dictionary dict) {
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

        if (guesses.possibleAnswers().size() == 1)
            return guesses.possibleAnswers().get(0);

        PriorityQueue<CandidateGuess> bestGuesses = new PriorityQueue<>();

        for (String guess : dictionary.getGuessWordsList()) {
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
                WordleWord word = wordleWord(candidateGuess.getGuess(), outcome);
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

    private WordleWord wordleWord(String guess, String feedbackString) {
        AnswerType[] answerTypes = new AnswerType[dictionary.WORD_LENGTH];
        for (int i = 0; i < answerTypes.length; i++) {
            switch (feedbackString.charAt(i)) {
                case 'W' -> answerTypes[i] = AnswerType.WRONG;
                case 'M' -> answerTypes[i] = AnswerType.MISPLACED;
                case 'C' -> answerTypes[i] = AnswerType.CORRECT;
                default -> throw new IllegalArgumentException(feedbackString);
            }
        }

        return new WordleWord(guess, answerTypes);
    }

    private CandidateGuess informationGain(String guess, List<String> possibleAnswers) {
        HashMap<String, Integer> outcomeFrequencies = getOutcomeFrequencies(guess, possibleAnswers);

        double informationGain = 0;
        for (String outcome : outcomeFrequencies.keySet()) {
            double p = ((double) outcomeFrequencies.get(outcome)) / guesses.possibleAnswers().size();
            informationGain += (p * log2(1 / p));
        }
        return new CandidateGuess(guess, informationGain, outcomeFrequencies);
    }

    private HashMap<String, Integer> getOutcomeFrequencies(String guess, List<String> possibleAnswers) {
        HashMap<String, Integer> outcomeFrequencies = new HashMap<>();

        for (String answer : possibleAnswers) {
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
