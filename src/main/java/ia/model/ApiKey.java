package ia.model;

public class ApiKey {
    private String provider;
    private String key;

    public ApiKey(String provider, String key) {
        this.provider = provider;
        this.key = key;
    }

    public String getProvider() {
        return provider;
    }

    public String getKey() {
        return key;
    }
}
