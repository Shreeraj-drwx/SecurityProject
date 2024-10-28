package ia.model;
public class AzureCredentials {
    private String clientId;
    private String clientSecret;
    private String tenantId;

    // Constructor
    public AzureCredentials(String clientId, String clientSecret, String tenantId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tenantId = tenantId;
    }

    // Getters and Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "AzureCredentials{" +
                "clientId='" + clientId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
        // Exclude clientSecret for security reasons
    }
}
