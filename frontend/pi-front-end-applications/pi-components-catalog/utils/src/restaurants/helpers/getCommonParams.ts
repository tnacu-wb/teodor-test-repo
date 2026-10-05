export const getCommonParams = (
  restaurantBrandNameForAemApi: string,
  locationName: string,
  subLocationName: string
) => {
  return {
    restaurant: restaurantBrandNameForAemApi,
    location: encodeURIComponent(locationName || ''),
    subLocation: encodeURIComponent(subLocationName || ''),
  };
};
