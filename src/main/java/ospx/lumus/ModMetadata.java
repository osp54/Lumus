package ospx.lumus;

@SuppressWarnings("unused")
public class ModMetadata {
    public String name = "Unknown", description = "Unknown", version = "1.0";

    public ModMetadata() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
