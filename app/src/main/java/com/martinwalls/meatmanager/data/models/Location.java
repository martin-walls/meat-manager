package com.martinwalls.meatmanager.data.models;

import java.io.Serializable;
import java.util.Comparator;

public class Location implements Serializable {

    /**
     * Stores the different types of {@link Location}.
     */
    public enum LocationType implements Serializable {
        Storage,
        Supplier,
        Destination;

        /**
         * Parses a {@link LocationType} from its name. This should be the same
         * as the String returned by {@link #name()}.
         */
        public static LocationType parseLocationType(String name) {
            for (LocationType type : values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }

        /**
         * Returns an array of the String names of each {@link LocationType},
         * as returned by {@link #name()}.
         */
        public static String[] getLocationTypeStrings() {
            String[] names = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                names[i] = values()[i].name();
            }
            return names;
        }
    }

    private int locationId;
    private String locationName;
    private LocationType locationType;
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String postcode;
    private String country;
    private String phone;
    private String email;

    public Location() {}

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getAddrLine1() {
        return addrLine1;
    }

    public void setAddrLine1(String addrLine1) {
        this.addrLine1 = addrLine1;
    }

    public String getAddrLine2() {
        return addrLine2;
    }

    public void setAddrLine2(String addrLine2) {
        this.addrLine2 = addrLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * {@link Comparator} to sort locations alphabetically.
     */
    public static Comparator<Location> comparatorAlpha() {
        return (location1, location2) ->
                location1.getLocationName().compareTo(location2.getLocationName());
    }
}
