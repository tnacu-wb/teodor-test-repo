import { Box } from '@chakra-ui/react';
import { Info, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';

interface Props {
  messageDescription?: string;
  messageTitle?: string;
  prefixDataTestId?: string;
}

export default function RestaurantUnavailableNotification({
  messageDescription,
  messageTitle,
  prefixDataTestId,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'RestaurantUnavailableNotification');
  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Notification
        maxWidth="full"
        variant="info"
        status="info"
        title={messageTitle}
        description={messageDescription}
        svg={<Info />}
      />
    </Box>
  );
}
