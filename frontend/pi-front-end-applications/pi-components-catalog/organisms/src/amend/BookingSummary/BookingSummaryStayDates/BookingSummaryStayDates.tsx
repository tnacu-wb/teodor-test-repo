import { Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import type { StayDatesLabels } from '@whitbread-eos/api';
import { formatDataTestId, formatDate, getNightsNumber } from '@whitbread-eos/utils';

import { isStayDatesSectionUpdated } from '../../helpers';

export interface BookingSummaryStayDatesProps {
  language: string;
  baseDataTestId: string;
  labels: StayDatesLabels;
  arrivalDate: string;
  departureDate: string;
  originalArrivalDate: string;
  originalDepartureDate: string;
}
export const BookingSummaryStayDates = (props: BookingSummaryStayDatesProps) => {
  const {
    language,
    baseDataTestId,
    labels,
    arrivalDate,
    departureDate,
    originalArrivalDate,
    originalDepartureDate,
  } = props;

  const dateFormat = 'EEE dd MMM yyyy';
  const noNights = getNightsNumber(arrivalDate, departureDate);
  const originalNoNights = getNightsNumber(originalArrivalDate, originalDepartureDate);
  const stayDatesSectionHasUpdates = isStayDatesSectionUpdated(
    originalArrivalDate,
    originalDepartureDate,
    arrivalDate,
    departureDate
  );

  return (
    <Flex
      data-testid={formatDataTestId(baseDataTestId, 'stay-dates-wrapper')}
      {...detailsWrapperStyle}
    >
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'stay-dates-title')}
        {...subtitleTextStyle}
      >
        {labels.yourStayDatesTitle}
      </Text>
      {stayDatesSectionHasUpdates &&
        displayDates(originalArrivalDate, originalDepartureDate, originalNoNights, true)}
      {displayDates(arrivalDate, departureDate, noNights)}
    </Flex>
  );

  function displayDates(
    arrival: string,
    departure: string,
    numberOfNights: number,
    strikeOut?: boolean
  ) {
    const textStyles = {
      ...infoTextStyle,
      ...(strikeOut && { textDecoration: 'line-through', color: 'lightGrey1' }),
    };
    return (
      <>
        <Text data-testid={formatDataTestId(baseDataTestId, 'stay-dates')} {...textStyles}>
          {`${formatDate(arrival, dateFormat, language)} - ${formatDate(
            departure,
            dateFormat,
            language
          )}`}
        </Text>
        <Text data-testid={formatDataTestId(baseDataTestId, 'number-of-nights')} {...textStyles}>
          {numberOfNights} {numberOfNights > 1 ? labels.nightsOption : labels.nightOption}
        </Text>
      </>
    );
  }
};

const detailsWrapperStyle = {
  direction: 'column',
  pb: { mobile: 'md', lg: 'lg' },
} as FlexProps;

const subtitleTextStyle = {
  pt: 'lg',
  pb: 'sm',
  fontSize: 'xl',
  lineHeight: '3',
  fontWeight: 'semibold',
};

const infoTextStyle = {
  lineHeight: '3',
  fontSize: 'md',
  color: 'darkGrey1',
  as: 'h6',
  fontWeight: 'normal',
} as TextProps;
