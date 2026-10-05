import { Box, BoxProps, Text } from '@chakra-ui/react';

interface Props {
  label: string;
  testId: string;
  styles?: BoxProps;
}

export default function HotelLastFewRooms({ label, testId, styles }: Readonly<Props>) {
  return (
    <Box {...styles}>
      <Text {...lastFewRoomsStyles} data-testid={testId}>
        {label}
      </Text>
    </Box>
  );
}

const lastFewRoomsStyles = {
  width: 'auto',
  verticalAlign: 'top',
  color: 'alert',
  fontSize: 'sm',
  fontWeight: 'semibold',
  lineHeight: 'normal',
  margin: '0',
} as BoxProps;
