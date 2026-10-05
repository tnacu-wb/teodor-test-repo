import { Box, Text } from '@chakra-ui/react';
import type { HIHotelAvailabilityResponse } from '@whitbread-eos/api';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { useStaticHotelInformation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  hotelAvailabilityResponse: HIHotelAvailabilityResponse;
  isHotelOpeningSoon: boolean;
}

export const SoldOutNotification = ({
  hotelAvailabilityResponse,
  isHotelOpeningSoon,
}: Readonly<Props>) => {
  const { name } = useStaticHotelInformation();
  const { t } = useTranslation(['common']);
  const isLoading = hotelAvailabilityResponse.isLoadingHotelAvailability;
  const isError = hotelAvailabilityResponse.isErrorHotelAvailability;
  const error = hotelAvailabilityResponse.errorHotelAvailability;
  const data = hotelAvailabilityResponse.dataHotelAvailability;
  const isAvailable =
    data?.hotelAvailability?.available && data?.hotelAvailability?.roomRates.length > 0;

  if (isLoading) {
    return <></>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  // do not show notification - if hotel is opening soon or has availability
  if ((!isHotelOpeningSoon && isAvailable) || isHotelOpeningSoon) {
    return null;
  }

  return (
    <Box mt={{ base: 'md', lg: '6' }} data-testid="soldout-notification">
      <Notification
        title=""
        description={`${name} ${t('hoteldetails.unavailableText')}`}
        status="info"
        variant="alert"
        svg={<Alert />}
        maxW={{ base: 'full' }}
        lineHeight="2"
        prefixDataTestId="soldout-notification"
      />
    </Box>
  );
};
