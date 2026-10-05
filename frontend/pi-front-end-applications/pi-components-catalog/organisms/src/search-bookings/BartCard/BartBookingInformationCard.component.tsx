import { Box, Flex, FlexProps, Text } from '@chakra-ui/react';
import {
  BartBookingInformation,
  BartBookingDataPackages,
  BartPackage,
  RoomInfoBart,
  SOURCE_SYSTEM,
  BartRoomStay,
  FIND_BOOKING_SOURCE_PMS,
} from '@whitbread-eos/api';
import {
  BartBookingDetailsExtras,
  BartBookingDetailsReservationInformation,
  BartBookingDetailsRoomInformation,
  BartBookingDetailsTotalCost,
} from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  getRoomDetailsForBartCard,
  getRoomTypeLabelsBySourceSystem,
  useCustomLocale,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  bookingData: BartBookingInformation;
  bartId?: string | null;
  sourcePms: string;
  baseDataTestId: string;
  isLoading: boolean;
  error: any;
  isError: boolean;
  roomTypes: RoomInfoBart[];
  getExtrasBart: (packages: BartPackage[], id: string) => BartBookingDataPackages[];
}

export default function BartBookingCardDetails({
  getExtrasBart,
  roomTypes,
  bookingData,
  baseDataTestId,
  sourcePms,
  bartId,
  isLoading,
  isError,
  error,
}: Readonly<Props>) {
  const { language } = useCustomLocale();
  const { t } = useTranslation(['common']);

  if (isLoading) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'data-loading')}>
        {t('searchresults.list.hotel.loading')}
      </Text>
    );
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }
  return (
    <Flex
      data-testid={baseDataTestId}
      {...containerStyle}
      sx={{ '@media print': { width: '100%', marginLeft: 'md' } }}
    >
      <Box>
        <BartBookingDetailsReservationInformation
          baseDataTestId={`${baseDataTestId}-ReservationInformation`}
          bookingType={bookingData.bookingType}
          rateType={bookingData.rateName}
          sourcePms={sourcePms}
          isBart={!!bartId}
          t={t}
        />
      </Box>

      {bookingData?.rooms?.map((room: BartRoomStay, index: number) => {
        const roomTypeLabelsBySourceSystem = getRoomTypeLabelsBySourceSystem(
          roomTypes,
          sourcePms === FIND_BOOKING_SOURCE_PMS.BART ? SOURCE_SYSTEM.BART : SOURCE_SYSTEM.OPERA,
          t
        );
        const roomLabel = getRoomDetailsForBartCard(
          room,
          roomTypeLabelsBySourceSystem[room?.roomType]
        );

        const packages = room.packages ? getExtrasBart(room.packages, 'id') : null;
        return (
          <Box
            mt="md"
            key={`${roomLabel} - ${room?.roomType}`}
            data-testid={formatDataTestId(baseDataTestId, `BartBookingsDetailsRoom-${index}`)}
          >
            <BartBookingDetailsRoomInformation
              roomNumber={index + 1}
              firstName={room?.reservationGuest?.givenName ?? ''}
              lastName={room?.reservationGuest?.surName ?? ''}
              roomName={roomLabel.roomType}
              noAdults={room?.adultsNumber}
              noKids={room?.childrenNumber}
              price={room?.totalRoomCost?.amount ?? 0}
              currency={room?.totalRoomCost?.currencyCode ?? ''}
              language={language}
              t={t}
            />
            {!!packages && (
              <BartBookingDetailsExtras packages={packages} language={language} t={t} />
            )}
          </Box>
        );
      })}
      <BartBookingDetailsTotalCost
        baseDataTestId={baseDataTestId}
        language={language}
        donationPkg={bookingData?.donations ?? {}}
        rateDescription={bookingData?.rateMessage ?? ''}
        balanceOutstanding={bookingData?.balanceOutstanding ?? {}}
        totalCost={bookingData?.totalCost}
        previousTotal={bookingData?.prepaidAmount ?? {}}
        shouldDisplayCityTaxMessage={bookingData?.hasCityTax ?? false}
        paymentOption={bookingData?.paymentOption ?? ''}
      />
    </Flex>
  );
}

const containerStyle = {
  ml: 'lg',
  w: { mobile: '100%' },
  color: 'darkGrey2',
  direction: 'column',
  fontSize: 'lg',
} as FlexProps;
