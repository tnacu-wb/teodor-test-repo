import { getSuggestions } from './searchSuggestions';

describe('getSuggestions', () => {
  it('should fetch suggestions and return JSON response', async () => {
    const mockResponse = { suggestions: ['suggestion1', 'suggestion2'] };

    const mockFetch = jest.fn().mockResolvedValue({
      json: jest.fn().mockResolvedValue(mockResponse),
    });

    // Mock the global window.fetch
    global.fetch = mockFetch;

    const value = 'inputValue';

    const result = await getSuggestions(value);

    expect(mockFetch).toHaveBeenCalledTimes(1);
    expect(result).toEqual(mockResponse);
  });
});
