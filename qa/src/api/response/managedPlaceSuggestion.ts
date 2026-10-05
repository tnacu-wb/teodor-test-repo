/**
 * One managed place suggestion object from Location field suggestions list
 */
export class ManagedPlaceSuggestion {
  [key: string]: unknown;
  geometry?: unknown;
  managedPlaceId?: string;
  placeId?: string;
  suggestion?: string;

  /**
   * ManagedPlaceSuggestion constructor
   * @param data object data
   * @param data.managedPlaceSuggestionApiResponse response from API
   */
  constructor(data: { managedPlaceSuggestionApiResponse?: Record<string, unknown> } = {}) {
    const managedPlaceSuggestionApiResponse = data.managedPlaceSuggestionApiResponse ?? {};
    this.geometry = managedPlaceSuggestionApiResponse.geometry;
    this.managedPlaceId = managedPlaceSuggestionApiResponse.managedPlaceId as string | undefined;
    this.placeId = managedPlaceSuggestionApiResponse.placeId as string | undefined;
    this.suggestion = managedPlaceSuggestionApiResponse.suggestion as string | undefined;
  }

  static fromResponse(data: { managedPlaceSuggestionApiResponse?: Record<string, unknown> }): ManagedPlaceSuggestion {
    return new ManagedPlaceSuggestion(data);
  }
}
