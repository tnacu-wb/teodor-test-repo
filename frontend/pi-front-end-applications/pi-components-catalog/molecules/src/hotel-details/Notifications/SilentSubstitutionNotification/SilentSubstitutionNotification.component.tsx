import { Box } from '@chakra-ui/react';
import { HIAEMroomType, HIRoomTypeInfoResponse, HUB } from '@whitbread-eos/api';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import { ReactElement } from 'react';

interface Props {
  brand: string;
  substitutedRooms: string[];
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
}

export default function SilentSubstitutionNotification({
  brand,
  substitutedRooms,
  roomTypeInformationResponse,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  const substitutedRoomLabels = substitutedRooms
    ?.map((pmsRoomType: string) => {
      return roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes?.reduce(
        (labels: string[], roomInfo: HIAEMroomType) => {
          if (
            roomInfo?.roomTypeCode === pmsRoomType ||
            roomInfo?.roomTypeCode.includes(pmsRoomType)
          ) {
            labels.push(roomInfo?.roomLabel);
          }
          return labels;
        },
        []
      );
    })
    .flat()
    .join(', ');

  if (!substitutedRoomLabels) {
    return null;
  }

  return (
    <Box mb="md" data-testid="substitution-notification">
      <Notification
        {...getNotificationPropsByType(substitutedRoomLabels)}
        prefixDataTestId="substitution-notification"
        isInnerHTML
        {...notificationStyles}
      />
    </Box>
  );

  function getNotificationPropsByType(substitutedRoomLabels: string): {
    description: string;
    variant: string;
    status: 'warning';
    svg: ReactElement;
  } {
    const unAvailableBrandContent =
      brand.toLowerCase() === HUB
        ? t('searchresults.list.restrictions.hub.available')
        : t('searchresults.list.restrictions.available');

    return {
      description: `${unAvailableBrandContent} ${t(
        'searchresults.list.restrictions.offer'
      )} ${substitutedRoomLabels} `,
      variant: 'alert',
      status: 'warning',
      svg: <Alert />,
    };
  }
}

const notificationStyles = {
  maxW: { base: 'full', md: '100%' },
  lineHeight: '2',
};
