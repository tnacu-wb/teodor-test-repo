import type { GridProps, TextProps } from '@chakra-ui/react';
import { Box, Grid, Text } from '@chakra-ui/react';
import type { BookingSummaryStayDatesInformationProps } from '@whitbread-eos/api';
import { formatDataTestId, formatDate, useSemanticTypography } from '@whitbread-eos/utils';

export interface Props extends BookingSummaryStayDatesInformationProps {
  t: (x: string, y?: { [key: string]: string }) => string;
  prefixDataTestId?: string;
  language?: string;
}

export default function BookingSummaryStayDatesInformation({
  arrivalDate,
  departureDate,
  noNights,
  t,
  prefixDataTestId,
  language = 'en',
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'StayDatesInformation');
  const getTypographyProps = useSemanticTypography();

  return (
    <Grid {...detailsWrapperStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Box>
        <Text
          {...infoLayoutStyle}
          {...getTypographyProps(infoEmphasisLegacyTypography, infoEmphasisSemanticTypography)}
        >
          {t('booking.summary.arrival')}
        </Text>
        <Text
          {...infoLayoutStyle}
          {...getTypographyProps(infoRegularLegacyTypography, infoRegularSemanticTypography)}
          data-testid={formatDataTestId(baseDataTestId, 'ArrivalDate')}
        >
          {arrivalDate ? formatDate(arrivalDate, 'E dd MMM yyyy', language) : ' '}
        </Text>
      </Box>
      <Box pt="md">
        <Text
          {...infoLayoutStyle}
          {...getTypographyProps(infoEmphasisLegacyTypography, infoEmphasisSemanticTypography)}
        >
          {t('booking.summary.departure')}
        </Text>
        <Text
          {...infoLayoutStyle}
          {...getTypographyProps(infoRegularLegacyTypography, infoRegularSemanticTypography)}
          data-testid={formatDataTestId(baseDataTestId, 'DepartureDate')}
        >
          {formatDate(departureDate, 'E dd MMM yyyy', language)}
        </Text>
      </Box>
      <Box pt="md">
        <Text
          {...infoLayoutStyle}
          {...getTypographyProps(infoEmphasisLegacyTypography, infoEmphasisSemanticTypography)}
        >
          {t('booking.summary.totalNights')}
        </Text>
        <Text
          {...infoLayoutStyle}
          {...getTypographyProps(infoRegularLegacyTypography, infoRegularSemanticTypography)}
          data-testid={formatDataTestId(baseDataTestId, 'NightsNumber')}
        >
          {`${noNights} ${
            noNights === 1 ? `${t('booking.summary.night')}` : `${t('booking.summary.nights')}`
          }`}
        </Text>
      </Box>
    </Grid>
  );
}

const detailsWrapperStyle = {
  direction: 'column',
  gridTemplateColumns: 'auto',
  pt: { mobile: 'md', lg: 'lg' },
} as GridProps;

const infoLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const infoEmphasisLegacyTypography = {
  lineHeight: '3',
  fontWeight: 'semibold',
} as TextProps;

const infoEmphasisSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as TextProps;

const infoRegularLegacyTypography = {
  lineHeight: '3',
  fontWeight: 'normal',
} as TextProps;

const infoRegularSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;
