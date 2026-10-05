import { Box } from '@chakra-ui/react';
import { AccessibilityInfo, HIRoomTypeInfoResponse, ReservationRoomType } from '@whitbread-eos/api';

import AccessibleRoomNotification from '../AccessibleRoomNotification';
import CotNotification from '../CotNotification';
import SilentSubstitutionNotificationWrapper from '../SilentSubstitutionNotificationWrapper';

export interface RateNotificationsProps {
  currentClassRoomTypes?: ReservationRoomType[];
  isNonSilentSubstituNotificPerRoomClassEnabled: boolean;
  brand: string;
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
  hasAccessibleRoom: boolean;
  accessibilityInfo: AccessibilityInfo | undefined;
  cot: {
    requested: boolean;
    available: boolean;
  };
  specialRoomLimitMessage: typeof Box;
}

export default function RateNotifications({
  currentClassRoomTypes,
  isNonSilentSubstituNotificPerRoomClassEnabled,
  brand,
  roomTypeInformationResponse,
  hasAccessibleRoom,
  accessibilityInfo,
  cot,
  specialRoomLimitMessage,
}: Readonly<RateNotificationsProps>) {
  return (
    <>
      {!!currentClassRoomTypes?.length && !isNonSilentSubstituNotificPerRoomClassEnabled && (
        <Box>
          <SilentSubstitutionNotificationWrapper
            brand={brand}
            roomTypeInformationResponse={roomTypeInformationResponse}
            currentClassRoomTypes={currentClassRoomTypes}
          />
        </Box>
      )}
      {hasAccessibleRoom && (
        <Box>
          <AccessibleRoomNotification data={accessibilityInfo?.text ?? ''} />
        </Box>
      )}
      {cot?.requested && (
        <Box>
          <CotNotification cotAvailable={cot.available} />
        </Box>
      )}
      {specialRoomLimitMessage}
    </>
  );
}
