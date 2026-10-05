import { Box } from '@chakra-ui/react';
import { Accessible24, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

interface Props {
  data: string;
}

export default function AccessibleRoomNotificationComponent({ data }: Readonly<Props>) {
  if (!data) {
    return null;
  }

  return (
    <Box data-testid="hdp_accessibleRoomNotification">
      <Notification
        status="info"
        variant="accessible"
        svg={<Accessible24 />}
        maxW="full"
        lineHeight="2"
        prefixDataTestId="hdp_accessibleRoomNotification"
        description={
          <Box ml="7" data-testid="hdp_accessibleRoomNotificationList" className="formatLinks">
            {renderSanitizedHtml(data)}
          </Box>
        }
      />
    </Box>
  );
}
