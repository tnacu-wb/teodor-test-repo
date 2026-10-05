import { Box, Button, Flex, FlexProps, Grid, Text, GridProps } from '@chakra-ui/react';
import {
  Area,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
  GET_HOTEL_INFORMATION,
  GET_BOOKING_INFORMATION,
  DATE_TYPE,
} from '@whitbread-eos/api';
import { Lock as LockIcon } from '@whitbread-eos/atoms';
import {
  useCustomLocale,
  useFeatureToggle,
  useQueryRequest,
  shouldDisplaySecureBooking,
  type BookingDataType,
  updateDashboardAnalytics,
} from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import PageLoader from '../PageLoader';

interface SecureBookingButtonProps {
  data: BookingDataType;
}

export default function SecureBookingButton({ data }: SecureBookingButtonProps) {
  const [pageLoaderStatus, setPageLoaderStatus] = useState(false);
  const { [FT_PI_BB_NON_GUARANTEED_REMINDER]: isFeatureEnabled } = useFeatureToggle();
  const { language, country } = useCustomLocale();
  const { t } = useTranslation();

  const { basketReference, hotelInfo, bookingReference, arrivalDate } = data;
  let bookingFlowId = hotelInfo?.bookingFlowId;
  const isItPremInOrBusiBooker = data?.area === Area.BB || data?.area === Area.PI;

  const { isLoading: isLoadingHotelInformation, data: hiData } = useQueryRequest(
    ['GetHotelInformation', hotelInfo?.hotelId, country, language],
    GET_HOTEL_INFORMATION,
    {
      hotelId: hotelInfo?.hotelId,
      country,
      language,
    },
    { enabled: !!isItPremInOrBusiBooker }
  );

  const isBookingInformationEnabled =
    !isLoadingHotelInformation && !!hiData && !bookingFlowId && !!isItPremInOrBusiBooker;

  const { data: bkngData } = useQueryRequest(
    ['GetBookingInformation', language, country, basketReference],
    GET_BOOKING_INFORMATION,
    {
      basketReference,
      language,
      country,
      bookingChannelCriteria: {
        channel: data?.area?.toUpperCase(),
        subchannel: 'WEB',
        language,
      },
    },
    { enabled: !!isBookingInformationEnabled }
  );

  if (!bookingFlowId) {
    bookingFlowId = bkngData?.bookingInformation?.bookingFlowId;
  }

  const hotelBrand = hiData?.hotelInformation?.brand;

  //Check the conditions to display the secure booking button
  const displayButton = !isLoadingHotelInformation
    ? shouldDisplaySecureBooking(data, hotelBrand, isFeatureEnabled)
    : false;
  const isBusinessBooker = !!(data?.area === Area.BB);

  const isValidBookingFlowId = isBusinessBooker || (!!bookingFlowId && bookingFlowId.trim() !== '');

  useEffect(() => {
    if (hotelInfo && arrivalDate && displayButton && isValidBookingFlowId) {
      const secureBookings = {
        bookingReference: bookingReference,
        arrivalDate: format(new Date(arrivalDate), DATE_TYPE.DD_MM_YYYY),
        hotelCode: hotelInfo?.hotelId,
        secureBookingAvailable: Boolean(displayButton && isValidBookingFlowId),
      };
      updateDashboardAnalytics({ secureBookings });
    }
  }, [displayButton, isValidBookingFlowId]);

  return (
    displayButton &&
    isValidBookingFlowId && (
      <Grid {...gridStyles}>
        <Flex {...contentWrapperStyle}>
          <Flex {...iconColumnStyle}>
            <Box {...iconWrapperStyle}>
              <LockIcon width={'2.0625rem'} />
            </Box>
          </Flex>
          <Flex {...textWrapperStyle}>
            <Text {...headingTextStyle}>{t('nonguaranteed.booking.arrivingAfter6pm')}</Text>
            <Text {...subTextStyle}>{t('nonguaranteed.booking.secureByCard')}</Text>
          </Flex>
        </Flex>
        <Box {...amendButtonStyleWrapper}>
          <Button
            {...amendButtonStyle}
            data-testid="SecureBooking-Button"
            onClick={() => {
              handleOnClick(
                basketReference,
                country,
                language,
                setPageLoaderStatus,
                isBusinessBooker,
                bookingFlowId
              );
              updateDashboardAnalytics({ secureBookingAction: true });
            }}
          >
            {t('nonguaranteed.booking.buttonlabel')}
          </Button>
        </Box>
        {!!pageLoaderStatus && <PageLoader text={t('searchresults.list.hotel.loading')} />}
      </Grid>
    )
  );
}

function handleOnClick(
  basketReference: string,
  country: string,
  language: string,
  setPageLoaderStatus: (val: boolean) => void,
  isBusinessBooker: boolean,
  bookingFlowId = ''
) {
  setPageLoaderStatus(true);
  const envPath = isBusinessBooker ? '/business-booker/booking-business/' : `/${bookingFlowId}/`;
  const targetUri = `/${country}/${language}${envPath}payment?reservationId=${basketReference}&secure-booking=true`;
  if (typeof window !== 'undefined') {
    window.location.href = targetUri;
  }
}

const gridStyles = {
  templateColumns: { base: '1fr', md: '1fr auto' },
  gap: '1rem',
  py: '1.75rem',
  px: '1.5rem',
  alignItems: 'center',
  border: '1px solid var(--chakra-colors-lightGrey2)',
  borderRadius: '8px',
  maxW: '100%',
  w: '100%',
  mb: { base: 'lg', md: '10%' },
  overflowWrap: 'break-word',
  h: { base: 'auto', md: '7rem' },
} as GridProps;

const contentWrapperStyle = {
  direction: { base: 'column', md: 'row' },
  gap: '1.25rem',
  align: 'bottom',
  w: '100%',
} as FlexProps;

const iconColumnStyle = {
  align: 'center',
  justifyContent: 'flex-start',
  display: { base: 'none', md: 'flex' },
};

const iconWrapperStyle = {
  py: 0,
  pr: 0,
};

const textWrapperStyle = {
  direction: 'column',
  justify: 'center',
  flex: 1,
} as FlexProps;

const headingTextStyle = {
  fontWeight: { base: 'bold', md: 'black' },
  fontSize: { base: 'md', md: 'xl' },
  lineHeight: { base: 2, md: 3 },
  color: { base: 'darkGrey1', md: 'btnSecondaryEnabled' },
  pb: 'sm',
  fontFamily: {
    base: 'Proxima Nova Sans, helvetica, arial, sans-serif',
    md: 'Premierinn-Sans, Proxima Nova Sans, helvetica, arial, sans-serif',
  },
};

const subTextStyle = {
  fontSize: { base: 'sm', md: 'md' },
  lineHeight: '3',
  color: 'darkGrey1',
  fontWeight: 'normal',
};

const amendButtonStyleWrapper = {
  w: { base: '100%', md: 'auto' },
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'flex-end',
  alignSelf: 'stretch',
} as any;

const amendButtonStyle = {
  fontSize: 'lg',
  fontWeight: 'semibold',
  lineHeight: '3',
  size: 'sm',
  variant: 'primary',
  minW: { base: '100%', md: '17.1875rem' },
  borderRadius: '4px',
  py: 'xmd',
  px: 'xl',
  h: '2.75rem',
  maxH: '2.75rem',
};
