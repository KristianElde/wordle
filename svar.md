# Runtime Analysis

For each method of the tasks give a runtime analysis in Big-O notation and a description of why it has this runtime.

**If you have implemented new methods not listed you must add these as well, e.g. any helper methods. You need to show how you analyzed any methods used by the methods listed below.**

The runtime should be expressed using these three parameters:

- `n` - number of words in the list allWords
- `m` - number of words in the list possibleWords
- `k` - number of letters in the wordleWords

## Task 1 - matchWord

- `WordleAnswer::matchWord`: O(k)
  - Looper gjennom `k` bokstaver i et wordleWord 3 ganger. Først en gang for å telle antallet av hver bokstav som er representert i ett gjett. Så en gang for å se etter korrekt plasserte bokstaver i gjettet. Til slutt looper metoden gjennom ordets k bokstaver en siste gang for å avgjøre om bokstavene er feilplasserte eller feil. Dette gir O(k \* 3) som blir O(k) ettersom at konstanter blir ubetydelig når k vokser.

## Task 2 - EliminateStrategy

- `WordleWordList::eliminateWords`: O(m\*k)
  - Går gjennom listen av alle `m` possibleAnswers og gjør `WordleWord::isPossibleWord` som kaller `WordleAnswer.matchWord()` for hvert mulige answer. Kjøretiden for `WordleAnswer::matchWord` er O(k) som forklart over.

## Task 3 - FrequencyStrategy

- `FrequencyStrategy::makeGuess`: O(k\*m)
  - Bruker metoden `createFrequencyMaps()` først for å lage et HashMap for hver posisjon av et ord, som teller opp frekvensen av hver bokstav i den gitte posisjon. Denne metoden har kjøretid O(k\*m) fordi den lager `k` HashMaps, hvorav hvert HashMap går gjennom alle `m`, possibleAnswers. Etter dette regnes det ut en _frequency score_ for hvert mulige svar. Metoden går gjennom `guesses.possibleAnswers()` som har `m` elementer, og gjør `calculateFrequencyScore()` for hvert possible answer. Denne metoden har kjøretid O(k), fordi den går gjennom hver bokstav i ordet og gjør et lookup i frequency-HashMapet for riktig posisjon. Dette gir kjøretiden O(m\*k) for det å regne ut _frequency score_ for alle mulige svar. Dette gir totalt for metoden kjøretiden O(k\*m + m\*k) => O(2\*k\*m). Siden konstanter blir ubetydelige når `k` og `m` vokser blir kjøretiden O(k\*m).

# Task 4 - Make your own (better) AI

For this task you do not need to give a runtime analysis.
Instead, you must explain your code. What was your idea for getting a better result? What is your strategy?

_Write your answer here_
