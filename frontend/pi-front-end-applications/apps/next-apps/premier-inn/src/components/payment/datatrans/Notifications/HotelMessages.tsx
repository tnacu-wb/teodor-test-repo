import { Box } from '@chakra-ui/react';
import { Info, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

interface Message {
  infoMsg: string;
  indexOrder: number;
}

interface Props {
  messages: Message[];
}

export function HotelMessages({ messages }: Readonly<Props>) {
  if (!messages.length) return null;

  return (
    <Box pt="sm" data-testid="BookingSummary-InfoMessages">
      {messages.map((item) => {
        if (!item.infoMsg) return null;
        return (
          <Box mt="md" key={item.indexOrder}>
            <Notification
              maxWidth="full"
              variant="info"
              status="info"
              description={<Box className="formatLinks">{renderSanitizedHtml(item.infoMsg)}</Box>}
              svg={<Info />}
            />
          </Box>
        );
      })}
    </Box>
  );
}
