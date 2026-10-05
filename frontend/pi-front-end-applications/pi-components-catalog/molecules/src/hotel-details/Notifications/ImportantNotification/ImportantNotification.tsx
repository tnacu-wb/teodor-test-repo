import { Box, Text } from '@chakra-ui/react';
import type { ImportantInfo, InfoItem } from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml, useStaticHotelInformation } from '@whitbread-eos/utils';
import { parse } from 'date-fns';
import { useTranslation } from 'next-i18next';

interface Props {
  arrival: string;
  departure: string;
}

const sortByPriority = (itemA: InfoItem, itemB: InfoItem) =>
  Number(itemA?.priority) - Number(itemB?.priority);

const getImportantInfoText = (infoItem: InfoItem) =>
  infoItem?.htmlText?.trim() ? infoItem.htmlText : (infoItem?.text ?? '');

const getImportantNotificationForTimePeriod = (
  importantInfo: ImportantInfo,
  arrival: string,
  departure: string
) => {
  const arrivalTime = new Date(arrival);
  const departureTime = new Date(departure);

  return importantInfo?.infoItems
    ?.filter((infoItem: InfoItem) => {
      if (infoItem?.hideOnHdp) return false;

      const startTime = parse(infoItem?.startDate ?? '', 'dd/MM/yyyy', new Date());
      const endTime = parse(infoItem?.endDate ?? '', 'dd/MM/yyyy', new Date());

      return (
        (startTime >= arrivalTime && startTime <= departureTime) ||
        (arrivalTime >= startTime && arrivalTime <= endTime)
      );
    })
    .sort(sortByPriority)
    .map(getImportantInfoText)
    .join('<br/>');
};

export const ImportantNotification = ({ arrival, departure }: Readonly<Props>) => {
  const { t } = useTranslation(['common']);
  const { importantInfo, isLoading, isError, error } = useStaticHotelInformation();

  if (!arrival || !departure || !importantInfo) {
    return null;
  }

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  const importantNotification = getImportantNotificationForTimePeriod(
    importantInfo,
    arrival,
    departure
  );

  if (!importantInfo?.infoItems?.length || !importantNotification?.length) {
    return null;
  }

  return (
    <Box mb="md" data-testid="important-notification">
      <Notification
        title={importantInfo?.title ?? ''}
        description={
          <Box className="formatLinks">{renderSanitizedHtml(importantNotification)}</Box>
        }
        variant="info"
        status="info"
        svg={<Info />}
        prefixDataTestId="important-notification"
        {...notificationStyles}
      />
    </Box>
  );
};

const notificationStyles = {
  maxW: { base: 'full', md: 'full' },
  lineHeight: '2',
  backgroundColor: 'baseWhite',
  border: 'none',
};
