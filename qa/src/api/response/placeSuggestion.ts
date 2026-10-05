/**
 * One place suggestion object from Location field suggestions list
 */
export class PlaceSuggestion {
  [key: string]: unknown;
  placeId?: string;
  suggestion?: string;

  /**
   * Place Suggestion constructor
   * @param data object data
   * @param data.placeSuggestionApiResponse response from API
   */
  constructor(data: { placeSuggestionApiResponse?: Record<string, unknown> } = {}) {
    const placeSuggestionApiResponse = data.placeSuggestionApiResponse ?? {};
    this.suggestion = placeSuggestionApiResponse.suggestion as string | undefined;
    this.placeId = placeSuggestionApiResponse.placeId as string | undefined;
  }

  static fromResponse(data: { placeSuggestionApiResponse?: Record<string, unknown> }): PlaceSuggestion {
    return new PlaceSuggestion(data);
  }
}
