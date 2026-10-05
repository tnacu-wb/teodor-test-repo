import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex, useMediaQuery } from '@chakra-ui/react';
import {
  Area,
  BASKET_STATUS,
  BC_RESERVATION_STATUS,
  BOOKING_TYPE,
  DpaInfo,
  ManageBookingResponse,
  OverridenUserInfo,
  paymentOptions,
} from '@whitbread-eos/api';
import { Alert, Card, Notification, Success } from '@whitbread-eos/atoms';
import { SecureBookingButton } from '@whitbread-eos/molecules';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction } from 'react';

import BookingActions from './BookingActions';
import { BookingDetails } from './BookingDetails';
import type { Props as BookingInfoProps } from './BookingInfoCard.container';
import HotelDetails from './HotelDetails';

export interface IDVProps {
  dpaInfo: DpaInfo;
  setDpaInfo: (value: DpaInfo) => void;
}

export interface OverrideReservationProps {
  setIsAgentOverrideModalVisible: Dispatch<SetStateAction<boolean>>;
  overridenUserInfo: OverridenUserInfo;
}

export interface Props extends BookingInfoProps, IDVProps, OverrideReservationProps {
  bookingType?: string;
  bookingReference: string;
  basketReference: string | null;
  tempBookingReference?: string;
  operaConfNumber?: string;
  bookingStatus: string;
  getBookingStatus: any;
  paymentOption: string;
  shouldShowTypeOfBooking?: boolean;
  sourcePms?: string;
  gdsReferenceNumber?: any;
  distBookingChannel?: any;
  bookingChannel?: any;
  area: Area;
  isAmendPage: boolean;
  isAmendSuccessful: boolean;
  arrivalDate: string;
  bookingSurname: string;
  handleResendConfirmationAction: () => void;
  manageBookingParams: {
    data: ManageBookingResponse;
    isError: boolean;
    isLoading: boolean;
    error: unknown;
  };
  isErrorFindOrCopyBooking: boolean;
  handleRepeatBookingAction?: (param?: () => void | undefined) => void;
  handleChangePayment?: () => void;
  basketStatus?: string;
  isChangedPaymentApplied?: boolean;
  hotelInfo?: {
    hotelId: string;
    bookingFlowId: string;
  };
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
}

export default function BookingInfoCardComponent(props: Readonly<Props>) {
  const { t } = useTranslation();
  const {
    isAmendPage,
    area,
    operaConfNumber,
    manageBookingParams,
    isErrorFindOrCopyBooking,
    hotelInfo,
  } = props;
  const [isMobileView] = useMediaQuery('(max-width: 765px)');
  const { isCancellable, isAmendable } = manageBookingParams.data.manageBooking;
  const displayOperaConfNumberCCUI =
    [Area.CCUI].includes(area) && !!operaConfNumber && (!isAmendable || !isCancellable);

  const secureBookingData = {
    basketReference: props?.basketReference as string,
    arrivalDate: props?.arrivalDate,
    area: props?.area,
    paymentOption: props?.paymentOption,
    bookingStatus: props?.basketStatus as string,
    bookingReference: props?.bookingReference,
    hotelInfo,
  };

  return (
    <>
      <Card {...cardWrapperStyle}>
        {!isMobileView && props?.bookingStatus !== BC_RESERVATION_STATUS.CANCELLED && (
          <SecureBookingButton data={secureBookingData} />
        )}
        <Flex {...flexWrapperStyle}>
          <BookingActions
            {...props}
            shouldRenderStatusAndActions={isAmendPage && [Area.PI, Area.BB].includes(area)}
          />
          <Box {...{ ...containerStyle(props.bookingStatus) }}>
            {(isErrorFindOrCopyBooking ||
              (displayOperaConfNumberCCUI && props.bookingType === BOOKING_TYPE.UPCOMING)) && (
              <Flex {...notificationFlexStyle}>
                <Box {...boxNotificationStyle}>
                  <Notification
                    status="warning"
                    description={
                      isErrorFindOrCopyBooking
                        ? t('errors.sorry')
                        : t('ccui.managebooking.notification.goToOpera')
                    }
                    variant="alert"
                    maxW="full"
                    svg={<Alert />}
                  />
                </Box>
              </Flex>
            )}
            {props.isChangedPaymentApplied &&
              (props.basketStatus === BASKET_STATUS.PAY_PENDING ||
                props.basketStatus === BASKET_STATUS.COMPLETED) &&
              props.paymentOption !== paymentOptions.RESERVE_WITHOUT_CARD &&
              props.bookingType === BOOKING_TYPE.UPCOMING && (
                <Flex {...notificationFlexStyle}>
                  <Box {...boxNotificationStyle}>
                    <Notification
                      status="success"
                      description={t('ccui.manageBooking.notification.paymentMethodUpdated')}
                      variant="success"
                      maxW="full"
                      svg={<Success />}
                    />
                  </Box>
                </Flex>
              )}
            <Flex {...{ ...containerFlexStyle(props.bookingStatus) }}>
              {isMobileView && props?.bookingStatus !== BC_RESERVATION_STATUS.CANCELLED && (
                <SecureBookingButton data={secureBookingData} />
              )}
              {props.bookingStatus !== BC_RESERVATION_STATUS.CANCELLED && (
                <HotelDetails {...props} />
              )}
              <BookingDetails {...props} />
            </Flex>
          </Box>
        </Flex>
      </Card>
    </>
  );
}

const cardWrapperStyle = {
  wrap: 'wrap',
  padding: 'lg',
} as FlexProps;

const flexWrapperStyle = {
  display: 'flex',
  boxShadow: 'none',
  flexDirection: { mobile: 'column', lg: 'row' },
  padding: 0,
  width: '100%',
} as FlexProps;

const boxNotificationStyle = {
  marginBottom: 'var(--chakra-space-lg)',
  width: '100%',
};
const notificationFlexStyle = {
  display: 'flex',
  flexDirection: 'row',
} as FlexProps;
const containerFlexStyle = (bookingStatus: string) => {
  return {
    display:
      bookingStatus === BC_RESERVATION_STATUS.CANCELLED ? { mobile: 'flex', lg: 'grid' } : 'flex',
    gridTemplateColumns: { lg: '1fr 1fr' },
    flexDirection: { mobile: 'column', lg: 'row' },
    justifyContent: 'space-between',
  } as FlexProps;
};

const containerStyle = (bookingStatus: string) => {
  return {
    marginTop: { mobile: 'var(--chakra-space-4)', lg: 0 },
    ml: { mobile: 0, lg: bookingStatus === BC_RESERVATION_STATUS.CANCELLED ? 0 : '5xl' },
    width: '100%',
  } as FlexProps;
};
