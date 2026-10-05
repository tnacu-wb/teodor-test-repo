import {
  ReservationById,
  BookingInformation,
  MealItem,
  MealKids,
  Packages,
  RoomSelection,
  UpsellsSelection,
  Area,
} from '@whitbread-eos/api';
import { differenceInDays, format } from 'date-fns';

import {
  getAuthCookie,
  getLoggedInUserInfo,
  getNightsNumber,
  getMealQuantity,
} from '../../getters';
import {
  adultsMealsSelector,
  calculateTotalCostRoomSelection,
  childrenMealsSelector,
  getUniqueRoomProperties,
} from '../../selectors';
import analytics, { analyticsTrackings } from './analytics';

export const extrasPackagesAnalyticsMap = (
  roomIndex: number,
  noNights: number,
  packages?: Packages
) => {
  return packages?.extrasItems?.map((extrasItem) => ({
    code: extrasItem.id ?? '',
    legend: extrasItem.name ?? '',
    quantity:
      packages?.roomSelection?.[roomIndex]?.packagesSelection?.find((p) => p.id === extrasItem.id)
        ?.noOfSelections ?? 0,
    price: extrasItem.price ? (extrasItem.price / noNights).toFixed(2) : '',
    currency: extrasItem?.currency ?? '',
  }));
};

const updateAncillariesAnalytics = (
  bookingInformation: BookingInformation,
  packages: Packages,
  variant?: string
) => {
  const firstRoom = bookingInformation?.reservationByIdList?.[0]?.roomStay || {};

  const noNights = getNightsNumber(firstRoom?.arrivalDate ?? '', firstRoom?.departureDate ?? '');
  const roomSelection: RoomSelection[] = packages.roomSelection ?? [];
  const adultsMeals: MealItem[] = adultsMealsSelector(packages?.meals, noNights);
  const childrenMeals: MealKids[] = childrenMealsSelector(packages?.mealsKids);
  const reasonForStay =
    bookingInformation?.reservationByIdList?.[0]?.additionalGuestInfo?.purposeOfStay;

  let totalAdults = 0;
  let totalChildren = 0;

  if (bookingInformation?.reservationByIdList) {
    bookingInformation.reservationByIdList.forEach((room: ReservationById) => {
      if (room?.roomStay) {
        totalAdults += room.roomStay.adultsNumber ?? 0;
        totalChildren += room.roomStay.childrenNumber ?? 0;
      }
    });
  }

  const arrivalDay = firstRoom?.arrivalDate ? format(new Date(firstRoom?.arrivalDate), 'EEEE') : '';

  const departureDay = firstRoom?.departureDate
    ? format(new Date(firstRoom?.departureDate), 'EEEE')
    : '';

  const ancPackagesSelection: Array<UpsellsSelection[]> = [];

  if (bookingInformation?.reservationByIdList) {
    bookingInformation?.reservationByIdList.forEach(
      (reservation: ReservationById, roomIndex: number) => {
        if (reservation) {
          const packagesPerRoom = [
            ...adultsMeals.map((meal: MealItem) => ({
              code: meal?.id ?? '',
              legend: meal?.name ?? '',
              quantity: getMealQuantity(packages, meal?.id ?? '', roomIndex),
              price: meal?.price?.toFixed(2) ?? '',
              currency: meal?.currency,
              freeBreakfastCode: meal?.freeBreakfastCode,
              freeBreakfastOption: meal?.freeBreakfastOption,
            })),
            ...childrenMeals.map((meal: MealItem) => ({
              code: meal?.id ?? '',
              legend: meal?.name ?? '',
              quantity: getMealQuantity(packages, meal?.id ?? '', roomIndex),
              price: '0.00',
            })),
            ...(extrasPackagesAnalyticsMap(roomIndex, noNights, packages) ?? []),
          ];
          ancPackagesSelection.push([...packagesPerRoom]);
        }
      }
    );
  }

  let roomTypes, roomNames;
  if (bookingInformation?.reservationByIdList) {
    roomTypes = getUniqueRoomProperties(bookingInformation?.reservationByIdList);
    roomNames = getUniqueRoomProperties(bookingInformation?.reservationByIdList, 'roomName');
  }

  analytics.update({
    alcoholAllowed: false,
    dinnerAllowance: false,
    otherChargesAllowed: false,
    validation: '',
    bookingReasonForStay: reasonForStay,
    wifiAccessAllowed: false,
    wifiOption: '',
    currencyCode: bookingInformation?.currencyCode ?? '',
    FromToDate: {
      ArrivalDay: arrivalDay,
      DepartureDay: departureDay,
      FromToDay: `${arrivalDay}-${departureDay}`,
    },
    rateCode: firstRoom?.ratePlanCode ?? '',
    productSelectedRate: firstRoom?.rateExtraInfo?.rateName,
    RoomTypes: roomTypes,
    RoomNames: roomNames,
    productDetails: [
      {
        type: analyticsTrackings.HOTEL,
        quantity: bookingInformation?.reservationByIdList?.length ?? 0,
        price: {
          basePrice: (
            (bookingInformation?.totalCost ?? 0) -
            calculateTotalCostRoomSelection(adultsMeals, roomSelection, noNights)
          ).toFixed(2),
        },
        productInfo: {
          sku: bookingInformation.hotelId ?? '',
          totalNumberOfRooms: bookingInformation?.reservationByIdList?.length.toString() ?? '',
          roomAdults: totalAdults.toString(),
          roomChildren: totalChildren.toString(),
          numberOfGuests: (totalAdults + totalChildren).toString(),
          startDate: firstRoom?.arrivalDate
            ? format(new Date(firstRoom?.arrivalDate), 'yyyy-MM-dd')
            : '',
          endDate: firstRoom?.departureDate
            ? format(new Date(firstRoom?.departureDate), 'yyyy-MM-dd')
            : '',
          numberOfNights: noNights.toString(),
          daysToCheckIn: firstRoom?.arrivalDate
            ? differenceInDays(new Date(firstRoom?.arrivalDate), new Date()).toString()
            : '',
        },
      },
    ],
    upsells: {
      rooms: ancPackagesSelection,
    },
  });

  if (variant === Area.BB || variant === Area.CCUI) {
    analytics.update({
      siteType: '6.5',
      bookingPanelComponentsOrder:
        'submitdetails,bookingoverview,bookingdonation,bookingdetails, infotext_FlexPID,infotext_FlexPI,infotext_FlexHub,infotext_Advance,infotext_standard,infotext, infotext_semiflex,submitdetails_0',
      contentComponentOrder: 'employees,upsell,contactpreference,wifi,submitdetails,contactdetails',
      loginFormComponentsOrder: 'bookingflowmessages,loginregisterauth0,',
    });
  }

  if (variant === Area.BB) {
    const idTokenCookie = getAuthCookie();

    const { sessionId, accessLevel, cdhCompanyId } = getLoggedInUserInfo(idTokenCookie);

    analytics.update({
      companyID: cdhCompanyId,
      sessionId: sessionId,
      userLevel: accessLevel,
    });
  }
};

export default updateAncillariesAnalytics;
