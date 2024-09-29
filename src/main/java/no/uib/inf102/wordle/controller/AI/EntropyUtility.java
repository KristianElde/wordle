package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.List;

import no.uib.inf102.wordle.model.word.AnswerType;
import no.uib.inf102.wordle.model.word.CandidateGuess;
import no.uib.inf102.wordle.model.word.WordleAnswer;
import no.uib.inf102.wordle.model.word.WordleCharacter;
import no.uib.inf102.wordle.model.word.WordleWord;

public class EntropyUtility {
    /**
     * Constructs a {@link WordleWord} object based on the provided guess, feedback
     * string, and word length.
     * The feedback string is used to interpret the correctness of each character in
     * the guess, and maps
     * each character's result to an {@link AnswerType}.
     *
     * The feedback string is expected to be composed of characters 'W', 'M', and
     * 'C', where:
     * <ul>
     * <li>'W' indicates that the character is WRONG (i.e., not in the word at
     * all).</li>
     * <li>'M' indicates that the character is MISPLACED (i.e., in the word but in
     * the wrong position).</li>
     * <li>'C' indicates that the character is CORRECT (i.e., in the correct
     * position).</li>
     * </ul>
     *
     * @param guess          The word that was guessed.
     * @param feedbackString A string of feedback characters corresponding to the
     *                       guess, where each character
     *                       represents the result of the guess at that position
     *                       ('W' for wrong, 'M' for misplaced,
     *                       and 'C' for correct).
     * @param wordLength     The length of the word being processed.
     * @return A {@link WordleWord} object representing the guess and its associated
     *         feedback results.
     * @throws IllegalArgumentException If the feedback string contains invalid
     *                                  characters.
     */

    protected static WordleWord wordleWord(String guess, String feedbackString, int wordLength) {
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

    /**
     * Calculates the information gain of a given guess when evaluated against a
     * list of possible answers.
     * The information gain is a measure of how much uncertainty is reduced by
     * making the guess. It is
     * calculated based on the frequencies of different possible outcomes when
     * comparing the guess to the
     * possible answers.
     *
     * @param guess           The guess word for which to calculate the information
     *                        gain.
     * @param possibleAnswers A list of possible answers remaining in the game, used
     *                        to compute the outcome frequencies.
     * @return A {@link CandidateGuess} object containing the guess, its calculated
     *         information gain, and the
     *         frequencies of outcomes based on this guess.
     */

    protected static CandidateGuess informationGain(String guess, List<String> possibleAnswers) {
        HashMap<String, Integer> outcomeFrequencies = getOutcomeFrequencies(guess, possibleAnswers);

        double informationGain = 0d;
        for (String outcome : outcomeFrequencies.keySet()) {
            double p = ((double) outcomeFrequencies.get(outcome)) / possibleAnswers.size();
            informationGain += (p * log2(1 / p));
        }
        return new CandidateGuess(guess, informationGain, outcomeFrequencies);
    }

    private static HashMap<String, Integer> getOutcomeFrequencies(String guess, List<String> possibleAnswers) {
        HashMap<String, Integer> outcomeFrequencies = new HashMap<>();

        for (String answer : possibleAnswers) {
            WordleWord feedback = WordleAnswer.matchWord(guess, answer);
            String feedbackString = feedbackString(feedback);
            outcomeFrequencies.put(feedbackString, outcomeFrequencies.getOrDefault(feedbackString, 0) + 1);
        }

        return outcomeFrequencies;
    }

    private static double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

    private static String feedbackString(WordleWord feedback) {
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
}
