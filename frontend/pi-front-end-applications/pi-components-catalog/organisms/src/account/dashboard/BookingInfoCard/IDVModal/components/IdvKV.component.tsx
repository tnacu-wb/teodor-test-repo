import { Box, Text, TextProps } from '@chakra-ui/react';

type IdvKVType = {
  text: string;
  value: string;
  dataTestId: string;
};

const IdvKV = ({ text, value, dataTestId }: IdvKVType) => (
  <Box flex="1" data-testid={dataTestId}>
    <Text {...textStyle}>{text}</Text>
    <Text {...valueStyle}>{value}</Text>
  </Box>
);

const textStyle = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: '500',
  lineHeight: '1.5rem',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as TextProps;

const valueStyle = {
  color: 'darkGrey2',
  fontSize: 'sm',
  fontWeight: '400',
  lineHeight: '1.25rem',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as TextProps;

export default IdvKV;
