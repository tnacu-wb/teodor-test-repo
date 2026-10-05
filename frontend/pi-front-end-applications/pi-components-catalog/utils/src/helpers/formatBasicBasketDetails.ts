import { HIBasicBasketDetails, HIBasketData } from '@whitbread-eos/api';

import { getMaxValueFromHDPRoomTypes, getNightsNumber } from '../getters';

export const formatBasicBasketDetails = (
  basket: HIBasketData,
  reservationId: string,
  isBB = false
) => {
  let bookingFlowId =
    basket?.bookingFlow?.bookingFlowItems?.find(
      (bfi) => bfi?.rateCategory === basket?.selectedRate?.rateCategory
    )?.bookingId ?? '';

  if (isBB) {
    bookingFlowId = 'booking-business';
  }

  const basketToBeSet: HIBasicBasketDetails = {
    hotelId: basket.hotelId,
    startDate: basket.arrival,
    endDate: basket.departure,
    nightsNumber: getNightsNumber(basket.arrival, basket.departure),
    rateCode: basket.selectedRate.ratePlanCode,
    adultsNumber: getMaxValueFromHDPRoomTypes(basket.selectedRate.roomTypes, 'adults'),
    childrenNumber: getMaxValueFromHDPRoomTypes(basket.selectedRate.roomTypes, 'children'),
    reservationId: reservationId,
    bookingFlowId: bookingFlowId,
  };
  return basketToBeSet;
};
