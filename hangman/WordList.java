package hangman;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordList {
    private Hashtable<Integer, List<String>> words;
    private boolean verbose;

    public WordList() {
        words = new Hashtable<Integer, List<String>>();
        verbose = false;
        File file = new File("words.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
                if (words.get(text.length()) == null) {
                    words.put(text.length(), new ArrayList<String>());
                }
                words.get(text.length()).add(text);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private List<String> findMatches(String unknown, List<Character> guessed) {
        if (unknown == null || words.get(unknown.length()) == null)
            return new ArrayList<String>();
        if (guessed == null || guessed.size() == 0)
            return words.get(unknown.length());
        String replacement = "[^";
        for (Character character : guessed)
            replacement += character;
        replacement += "]";
        String regex = unknown.replace("_", replacement);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return words.get(unknown.length()).stream().filter(word -> pattern.matcher(word).matches()).collect(Collectors.toList());
    }

    private String removeGuessed(String word, List<Character> guessed) {
        if (word == null || guessed == null || guessed.size() == 0)
            return word;
        String regex = "[";
        for (Character character : guessed)
            regex += character;
        regex += "]";
        return word.replaceAll(regex, "").toLowerCase();
    }

    public void setVerbose(boolean val) {
        verbose = val;
    }

    public char mostLikelyGuess(List<String> unknowns, List<Character> guessed) {
        Hashtable<Character, Integer> counts = new Hashtable<Character, Integer>();
        String alphabet = "etaoinsrhdlucmfywgpbvkxqjz";
        for (int i = 0; i < alphabet.length(); i++)
            counts.put(alphabet.charAt(i), 0);
        if (unknowns != null)
            unknowns.forEach(unknown -> {
                if (verbose)
                    System.out.print("Unknown : " + unknown);
                List<String> matches = findMatches(unknown, guessed);
                matches.forEach(match -> {
                    if (verbose)
                        System.out.print(" " + match);
                    String removed = removeGuessed(match.replaceAll("[^" + alphabet + "]", ""), guessed);
                    int toAdd = 1;
                    switch (matches.size()) {
                        case 1:
                            toAdd *= 200;
                            break;
                        case 2:
                            toAdd *= 100;
                            break;
                        case 3:
                            toAdd *= 50;
                            break;
                        case 4:
                            toAdd *= 25;
                            break;
                        case 5:
                            toAdd *= 12;
                            break;
                    }
                    for (int i = 0; i < removed.length(); i++)
                        counts.put(removed.charAt(i), counts.get(removed.charAt(i)) + toAdd);
                });
                if (verbose)
                    System.out.println();
            });
        int maxCount = -1;
        char mostFreq = ' ';
        for (int i = 0; i < alphabet.length(); i++)
            if (counts.get(alphabet.charAt(i)) > maxCount && (guessed == null || !guessed.contains(alphabet.charAt(i)))) {
                maxCount = counts.get(alphabet.charAt(i));
                mostFreq = alphabet.charAt(i);
            }
        return mostFreq;
    }
}
