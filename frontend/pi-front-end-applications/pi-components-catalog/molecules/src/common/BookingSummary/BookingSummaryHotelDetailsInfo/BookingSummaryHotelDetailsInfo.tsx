import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import { BookingSummaryHotelInformationProps } from '@whitbread-eos/api';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';

export interface Props extends BookingSummaryHotelInformationProps {
  prefixDataTestId?: string;
}

export default function BookingSummaryHotelDetailsInfo({
  hotelName,
  hotelAddress,
  prefixDataTestId,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'HotelInformation');
  const getTypographyProps = useSemanticTypography();

  return (
    <Flex {...detailsWrapperStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Text
        {...hotelNameTextStyle}
        {...getTypographyProps(hotelNameLegacyTypography, hotelNameSemanticTypography)}
        data-testid={formatDataTestId(baseDataTestId, 'HotelName')}
      >
        {hotelName}
      </Text>
      <Box mt="sm" data-testid={formatDataTestId(baseDataTestId, 'HotelAddress')}>
        <Flex {...addressWrapperStyle}>
          {hotelAddress.map((address: string) => (
            <Text
              key={address}
              {...infoTextStyle}
              {...getTypographyProps(infoLegacyTypography, infoSemanticTypography)}
            >
              {address}
            </Text>
          ))}
        </Flex>
        <Text
          {...addressWrapperMobile}
          {...getTypographyProps(infoLegacyTypography, infoSemanticTypography)}
        >
          {hotelAddress.join(', ')}
        </Text>
      </Box>
    </Flex>
  );
}

const detailsWrapperStyle = {
  direction: 'column',
  pb: { mobile: 'md', lg: 'lg' },
} as FlexProps;

const addressWrapperStyle = {
  direction: 'column',
  display: { mobile: 'none', lg: 'flex' },
} as FlexProps;

const infoTextStyle = {
  color: 'darkGrey1',
  as: 'h6',
} as TextProps;

const infoLegacyTypography = {
  lineHeight: '3',
  fontSize: 'md',
  fontWeight: 'normal',
} as TextProps;

const infoSemanticTypography = { textStyle: 'body-m-regular' };

const hotelNameTextStyle = {
  ...infoTextStyle,
  as: 'h5',
} as TextProps;

const hotelNameLegacyTypography = {
  lineHeight: '3',
  fontSize: 'lg',
  fontWeight: 'semibold',
} as TextProps;

const hotelNameSemanticTypography = { textStyle: 'body-l-emphasis' };

const addressWrapperMobile = {
  ...infoTextStyle,
  display: { mobile: 'flex', lg: 'none' },
};
