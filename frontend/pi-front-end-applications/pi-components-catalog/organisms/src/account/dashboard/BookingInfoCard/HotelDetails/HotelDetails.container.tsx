import { BoxProps, Flex } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { Area } from '@whitbread-eos/api';
import { Alert, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import {
  hotelInformationSelector,
  isStringValid,
  useCustomLocale,
  useAuthToken,
  useLoggedOrCCUI,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useCallback, useEffect, useState } from 'react';

import { fetchBookingConfirmation, fetchHotelInformation } from '../../fetchBookingConfirmation';
import HotelDetailsComponent, { HotelDetails } from './HotelDetails.component';

export interface Props {
  bookingReference: string;
  basketReference: string | null;
  hotelId?: string;
  area?: Area;
}

export default function HotelDetailsContainer({
  bookingReference,
  basketReference,
  hotelId,
  area,
}: Readonly<Props>) {
  const { t } = useTranslation();

  const { language, country } = useCustomLocale();
  const { token } = useAuthToken();
  const [bookingState, setBookingState] = useState({
    isLoading: false,
    error: null,
  });
  const loggedOrCCUI = useLoggedOrCCUI(area);

  const [data, setData] = useState<HotelDetails>({
    hotelInformation: {
      brand: '',
      galleryImages: [{ alt: '', thumbnailSrc: '' }],
      links: { detailsPage: '' },
      parkingDescription: '',
      address: '',
    },
  });

  const getHotelDetails = async (hotelId: string) => {
    try {
      const data = await fetchHotelInformation(queryClient, hotelId, language, country);
      if (typeof data?.hotelInformation?.address === 'object')
        data.hotelInformation.address = hotelInformationSelector(
          data?.hotelInformation
        )?.hotelAddress?.join(', ');
      setData(data);
      setBookingState({ ...bookingState, isLoading: false });
    } catch (error) {
      console.log(error);
    }
  };

  const queryClient = useQueryClient();
  const getHotelIdAndDetails = useCallback(
    async (bookingReference: string, basketReference: string | null) => {
      setBookingState({
        ...bookingState,
        isLoading: true,
      });

      try {
        const bookingData = await fetchBookingConfirmation({
          queryClient,
          loggedOrCCUI,
          bookingReference,
          basketReference,
          language,
          country,
          area,
          token,
        });

        const hotelId = bookingData?.hotelId;
        await getHotelDetails(hotelId);
      } catch (error: any) {
        setBookingState({
          isLoading: false,
          error: error,
        });
      }
    },
    []
  );

  useEffect(() => {
    // bookingReference and basketReference won't have always value, since we are displaying Bart and Opera bookings
    if (isStringValid(bookingReference) || isStringValid(basketReference)) {
      if (hotelId && isStringValid(hotelId)) {
        getHotelDetails(hotelId);
        return;
      }
      getHotelIdAndDetails(bookingReference, basketReference);
    }
  }, [bookingReference, basketReference, hotelId]);

  if (bookingState.error) {
    return (
      <Notification
        status="error"
        description={bookingState.error}
        variant="alert"
        maxW="full"
        svg={<Alert />}
      />
    );
  }

  if (bookingState.isLoading) {
    return (
      <Flex {...loadingStyle} data-testid="Loading-HotelDetails">
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  return <HotelDetailsComponent data={data} />;
}

//<editor-fold desc="Styles" defaultstate="collapsed">

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 999,
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
//</editor-fold>
