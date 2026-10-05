import { Box, Button, Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import { AMEND_3CP_VISITED_KEY, Area } from '@whitbread-eos/api';
import { formatDataTestId, useCustomLocale, useUserData } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import { returnBookingStatusLabel } from '../BookingInfoCard/BookingActions/BookingActions.component';

interface Props {
  checkInLabel: string;
  checkOutLabel: string;
  hotelName: string;
  shouldRenderDashboardButton?: boolean;
  area: Area;
  bookingStatus?: string;
  isBICHeaderBookingStatusEnabled?: boolean;
}
export default function BookingInfoCardHeader({
  checkInLabel,
  checkOutLabel,
  hotelName,
  shouldRenderDashboardButton,
  area,
  bookingStatus,
  isBICHeaderBookingStatusEnabled,
}: Readonly<Props>) {
  const baseDataTestId = 'BookingInfoCardHeader';
  const { t } = useTranslation(['common']);
  const { isLoggedIn } = useUserData();
  const { language, country } = useCustomLocale();
  const bookingStatusText = t(returnBookingStatusLabel(bookingStatus || ''));

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Text as="h1" {...titleStyles} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
        {t('dashboard.bookings.bookingInformation')}
      </Text>
      <Text
        as="h6"
        {...hotelNameStyles}
        {...bookingStatusStyles}
        data-testid={formatDataTestId(baseDataTestId, 'HotelName')}
      >
        {hotelName}
        {isBICHeaderBookingStatusEnabled && bookingStatusText !== '' && (
          <Text
            as="span"
            className={`booking-status booking-status--${bookingStatus?.toLowerCase()}`}
          >
            {bookingStatusText}
          </Text>
        )}
      </Text>
      <Flex {...datesWrapperStyles}>
        <Flex {...checkInOutDatesStyles}>
          <Box>
            <Text as="h6" {...dateLabelStyles}>
              {t('dashboard.bookings.checkIn')}
            </Text>
            <Text
              as="h6"
              {...dateStyles}
              data-testid={formatDataTestId(baseDataTestId, 'CheckInDate')}
            >
              {checkInLabel}
            </Text>
          </Box>
          <Box ml="2xl">
            <Text as="h6" {...dateLabelStyles}>
              {t('dashboard.bookings.checkOut')}
            </Text>
            <Text
              as="h6"
              {...dateStyles}
              data-testid={formatDataTestId(baseDataTestId, 'CheckOutDate')}
            >
              {checkOutLabel}
            </Text>
          </Box>
        </Flex>
        {[Area.PI, Area.BB].includes(area) && shouldRenderDashboardButton && renderPIBBButton()}
        {area === Area.CCUI && shouldRenderDashboardButton && renderCCUIButtons()}
      </Flex>
    </Box>
  );

  function renderCCUIButtons() {
    const handleBackToDashboard = () => {
      window.location.href = `/${country}/${language}/bookings`;
    };

    const handleBackToHomepage = () => {
      window.location.href = `/${country}/${language}`;
    };

    return (
      <Box>
        <Button
          data-testid={formatDataTestId(baseDataTestId, 'CCUIBackToDashboardButton')}
          variant="tertiary"
          marginRight="lg"
          onClick={handleBackToDashboard}
        >
          {t('amend.backButtonText')}
        </Button>
        <Button
          data-testid={formatDataTestId(baseDataTestId, 'CCUIBackToHomepageButton')}
          variant="secondary"
          onClick={handleBackToHomepage}
        >
          {t('amend.anonymousBackButtonText')}
        </Button>
      </Box>
    );
  }

  function renderPIBBButton() {
    return (
      <Box>
        <Button
          data-testid={formatDataTestId(baseDataTestId, 'BackToDashboardButton')}
          onClick={handleBackButton}
          {...backToDashboardButton}
        >
          {isLoggedIn ? t('amend.backButtonText') : t('amend.anonymousBackButtonText')}
        </Button>
      </Box>
    );
  }

  function handleBackButton() {
    sessionStorage.removeItem(AMEND_3CP_VISITED_KEY);

    const isBB = area === Area.BB ? '/business-booker' : '';
    window.location.href = isLoggedIn
      ? `/${country}/${language}${isBB}/account/dashboard.html`
      : `/${country}/${language}${isBB}/home.html`;
  }
}

const titleStyles = {
  color: 'baseBlack',
  fontWeight: 'semibold',
  lineHeight: 5,
  fontSize: { mobile: '3xl', xs: '3xxl' },
} as TextProps;

const hotelNameStyles = {
  mt: 'lg',
  fontWeight: '700',
  lineHeight: 3,
  fontSize: 'lg',
  color: 'darkGrey1',
} as TextProps;

const checkInOutDatesStyles = {
  flexDirection: 'row',
  mt: 'lg',
  mb: { mobile: 'lg', md: '2xl' },
  justifyContent: { mobile: 'space-between', sm: 'start' },
} as FlexProps;

const dateLabelStyles = {
  color: 'darkGrey1',
  fontWeight: 'normal',
  lineHeight: 3,
  fontSize: 'md',
} as TextProps;

const dateStyles = {
  color: 'darkGrey1',
  fontWeight: 'semibold',
  lineHeight: 3,
  fontSize: 'md',
} as TextProps;

const datesWrapperStyles = {
  justifyContent: { md: 'space-between' },
  flexDirection: { mobile: 'column', md: 'row' },
} as FlexProps;

const backToDashboardButton = {
  mt: { md: 'lg' },
  mb: '2xl',
  width: { mobile: '17rem', xs: '18rem' },
  variant: 'secondary',
};

const bookingStatusStyles = {
  sx: {
    '.booking-status': {
      fontWeight: '600',
      lineHeight: '140%',
      fontSize: '13px',
      borderRadius: '16px',
      padding: '2px 8px',
      marginLeft: 'xmd',
      color: '#1C8754',
      backgroundColor: '#E5F2F6',
      '&--cancelled': { color: '#D90941', backgroundColor: '#FBE6EC' },
    },
  },
} as TextProps;
