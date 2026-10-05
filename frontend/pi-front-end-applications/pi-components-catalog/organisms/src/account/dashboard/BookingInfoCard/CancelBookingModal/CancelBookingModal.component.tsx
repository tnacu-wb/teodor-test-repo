import type { BoxProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import { Area } from '@whitbread-eos/api';
import {
  Button,
  ButtonProps,
  Info,
  ModalVariants,
  Notification,
  Tick24,
} from '@whitbread-eos/atoms';
import { isStringValid, useCustomLocale } from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { de } from 'date-fns/locale';
import { useTranslation } from 'next-i18next';
import type { Dispatch, SetStateAction } from 'react';
import { useEffect, useState } from 'react';

export interface Props {
  hotelName: string;
  bookedFor: string;
  arrivalDate: string;
  noNights: number;
  bookedBy: string;
  isModalVisible: boolean;
  onModalClose: Dispatch<SetStateAction<boolean>>;
  isError: boolean;
  error: unknown;
  onClickKeepBooking: () => void;
  onClickCancelBooking: () => void;
  cancelReservationData: string | undefined | null;
  area?: Area;
  backBtnText: string;
  backBtnUrl?: string;
  onClickBack?: () => void;
  bookingReference?: string;
  isCancelDisabled?: boolean;
}

export default function CancelBookingModal({
  hotelName,
  bookedFor,
  arrivalDate,
  noNights,
  isModalVisible,
  onModalClose,
  isError,
  error,
  onClickKeepBooking,
  onClickCancelBooking,
  cancelReservationData,
  area,
  backBtnText,
  backBtnUrl,
  bookedBy,
  onClickBack,
  bookingReference,
  isCancelDisabled,
}: Readonly<Props>) {
  const { country, language } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const isWindowDefined = typeof window !== 'undefined';
  const [origin, setOrigin] = useState('');

  let fmtArrivalDate = arrivalDate;
  useEffect(() => {
    if (language === 'de' && !Number.isNaN(new Date(arrivalDate).getTime())) {
      fmtArrivalDate = format(new Date(arrivalDate), 'E dd MMM yyyy', { locale: de });
    }
  }, [arrivalDate]);

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  const noNightsText = `${noNights} ${t(
    noNights > 1 ? 'dashboard.bookings.nights' : 'dashboard.bookings.night'
  )}`;

  return (
    <ModalVariants
      onClose={() => onModalClose(!isModalVisible)}
      variant="info"
      isOpen={isModalVisible}
      variantProps={{ title: '', delimiter: false }}
    >
      <Box {...modalStyles}>{renderModalContent()}</Box>
    </ModalVariants>
  );

  function renderModalContent() {
    if (isError) {
      return <Text>{(error as Error).message}</Text>;
    }
    return (
      <Flex flexDir="column" data-testid="CancelBookingModalContainer">
        {renderCancelBookingNotification()}
        <Text {...titleStyle} mt="0">
          {t('dashboard.bookings.cancelModalTitle')}
        </Text>
        <Text {...cancelModalDescriptionStyle}>
          {t('dashboard.bookings.cancelModalDescription')}
        </Text>
        <Text className="sessioncamhidetext assist-no-show" {...titleStyle}>
          {bookedFor}
        </Text>
        <Text {...bookingDescriptionStyle}>{hotelName}</Text>
        <Text {...bookingDescriptionStyle} mt="0">
          {fmtArrivalDate}, {noNightsText}
        </Text>
        {bookedBy?.length > 0 && (
          <Text {...titleStyle}>{`${t('dashboard.bookings.bookerLabel')}: ${bookedBy}`}</Text>
        )}
        {isStringValid(cancelReservationData) && area === Area.PI && (
          <Button
            data-testid="CancelBookingModalBackButton"
            {...backToHomePageButtonStyle}
            onClick={() => {
              if (onClickBack) {
                onClickBack();
                return;
              }
              window.location.href = `${origin}/${country}/${language}/${backBtnUrl}`;
            }}
          >
            {backBtnText}
          </Button>
        )}
        {isStringValid(cancelReservationData) && area === Area.BB && (
          <Button
            data-testid="CancelBookingModalBackButton"
            {...backToHomePageButtonStyle}
            onClick={() => onModalClose(!isModalVisible)}
          >
            {backBtnText}
          </Button>
        )}
        {!isStringValid(cancelReservationData) && (
          <Box>
            <Button
              {...cancelButtonStyle}
              onClick={onClickCancelBooking}
              isDisabled={isCancelDisabled}
            >
              {t('dashboard.bookings.cancelButton')}
            </Button>

            <Button
              {...amendButtonStyle}
              onClick={onClickKeepBooking}
              isDisabled={isCancelDisabled}
            >
              {t('dashboard.bookings.keepBookingButton')}
            </Button>
          </Box>
        )}
      </Flex>
    );
  }

  function renderCancelBookingNotification() {
    if (cancelReservationData === undefined) {
      return null;
    } else if (cancelReservationData != null) {
      return (
        <Box mb="md">
          <Notification
            maxWidth="full"
            variant="success"
            status="success"
            description={
              <Text as="span" {...successMessageNotificationStyle}>
                {`${t('dashboard.bookings.bookingCancelledNotification')} `}
                <Text as="span" fontWeight="semibold">
                  {/* DNRQ-46771 - use bookingReference for now, for - Manage Cancel Booking Modal */}
                  {isStringValid(bookingReference) ? bookingReference : cancelReservationData}
                </Text>
              </Text>
            }
            svg={<Tick24 />}
          />
        </Box>
      );
    } else {
      return (
        <Box mb="md">
          <Notification
            maxWidth="full"
            variant="error"
            status="error"
            description={t('dashboard.bookings.bookingCancelledError')}
            title={t('dashboard.bookings.bookingCancelledErrorTitle')}
            svg={<Info color="var(--chakra-colors-error)" />}
          />
        </Box>
      );
    }
  }
}

const successMessageNotificationStyle = {
  fontSize: 'sm',
  lineHeight: '2',
  color: 'darkGrey1',
  fontFamily: 'body',
} as TextProps;

const modalStyles = {
  w: { base: '100vw', sm: '18.125rem' },
  px: 'md',
  pb: 'lg',
} as BoxProps;

const titleStyle = {
  mt: 'md',
  color: 'darkGrey1',
  fontSize: 'md',
  lineHeight: '3',
  fontWeight: 'semibold',
} as TextProps;

const cancelModalDescriptionStyle = {
  mt: 'sm',
  color: 'darkGrey2',
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
} as TextProps;

const bookingDescriptionStyle = {
  mt: 'sm',
  color: 'darkGrey2',
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
} as TextProps;

const cancelButtonStyle = {
  size: 'sm',
  variant: 'primary',
  w: '100%',
  mt: 'xl',
} as ButtonProps;

const backToHomePageButtonStyle = {
  size: 'sm',
  variant: 'secondary',
  w: '100%',
  mt: 'xl',
} as ButtonProps;

const amendButtonStyle = {
  size: 'sm',
  variant: 'tertiary',
  w: '100%',
  mt: 'md',
} as ButtonProps;
