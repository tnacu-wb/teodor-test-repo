import { BookingInformationCriteria } from '../models/booking-information-criteria';
import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { BookingInformationByBasketPipelineAction } from '../actions/BookingInformationByBasketPipelineAction';
import { HotelInformationForBookingPipelineAction } from '../actions/HotelInformationForBookingPipelineAction';
import { RateInformationPipelineAction } from '../actions/RateInformationPipelineAction';
import { RoomTypePipelineAction } from '../actions/RoomTypePipelineAction';
import { BookingFlowIdPipelineAction } from '../actions/BookingFlowIdPipelineAction';
import { BookingInfoMessagesPipelineAction } from '../actions/BookingInfoMessagesPipelineAction';
import { RateCodePricingPipelineAction } from '../actions/RateCodePricingPipelineAction';
import { handleError } from '../../../exception/error-handler';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { logPipelineError } from '../../../utils/base-utils';

export const getBookingInformationService = async (
  bookingInformationCriteria: BookingInformationCriteria,
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  const actions = [
    new BookingInformationByBasketPipelineAction(),
    new HotelInformationForBookingPipelineAction(),
    new RateInformationPipelineAction(),
    new RoomTypePipelineAction(),
    new BookingFlowIdPipelineAction(),
    new BookingInfoMessagesPipelineAction(),
    new RateCodePricingPipelineAction()
  ];

  try {
    let pipelineContext = await pipelineManager.manage(
      actions,
      bookingInformationCriteria,
      context
    );
    let rateInfo = pipelineContext.get(ActionContextKeys.RATE_INFORMATION);
    let bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION_BY_BASKET);
    let hotelInfo = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING);
    let roomType = pipelineContext.get(ActionContextKeys.ROOM_TYPE);
    let infoMessages = pipelineContext.get(ActionContextKeys.BOOKING_INFO_MESSAGES);
    let bookingFlowId = pipelineContext.get(ActionContextKeys.BOOKING_FLOW_ID);
    let rateCodePricing = pipelineContext.get(ActionContextKeys.RATE_CODE_PRICING);

    rateInfo.bookingInformation = bookingInfo;
    rateInfo.rateClassifications.forEach((item: any) => {
      bookingInfo.reservationByIdList.forEach((reservation: any) => {
        if (item.rateClassification === reservation.roomStay.ratePlanCode) {
          reservation.roomStay.rateExtraInfo = item;
        }
      });
    });

    // set default accessible room
    let accessibleRoomInfo = {
      phoneNumber: hotelInfo.contactDetails.hotelNationalPhone,
      isAccessible: false
    };

    bookingInfo.reservationByIdList.forEach((reservation: any) => {
      accessibleRoomInfo = {
        phoneNumber: hotelInfo.contactDetails.hotelNationalPhone,
        isAccessible: false
      };
      roomType.roomTypes.forEach((roomTypeItem: any) => {
        if (roomTypeItem.roomTypeCode.includes(reservation.roomStay.roomType)) {
          let roomExtraInfo = {
            roomType: reservation.roomStay.roomType,
            roomName: roomTypeItem.roomLabel,
            roomDescription: roomTypeItem.roomDescription
          };
          if (roomTypeItem.roomCategory.indexOf('Accessible') >= 0) {
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

    let rateCategory = '';
    let rateDisplaySet = '';
    rateInfo.rateClassifications.forEach((rate: any) => {
      if (rate.ratePlanCode === bookingInfo.reservationByIdList[0].roomStay.ratePlanCode) {
        rateCategory = rate.rateCategory;
        rateDisplaySet = rate.rateDisplaySet;
      }
    });

    let messages: any[] = [];
    infoMessages.infoMessages.forEach((item: any) => {
      if (
        item.rate === bookingInfo.reservationByIdList[0].roomStay.ratePlanCode &&
        item.rateCategory === rateCategory &&
        item.rateDisplaySet === rateDisplaySet
      ) {
        item.messages.forEach((msg: any) => {
          messages.push(msg.message);
        });
      }
    });

    rateInfo.bookingInformation.bookingFlowId = bookingFlowId;
    rateInfo.bookingInformation.infoMessages = messages;
    rateInfo.bookingInformation.hotelName = hotelInfo.name;

    // set flex related fields
    let upgradeToFlex: {};
    if (
      !rateCodePricing.totalNetAmount ||
      bookingInfo.reservationByIdList[0].roomStay.ratePlanCode === 'FLEXRATE' ||
      bookingInfo.reservationByIdList[0].roomStay.ratePlanCode === 'EMPLOYEE' ||
      bookingInformationCriteria.bookingChannelCriteria.channel === 'BB'
    ) {
      upgradeToFlex = {};
    } else {
      upgradeToFlex = {
        flexRateCode: rateCodePricing.ratePlanCode,
        amount: rateCodePricing.totalNetAmount,
        currency: rateCodePricing.currencyCode
      };
    }
    rateInfo.bookingInformation.upgradeToFlex = upgradeToFlex;

    rateInfo?.bookingInformation?.reservationByIdList?.forEach((reservation: any) => {
      if (reservation.additionalGuestInfo.purposeOfStay?.startsWith('NT')) {
        reservation.additionalGuestInfo.purposeOfStay =
          reservation.additionalGuestInfo.purposeOfStay.replace(/^NT/, '');
      }
    });

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
    rateInfo.bookingInformation.cityTaxTotal = cityTaxTotal;

    return rateInfo.bookingInformation;
  } catch (error) {
    logPipelineError(error, bookingInformationCriteria, getBookingInformationService);
    throw error;
  }
};
