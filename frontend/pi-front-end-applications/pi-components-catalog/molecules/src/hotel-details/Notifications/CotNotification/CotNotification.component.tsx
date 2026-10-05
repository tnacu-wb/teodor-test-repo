import { Box } from '@chakra-ui/react';
import { Alert, Info, Notification } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import { ReactElement } from 'react';

interface Props {
  cotAvailable: boolean;
}

export default function CotNotification({ cotAvailable }: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  return (
    <Box mb="md" data-testid="cot-notification">
      <Notification
        {...getNotificationPropsByType()}
        prefixDataTestId="cot-notification"
        isInnerHTML
        {...notificationStyles}
      />
    </Box>
  );

  function getNotificationPropsByType(): {
    description: string;
    variant: string;
    status: 'info' | 'warning';
    svg: ReactElement;
  } {
    if (!cotAvailable) {
      return {
        description: t('booking.cot.notAvailable'),
        variant: 'alert',
        status: 'warning',
        svg: <Alert />,
      };
    }

    return {
      description: t('booking.cot.available'),
      variant: 'info',
      status: 'info',
      svg: <Info />,
    };
  }
}

const notificationStyles = {
  maxW: { base: 'full', md: '100%' },
  lineHeight: '2',
};
