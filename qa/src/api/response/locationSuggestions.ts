import { ManagedPlaceSuggestion } from './managedPlaceSuggestion';
import { PlaceSuggestion } from './placeSuggestion';
import { PropertySuggestion } from './propertySuggestion';

/**
 * The Location field suggestions list from API response
 */
export class LocationSuggestions {
  [key: string]: unknown;
  managedPlaces: ManagedPlaceSuggestion[] = [];
  places: PlaceSuggestion[] = [];
  properties: PropertySuggestion[] = [];

  /**
   * LocationSuggestions constructor
   * @param data object data
   * @param data.locationSuggestionsApiResponse response from API
   */
  constructor(
    data: {
      locationSuggestionsApiResponse?: {
        managedPlaces?: Array<Record<string, unknown>>;
        places?: Array<Record<string, unknown>>;
        properties?: Array<Record<string, unknown>>;
      };
    } = {},
  ) {
    const locationSuggestionsApiResponse = data.locationSuggestionsApiResponse ?? {};
    this.managedPlaces = (locationSuggestionsApiResponse.managedPlaces ?? []).map(
      (managedPlace) => new ManagedPlaceSuggestion({ managedPlaceSuggestionApiResponse: managedPlace }),
    );
    this.places = (locationSuggestionsApiResponse.places ?? []).map(
      (place) => new PlaceSuggestion({ placeSuggestionApiResponse: place }),
    );
    this.properties = (locationSuggestionsApiResponse.properties ?? []).map(
      (property) => new PropertySuggestion({ propertySuggestionApiResponse: property }),
    );
  }

  static fromResponse(data: {
    locationSuggestionsApiResponse?: {
      managedPlaces?: Array<Record<string, unknown>>;
      places?: Array<Record<string, unknown>>;
      properties?: Array<Record<string, unknown>>;
    };
  }): LocationSuggestions {
    return new LocationSuggestions(data);
  }

}
