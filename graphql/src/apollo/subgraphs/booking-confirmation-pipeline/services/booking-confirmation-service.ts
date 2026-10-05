import { BookingConfirmationCriteria } from '../models/booking-confirmation-criteria';
import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { BookingInformationByBasketPipelineAction } from '../actions/BookingInformationByBasketPipelineAction';
import { HotelInformationForBookingPipelineAction } from '../actions/HotelInformationForBookingPipelineAction';
import { RateInformationPipelineAction } from '../actions/RateInformationPipelineAction';
import { RoomTypePipelineAction } from '../actions/RoomTypePipelineAction';
import { BookingFlowIdPipelineAction } from '../actions/BookingFlowIdPipelineAction';
import { BookingInfoMessagesPipelineAction } from '../actions/BookingInfoMessagesPipelineAction';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { BookingInformationByBasketAuthenticatedPipelineAction } from '../actions/BookingInformationByBasketAuthenticatedPipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { BookingConfirmationAuthenticatedCriteria } from '../models/booking-confirmation-authenticated-criteria';
import { BookingInformationByBasketAuthenticatedWithTokenPipelineAction } from '../actions/BookingInformationByBasketAuthenticatedWithTokenPipelineAction';
import { BookingConfirmationAuthenticatedWithTokenCriteria } from '../models/booking-confirmation-authenticated-with-token-criteria';
import { logPipelineError } from '../../../utils/base-utils';
import { validateBasketReference } from '../../../utils/basket-utils';

function bookingConfirmation(pipelineContext: PipelineContext) {
  let rateInfo = pipelineContext.get(ActionContextKeys.RATE_INFORMATION);
  let bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION_BY_BASKET);
  let bookingInfoMessages = pipelineContext.get(ActionContextKeys.BOOKING_INFO_MESSAGES);
  let hotelInfo = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING);
  let roomType = pipelineContext.get(ActionContextKeys.ROOM_TYPE);
  let bookingFlowId = pipelineContext.get(ActionContextKeys.BOOKING_FLOW_ID);

  rateInfo.bookingInformation = bookingInfo;
  let bookingSpinnerConfig = bookingInfoMessages.bookingSpinnerConfig;

  rateInfo.rateClassifications.forEach((item: any) => {
    bookingInfo.reservationByIdList.forEach((reservation: any) => {
      if (item.rateClassification === reservation.roomStay.ratePlanCode) {
        reservation.roomStay.rateExtraInfo = item;
      }
    });
  });

  //set default accessible room
  let accessibleRoomInfo = {
    phoneNumber: hotelInfo.contactDetails.hotelNationalPhone,
    isAccessible: false
  };

  roomType.roomTypes.forEach((roomTypeItem: any) => {
    bookingInfo.reservationByIdList.forEach((reservation: any) => {
      let finalPackages: any[] = [];
      reservation.reservationPackageList.forEach((reservationPackage: any) => {
        if (
          reservationPackage.packageCode !== 'CITYTAX' &&
          reservationPackage.packageCode !== 'CITYEXP'
        ) {
          finalPackages.push(reservationPackage);
        }
      });
      reservation.reservationPackageList = finalPackages;

      if (roomTypeItem.roomTypeCode.includes(reservation.roomStay.roomType)) {
        let roomExtraInfo = {
          roomType: reservation.roomStay.roomType,
          roomName: roomTypeItem.roomLabel,
          roomDescription: roomTypeItem.roomDescription,
          groupId: roomTypeItem.groupId
        };
        if (roomTypeItem.roomCategory.includes('Accessible')) {
          accessibleRoomInfo = {
            phoneNumber: hotelInfo.contactDetails.hotelNationalPhone,
            isAccessible: true
          };
        }
        reservation.roomStay.roomExtraInfo = roomExtraInfo;
        reservation.roomStay.accessibleRoom = accessibleRoomInfo;
      }
    });
  });

  let messages: any[] = [];
  bookingInfoMessages.infoMessages.forEach((item: any) => {
    if (item.rate === bookingInfo.reservationByIdList[0].roomStay.ratePlanCode) {
      item.messages.forEach((msg: any) => {
        messages.push(msg.message);
      });
    }
  });

  let rateMessage;
  rateInfo.rateClassifications.forEach((rateItem: any) => {
    if (rateItem.rateClassification === bookingInfo.reservationByIdList[0].roomStay.ratePlanCode) {
      rateMessage = rateItem.rateNotes;
    }
  });

  rateInfo.bookingInformation.bookingFlowId = bookingFlowId;
  rateInfo.bookingInformation.infoMessages = messages;
  rateInfo.bookingInformation.hotelName = hotelInfo.name;

  // set flex related fields
  rateInfo.bookingInformation.upgradeToFlex = {};

  // calculate city tax total from all reservations' rates per night
  let cityTaxTotal = 0;
  bookingInfo.reservationByIdList.forEach((reservation: any) => {
    if (reservation.roomStay?.ratesPerNight) {
      reservation.roomStay.ratesPerNight.forEach((rate: any) => {
        if (rate.cityTaxPerNight) {
          cityTaxTotal += rate.cityTaxPerNight;
        }
      });
    }
  });

  return {
    ...rateInfo.bookingInformation,
    bookingSpinnerConfig: bookingSpinnerConfig,
    rateMessage: rateMessage,
    bookingReference: bookingInfo.bookingReference,
    idContext: bookingInfo.idContext,
    cityTaxTotal: cityTaxTotal
  };
}

