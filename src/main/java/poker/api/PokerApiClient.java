package poker.api;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PokerApiClient {

    private final String apiUrl;

    public PokerApiClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Sends a GraphQL query to the API endpoint.
     *
     * @param query the GraphQL query string.
     * @return the response from the API as a String.
     * @throws Exception if the request fails.
     */
    public String sendQuery(String query) throws Exception {
        return sendRequest("{ \"query\": \"" + query.replace("\n", " ") + "\" }");
    }

    /**
     * Sends a GraphQL mutation to determine the winner of a poker game.
     *
     * @param playerInput the player input string in the required format.
     * @return a map containing the result from the API.
     * @throws Exception if the request fails.
     */
    public Map<String, Object> determineWinner(String playerInput) throws Exception {
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

        String response = sendRequest("{ \"query\": \"" + mutation.replace("\n", " ") + "\" }");

        // Parse the JSON response into a Map (you can use a library like Jackson or Gson for parsing)
        return parseResponse(response);
    }

    /**
     * Sends an HTTP POST request to the API endpoint.
     *
     * @param jsonBody the request body in JSON format.
     * @return the response from the API as a String.
     * @throws Exception if the request fails.
     */
    private String sendRequest(String jsonBody) throws Exception {
        // Open an HTTP connection
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the connection
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the request body
        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        // Read the response
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        } else {
            throw new RuntimeException("API request failed with response code: " + responseCode);
        }
    }

    /**
     * Parses the JSON response into a Map (replace this with an actual parser like Jackson/Gson).
     *
     * @param response the JSON response string.
     * @return a parsed Map containing the response data.
     */
    private Map<String, Object> parseResponse(String response) {
        // Placeholder: Replace with actual JSON parsing logic (e.g., using Jackson or Gson)
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("winners", List.of("player1", "player3"));
        return mockResult;
    }
}
