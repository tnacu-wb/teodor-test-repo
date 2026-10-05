import type { BoxProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import {
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  paymentSteps,
} from '@whitbread-eos/api';
import { ChevronDown24, ChevronUp24, Icon } from '@whitbread-eos/atoms';
import { BookingSummaryInfoMessages } from '@whitbread-eos/molecules';
import {
  formatCurrency,
  formatDataTestId,
  formatDate,
  formatPrice,
  formatUrlTermsConditions,
  isStringValid,
  renderSanitizedHtml,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useState } from 'react';

import BookingSummaryCard from '../BookingSummaryCard';

export interface Props {
  reservationDetails: BookingDataReservationDetailsProps;
  bookingSummaryData: BookingSummaryDataProps;
  totalCostAmount: number;
  prefixDataTestId?: string;
  t: (x: string, y?: { [key: string]: string }) => string;
  language: string | undefined;
  infoMessages?: string[];
  announcement?: any;
  taxesMessage?: string;
  isExtrasDisplayed?: boolean;
  isSoftBundlesVisible?: boolean;
  isCityTaxBreakdownEnabled?: boolean;
}

export default function BookingSummaryMobile({
  reservationDetails,
  bookingSummaryData,
  totalCostAmount,
  t,
  language,
  prefixDataTestId,
  infoMessages,
  announcement,
  taxesMessage,
  isExtrasDisplayed,
  isSoftBundlesVisible,
  isCityTaxBreakdownEnabled,
}: Readonly<Props>) {
  const { currency, noNights, departureDate, arrivalDate, noRooms } = reservationDetails;
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const getTypographyProps = useSemanticTypography();

  return (
    <Box
      {...bookingSummaryCardStyle}
      data-testid={formatDataTestId(prefixDataTestId, 'SectionWrapper')}
    >
      {renderSummary()}
      {isExpanded && (
        <>
          <BookingSummaryCard
            t={t}
            language={language}
            totalCostAmount={totalCostAmount}
            bookingSummaryData={bookingSummaryData}
            prefixDataTestId={prefixDataTestId}
            currencyCode={reservationDetails.currency}
            taxesMessage={taxesMessage}
            isExtrasDisplayed={isExtrasDisplayed}
            isSoftBundlesVisible={isSoftBundlesVisible}
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
          />
          {announcement}
          {bookingSummaryData?.paymentStepState !== paymentSteps.CARD_DETAILS && (
            <>
              {bookingSummaryData?.termsAndConditionsText && (
                <Box
                  sx={termsAndConditionsStyle}
                  {...getTypographyProps(
                    termsAndConditionsLegacyTypography,
                    termsAndConditionsSemanticTypography
                  )}
                  data-testid="termsAndConditionsPayment"
                  className="formatLinks"
                >
                  {renderSanitizedHtml(
                    formatUrlTermsConditions(bookingSummaryData?.termsAndConditionsText)
                  )}
                </Box>
              )}
            </>
          )}
          <BookingSummaryInfoMessages
            infoMessages={infoMessages}
            prefixDataTestId={prefixDataTestId}
          />
        </>
      )}
    </Box>
  );

  function renderSummary() {
    const formatArrivalDate = isStringValid(arrivalDate)
      ? formatDate(arrivalDate, 'dd MMM', language)
      : '';
    const formatDepartureDate = isStringValid(departureDate)
      ? formatDate(departureDate, 'dd MMM', language)
      : '';
    const dateFormat = `${formatArrivalDate} - ${formatDepartureDate}`;

    const amountFormat = formatPrice(
      formatCurrency(currency),
      totalCostAmount.toFixed(2),
      language
    );

    const roomsFormat =
      noRooms === 1
        ? `${noRooms} ${t('hoteldetails.bookingsummary.room')}`
        : `${noRooms} ${t('hoteldetails.bookingsummary.rooms')}`;

    const nightsFormat =
      noNights === 1
        ? `${noNights} ${t('booking.summary.night')}`
        : `${noNights} ${t('booking.summary.nights')}`;

    const displayRate = `${t('booking.summary.rate')} ${bookingSummaryData?.rateInformation?.rate}`;

    return (
      <Flex
        {...summaryContainerStyle}
        onClick={() => setIsExpanded(!isExpanded)}
        data-testid={formatDataTestId(prefixDataTestId, 'SectionHeader')}
      >
        <Flex
          direction="column"
          display={{ mobile: 'flex', sm: 'none' }}
          data-testid={formatDataTestId(prefixDataTestId, 'HeaderLine2Lines')}
          {...bookDescriptionLayoutStyle}
          {...getTypographyProps(
            bookDescriptionLegacyTypography,
            bookDescriptionSemanticTypography
          )}
        >
          <Text
            data-testid={formatDataTestId(prefixDataTestId, 'HeaderLine-StayInfo')}
          >{`${roomsFormat}, ${nightsFormat} | ${dateFormat}`}</Text>
          <Text data-testid={formatDataTestId(prefixDataTestId, 'HeaderLine-CostAndRate')}>
            <Text as="span" data-testid={formatDataTestId(prefixDataTestId, 'HeaderLine-Amount')}>
              {amountFormat}
            </Text>
            <Text as="span" data-testid={formatDataTestId(prefixDataTestId, 'HeaderLine-Rate')}>
              , {displayRate}
            </Text>
          </Text>
        </Flex>
        <Text
          {...bookDescriptionLayoutStyle}
          {...getTypographyProps(
            bookDescriptionLegacyTypography,
            bookDescriptionSemanticTypography
          )}
          display={{ mobile: 'none', sm: 'block' }}
          data-testid={formatDataTestId(prefixDataTestId, 'HeaderLine1Line')}
        >
          {`${roomsFormat}, ${nightsFormat} | ${dateFormat} | ${amountFormat} | ${displayRate}`}
        </Text>
        <Box
          ml="md"
          alignSelf="flex-start"
          data-testid={formatDataTestId(prefixDataTestId, 'ExpandButton')}
        >
          {isExpanded ? (
            <Icon svg={<ChevronUp24 data-testid="isExpanded" />} />
          ) : (
            <Icon svg={<ChevronDown24 data-testid="isNotExpanded" />} />
          )}
        </Box>
      </Flex>
    );
  }
}

const bookingSummaryCardStyle = {
  bg: 'lightGrey5',
  p: 'md',
} as BoxProps;

const bookDescriptionLayoutStyle = {
  as: 'h6',
  color: 'darkGrey1',
} as TextProps;

const bookDescriptionLegacyTypography = {
  fontSize: 'md',
  lineHeight: '3',
} as TextProps;

const bookDescriptionSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as TextProps;

const summaryContainerStyle = {
  alignItems: 'center',
  justifyContent: { mobile: 'space-between', sm: 'center' },
  fontWeight: 'semibold',
} as BoxProps;

const termsAndConditionsLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
} as TextProps;

const termsAndConditionsSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const termsAndConditionsStyle = {
  mt: 'lg',
  a: {
    textDecoration: 'underline',
    color: 'zipSecondary',
  },
};
