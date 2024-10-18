package ia.model;

public class ApiKey {
    private String provider;
    private String key;

    private String subscriptionId;
    public ApiKey(String provider, String key, String subscriptionId) {
        this.provider = provider;
        this.key = key;
        this.subscriptionId = subscriptionId;
    }

    public String getProvider() {
        return provider;
    }

    public String getKey() {
        return key;
    }
    public String getSubscriptionId() { return subscriptionId; }
}
