package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.List;

import no.uib.inf102.wordle.model.word.AnswerType;
import no.uib.inf102.wordle.model.word.WordleAnswer;
import no.uib.inf102.wordle.model.word.WordleCharacter;
import no.uib.inf102.wordle.model.word.WordleWord;

public class entropyUtility {

    static String feedbackString(WordleWord feedback) {
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

    static WordleWord wordleWord(String guess, String feedbackString, int wordLength) {
        AnswerType[] answerTypes = new AnswerType[wordLength];
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

    static HashMap<String, Integer> getOutcomeFrequencies(String guess, List<String> possibleAnswers) {
        HashMap<String, Integer> outcomeFrequencies = new HashMap<>();

        for (String answer : possibleAnswers) {
            WordleWord feedback = WordleAnswer.matchWord(guess, answer);
            String feedbackString = entropyUtility.feedbackString(feedback);
            outcomeFrequencies.put(feedbackString, outcomeFrequencies.getOrDefault(feedbackString, 0) + 1);
        }
        return outcomeFrequencies;
    }

    static double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

}
