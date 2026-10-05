import { Box } from '@chakra-ui/react';
import { Info, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

interface Props {
  isVisible: boolean;
  paymentFailedErrorMessage: string;
}

export function PaymentErrorNotification({
  isVisible,
  paymentFailedErrorMessage,
}: Readonly<Props>) {
  if (!isVisible) return null;

  return (
    <Box pt="lg">
      <Notification
        prefixDataTestId="Payment-Failed-Error"
        variant="error"
        status="error"
        description={<Box>{renderSanitizedHtml(paymentFailedErrorMessage)}</Box>}
        svg={<Info color="var(--chakra-colors-error)" />}
      />
    </Box>
  );
}
