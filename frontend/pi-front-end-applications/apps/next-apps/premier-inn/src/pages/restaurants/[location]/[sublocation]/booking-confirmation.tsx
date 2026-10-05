import { Flex, BoxProps } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import { ErrorBoundary, LoadingSpinner } from '@whitbread-eos/atoms';
import { BookingConfirmation } from '@whitbread-eos/organisms/restaurants';
import {
  BOOKING_REFERENCE_ID,
  getCookie,
  useAppData,
  decodeFromBase64,
} from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';
import type { ReactElement } from 'react';

import { RestaurantLayout } from '~components/layouts/RestaurantLayouts';
import { getBookingConfirmationDetailsDatFn } from '~page-helper/restaurants/getBookingConfirmationDetails.data';

const RESTAURANT_BRAND_NAME = 'restaurant-premierinn';

ConfirmationPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <RestaurantLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </RestaurantLayout>
  );
};

export default function ConfirmationPage(props: any) {
  const {
    eventId,
    enquiryId,
    restaurantBrandNameForAemApi,
    restaurantBrandName,
    locationName,
    subLocationName,
  } = props;
  const appData = useAppData();
  const router = useRouter();
  const [isRedirectToHome, setIsRedirectToHome] = useState(false);

  useEffect(() => {
    const checkRedirection = () => {
      if (
        (eventId ?? enquiryId) + restaurantBrandName !==
        decodeFromBase64(String(getCookie(BOOKING_REFERENCE_ID)))
      ) {
        setIsRedirectToHome(true);
      }
    };
    checkRedirection();
    if (isRedirectToHome) {
      // Perform redirection on initial render
      router.push(
        `/gb/en/restaurants${locationName ? `/${locationName}` : ''}${
          subLocationName ? `/${subLocationName}` : ''
        }/book`
      );
    }
  }, [eventId, enquiryId, appData, isRedirectToHome, router]);

  return isRedirectToHome ? (
    <Flex {...loadingStyle}>
      <LoadingSpinner />
    </Flex>
  ) : (
    <BookingConfirmation
      restaurantBrandNameForAemApi={restaurantBrandNameForAemApi}
      location={locationName}
      subLocation={subLocationName}
      eventId={eventId}
      enquiryId={enquiryId}
    />
  );
}

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const {
    event: eventId,
    enquiry: enquiryId,
    location,
    sublocation: subLocation,
  } = context.query as {
    event: string;
    enquiry: string;
    location: string;
    sublocation: string;
  };

  const queryClient = new QueryClient();

  await getBookingConfirmationDetailsDatFn({
    queryClient,
    eventId,
    enquiryId,
    ...context,
  });

  return {
    props: {
      eventId: eventId ?? null,
      enquiryId: enquiryId ?? null,
      restaurantBrandNameForAemApi: RESTAURANT_BRAND_NAME,
      restaurantBrandName: RESTAURANT_BRAND_NAME,
      locationName: location || '',
      subLocationName: subLocation || '',
    },
  };
}

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 1,
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;
