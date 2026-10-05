import { Box } from '@chakra-ui/react';
import { Info, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export function HeaderNotification() {
  const { t } = useTranslation(['common']);

  return (
    <Notification
      maxWidth="full"
      variant="info"
      status="info"
      description={
        <Box className="formatLinks">{renderSanitizedHtml(t('booking.header.notification'))}</Box>
      }
      svg={<Info />}
      wrapperStyles={{ mb: 'xl' }}
    />
  );
}
