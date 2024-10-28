package ia.model;

public class AzureVmResource extends VmResource{
    public AzureVmResource(String name, String type, String id, String location, String vmSize, String osType, String offer) {
        super(name, type, id, location, vmSize, osType, offer);
    }
}
