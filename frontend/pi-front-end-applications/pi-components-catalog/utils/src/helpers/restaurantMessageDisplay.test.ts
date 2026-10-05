import type { Restaurant } from '@whitbread-eos/api';

import { getRestaurantMessageDisplay } from './restaurantMessageDisplay';

describe('getRestaurantMessageDisplay', () => {
  const mockRestaurant: Restaurant = {
    messageHeader: 'Restaurant Header',
    messageDescription: 'Restaurant Description',
    restaurantNotFound: false,
    noMealsFound: false,
  };

  const STAY_ARRIVAL_DATE = '2024-06-10';
  const STAY_DEPARTURE_DATE = '2024-06-15';

  const mockAncillaryCloseout = {
    items: [
      {
        noMealsHeading: 'Custom Closeout Heading',
        noMealsMessage: 'Custom Closeout Message',
        startDate: '01/06/2024',
        endDate: '30/06/2024',
      },
    ],
  };

  describe('when ancillary closeout has custom copy', () => {
    test('should prioritize custom closeout copy over restaurant message', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: mockAncillaryCloseout,
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(true);
      expect(result.showRestaurantMessage).toBe(true);
      expect(result.displayTitle).toBe('Custom Closeout Heading');
      expect(result.displayDescription).toBe('Custom Closeout Message');
    });

    test('should return custom copy when ancillary closeout is active with no restaurant issue', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: mockRestaurant,
        ancillaryCloseoutData: mockAncillaryCloseout,
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(true);
      expect(result.showRestaurantMessage).toBe(false);
      expect(result.displayTitle).toBe('Custom Closeout Heading');
      expect(result.displayDescription).toBe('Custom Closeout Message');
    });

    test('should fall back to legacy copy when only noMealsHeading is provided', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: 'Only Heading',
              noMealsMessage: '',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      // Partial custom copy (heading only) is rejected; falls back to legacy
      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.displayTitle).toBe('Restaurant Header');
      expect(result.displayDescription).toBe('Restaurant Description');
    });

    test('should fall back to legacy copy when only noMealsMessage is provided', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: '',
              noMealsMessage: 'Only Message',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      // Partial custom copy (message only) is rejected; falls back to legacy
      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.displayTitle).toBe('Restaurant Header');
      expect(result.displayDescription).toBe('Restaurant Description');
    });

    test('should ignore whitespace-only custom copy', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: mockRestaurant,
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: '   ',
              noMealsMessage: '   ',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.showRestaurantMessage).toBe(false);
      expect(result.displayTitle).toBe('');
      expect(result.displayDescription).toBe('');
    });
  });

  describe('date relevance', () => {
    test('should ignore custom copy from an item whose closeout window does not overlap the stay', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: 'Out of range heading',
              noMealsMessage: 'Out of range message',
              startDate: '01/01/2023',
              endDate: '31/01/2023',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.displayTitle).toBe('Restaurant Header');
      expect(result.displayDescription).toBe('Restaurant Description');
    });

    test('should skip an out-of-range item and use the first item that is date-relevant', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: 'Out of range heading',
              noMealsMessage: 'Out of range message',
              startDate: '01/01/2023',
              endDate: '31/01/2023',
            },
            {
              noMealsHeading: 'Relevant heading',
              noMealsMessage: 'Relevant message',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(true);
      expect(result.displayTitle).toBe('Relevant heading');
      expect(result.displayDescription).toBe('Relevant message');
    });

    test('should skip null closeout items and use a later valid item', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: {
          items: [
            null,
            {
              noMealsHeading: 'Relevant heading',
              noMealsMessage: 'Relevant message',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(true);
      expect(result.displayTitle).toBe('Relevant heading');
      expect(result.displayDescription).toBe('Relevant message');
    });

    test('should skip malformed closeout dates and use a later valid item', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: 'Malformed heading',
              noMealsMessage: 'Malformed message',
              startDate: '30/06/2024',
              endDate: '01/06/2024',
            },
            {
              noMealsHeading: 'Relevant heading',
              noMealsMessage: 'Relevant message',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(true);
      expect(result.displayTitle).toBe('Relevant heading');
      expect(result.displayDescription).toBe('Relevant message');
    });

    test('should not surface custom copy when arrivalDate is missing, even with a complete-copy item', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: mockAncillaryCloseout,
        arrivalDate: undefined,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.displayTitle).toBe('Restaurant Header');
      expect(result.displayDescription).toBe('Restaurant Description');
    });

    test('should not surface custom copy when both dates are missing, even with a complete-copy item', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: mockAncillaryCloseout,
        arrivalDate: undefined,
        departureDate: undefined,
      });

      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.displayTitle).toBe('Restaurant Header');
      expect(result.displayDescription).toBe('Restaurant Description');
    });
  });

  describe('when restaurant has message fields', () => {
    test('should use restaurant message when restaurant has issue', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.showRestaurantMessage).toBe(true);
      expect(result.displayTitle).toBe('Restaurant Header');
      expect(result.displayDescription).toBe('Restaurant Description');
    });

    test('should use empty strings when restaurant has empty message fields', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: {
          messageHeader: '',
          messageDescription: '',
          restaurantNotFound: false,
          noMealsFound: true,
        },
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.showRestaurantMessage).toBe(true);
      expect(result.displayTitle).toBe('');
      expect(result.displayDescription).toBe('');
    });

    test('should use empty strings when restaurant has whitespace-only message fields', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: {
          messageHeader: '   ',
          messageDescription: '   ',
          restaurantNotFound: false,
          noMealsFound: true,
        },
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.showRestaurantMessage).toBe(true);
      expect(result.displayTitle).toBe('');
      expect(result.displayDescription).toBe('');
    });
  });

  describe('when hasRestaurantIssue is true', () => {
    test('should show restaurant message when restaurantNotFound is true', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, restaurantNotFound: true },
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.showRestaurantMessage).toBe(true);
    });

    test('should show restaurant message when noMealsFound is true', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.showRestaurantMessage).toBe(true);
    });
  });

  describe('when no message should be shown', () => {
    test('should not show message when no issues and no custom copy', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: mockRestaurant,
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.showRestaurantMessage).toBe(false);
      expect(result.displayTitle).toBe('');
      expect(result.displayDescription).toBe('');
    });

    test('should use empty strings when all copy fields are empty', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: {
          messageHeader: undefined,
          messageDescription: undefined,
          restaurantNotFound: false,
          noMealsFound: true,
        },
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.displayTitle).toBe('');
      expect(result.displayDescription).toBe('');
    });
  });

  describe('priority order', () => {
    test('should follow priority: custom copy > restaurant message > empty strings', () => {
      // Custom copy takes priority
      const resultWithCustom = getRestaurantMessageDisplay({
        restaurant: {
          messageHeader: 'Restaurant Header',
          messageDescription: 'Restaurant Description',
          noMealsFound: true,
        } as Restaurant,
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: 'Custom Heading',
              noMealsMessage: 'Custom Message',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(resultWithCustom.displayTitle).toBe('Custom Heading');
      expect(resultWithCustom.displayDescription).toBe('Custom Message');

      // Restaurant message used when no custom copy
      const resultWithoutCustom = getRestaurantMessageDisplay({
        restaurant: {
          messageHeader: 'Restaurant Header',
          messageDescription: 'Restaurant Description',
          noMealsFound: true,
        } as Restaurant,
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(resultWithoutCustom.displayTitle).toBe('Restaurant Header');
      expect(resultWithoutCustom.displayDescription).toBe('Restaurant Description');

      // Empty strings used when no other copy available
      const resultWithDefaults = getRestaurantMessageDisplay({
        restaurant: {
          messageHeader: '',
          messageDescription: '',
          noMealsFound: true,
        } as Restaurant,
        ancillaryCloseoutData: { items: [] },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(resultWithDefaults.displayTitle).toBe('');
      expect(resultWithDefaults.displayDescription).toBe('');
    });
  });

  describe('undefined/null handling', () => {
    test('should handle undefined ancillaryCloseoutData', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: { ...mockRestaurant, noMealsFound: true },
        ancillaryCloseoutData: undefined,
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      expect(result.hasCustomRestaurantCopy).toBe(false);
      expect(result.showRestaurantMessage).toBe(true);
    });

    test('should handle null restaurant with custom copy', () => {
      const result = getRestaurantMessageDisplay({
        restaurant: null,
        ancillaryCloseoutData: {
          items: [
            {
              noMealsHeading: 'Custom Header',
              noMealsMessage: 'Custom Message',
              startDate: '01/06/2024',
              endDate: '30/06/2024',
            },
          ],
        },
        arrivalDate: STAY_ARRIVAL_DATE,
        departureDate: STAY_DEPARTURE_DATE,
      });

      // With no restaurant issue but custom copy present, custom copy is returned for use by the caller
      expect(result.hasCustomRestaurantCopy).toBe(true);
      expect(result.showRestaurantMessage).toBe(false);
      expect(result.displayTitle).toBe('Custom Header');
      expect(result.displayDescription).toBe('Custom Message');
    });
  });
});
