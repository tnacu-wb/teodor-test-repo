import { Box, Divider, DividerProps } from '@chakra-ui/react';
import type {
  AmendReservation,
  AmendRoomsAndGuestsLabels,
  BookingConfirmationType,
  BookingSummaryLabels,
  BookingSummaryRoomInformationProps,
  StayDatesLabels,
  ExtrasPackagesPrices,
} from '@whitbread-eos/api';
import { getNightsNumber } from '@whitbread-eos/utils';

import BookingSummaryRoomDetails from './BookingSummaryRoomDetails';
import BookingSummaryStayDates from './BookingSummaryStayDates';

export interface Props {
  language: string;
  baseDataTestId: string;
  bookingInformation: BookingConfirmationType;
  roomsPackages: BookingSummaryRoomInformationProps[];
  originalArrivalDate: string;
  originalDepartureDate: string;
  stayDatesLabels: StayDatesLabels;
  bookingSummaryLabels: BookingSummaryLabels;
  roomsAndGuestsLabels: AmendRoomsAndGuestsLabels;
  extrasItemsPrices?: ExtrasPackagesPrices;
}
export default function BookingSummary(props: Readonly<Props>) {
  const {
    language,
    baseDataTestId,
    bookingInformation,
    roomsPackages,
    originalArrivalDate,
    originalDepartureDate,
    stayDatesLabels,
    bookingSummaryLabels,
    roomsAndGuestsLabels,
    extrasItemsPrices,
  } = props;

  const noNights = getNightsNumber(
    bookingInformation?.reservationByIdList[0]?.roomStay?.arrivalDate,
    bookingInformation?.reservationByIdList[0]?.roomStay?.departureDate
  );

  return (
    <Box>
      <BookingSummaryStayDates
        language={language}
        baseDataTestId={baseDataTestId}
        labels={stayDatesLabels}
        arrivalDate={bookingInformation?.reservationByIdList[0]?.roomStay?.arrivalDate}
        departureDate={bookingInformation?.reservationByIdList[0]?.roomStay?.departureDate}
        originalArrivalDate={originalArrivalDate}
        originalDepartureDate={originalDepartureDate}
      />
      <Divider {...dividerStyles} />
      {bookingInformation?.reservationByIdList?.map(
        (reservation: AmendReservation, index: number) => (
          <>
            <BookingSummaryRoomDetails
              key={reservation.reservationId}
              language={language}
              baseDataTestId={baseDataTestId}
              bookingSummaryLabels={bookingSummaryLabels}
              roomsAndGuestsLabels={roomsAndGuestsLabels}
              reservation={reservation}
              roomNumber={index}
              currency={bookingInformation.currencyCode}
              roomPackages={roomsPackages[index]}
              noNights={noNights}
              extrasItemsPrices={extrasItemsPrices}
            />
            <Divider {...dividerStyles} />
          </>
        )
      )}
    </Box>
  );
}

const dividerStyles = {
  borderColor: 'lightGrey4',
  opacity: '1',
} as DividerProps;
