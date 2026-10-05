package com.whitbread.premierinn.api.response.availability;

import com.whitbread.premierinn.api.response.InstanceFactory;

public class FacilityFactory extends InstanceFactory {

    public static Facility createFreeParking() {
        return new AutoValue_Facility(Facility.Codes.FREE_PARKING, "Free parking", "Free Parking");
    }

    public static Facility createChargeableParking() {
        return new AutoValue_Facility(Facility.Codes.CHARGEABLE_PARKING, "Chargeable parking", "Chargeable parking");
    }

    public static Facility createChargeableOnSiteParking() {
        return new AutoValue_Facility(Facility.Codes.CHARGEABLE_ONSITE_PARKING, "Chargeable on-site parking", "Chargeable on-site parking");
    }
}