import { Box, Text } from '@chakra-ui/react';
import type { HIHotelAvailabilityResponse } from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

interface Props {
  hotelAvailabilityResponse: HIHotelAvailabilityResponse;
  isHotelOpeningSoon: boolean;
  hotelName: string;
  hotelOpeningDate: string | undefined;
  language: string;
}

export const OpeningSoonNotification = ({
  hotelAvailabilityResponse,
  isHotelOpeningSoon,
  hotelName,
  hotelOpeningDate,
  language,
}: Readonly<Props>) => {
  const { t } = useTranslation(['common']);

  const isLoading = hotelAvailabilityResponse.isLoadingHotelAvailability;
  const isError = hotelAvailabilityResponse.isErrorHotelAvailability;
  const error = hotelAvailabilityResponse.errorHotelAvailability;

  if (isLoading) {
    return <></>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!isHotelOpeningSoon) {
    return null;
  }

  const formattedOpeningDate =
    hotelOpeningDate &&
    new Date(hotelOpeningDate).toLocaleDateString(language === 'en' ? 'en-GB' : 'de-DE', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });

  return (
    <Box mt={{ base: 'md', lg: '6' }} data-testid="opening-soon-notification">
      <Notification
        title=""
        description={`${hotelName} ${t('hoteldetails.openingDateText')} ${formattedOpeningDate}`}
        status="info"
        variant="info"
        svg={<Info />}
        maxW={{ base: 'full' }}
        lineHeight="2"
        prefixDataTestId="opening-soon-notification"
      />
    </Box>
  );
};
