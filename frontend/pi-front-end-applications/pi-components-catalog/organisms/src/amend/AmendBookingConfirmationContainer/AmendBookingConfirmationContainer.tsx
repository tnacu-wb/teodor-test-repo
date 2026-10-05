import { Box, BoxProps, Flex } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  AmendConfirmationErrorLS,
  Area,
  BASKET_STATUS,
  BookingSpinnerConfig,
  CONFIRM_AMEND_STATUS,
  INITIAL_CONFIRM_AMEND_ERROR_KEY,
  INITIAL_CONFIRM_AMEND_ERROR_VALUE,
} from '@whitbread-eos/api';
import { Alert, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import { useCustomLocale, usePollBasketStatus, useSessionStorage } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { NextRouter } from 'next/router';

import {
  BBSearchContainer,
  BookingInfoCardWrapper,
  CCUISearchContainer,
  PISearchContainer,
} from '../../index';

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
  basketReference: string;
  tempBookingReference: string;
  bookingReference: string;
  variant: Area;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  email?: string;
  bookingSpinnerConfig?: BookingSpinnerConfig[];
  showSearch?: boolean;
}
export default function AmendBookingConfirmationContainer({
  queryClient,
  router,
  basketReference,
  bookingReference,
  tempBookingReference,
  variant,
  amendBookingStatus,
  bookingSpinnerConfig,
  email,
  showSearch = true,
}: Readonly<Props>) {
  const { country, language } = useCustomLocale();
  const { t } = useTranslation();
  const baseDataTestId = 'booking-confirmation';
  const [, setConfirmAmendErrorValue] = useSessionStorage<AmendConfirmationErrorLS>(
    INITIAL_CONFIRM_AMEND_ERROR_KEY,
    INITIAL_CONFIRM_AMEND_ERROR_VALUE
  );

  const amendConfirmationCommonURL = `/amend/booking-confirmation?bookingReference=${bookingReference}`;
  const amendConfirmationURL =
    variant === Area.BB
      ? `/${country}/${language}/business-booker${amendConfirmationCommonURL}`
      : `/${country}/${language}${amendConfirmationCommonURL}`;

  const { pollingInProgress, pollingTimedOut, basketStatus, dynamicSpinnerLabel, errorCode } =
    usePollBasketStatus(
      tempBookingReference ?? '',
      bookingSpinnerConfig ?? [],
      amendBookingStatus === CONFIRM_AMEND_STATUS.payment
    );

  if (country === 'de' && errorCode) {
    setConfirmAmendErrorValue(errorCode as AmendConfirmationErrorLS);
  }

  if (!pollingInProgress && amendBookingStatus === CONFIRM_AMEND_STATUS.payment) {
    if (basketStatus === BASKET_STATUS.AMENDED) {
      router.push(
        `${amendConfirmationURL}&status=${CONFIRM_AMEND_STATUS.success}&basketReference=${basketReference}&tempBookingReference=${tempBookingReference}`
      );
    } else if (basketStatus === BASKET_STATUS.AMEND_FAILED) {
      router.push(
        `/${country}/${language}${
          variant === Area.BB ? '/business-booker' : ''
        }/amend/details?bookingReference=${bookingReference}&tempBookingReference=${tempBookingReference}&status=${
          CONFIRM_AMEND_STATUS.paymentError
        }`
      );
    } else if (basketStatus === BASKET_STATUS.AMENDING && errorCode) {
      router.push(
        `${amendConfirmationURL}&status=${CONFIRM_AMEND_STATUS.error}&basketReference=${basketReference}&tempBookingReference=${tempBookingReference}`
      );
    } else if (basketStatus !== BASKET_STATUS.AMENDING || pollingTimedOut) {
      router.push(
        `${amendConfirmationURL}&status=${CONFIRM_AMEND_STATUS.paymentError}&basketReference=${basketReference}&tempBookingReference=${tempBookingReference}`
      );
    }
  }

  if (pollingInProgress) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner loadingText={dynamicSpinnerLabel} />
      </Flex>
    );
  }

  return (
    <Box data-testid={`${baseDataTestId}-wrapper`}>
      {amendBookingStatus !== CONFIRM_AMEND_STATUS.success &&
      amendBookingStatus !== CONFIRM_AMEND_STATUS.error &&
      amendBookingStatus !== CONFIRM_AMEND_STATUS.payment &&
      amendBookingStatus !== CONFIRM_AMEND_STATUS.paymentError ? (
        <Box m="lg">
          <Notification
            status="warning"
            description={t('errors.sorry')}
            variant="alert"
            maxW="full"
            svg={<Alert />}
          />
        </Box>
      ) : (
        <>
          {showSearch && renderSearchComponent(variant, queryClient, router)}
          <BookingInfoCardWrapper
            area={variant}
            bookingReference={bookingReference}
            basketReference={basketReference}
            tempBookingReference={tempBookingReference}
            inputValues={{}}
            isAmendPage={true}
            amendBookingStatus={amendBookingStatus as CONFIRM_AMEND_STATUS}
            email={email ?? ''}
          />
        </>
      )}
    </Box>
  );
}

function renderSearchComponent(variant: Area, queryClient: QueryClient, router: NextRouter) {
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  switch (variant) {
    case Area.BB:
      return (
        <BBSearchContainer
          queryClient={queryClient}
          searchLocation={searchLocation?.toString()}
          ARRdd={Number(ARRdd)}
          ARRmm={Number(ARRmm)}
          ARRyyyy={Number(ARRyyyy)}
          NIGHTS={Number(NIGHTS)}
          ROOMS={Number(ROOMS)}
          isSummaryActive={false}
          variant="bb"
        />
      );
    case Area.CCUI:
      return (
        <CCUISearchContainer
          queryClient={queryClient}
          searchLocation={searchLocation?.toString()}
          ARRdd={Number(ARRdd)}
          ARRmm={Number(ARRmm)}
          ARRyyyy={Number(ARRyyyy)}
          NIGHTS={Number(NIGHTS)}
          ROOMS={Number(ROOMS)}
          isSummaryActive={false}
        />
      );
    case Area.PI:
    default:
      return (
        <PISearchContainer
          queryClient={queryClient}
          searchLocation={searchLocation?.toString()}
          ARRdd={Number(ARRdd)}
          ARRmm={Number(ARRmm)}
          ARRyyyy={Number(ARRyyyy)}
          NIGHTS={Number(NIGHTS)}
          ROOMS={Number(ROOMS)}
          isSummaryActive={false}
        />
      );
  }
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
