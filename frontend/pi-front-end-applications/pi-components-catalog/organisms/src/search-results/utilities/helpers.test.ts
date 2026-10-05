import '@testing-library/jest-dom';
import { NextRouter, useRouter } from 'next/router';

import {
  formatDateSearchQueryUrl,
  getSearchQueryUrl,
  getFallbackSearchPlace,
  getNewUrlSortValue,
  getSearchRedirectURL,
} from './helpers';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('Helpers functions', () => {
  describe('formatDateSearchQueryUrl function', () => {
    const value = 'test';
    it('should return value if value is empty', () => {
      expect(formatDateSearchQueryUrl('ARRyy', '')).toBe('');
    });
    it('should return specific format for ARRdd or ARRmm', () => {
      expect(formatDateSearchQueryUrl('ARRdd', value)).toBe('st');
      expect(formatDateSearchQueryUrl('ARRmm', value)).toBe('st');
    });

    it('should not truncate "today" or "tomorrow" for ARRdd if isMetaArrivalDayKeywordFlagEnabled is true', () => {
      expect(formatDateSearchQueryUrl('ARRdd', 'today', true)).toBe('today');
      expect(formatDateSearchQueryUrl('ARRdd', 'tomorrow', true)).toBe('tomorrow');
    });
    it('should truncate ARRdd if isMetaArrivalDayKeywordFlagEnabled is false', () => {
      expect(formatDateSearchQueryUrl('ARRdd', '011', false)).toBe('11');
    });
    it('should return value otherwise', () => {
      expect(formatDateSearchQueryUrl('ARRyy', value)).toBe(value);
    });
  });

  describe('getSearchQueryUrl function', () => {
    beforeAll(() => {
      mockUseRouter.mockReturnValue({
        query: { language: 'en', country: 'gb', SORT: '1' },
      });
    });
    it('should return value if value is empty', () => {
      expect(getSearchQueryUrl(useRouter(), ['SORT'], formatDateSearchQueryUrl)).toBe(
        'language=en&country=gb'
      );
    });
  });

  describe('getFallbackSearchPlace', () => {
    it('should return the correct value when there is a matching place/property', () => {
      const searchSuggestions = {
        properties: [
          {
            code: 'LON',
            brand: 'PI',
            suggestion: 'london bridge hotel',
            geometry: {
              type: 'LATLONG',
              coordinates: [1234],
            },
          },
        ],
        managedPlaces: [],
        places: [
          {
            suggestion: 'london bridge',
            placeId: '123',
          },
        ],
      };

      expect(getFallbackSearchPlace('london bridge', searchSuggestions)).toStrictEqual({
        PLACEID: '123',
      });
    });

    it('should return the first place when there is no matching place/property', () => {
      const searchSuggestions = {
        properties: [
          {
            code: 'LON',
            brand: 'PI',
            suggestion: 'london',
            geometry: {
              type: 'LATLONG',
              coordinates: [123],
            },
          },
        ],
        managedPlaces: [],
        places: [
          {
            suggestion: 'manchester',
            placeId: '1234',
          },
        ],
      };

      expect(getFallbackSearchPlace('manchester salford', searchSuggestions)).toStrictEqual({
        PLACEID: '1234',
        'searchModel.searchTerm': 'manchester',
      });
    });
  });

  describe('getNewUrlSortValue function', () => {
    it('should return the sort value for price', () => {
      expect(getNewUrlSortValue({ id: 'DISTANCE' }, '1')).toBe('2');
    });

    it('should return the sort value for recommended', () => {
      expect(
        getNewUrlSortValue({ id: 'RECOMMENDATION' }, '1', {
          isPiSortOrderDropdownEnabled: true,
          isBbSortOrderDropdownEnabled: false,
        })
      ).toBe('1');
    });

    it('should return the sort value for distance', () => {
      expect(
        getNewUrlSortValue({ id: 'DISTANCE' }, '1', {
          isPiSortOrderDropdownEnabled: false,
          isBbSortOrderDropdownEnabled: true,
        })
      ).toBe('1');
    });
  });

  describe('getSearchRedirectURL function', () => {
    it('should return the correct URL with the search term and other query parameters', () => {
      const expectedURL = 'gb/en/search.html?searchTerm=london&country=gb&language=en';
      expect(
        getSearchRedirectURL(
          {
            query: { searchTerm: 'london', country: 'gb', language: 'en' },
          } as unknown as NextRouter,
          [],
          'gb',
          'en'
        )
      ).toBe(expectedURL);
    });
  });
});
