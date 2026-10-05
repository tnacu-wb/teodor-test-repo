import { Box, BoxProps, Text } from '@chakra-ui/react';

import { formatDataTestId } from '../../utils/formatters';

interface Props extends BoxProps {
  t: (x: string, y?: { [key: string]: string }) => string;
}

export default function BookingNotConfirmed({ t }: Readonly<Props>) {
  const baseDataTestId = 'BookingNotConfirmed';

  return (
    <Box {...contentWrapperStyle} sx={{ '@media print': { display: 'none' } }}>
      <Text {...titleStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
        {t('booking.confirmation.sorry')}
      </Text>
    </Box>
  );
}
const contentWrapperStyle = {
  w: 'full',
};

const titleStyle = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
  mb: 'md',
};
