import { QueryClient } from '@tanstack/react-query';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { TableBookingForm } from '@whitbread-eos/organisms/restaurants';
import { useAppDataDispatch } from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import React, { useEffect } from 'react';
import type { ReactElement } from 'react';

import { RestaurantLayout } from '~components/layouts/RestaurantLayouts';
import { getRestaurantInitialContentDataFn } from '~page-helper/restaurants/getRestaurantInitialContentData.data';

const RESTAURANT_BRAND_NAME = 'restaurant-premierinn';
const RESTAURANT_BRAND_NAME_MARKETING = 'Restaurant Premier Inn';

export interface LocationData {
  address1: string;
  address2: string;
  address3: string;
  address4: string;
  externalSourceSystem: string;
  externalSystemIdentifier: string;
  id: string;
  googleMapURL: string;
  path: string;
  title: string;
}
interface Props {
  occasionId: string;
  siteId: string;
  restaurantBrandNameForAemApi: string;
  restaurantBrandNameForMarketing: string;
  restaurantBrandName: string;
  locationName: string;

  subLocationName: string;
}

BookingDetailsPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <RestaurantLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </RestaurantLayout>
  );
};

export default function BookingDetailsPage(props: Readonly<Props>) {
  const {
    occasionId,
    siteId,
    restaurantBrandName,
    restaurantBrandNameForMarketing,
    restaurantBrandNameForAemApi,
    locationName,
    subLocationName,
  } = props;

  const dispatch = useAppDataDispatch();

  useEffect(() => {
    dispatch({ type: 'changeOccasionId', payload: occasionId });
    dispatch({ type: 'changeSiteId', payload: siteId });
    dispatch({
      type: 'changeRestaurantNameForMarketing',
      payload: restaurantBrandNameForMarketing,
    });
  }, [dispatch, restaurantBrandName, locationName, occasionId, restaurantBrandNameForMarketing]);
  return (
    <TableBookingForm
      restaurantBrandName={restaurantBrandName}
      restaurantBrandNameForAemApi={restaurantBrandNameForAemApi}
      location={locationName}
      subLocation={subLocationName}
      restaurantBrandNameForMarketing={restaurantBrandNameForMarketing}
    />
  );
}

export const getServerSideProps = async (context: GetServerSidePropsContext) => {
  const { location, sublocation: subLocation } = context.query as {
    location: string;
    sublocation: string;
  };

  const queryClient = new QueryClient();

  const { occasionId, siteId, dehydratedState, resourceNotFound } =
    await getRestaurantInitialContentDataFn({
      queryClient,
      location: encodeURIComponent(location || ''),
      subLocation: encodeURIComponent(subLocation || ''),
      restaurantBrandName: RESTAURANT_BRAND_NAME,
      restaurantBrandNameForAemApi: RESTAURANT_BRAND_NAME,
      ...context,
    });

  // for 404 redirect, if bad location
  if (resourceNotFound) {
    return {
      notFound: true,
    };
  }

  return {
    props: {
      restaurantBrandName: RESTAURANT_BRAND_NAME,
      restaurantBrandNameForAemApi: RESTAURANT_BRAND_NAME,
      restaurantBrandNameForMarketing: RESTAURANT_BRAND_NAME_MARKETING,
      locationName: location || '',
      subLocationName: subLocation || '',
      occasionId: occasionId,
      siteId: siteId,
      dehydratedState,
    },
  };
};
