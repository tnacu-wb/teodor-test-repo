import {
  AccordionPanelProps,
  AccordionProps,
  Box,
  BoxProps,
  Flex,
  Text,
  TextProps,
} from '@chakra-ui/react';
import type {
  AmendRoomsAndGuestsLabels,
  BookingConfirmationType,
  BookingSummaryLabels,
  BookingSummaryRoomInformationProps,
  StayDatesLabels,
  SummaryOfPaymentsLabels,
  SummaryOfPaymentsType,
  ExtrasPackagesPrices,
} from '@whitbread-eos/api';
import { Area } from '@whitbread-eos/api';
import { Accordion, Button, ButtonProps, Info, Notification } from '@whitbread-eos/atoms';
import { AmendEmailAddressModal } from '@whitbread-eos/molecules';
import { formatDataTestId, renderSanitizedHtml, useScreenSize } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useState } from 'react';

import BookingSummaryCost from './BookingSummaryCost';
import { BookingSummary } from './index';

interface Props extends BoxProps {
  language: string;
  bookingInformation: BookingConfirmationType;
  roomsPackages: BookingSummaryRoomInformationProps[];
  originalArrivalDate: string;
  originalDepartureDate: string;
  stayDatesLabels: StayDatesLabels;
  roomsAndGuestsLabels: AmendRoomsAndGuestsLabels;
  bookingSummaryLabels: BookingSummaryLabels;
  summaryOfPayments: SummaryOfPaymentsType;
  summaryOfPaymentsLabels: SummaryOfPaymentsLabels;
  isConfirmButtonEnabled: boolean;
  onConfirmChanges: () => void;
  handleRedirectToAmendPayment?: () => void;
  isRedirectToAmendPaymentEnabled?: boolean;
  hideConfirmButton?: boolean;
  variant?: string;
  setEmailCallback: React.Dispatch<React.SetStateAction<string>>;
  showEciLcoNotification?: boolean;
  extrasItemsPrices?: ExtrasPackagesPrices;
  isCityTaxEnabled: boolean;
  isCityTaxAmendEnabled?: boolean;
}

