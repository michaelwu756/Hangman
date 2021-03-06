package hangman;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hangman {
    private URL url;
    private URL resetUrl;
    private WordList wordList;
    private JSONObject resp;
    private Mode mode;
    private boolean logging;

    public Hangman() {
        wordList = new WordList();
        mode = Mode.NORMAL;
        logging = false;
        try {
            url = new URL("http://upe.42069.fun/Oh2hl");
            resetUrl = new URL("http://upe.42069.fun/Oh2hl/reset");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hangman game = new Hangman();
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            switch (input.toLowerCase()) {
                case "exit":
                    System.exit(0);
                    break;
                case "help":
                    System.out.println("Commands:");
                    System.out.println("\texit: quit the application");
                    System.out.println("\thelp: display this help prompt");
                    System.out.println("\tplay: plays hangman for a specified number of games");
                    System.out.println("\treset: reset the number of games on the server");
                    System.out.println("\tverbose: show details of how the application decides guesses");
                    System.out.println("\tquiet: turn verbose mode off");
                    System.out.println("\tlogging: store results of game into a file named dictionary.txt");
                    System.out.println("\tno-logging: turns logging off");
                    System.out.println("\tcheat-mode: restarts game with one guess remaining");
                    System.out.println("\tlose-mode: tries to lose the game. used for faster harvesting of data");
                    System.out.println("\tnormal-mode: return to default play mode");
                    break;
                case "play":
                    int num = 0;
                    do {
                        System.out.print("Enter the number of games to play: ");
                        while (!scanner.hasNextInt()) {
                            scanner.nextLine();
                            System.out.print("Enter the number of games to play: ");
                        }
                        num = scanner.nextInt();
                    } while (num < 1);
                    scanner.nextLine();
                    game.play(num);
                    break;
                case "reset":
                    game.reset();
                    break;
                case "verbose":
                    game.setVerbose(true);
                    break;
                case "quiet":
                    game.setVerbose(false);
                    break;
                case "logging":
                    game.setLogging(true);
                    break;
                case "no-logging":
                    game.setLogging(false);
                    break;
                case "cheat-mode":
                    game.setMode(Mode.CHEAT);
                    break;
                case "lose-mode":
                    game.setMode(Mode.LOSE);
                    break;
                case "normal-mode":
                    game.setMode(Mode.NORMAL);
                    break;
                default:
                    System.out.println("Commands: exit, help, play, reset, verbose, quiet, logging, no-logging, cheat-mode, lose-mode, normal-mode");
            }
        }
    }

    public void play(int num) {
        for (int i = 1; i <= num; i++) {
            System.out.println("Game Number : " + i);
            List<Character> guessed = new ArrayList<Character>();
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!newGame());
            List<String> unknowns = getUnknowns();
            do {
                char nextGuess = wordList.mostLikelyGuess(unknowns, guessed);
                if (mode == Mode.LOSE) {
                    String alphabet = "zjqxkvbpgwyfmculdhrsnioate";
                    for (char c : alphabet.toCharArray())
                        if(!guessed.contains(c)) {
                            nextGuess = c;
                            break;
                        }
                }
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!guess(nextGuess));
                guessed.add(nextGuess);
                unknowns = getUnknowns();
            } while (resp.getString("status").equals("ALIVE") && (mode != Mode.CHEAT || resp.getInt("remaining_guesses") > 1));
            if (mode == Mode.CHEAT && resp.getString("status").equals("ALIVE"))
                i--;
            else if (logging) {
                try {
                    String result = resp.getString("lyrics").replaceAll("[^a-zA-Z_ '-]", "").trim().replaceAll(" +", " ").replaceAll(" ", "\n");
                    result += "\n";
                    Path path = Paths.get("dictionary.txt");
                    if (!Files.exists(path))
                        Files.createFile(path);
                    Files.write(path, result.getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean reset() {
        try {
            URLConnection con = resetUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("POST");
            byte[] out = ("{\"email\":\"michaelwu756@gmail.com\"}").getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(out);
            os.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setVerbose(boolean val) {
        wordList.setVerbose(val);
    }

    public void setLogging(boolean val) {
        logging = val;
    }

    public void setMode(Mode m) {
        mode = m;
    }

    private List<String> getUnknowns() {
        List<String> res = new ArrayList<String>();
        String state = resp.getString("state");
        state = state.replaceAll("[^a-zA-Z_ '-]", "");
        String[] splitState = state.split(" ");
        for (String unknown : splitState) {
            res.add(unknown);
        }
        return res;
    }

    private boolean guess(char g) {
        try {
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("POST");
            byte[] out = ("{\"guess\":\"" + g + "\"}").getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(out);
            os.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Guessed: " + g);
            System.out.println(response.toString());
            resp = new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean newGame() {
        try {
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setDoInput(true);
            http.setRequestMethod("GET");
            http.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            resp = new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private enum Mode {
        NORMAL, CHEAT, LOSE
    }
}
