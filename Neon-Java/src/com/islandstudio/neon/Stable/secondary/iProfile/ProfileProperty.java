package com.islandstudio.neon.stable.secondary.iProfile;

public enum ProfileProperty {
    NAME("Name"),
    RANK("Rank"),
    UUID("UUID"),
    IS_MODERATING("isModerating");

    private final String propertyName;

    ProfileProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
