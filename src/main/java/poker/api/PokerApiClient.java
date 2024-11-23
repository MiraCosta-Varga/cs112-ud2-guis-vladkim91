package poker.api;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PokerApiClient {

    private final String apiUrl;

    public PokerApiClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    private String sendRequest(String jsonBody) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == 200) {
            try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println("Response Payload: " + response);
                return response;
            }
        } else {
            try (Scanner scanner = new Scanner(connection.getErrorStream(), StandardCharsets.UTF_8)) {
                String errorResponse = scanner.useDelimiter("\\A").next();
                System.err.println("Error Response Payload: " + errorResponse);
                throw new RuntimeException("API request failed with response code: " + responseCode + " and message: " + errorResponse);
            }
        }
    }

    public Map<String, Object> determineWinner(String playerInput) throws Exception {
        playerInput = playerInput.replace("\"", "\\\"");

        String mutation = """
                mutation {
                    calcWinner(input: { playerInput: "%s" }) {
                        winners {
                            id
                            hand
                        }
                        players {
                            id
                            hand
                            handStrength
                        }
                    }
                }
                """.formatted(playerInput);

        String payload = String.format("{ \"query\": \"%s\" }", mutation.replace("\n", " ").replace("\"", "\\\""));
        System.out.println("Payload Sent to API: " + payload);

        String response = sendRequest(payload);

        return parseResponse(response);
    }
    private Map<String, Object> parseResponse(String response) {
        Map<String, Object> result = new HashMap<>();

        try (JsonReader jsonReader = Json.createReader(new StringReader(response))) {
            JsonObject responseJson = jsonReader.readObject();

            // Navigate to "data -> calcWinner -> winners"
            JsonObject data = responseJson.getJsonObject("data");
            JsonArray calcWinner = data.getJsonArray("calcWinner");

            if (calcWinner != null && !calcWinner.isEmpty()) {
                JsonObject calcWinnerObject = calcWinner.getJsonObject(0);
                JsonArray winners = calcWinnerObject.getJsonArray("winners");

                // Extract winner IDs
                List<String> winnerIds = new ArrayList<>();
                for (JsonObject winner : winners.getValuesAs(JsonObject.class)) {
                    winnerIds.add(winner.getString("id"));
                }
                result.put("winners", winnerIds);

                // Extract player details
                JsonArray players = calcWinnerObject.getJsonArray("players");
                List<Map<String, Object>> playerDetails = new ArrayList<>();
                for (JsonObject player : players.getValuesAs(JsonObject.class)) {
                    Map<String, Object> playerDetail = new HashMap<>();
                    playerDetail.put("id", player.getString("id"));
                    playerDetail.put("hand", player.getJsonArray("hand")
                            .getValuesAs(JsonString.class)
                            .stream()
                            .map(JsonString::getString)
                            .toList());
                    playerDetail.put("handStrength", player.getInt("handStrength"));
                    playerDetails.add(playerDetail);
                }
                result.put("players", playerDetails);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
        }

        return result;
    }

}
