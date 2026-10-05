import { Box, BoxProps, Text } from '@chakra-ui/react';

interface Props {
  label: string;
  testId: string;
  styles?: BoxProps;
}

export default function HotelDiscountApplied({ label, testId, styles }: Readonly<Props>) {
  return (
    <Box {...styles}>
      <Text {...discountAppliedlabelStyles} data-testid={testId}>
        {label}
      </Text>
    </Box>
  );
}

const discountAppliedlabelStyles = {
  color: 'discountApplied',
  fontSize: {
    mobile: 'xs',
    md: 'sm',
  },
  fontWeight: {
    mobile: 'bold',
    md: 'semibold',
  },
  lineHeight: '2',
} as BoxProps;