export const getBookingConfirmation = async (
  bookingConfirmationCriteria: BookingConfirmationCriteria,
  context: any
): Promise<any> => {
  if (
    bookingConfirmationCriteria.flow === 'CONFIRMATION_PAGE' ||
    bookingConfirmationCriteria.flow === 'REGISTER_PAGE'
  ) {
    validateBasketReference(context, bookingConfirmationCriteria.basketReference);
  }

  const pipelineManager = new PipelineManager();

  const actions = [
    new BookingInformationByBasketPipelineAction(),
    new HotelInformationForBookingPipelineAction(),
    new RateInformationPipelineAction(),
    new RoomTypePipelineAction(),
    new BookingFlowIdPipelineAction(),
    new BookingInfoMessagesPipelineAction()
  ];

  try {
    let pipelineContext = await pipelineManager.manage(
      actions,
      bookingConfirmationCriteria,
      context
    );

    return bookingConfirmation(pipelineContext);
  } catch (error: Error | any) {
    logPipelineError(error, bookingConfirmationCriteria, getBookingConfirmation);
    throw error;
  }
};

export const getBookingConfirmationAuthenticated = async (
  bookingConfirmationAuthenticatedCriteria: BookingConfirmationAuthenticatedCriteria,
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  const actions = [
    new BookingInformationByBasketAuthenticatedPipelineAction(),
    new HotelInformationForBookingPipelineAction(),
    new RateInformationPipelineAction(),
    new RoomTypePipelineAction(),
    new BookingFlowIdPipelineAction(),
    new BookingInfoMessagesPipelineAction()
  ];
  try {
    let pipelineContext = await pipelineManager.manage(
      actions,
      bookingConfirmationAuthenticatedCriteria,
      context
    );

    return bookingConfirmation(pipelineContext);
  } catch (error: Error | any) {
    logPipelineError(
      error,
      bookingConfirmationAuthenticatedCriteria,
      getBookingConfirmationAuthenticated
    );
    throw error;
  }
};

export const getBookingConfirmationAuthenticatedWithToken = async (
  bookingConfirmationAuthenticatedWithTokenCriteria: BookingConfirmationAuthenticatedWithTokenCriteria,
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  const actions = [
    new BookingInformationByBasketAuthenticatedWithTokenPipelineAction(),
    new HotelInformationForBookingPipelineAction(),
    new RateInformationPipelineAction(),
    new RoomTypePipelineAction(),
    new BookingFlowIdPipelineAction(),
    new BookingInfoMessagesPipelineAction()
  ];
  try {
    let pipelineContext = await pipelineManager.manage(
      actions,
      bookingConfirmationAuthenticatedWithTokenCriteria,
      context
    );

    return bookingConfirmation(pipelineContext);
  } catch (error: Error | any) {
    logPipelineError(
      error,
      bookingConfirmationAuthenticatedWithTokenCriteria,
      getBookingConfirmationAuthenticatedWithToken
    );
    throw error;
  }
};
