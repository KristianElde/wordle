package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.List;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

/**
 * This strategy finds the word within the possible words which has the highest
 * expected
 * number of green matches.
 */
public class FrequencyStrategy implements IStrategy {

    private Dictionary dictionary;
    private WordleWordList guesses;

    public FrequencyStrategy(Dictionary dictionary) {
        this.dictionary = dictionary;
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) {
        if (feedback != null) {
            guesses.eliminateWords(feedback);
        }
        HashMap<Character, Integer>[] frequencyHashMaps = createFrequencyMaps(guesses.possibleAnswers());

        String bestAnswer = "";
        int bestFrequency = 0;

        for (String answer : guesses.possibleAnswers()) {
            int frequency = calculateFrequencyScore(frequencyHashMaps, answer);

            if (frequency > bestFrequency) {
                bestFrequency = frequency;
                bestAnswer = answer;
            }
        }

        return bestAnswer;
    }

    private HashMap<Character, Integer>[] createFrequencyMaps(List<String> possibleAnswers) {
        HashMap<Character, Integer>[] frequencyHashMaps = new HashMap[dictionary.WORD_LENGTH];

        for (int i = 0; i < this.dictionary.WORD_LENGTH; i++) {
            HashMap<Character, Integer> map = new HashMap<>();
            for (String word : possibleAnswers)
                map.put(word.charAt(i), map.getOrDefault(word.charAt(i), 0) + 1);
            frequencyHashMaps[i] = map;
        }

        return frequencyHashMaps;
    }

    private int calculateFrequencyScore(HashMap<Character, Integer>[] maps, String answer) {
        int score = 0;
        for (int i = 0; i < this.dictionary.WORD_LENGTH; i++)
            score += maps[i].getOrDefault(answer.charAt(i), 0);

        return score;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
    }
}