package app;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.Map;

public class WaardleApp {

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/templates/waardle";
                staticFiles.location = Location.CLASSPATH;
            });
        }).start(7000);

        app.post("/login", ctx -> handleLogin(ctx));
        app.post("/signup", ctx -> handleSignup(ctx));
        app.get("/logout", ctx -> handleLogout(ctx));

        app.get("/me", ctx -> {
            String username = ctx.sessionAttribute("user");
            if (username == null) {
                ctx.status(401).result("Not logged in");
                return;
            }
            ctx.json(new UserResponse(username));
        });

        app.post("/guess", ctx -> {
            String username = ctx.sessionAttribute("user");

            GuessRequest body = ctx.bodyAsClass(GuessRequest.class);
            if (body.guess == null || body.guess.length() != 5) {
                ctx.status(400).result("Guess must be 5 letters");
                return;
            }
            String guess = body.guess.toUpperCase();

            try (var conn = DB.getConnection()) {

                var wordStmt = conn.prepareStatement(
                        "SELECT word_id, word_name FROM words WHERE word_date = CURRENT_DATE"
                );
                var rsWord = wordStmt.executeQuery();
                if (!rsWord.next()) {
                    ctx.status(500).result("Intet dagligt ord fundet for i dag!");
                    return;
                }
                int wordId = rsWord.getInt("word_id");
                String dailyWord = rsWord.getString("word_name").toUpperCase();

                var validStmt = conn.prepareStatement("SELECT 1 FROM words WHERE word_name = ?");
                validStmt.setString(1, guess);
                var rsValid = validStmt.executeQuery();
                if (!rsValid.next()) {
                    ctx.status(400).result("Ord findes ikke i databasen!");
                    return;
                }

                if (username != null) {
                    var userStmt = conn.prepareStatement("SELECT user_id FROM users WHERE user_name = ?");
                    userStmt.setString(1, username);
                    var rsUser = userStmt.executeQuery();
                    if (!rsUser.next()) {
                        ctx.status(500).result("User not found");
                        return;
                    }
                    int userId = rsUser.getInt("user_id");

                    int gameId;
                    int attempts = 0;
                    boolean isWon = false;

                    var gameStmt = conn.prepareStatement(
                            "SELECT game_id, attempts, is_won FROM games WHERE user_id = ? AND word_id = ?"
                    );
                    gameStmt.setInt(1, userId);
                    gameStmt.setInt(2, wordId);
                    var rsGame = gameStmt.executeQuery();

                    if (rsGame.next()) {
                        gameId = rsGame.getInt("game_id");
                        attempts = rsGame.getInt("attempts");
                        isWon = rsGame.getBoolean("is_won");
                    } else {
                        var insertGame = conn.prepareStatement(
                                "INSERT INTO games (user_id, word_id, attempts, is_won) VALUES (?, ?, 0, false)",
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        );
                        insertGame.setInt(1, userId);
                        insertGame.setInt(2, wordId);
                        insertGame.executeUpdate();
                        var keys = insertGame.getGeneratedKeys();
                        keys.next();
                        gameId = keys.getInt(1);
                    }

                    if (isWon) {
                        ctx.json(Map.of(
                                "message", "Spillet er allerede vundet!",
                                "dailyWord", dailyWord,
                                "gameOver", true
                        ));
                        return;
                    }

                    if (attempts >= 6) {
                        ctx.json(Map.of(
                                "message", "Game Over! Du har brugt alle 6 gæt.",
                                "dailyWord", dailyWord,
                                "gameOver", true
                        ));
                        return;
                    }

                    String result = checkGuess(guess, dailyWord);

                    var insertGuess = conn.prepareStatement(
                            "INSERT INTO guesses (game_id, guess_word, attempt_number, result) VALUES (?, ?, ?, ?)"
                    );
                    insertGuess.setInt(1, gameId);
                    insertGuess.setString(2, guess);
                    insertGuess.setInt(3, attempts + 1);
                    insertGuess.setString(4, result);
                    insertGuess.executeUpdate();

                    boolean won = guess.equalsIgnoreCase(dailyWord);
                    var updateGame = conn.prepareStatement(
                            "UPDATE games SET attempts = ?, is_won = ? WHERE game_id = ?"
                    );
                    updateGame.setInt(1, attempts + 1);
                    updateGame.setBoolean(2, won);
                    updateGame.setInt(3, gameId);
                    updateGame.executeUpdate();

                    ctx.json(Map.of(
                            "result", result,
                            "won", won,
                            "gameOver", false,
                            "attempts", attempts + 1
                    ));

                } else {
                    String result = checkGuess(guess, dailyWord);
                    boolean won = guess.equalsIgnoreCase(dailyWord);
                    ctx.json(Map.of(
                            "result", result,
                            "won", won,
                            "gameOver", false,
                            "attempts", 1
                    ));
                }

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server error");
            }
        });
    }

    private static void handleLogin(io.javalin.http.Context ctx) {
        try {
            User loginUser = ctx.bodyAsClass(User.class);
            try (var conn = DB.getConnection()) {
                var stmt = conn.prepareStatement("SELECT password_hash FROM users WHERE user_name = ?");
                stmt.setString(1, loginUser.username);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    String hashedPassword = rs.getString("password_hash");
                    if (BCrypt.checkpw(loginUser.password, hashedPassword)) {
                        ctx.sessionAttribute("user", loginUser.username);
                        ctx.result("Login success");
                    } else {
                        ctx.status(401).result("Forkert login");
                    }
                } else {
                    ctx.status(401).result("Forkert login");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).result("Invalid request");
        }
    }

    private static void handleSignup(io.javalin.http.Context ctx) {
        try {
            User newUser = ctx.bodyAsClass(User.class);
            if (newUser.username == null || newUser.username.isEmpty() ||
                    newUser.password == null || newUser.password.isEmpty()) {
                ctx.status(400).result("Ugyldigt input");
                return;
            }
            try (var conn = DB.getConnection()) {
                var checkStmt = conn.prepareStatement("SELECT 1 FROM users WHERE user_name = ?");
                checkStmt.setString(1, newUser.username);
                var rs = checkStmt.executeQuery();
                if (rs.next()) {
                    ctx.status(400).result("Bruger findes allerede");
                    return;
                }
                String hashedPassword = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
                var insertStmt = conn.prepareStatement(
                        "INSERT INTO users (user_name, password_hash) VALUES (?, ?)"
                );
                insertStmt.setString(1, newUser.username);
                insertStmt.setString(2, hashedPassword);
                insertStmt.executeUpdate();
                ctx.result("Bruger oprettet");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Database error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).result("Invalid request");
        }
    }

    private static void handleLogout(io.javalin.http.Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login.html");
    }

    public static class GuessRequest {
        public String guess;
    }

    private static String checkGuess(String guess, String word) {
        char[] result = new char[5];
        char[] guessChars = guess.toCharArray();
        char[] wordChars = word.toCharArray();

        Arrays.fill(result, 'B');

        for (int i = 0; i < 5; i++) {
            if (guessChars[i] == wordChars[i]) {
                result[i] = 'G';
                wordChars[i] = '*';
                guessChars[i] = '_';
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] == 'G') continue;
            for (int j = 0; j < 5; j++) {
                if (guessChars[i] == wordChars[j]) {
                    result[i] = 'Y';
                    wordChars[j] = '*';
                    break;
                }
            }
        }

        return new String(result);
    }

    public static class User {
        public String username;
        public String password;
    }

    public static class UserResponse {
        public String username;
        public UserResponse(String username) { this.username = username; }
    }
}