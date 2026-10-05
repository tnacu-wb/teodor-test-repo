import type { BoxProps, FlexProps } from '@chakra-ui/react';
import { Box, Flex } from '@chakra-ui/react';
import {
  Area,
  BC_RESERVATION_STATUS,
  BOOKING_TYPE,
  DpaInfo,
  OverridenUserInfo,
  BookingChannelCriteria,
  RoomDetails,
} from '@whitbread-eos/api';
import {
  BookingDetailsReservationInformation,
  BookingDetailsRoomInformation,
  BookingDetailsTotalCost,
  CityTax,
} from '@whitbread-eos/molecules';
import { formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import { IDVDataProps } from '../IDVModal';
import { BookingDetailsProp } from './BookingDetails.container';
import {
  BookingDetailsController,
  BookingDetailsControllerComponent,
} from './BookingDetailsController';

export interface Props extends BoxProps {
  baseDataTestId?: string;
  basketReference: string | null;
  bookingReference: string;
  shouldShowTypeOfBooking?: boolean;
  area?: Area;
  getBookingStatus?: any;
  bookingStatus: string;
  bookingType?: string;
  idvData?: IDVDataProps;
  setIdvData?: (value: IDVDataProps) => void;
  defaultDataFromBooking?: any;
  dpaInfo?: DpaInfo;
  setDpaInfo?: (value: DpaInfo) => void;
  bookingDetails: BookingDetailsProp;
  noOfRooms?: number;
  sourcePms?: string;
  overridenUserInfo?: OverridenUserInfo;
  inputValues?: any;
  gdsReferenceNumber?: any;
  distBookingChannel?: any;
  skipContainerRendering?: boolean;
  bookingChannel?: BookingChannelCriteria;
  sourceSystem?: string;
  isAmendSuccessful?: boolean;
  arrival?: string;
  bookingSurname?: string;
  showExtras?: boolean;
  shouldExpandLeft?: boolean;
  isReadOnly?: boolean;
  isAmendPage?: boolean;
  operaConfNumber?: string;
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
  isCityTaxAmendEnabled?: boolean;
}

export default function BookingDetails({
  baseDataTestId,
  basketReference,
  bookingReference,
  shouldShowTypeOfBooking,
  area = 'pi' as Area.PI,
  getBookingStatus,
  bookingStatus,
  bookingType,
  idvData,
  setIdvData,
  defaultDataFromBooking,
  dpaInfo,
  setDpaInfo,
  bookingDetails,
  noOfRooms,
  sourcePms = 'opera',
  overridenUserInfo,
  inputValues,
  skipContainerRendering = false,
  gdsReferenceNumber,
  distBookingChannel,
  bookingChannel,
  sourceSystem,
  isAmendSuccessful = false,
  arrival,
  bookingSurname,
  isReadOnly,
  isAmendPage,
  showExtras = true,
  operaConfNumber,
  isRemovePIIDataFromLocalStorageEnabled = false,
  isCityTaxAmendEnabled,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { language } = useCustomLocale();

  const {
    currencyCode,
    totalCost,
    roomDetails,
    hotelId,
    paymentOption,
    paymentMethod,
    balanceOutstanding,
    previousTotal,
    newTotal,
    donationPkg,
    rateType,
    shouldDisplayCityTaxMessage,
    cancellationInfoResponse,
    hotelName,
    bookedFor,
    arrivalDate,
    noNights,
    cardType,
    guestSurname,
    reasonForStay,
    dinnerAllowance,
    rateTags,
    cityTaxTotal,
  } = bookingDetails;
  return (
    <Flex
      {...{ ...containerStyle(bookingStatus) }}
      w={{
        mobile: '100%',
        lg: area === Area.CCUI && !isAmendPage ? '-webkit-fill-available' : '100%',
      }}
      sx={{ '@media print': { width: '100%', marginLeft: 'md' } }}
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
    >
      {shouldShowTypeOfBooking && (
        <Box>
          <BookingDetailsReservationInformation
            baseDataTestId={'operaCardReservationInfo'}
            sourcePms={sourcePms}
            rateType={rateType}
            isBart={false}
            t={t}
            distBookingChannel={distBookingChannel}
            reasonForStay={reasonForStay}
            gdsReferenceNumber={gdsReferenceNumber}
            isReadOnly={isReadOnly}
            paymentOption={paymentOption}
            paymentMethod={paymentMethod}
          />
        </Box>
      )}

      {roomDetails?.map((room: RoomDetails, index: number) => {
        return (
          <Box
            mt={{ mobile: 'lg', lg: index === 0 ? 0 : 'lg' }}
            mb={{
              mobile: index === roomDetails.length - 1 ? 'lg' : 0,
              lg: index === roomDetails.length - 1 ? 'lg' : 0,
            }}
            key={`${room.roomType} - ${room.roomPrice}`}
            data-testid={formatDataTestId(baseDataTestId, 'BookingDetailWrapper')}
          >
            <BookingDetailsRoomInformation
              bookingStatus={bookingStatus}
              roomNumber={index + 1}
              {...room}
              currencyCode={currencyCode}
              showExtras={showExtras}
              area={area}
            />
          </Box>
        );
      })}

      {![BC_RESERVATION_STATUS.CANCELLED, BOOKING_TYPE.CANCELLED].includes(
        bookingStatus as BOOKING_TYPE | BC_RESERVATION_STATUS
      ) && (
        <>
          {isCityTaxAmendEnabled && cityTaxTotal !== undefined && cityTaxTotal > 0 && (
            <CityTax
              cityTaxTotal={cityTaxTotal}
              baseDataTestId={baseDataTestId as string}
              language={language}
              currency={currencyCode as string}
              priceStylesProps={{
                fontSize: 'lg',
                fontWeight: 'medium',
                color: 'darkGrey2',
                width: '100%',
              }}
            />
          )}

          <BookingDetailsTotalCost
            currency={currencyCode}
            totalCost={totalCost}
            balanceOutstanding={balanceOutstanding}
            previousTotal={previousTotal}
            newTotal={newTotal}
            paymentOption={paymentOption}
            donationPkg={donationPkg}
            shouldDisplayCityTaxMessage={shouldDisplayCityTaxMessage}
            paidWithPiba={cardType === 'AT'}
            bookingStatus={bookingStatus}
            dinnerAllowance={dinnerAllowance}
            isAmendPage={isAmendPage}
            rateTags={rateTags}
          />
        </>
      )}
      {skipContainerRendering && cancellationInfoResponse ? (
        <BookingDetailsControllerComponent
          manageBookingData={{
            isAmendable: cancellationInfoResponse.amendable ?? false,
            isCancellable: cancellationInfoResponse.cancelable ?? false,
            isRuleCompliant: cancellationInfoResponse.ruleCompliant ?? false,
            aemLabelKey: cancellationInfoResponse.aemLabelKey ?? '',
          }}
          refetchManageBooking={getBookingStatus}
          bookingReference={bookingReference}
          basketReference={basketReference}
          bookingStatus={bookingStatus}
          skipBookingRequest={skipContainerRendering}
          hotelName={hotelName as string}
          bookedFor={bookedFor as string}
          arrivalDate={arrivalDate as string}
          noOfRooms={noOfRooms as number}
          noNights={noNights as number}
          hotelId={hotelId}
          bookingChannel={bookingChannel}
          sourceSystem={sourceSystem}
          guestSurname={guestSurname}
          area={area}
          isAmendSuccessful={isAmendSuccessful}
          rateType={bookingDetails.rateType}
          bookingSurname={bookingSurname}
          isAmendPage={isAmendPage}
          operaConfNumber={operaConfNumber}
          isRemovePIIDataFromLocalStorageEnabled={isRemovePIIDataFromLocalStorageEnabled}
        />
      ) : (
        <BookingDetailsController
          hotelId={hotelId}
          basketReference={basketReference ?? ''}
          bookingReference={bookingReference ?? ''}
          area={area}
          getBookingStatus={getBookingStatus}
          bookingStatus={bookingStatus}
          bookingType={bookingType}
          idvData={idvData}
          setIdvData={setIdvData}
          defaultDataFromBooking={defaultDataFromBooking}
          dpaInfo={dpaInfo}
          setDpaInfo={setDpaInfo}
          overridenUserInfo={overridenUserInfo}
          inputValues={inputValues}
          isAmendSuccessful={isAmendSuccessful}
          rateType={bookingDetails.rateType}
          arrivalDate={arrival}
          bookingSurname={bookingSurname}
          isAmendPage={isAmendPage}
          operaConfNumber={operaConfNumber}
          isRemovePIIDataFromLocalStorageEnabled={isRemovePIIDataFromLocalStorageEnabled}
        />
      )}
    </Flex>
  );
}

const containerStyle = (bookingStatus: string) => {
  return {
    pl: { mobile: 0, lg: bookingStatus === BC_RESERVATION_STATUS.CANCELLED ? 0 : 'lg' },
    color: 'darkGrey2',
    direction: 'column',
    fontSize: 'lg',
    gridColumnStart: 2,
  } as FlexProps;
};
