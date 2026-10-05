import { Box, BoxProps, Text } from '@chakra-ui/react';

interface Props {
  label: string;
  testId: string;
  styles?: BoxProps;
}

export default function HotelSoldOut({ label, testId, styles }: Readonly<Props>) {
  return (
    <Box {...styles}>
      <Text {...soldOutlabelStyles} data-testid={testId}>
        {label}
      </Text>
    </Box>
  );
}

const soldOutlabelStyles = {
  color: 'darkGrey1',
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
