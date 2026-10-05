import { getCommonParams } from './getCommonParams';

describe('getCommonParams function', () => {
  // Test case for when all parameters are provided
  it('returns correct common parameters when all inputs are provided', () => {
    // Arrange
    const restaurantBrandNameForAemApi = 'example';
    const locationName = 'New York';
    const subLocationName = 'Downtown';

    // Act
    const result = getCommonParams(restaurantBrandNameForAemApi, locationName, subLocationName);

    // Assert
    expect(result).toEqual({
      restaurant: restaurantBrandNameForAemApi,
      location: encodeURIComponent(locationName),
      subLocation: encodeURIComponent(subLocationName),
    });
  });

  // Test case for when locationName and subLocationName are null
  it('returns correct common parameters when locationName and subLocationName are null', () => {
    // Arrange
    const restaurantBrandNameForAemApi = 'example';
    const locationName: string | null = '';
    const subLocationName: string | null = '';

    // Act
    const result = getCommonParams(restaurantBrandNameForAemApi, locationName, subLocationName);

    // Assert
    expect(result).toEqual({
      restaurant: restaurantBrandNameForAemApi,
      location: '',
      subLocation: '',
    });
  });
});