export default function BookingSummaryWrapper({
  language,
  bookingInformation,
  roomsPackages,
  originalArrivalDate,
  originalDepartureDate,
  stayDatesLabels,
  roomsAndGuestsLabels,
  bookingSummaryLabels,
  summaryOfPayments,
  summaryOfPaymentsLabels,
  isConfirmButtonEnabled,
  onConfirmChanges,
  handleRedirectToAmendPayment,
  isRedirectToAmendPaymentEnabled,
  hideConfirmButton,
  variant,
  setEmailCallback,
  showEciLcoNotification,
  extrasItemsPrices,
  isCityTaxEnabled,
  isCityTaxAmendEnabled,
}: Readonly<Props>) {
  const baseDataTestId = 'amend-booking-summary';
  const { t } = useTranslation();

  const bookingSummaryComponent = (
    <BookingSummary
      language={language}
      baseDataTestId={baseDataTestId}
      bookingInformation={bookingInformation}
      roomsPackages={roomsPackages}
      originalArrivalDate={originalArrivalDate}
      originalDepartureDate={originalDepartureDate}
      stayDatesLabels={stayDatesLabels}
      bookingSummaryLabels={bookingSummaryLabels}
      roomsAndGuestsLabels={roomsAndGuestsLabels}
      extrasItemsPrices={extrasItemsPrices}
    />
  );
  const accordionItems = [
    {
      title: bookingSummaryLabels?.expandDetail,
      content: bookingSummaryComponent,
    },
  ];
  const { isLessThanLg } = useScreenSize();
  const isCCUI = variant === Area.CCUI;
  const [isModalVisible, setIsModalVisible] = useState(false);
  const onModalClose = () => setIsModalVisible((prevState) => !prevState);

  const handleAmendSubmit = () => {
    if (isCCUI) {
      setIsModalVisible(true);
      return;
    }
    onConfirmChanges();
  };

  return (
    <Flex data-testid={formatDataTestId(baseDataTestId, 'section')} direction="column">
      <Box {...bookingSummaryStyle}>
        <Text data-testid={formatDataTestId(baseDataTestId, 'title')} {...titleTextStyle}>
          {bookingSummaryLabels.title}
        </Text>
        {isLessThanLg ? (
          <Accordion
            accordionItems={accordionItems}
            bgColor={'baseWhite'}
            accordionOverwriteStyles={accordionOverwriteStyles}
          />
        ) : (
          bookingSummaryComponent
        )}
        {summaryOfPayments && (
          <BookingSummaryCost
            language={language}
            baseDataTestId={baseDataTestId}
            currency={bookingInformation.currencyCode}
            summaryOfPayments={summaryOfPayments}
            summaryOfPaymentsLabels={summaryOfPaymentsLabels}
            isCityTaxEnabled={isCityTaxEnabled}
            cityTaxTotal={bookingInformation.cityTaxTotal}
            isCityTaxAmendEnabled={isCityTaxAmendEnabled}
          />
        )}
        {isLessThanLg && !hideConfirmButton && (
          <Button
            size="sm"
            variant="primary"
            name="confirm-changes-button"
            {...buttonStyles}
            width="full"
            isDisabled={!isConfirmButtonEnabled}
            onClick={
              isRedirectToAmendPaymentEnabled ? handleRedirectToAmendPayment : handleAmendSubmit
            }
          >
            {isRedirectToAmendPaymentEnabled
              ? bookingSummaryLabels.continueToPaymentLabel
              : bookingSummaryLabels.confirmChangesLabel}
          </Button>
        )}
      </Box>
      {showEciLcoNotification && (
        <Box
          mt="lg"
          data-testid={formatDataTestId(baseDataTestId, 'Notification-Info-EciLco')}
          {...notificationInfoEciStyle}
        >
          <Notification
            variant="info"
            status="info"
            description={renderSanitizedHtml(t('ancillaries.amend.removal.notification')) as string}
            svg={<Info />}
          />
        </Box>
      )}
      {!isLessThanLg && !hideConfirmButton && (
        <Button
          variant="primary"
          size="sm"
          name="confirm-changes-button"
          {...buttonStyles}
          mb="md"
          isDisabled={!isConfirmButtonEnabled}
          onClick={
            isRedirectToAmendPaymentEnabled ? handleRedirectToAmendPayment : handleAmendSubmit
          }
        >
          {isRedirectToAmendPaymentEnabled
            ? bookingSummaryLabels.continueToPaymentLabel
            : bookingSummaryLabels.confirmChangesLabel}
        </Button>
      )}
      {isCCUI && (
        <AmendEmailAddressModal
          isModalVisible={isModalVisible}
          bookingEmail={bookingInformation.reservationByIdList[0]?.billing?.email ?? ''}
          onConfirmChanges={onConfirmChanges}
          onModalClose={onModalClose}
          setEmailCallback={setEmailCallback}
        />
      )}
    </Flex>
  );
}

const accordionContainerStyle = {
  borderX: 'none',
} as AccordionProps;

const accordionItemButtonStyle = {
  p: 'var(--chakra-space-lg) 0 var(--chakra-space-xs) 0',
  borderBottom: '1px solid var(--chakra-colors-lightGrey4)',
} as BoxProps & ButtonProps;

const accordionItemTextStyle = {
  fontSize: 'md',
  fontWeight: 'bold',
  lineHeight: '3',
} as TextProps;

const accordionItemPanelStyle = {
  padding: 0,
} as AccordionPanelProps;

const accordionItemStyle = {
  border: 0,
};

const accordionOverwriteStyles = {
  container: accordionContainerStyle,
  button: accordionItemButtonStyle,
  text: accordionItemTextStyle,
  panel: accordionItemPanelStyle,
  item: accordionItemStyle,
};

const bookingSummaryStyle = {
  width: {
    base: 'full',
    lg: '18.063rem',
    xl: '19.313rem',
  },

  mt: {
    mobile: '0px',
    lg: '5xl',
  },
  borderRadius: '0.313rem',
  border: '1px solid var(--chakra-colors-lightGrey3)',
  pt: 'lg',
  pb: 'md',
  px: 'md',
  color: 'darkGrey1',
};

const buttonStyles = {
  w: 'full',
  mt: 'lg',
};

const titleTextStyle = {
  fontSize: '2xl',
  lineHeight: '4',
  color: 'darkGrey1',
  as: 'h3',
  fontWeight: 'bold',
} as TextProps;

const notificationInfoEciStyle = {
  width: {
    base: 'full',
    lg: '18.063rem',
    xl: '19.313rem',
  },
};
