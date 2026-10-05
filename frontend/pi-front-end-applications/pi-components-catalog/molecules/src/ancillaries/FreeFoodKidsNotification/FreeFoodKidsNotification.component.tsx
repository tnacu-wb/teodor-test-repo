import { Box } from '@chakra-ui/react';
import { Info, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  prefixDataTestId?: string;
  isDinnerIncluded?: boolean;
}

export default function FreeFoodKidsNotification({
  prefixDataTestId,
  isDinnerIncluded = false,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'FreeFoodKidsNotification');

  let header = t('upsell.notification.kids.header');
  let subHeader = t('upsell.notification.kids.subheader');

  //TODO: replace the keys with right AEM keys once AEM task is completed
  if (isDinnerIncluded) {
    header = t('upsell.notification.kids.header');
    subHeader = t('upsell.notification.kids.subheader');
  }

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Notification
        maxWidth="full"
        variant="info"
        status="info"
        title={header}
        description={subHeader}
        svg={<Info />}
      />
    </Box>
  );
}
