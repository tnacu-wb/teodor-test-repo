import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import type { BookingSummaryRateInformationProps } from '@whitbread-eos/api';
import { HotelBrand } from '@whitbread-eos/api';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';

export interface Props extends BookingSummaryRateInformationProps {
  t: (x: string, y?: { [key: string]: string }) => string;
  prefixDataTestId?: string;
  brand?: string;
  rateDescription?: string;
}

export default function BookingSummaryRateInformation({
  rate,
  t,
  prefixDataTestId,
  brand,
  rateDescription,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'RateInformation');
  const getTypographyProps = useSemanticTypography();

  return (
    <Flex {...rateContainerStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Text
        {...rateLabelLayoutStyle}
        {...getTypographyProps(rateLabelLegacyTypography, rateLabelSemanticTypography)}
        data-testid={formatDataTestId(baseDataTestId, 'Label')}
      >
        {t('booking.summary.tarif')}: {`${brand === HotelBrand.HUB ? 'hub' : ''} ${rate}`}
      </Text>
      <Text
        {...rateDescriptionLayoutStyle}
        {...getTypographyProps(rateDescriptionLegacyTypography, rateDescriptionSemanticTypography)}
        data-testid={formatDataTestId(baseDataTestId, 'RateDescription')}
      >
        {rateDescription}
      </Text>
    </Flex>
  );
}

const rateLabelLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const rateLabelLegacyTypography = {
  fontSize: 'md',
  lineHeight: 3,
  fontWeight: 'semibold',
} as TextProps;

const rateLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as TextProps;

const rateDescriptionLayoutStyle = {
  color: 'darkGrey1',
  pt: 'xs',
} as TextProps;

const rateDescriptionLegacyTypography = {
  fontSize: 'md',
  lineHeight: 2,
  fontWeight: 'normal',
} as TextProps;

const rateDescriptionSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const rateContainerStyle = {
  pr: { md: 'md', lg: '0' },
  flexDirection: 'column',
} as FlexProps;
