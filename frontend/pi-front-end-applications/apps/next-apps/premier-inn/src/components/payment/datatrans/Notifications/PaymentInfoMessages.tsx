import { Box, BoxProps } from '@chakra-ui/react';
import { Info, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

interface Message {
  index: number;
  messagesNotif: string;
}

interface Props {
  messages: Message[];
}

export function PaymentInfoMessages({ messages }: Readonly<Props>) {
  if (!messages.length) return null;

  return (
    <Box mb="5xl" data-testid="PaymentType-InfoMessages">
      {messages.map((item) => (
        <Box mt="md" key={item.index}>
          <Notification
            variant="info"
            status="info"
            description={<Box>{renderSanitizedHtml(item.messagesNotif)}</Box>}
            svg={<Info />}
            wrapperStyles={paymentTypeInfoMsgStyle}
          />
        </Box>
      ))}
    </Box>
  );
}

const paymentTypeInfoMsgStyle = {
  w: { mobile: 'full', xs: 'full', md: '45rem', lg: '50.5rem', xl: 'full' },
} as BoxProps;
