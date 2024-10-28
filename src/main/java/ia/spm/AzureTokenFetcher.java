package ia.spm;

import ia.model.AzureCredentials;
import ia.model.DatabaseUtility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class AzureTokenFetcher {

    public static String getAccessToken(int userId) throws SQLException {
        // Retrieve credentials from the database
        AzureCredentials credentials = DatabaseUtility.getAzureCredentials(userId);
        if (credentials == null) {
            throw new RuntimeException("No Azure credentials found for user ID: " + userId);
        }

        String clientId = credentials.getClientId();
        String clientSecret = credentials.getClientSecret();
        String tenantId = credentials.getTenantId();

        String tokenEndpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
        String requestBody = "grant_type=client_credentials" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&scope=https%3A%2F%2Fmanagement.azure.com%2F.default";

        try {
            // Create connection
            URL url = new URL(tokenEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the token from the response
                String jsonResponse = response.toString();
                String accessToken = parseAccessTokenFromResponse(jsonResponse);
                System.out.println("AccessToken"+ accessToken);
                return accessToken;
            } else {
                // Handle error response
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                throw new RuntimeException("Failed to get access token. Response code: " + responseCode + ". Error: " + errorResponse);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching access token: " + e.getMessage(), e);
        }
    }

    // Helper method to extract access token from JSON response
    private static String parseAccessTokenFromResponse(String jsonResponse) {
        // Find the access token in the JSON response (basic parsing for simplicity)
        int startIndex = jsonResponse.indexOf("\"access_token\":\"") + 16;
        int endIndex = jsonResponse.indexOf("\"", startIndex);
        return jsonResponse.substring(startIndex, endIndex);
    }

    public static void main(String[] args) throws SQLException {
        // Replace with the actual user ID for testing
        int userId = 1; // Example user ID
        String accessToken = getAccessToken(userId);
        System.out.println("Access Token: " + accessToken);
    }
}
