import getConfig from 'next/config';

const { publicRuntimeConfig = {} } = getConfig() || {};

export const getRestaurantBrandGTMId = (restaurantBrandName: string) => {
  let GTM_ID = '';

  if (restaurantBrandName) {
    GTM_ID =
      publicRuntimeConfig[`NEXT_PUBLIC_${restaurantBrandName.toUpperCase()}_GOOGLE_TAG_MANAGER_ID`];
  }
  return GTM_ID;
};
