package ia.controller;

import ia.model.ApiKey;
import ia.spm.AzureTokenFetcher;
import ia.spm.EncryptionUtil;
import ia.spm.MysqlConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApiFetcher {
    public List<String> fetchSecurityRecommendations2(int userId) {
        List<String> recommendations = new ArrayList<>();
        List<ApiKey> apiKeys = getApiKeys(userId); // Get all API keys for the user

        // Fetch security recommendations for each API key
        for (ApiKey apiKey : apiKeys) {
            String provider = apiKey.getProvider();
            String subscriptionId = "97df98f8-3422-4b7c-803d-59226b89c54d";

            // Fetch security recommendations for Azure using HTTP request
            if ("Azure".equalsIgnoreCase(provider)) {
                String url = "https://api.securitycenter.microsoft.com/api/machines/" + subscriptionId +
                        "/recommendations";

                // Fetch the token using AzureTokenFetcher
                String token;
                try {
                    token = AzureTokenFetcher.getAccessToken(userId);
                } catch (RuntimeException e) {
                    recommendations.add("Error fetching Azure access token: " + e.getMessage());
                    continue;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                try {
                    // Make an HTTP GET request to fetch recommendations
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestProperty("Content-Type", "application/json");

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

                        // Parse the JSON response

                    } else {
                        // Handle errors
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                        recommendations.add("Failed to fetch recommendations for " + provider + ". Response code: " + responseCode + ". Error: " + errorResponse.toString());
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    recommendations.add("Error fetching recommendations: " + e.getMessage());
                }
            }
        }
        return recommendations;
    }
    private List<ApiKey> getApiKeys(int userId) {
        List<ApiKey> apiKeys = new ArrayList<>();
        try (Connection connection = MysqlConnection.openConnection()) {
            String sql = "SELECT api_provider, api_key, subscription_id FROM api_keys WHERE user_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String provider = rs.getString("api_provider");
                        String encryptedKey = rs.getString("api_key");
                        String subscriptionId = rs.getString("subscription_id");
                        String secretKey = "1234123412341234"; // Secret key used for encryption
                        String decryptedKey = EncryptionUtil.decrypt(encryptedKey, secretKey);
                        apiKeys.add(new ApiKey(provider, decryptedKey, subscriptionId));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return apiKeys;
    }
}
