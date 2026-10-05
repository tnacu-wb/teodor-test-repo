package com.whitbread.premierinn.api.response.availability;

import com.whitbread.premierinn.api.response.InstanceFactory;


public class TripAdvisorFactory extends InstanceFactory {

    public static TripAdvisor create() {
        return new AutoValue_TripAdvisor("https://www.tripadvisor.co.uk/img/cdsi/img2/ratings/traveler/4.0-13694-4.png", 999);
    }

    public static TripAdvisor createEmpty() {
        return new AutoValue_TripAdvisor(null, null);
    }

    public static TripAdvisor createImageOnly() {
        return new AutoValue_TripAdvisor("https://www.tripadvisor.co.uk/img/cdsi/img2/ratings/traveler/4.0-13694-4.png", null);
    }

    public static TripAdvisor createNumberOnly() {
        return new AutoValue_TripAdvisor(null, 999);
    }

}