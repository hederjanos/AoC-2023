package util.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

// INFO Use just in place!
public class PuzzleDownloader {
    public static final int FROM = 2015;
    public static final int DAYS = 25;
    public static final String AOC_BASE_URL = "https://adventofcode.com/";
    public static final String SESSION = "session";
    private final String sessionId;
    private final HttpClient httpClient;

    public static void main(String[] args) {
        String sessionId = System.getenv("AOC_SESSION_ID");
        if (sessionId == null || sessionId.isBlank()) {
            error("AOC_SESSION_ID does not exist!");
        }

        if (args.length != 2) {
            error("");
        }

        String yearGroup = getYearPattern();
        String dayGroup = "[1-9]|1[0-9]|2[0-5]";

        Integer year = null;
        Integer day = null;

        Pattern pattern = Pattern.compile("^" + "(" + yearGroup + ")" + "\\s" + "(" + dayGroup + ")" + "$");
        Matcher matcher = pattern.matcher(args[0] + " " + args[1]);
        while (matcher.find()) {
            year = Integer.parseInt(matcher.group(1));
            day = Integer.parseInt(matcher.group(2));
        }
        if (year != null) {
            PuzzleDownloader puzzleDownloader = new PuzzleDownloader(sessionId);
            puzzleDownloader.refreshInput(year, day);
        } else {
            pattern = Pattern.compile("^" + yearGroup + "\\s" + "all" + "$");
            matcher = pattern.matcher(args[0] + " " + args[1]);
            while (matcher.find()) {
                year = Integer.parseInt(matcher.group(1));
            }
            if (year != null) {
                PuzzleDownloader puzzleDownloader = new PuzzleDownloader(sessionId);
                puzzleDownloader.refreshInputs(year);
            }
        }
        if (year == null) {
            error("");
        }
    }

    private static String getYearPattern() {
        StringBuilder years = new StringBuilder();
        for (int i = FROM; i <= Year.from(Instant.now().atZone(ZoneId.systemDefault())).getValue(); i++) {
            if (i != FROM) {
                years.append("|");
            }
            years.append(i);
        }
        return years.toString();
    }

    private static void error(String message) {
        if (message != null && !message.isBlank()) {
            System.err.println(message);
        } else {
            System.err.println("Argument section must match the pattern (YYYY d|dd, year must be between 2015 and current year, days must be between 1 and 25) OR 'all' expression!!");
        }
        System.exit(1);
    }

    public PuzzleDownloader(String sessionId) {
        this.sessionId = sessionId;
        this.httpClient = createHttpClient();
        System.out.println("Downloader initialized.");
    }

    private HttpClient createHttpClient() {
        try {
            HttpCookie sessionCookie = new HttpCookie(SESSION, sessionId);
            sessionCookie.setPath("/");
            sessionCookie.setVersion(0);
            CookieManager cookieManager = new CookieManager();
            cookieManager.getCookieStore().add(new URI(AOC_BASE_URL), sessionCookie);
            return HttpClient.newBuilder().cookieHandler(cookieManager).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshInput(int year, int day) {
        if (isBeforeNow(day)) {
            downloadInput(AOC_BASE_URL + year + "/day/" + day + "/input", "day" + day + ".txt");
        } else {
            error("Too early day!");
        }
    }

    private boolean isBeforeNow(int day) {
        return Instant.parse(String.format("2023-12-%02dT05:00:00Z", day)).isBefore(Instant.now());
    }

    public void refreshInputs(int year) {
        IntStream.rangeClosed(1, DAYS)
                .filter(this::isBeforeNow)
                .forEach(i -> downloadInput(AOC_BASE_URL + year + "/day/" + i + "/input", "day" + i + ".txt"));
    }

    private void downloadInput(String url, String fileName) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("session", sessionId)
                    .GET()
                    .uri(new URI(url))
                    .build();
            String input = httpClient.send(httpRequest, BodyHandlers.ofString()).body();
            Path path = Path.of("src/main/resources", fileName);
            writeFile(path, input);
            System.out.println(String.format("Input file was successfully downloaded from: %s and saved as : %s.", url, fileName));
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path path, String input) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            bufferedWriter.write(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
