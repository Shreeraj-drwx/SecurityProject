package ia.model;
import ia.spm.MysqlConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtility {

    public static void saveAzureCredentials(int userId, String clientId, String clientSecret, String tenantId) throws SQLException {
        String query = "INSERT INTO azure_cloud_credentials (user_id, client_id, client_secret, tenant_id) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE client_id = ?, client_secret = ?, tenant_id = ?";
        try (Connection connection = MysqlConnection.openConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, clientId);
            stmt.setString(3, clientSecret);
            stmt.setString(4, tenantId);
            stmt.setString(5, clientId);
            stmt.setString(6, clientSecret);
            stmt.setString(7, tenantId);
            stmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Database driver not found.", e);
        }
    }

    public static AzureCredentials getAzureCredentials(int userId) throws SQLException {
        String query = "SELECT client_id, client_secret, tenant_id FROM azure_cloud_credentials WHERE user_id = ?";
        try (Connection connection = MysqlConnection.openConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AzureCredentials(
                            rs.getString("client_id"),
                            rs.getString("client_secret"),
                            rs.getString("tenant_id")
                    );
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Database driver not found.", e);
        }
        return null;
    }
    public static void saveAzureVmResource(AzureVmResource vmResource, int userId) throws SQLException {
        String query = "INSERT INTO vm_resources (id, user_id, name, type, location, vm_size, os_type, offer) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = ?, type = ?, location = ?, vm_size = ?, os_type = ?, offer = ?";
        try (Connection connection = MysqlConnection.openConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, vmResource.getId());
            stmt.setInt(2, userId);
            stmt.setString(3, vmResource.getName());
            stmt.setString(4, vmResource.getType());
            stmt.setString(5, vmResource.getLocation());
            stmt.setString(6, vmResource.getVmSize());
            stmt.setString(7, vmResource.getOsType());
            stmt.setString(8, vmResource.getOffer());

            stmt.setString(9, vmResource.getName());
            stmt.setString(10, vmResource.getType());
            stmt.setString(11, vmResource.getLocation());
            stmt.setString(12, vmResource.getVmSize());
            stmt.setString(13, vmResource.getOsType());
            stmt.setString(14, vmResource.getOffer());

            stmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Database driver not found.", e);
        }
    }

    public static List<AzureVmResource> getAzureVmResources(int userId) throws SQLException {
        List<AzureVmResource> vmResources = new ArrayList<>();
        String query = "SELECT id, name, type, location, vm_size, os_type, offer FROM vm_resources WHERE user_id = ?";
        try (Connection connection = MysqlConnection.openConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AzureVmResource vm = new AzureVmResource(
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("id"),
                            rs.getString("location"),
                            rs.getString("vm_size"),
                            rs.getString("os_type"),
                            rs.getString("offer")
                    );
                    vmResources.add(vm);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Database driver not found.", e);
        }
        return vmResources;
    }
}

