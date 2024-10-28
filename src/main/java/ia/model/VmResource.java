package ia.model;

public class VmResource {


    public VmResource(String name, String type, String id, String location, String vmSize, String osType, String offer) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.location = location;
        this.vmSize = vmSize;
        this.osType = osType;
        this.offer = offer;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getVmSize() {
        return vmSize;
    }

    public String getOsType() {
        return osType;
    }

    public String getOffer() {
        return offer;
    }

    private String name;
    private String type;
    private String id;
    private String location;
    private String vmSize;
    private String osType;
    private String offer;

}
