import { getRestaurantBrandGTMId } from './getGTMID';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_BEEFEATER_GOOGLE_TAG_MANAGER_ID: 'GTM-MMDPX3P',
  },
}));

describe('getGTMID function which returns GTMID (Google Tag Manager ID)', () => {
  it('should return the correct GTMID for restaurant brand', () => {
    const restaurantBrandName = 'beefeater';
    const googleTageManagerID = getRestaurantBrandGTMId(restaurantBrandName);
    expect(googleTageManagerID).toEqual('GTM-MMDPX3P');
  });
  it('should return GTMID empty string for empty restaurant brand', () => {
    const restaurantBrandName = '';
    const googleTageManagerID = getRestaurantBrandGTMId(restaurantBrandName);
    expect(googleTageManagerID).toEqual('');
  });
  it('should return undefined GTMID string for non matching restaurant brand', () => {
    const restaurantBrandName = 'bob';
    const googleTageManagerID = getRestaurantBrandGTMId(restaurantBrandName);
    expect(googleTageManagerID).toEqual(undefined);
  });
});
