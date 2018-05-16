import java.util.Scanner;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Hangman {
        private URL url;
        private URL resetUrl;
        public Hangman() {
                try {
                        url = new URL("http://upe.42069.fun/2qOo5");
                        resetUrl = new URL("http://upe.42069.fun/2qOo5/reset");
                } catch (MalformedURLException e) {
                        e.printStackTrace();
                        System.exit(1);
                }
        }
        public void guess(char g) {
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
                        while ((inputLine = in.readLine()) != null)
                                System.out.println(inputLine);
                        in.close();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
        public void status() {
                try {
                        URLConnection con = url.openConnection();
                        HttpURLConnection http = (HttpURLConnection) con;
                        http.setDoInput(true);
                        http.setRequestMethod("GET");
                        http.connect();
                        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                                System.out.println(inputLine);
                        in.close();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
        public void reset() {
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
                }
        }
        public static void main(String[] args) {
                Scanner scanner = new Scanner(System.in);
                Hangman game = new Hangman();
                while(true) {
                        System.out.print("> ");
                        String input = scanner.nextLine();
                        switch (input.toLowerCase()) {
                                case "exit":
                                        System.exit(0);
                                break;
                                case "help":
                                        System.out.println("Commands: exit, help, play, status, reset");
                                break;
                                case "guess":
                                        String scanned;
                                        do {
                                                System.out.print("Enter a guess: ");
                                                scanned = scanner.nextLine();
                                        } while (scanned.length()==0);
                                        char guess = scanned.charAt(0);
                                        game.guess(guess);
                                break;
                                case "status":
                                        game.status();
                                break;
                                case "reset":
                                        game.reset();
                                break;
                                default:
                                        System.out.println("Commands: exit, help, play, status, reset");
                        }
                }
        }
}