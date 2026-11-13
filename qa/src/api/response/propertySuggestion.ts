/**
 * One property suggestion object from Location field suggestions list
 */
export class PropertySuggestion {
  [key: string]: unknown;
  brand?: string;
  code?: string;
  geometry?: unknown;
  suggestion?: string;

  /**
   * Property Sugesstion constructor
   * @param data object data
   * @param data.propertySuggestionApiResponse response from API
   */
  constructor(data: { propertySuggestionApiResponse?: Record<string, unknown> } = {}) {
    const propertySuggestionApiResponse = data.propertySuggestionApiResponse ?? {};
    this.code = propertySuggestionApiResponse.code as string | undefined;
    this.brand = propertySuggestionApiResponse.brand as string | undefined;
    this.geometry = propertySuggestionApiResponse.geometry;
    this.suggestion = propertySuggestionApiResponse.suggestion as string | undefined;
  }

  static fromResponse(data: { propertySuggestionApiResponse?: Record<string, unknown> }): PropertySuggestion {
    return new PropertySuggestion(data);
  }
}
