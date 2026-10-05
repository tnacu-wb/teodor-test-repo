import {
  DailyRate,
  FoodPerRoom,
  FoodRatePlan,
  RatesPerDate,
  FoodPerRoomCodes,
  UpsellsSelection,
  ProductDetailsInterface,
  RoomSelection,
  MealItem,
  MealKids,
  Packages,
  Area,
  BCReservationListItem,
  BookingConfirmation,
  PaymentAnalytics,
} from '@whitbread-eos/api';
import { differenceInDays, format } from 'date-fns';

import {
  getMealQuantity,
  getAuthCookie,
  getLoggedInUserInfo,
  getNightsNumber,
} from '../../getters';
import {
  adultsMealsSelector,
  calculateTotalCostRoomSelection,
  childrenMealsSelector,
  getUniqueRoomProperties,
} from '../../selectors';
import analytics, {
  analyticsConfirmation,
  analyticsTrackings,
  analyticsTrackings as trackingTypes,
} from './analytics';
import { extrasPackagesAnalyticsMap } from './ancillariesAnalytics';

export const analyticsConfirmationPageName = 'booking confirmation';

const initConfirmationAnalytics = (bookingFlowComplete: boolean) => {
  const {
    paymentSessionID,
    paymentTakenNow,
    paymentTemplateID,
    productSelectedRate,
    bookingReasonForStay,
    productDetails,
    browserTimeZone,
    cardType,
    pageName,
    validation,
    upsells,
    paymentOutage,
  } = window.analyticsData || {};

  const { bookingId } = window.analyticsDataCartConfirmation || {};

  analyticsConfirmation.update({
    BusinessAccountCardExtrasUpsells: {},
    CartItems: productDetails,
    InvoiceType: 'Email',
    bookingCity: browserTimeZone,
    userisSubscribed: false,
    typeOfTrip: bookingReasonForStay,
    paymentTakenNow,
    productSelectedRate,
    businessAccQuestions: '',
    userDefinedQuestions: '',
    bookingId: bookingId || '',
    bookingPanelComponentsOrder: '',
    contentComponentOrder: '',
    bookingZipCode: '',
    paymentSessionID: '',
    paymentTemplateID: '',
    price: {
      cartTotal: {
        amount: '',
        currency: '',
      },
      currency: '',
      voucherCode: '',
      voucherDiscount: '',
    },
    cardType: '',
    validation: '',
    paymentOutage,
  });

  if (cardType) {
    analyticsConfirmation.update({ cardType });
  }
  if (paymentTemplateID) {
    analyticsConfirmation.update({ paymentTemplateID });
  }
  if (paymentSessionID) {
    analyticsConfirmation.update({ paymentSessionID });
  }

  if (validation) {
    analyticsConfirmation.update({ validation });
  }

  if (productDetails && productDetails.length == 1) {
    let additionalCartItems: ProductDetailsInterface[] = [];
    const baseCartPrice = productDetails[0].price.basePrice;
    const totalNumberOfRooms = productDetails[0].productInfo.totalNumberOfRooms ?? '1';
    additionalCartItems.push({
      type: trackingTypes.DATE,
      quantity: +totalNumberOfRooms,
      price: { basePrice: baseCartPrice },
      productInfo: { sku: productDetails[0].productInfo.startDate },
    });

    if (upsells && upsells.rooms.length > 0 && upsells.rooms[0].length > 0) {
      const totalNumberOfNights = productDetails[0].productInfo.numberOfNights ?? '1';
      const foodRatePlans: Array<FoodRatePlan> = [];
      const foodPerRoom: Array<FoodPerRoom> = [];
      let totalFoodPrice = 0.0;
      let totalFoodQuantity = 0;
      let currency: string | undefined;
      upsells.rooms.forEach((roomUpsells: UpsellsSelection[], roomIndex: number) => {
        currency = roomUpsells[0].currency;
        const foodPerRoomCodes: Array<FoodPerRoomCodes> = [];
        roomUpsells.forEach((roomUpsell: UpsellsSelection) => {
          foodRatePlans.push(getFoodRatePlan(roomUpsell, totalNumberOfNights, currency));
          if (roomUpsell.quantity > 0) {
            const pricePerRoomCode = +roomUpsell.price * roomUpsell.quantity * +totalNumberOfNights;
            foodPerRoomCodes.push(getFoodPerRoomCode(roomUpsell, pricePerRoomCode, currency));
            totalFoodPrice += pricePerRoomCode;
            totalFoodQuantity += roomUpsell.quantity;
          }
        });
        if (foodPerRoomCodes.length > 0) {
          foodPerRoom.push({
            codes: foodPerRoomCodes,
            roomNumber: String(roomIndex + 1),
          });
        }
      });

      const uniqueFoodRatePlans = foodRatePlans.filter(
        (obj, index) => foodRatePlans.findIndex((item) => item.code === obj.code) === index
      );

      additionalCartItems = trackFoodUpsellsRatePlan(
        additionalCartItems,
        uniqueFoodRatePlans,
        foodPerRoom,
        +totalNumberOfRooms,
        totalFoodQuantity,
        totalFoodPrice,
        baseCartPrice,
        currency
      );
    }
    additionalCartItems.push({
      type: trackingTypes.ADDON,
      quantity: 0,
      price: { basePrice: '0.00' },
      productInfo: { sku: 'Donation' },
    });
    analyticsConfirmation.update({ CartItems: [...productDetails, ...additionalCartItems] });
  }

  if (window.__satelliteLoaded && pageName === analyticsConfirmationPageName) {
    // direct call rule (Adobe Analytics) for confirmation page
    window._satellite.track(
      bookingFlowComplete ? 'bookingflowComplete' : 'operaConfirmationFailed'
    );
  }
};

export const updateConfirmationAnalytics = () => {
  const { dailyRates, donationAmount } = window.analyticsData || {};
  const { CartItems, price } = window.analyticsDataCartConfirmation || {};
  if (CartItems && dailyRates) {
    trackDailyRates(CartItems, dailyRates, price?.currency);
  }
  if (CartItems && donationAmount) {
    trackDonationAddon(CartItems, donationAmount);
  }
};

export const updateConfirmationPageAnalytics = (
  bookingConfirmation: BookingConfirmation,
  packages: Packages,
  paymentAnalyticsData?: PaymentAnalytics,
  variant?: string
) => {
  const firstRoom = bookingConfirmation.reservationByIdList[0].roomStay || {};

  const noNights = getNightsNumber(firstRoom?.arrivalDate, firstRoom?.departureDate);
  const roomSelection: RoomSelection[] = packages.roomSelection ?? [];
  const adultsMeals: MealItem[] = adultsMealsSelector(packages?.meals, noNights);
  const childrenMeals: MealKids[] = childrenMealsSelector(packages?.mealsKids);
  const reasonForStay =
    bookingConfirmation?.reservationByIdList?.[0]?.additionalGuestInfo?.purposeOfStay;

  let totalAdults = 0;
  let totalChildren = 0;

  bookingConfirmation?.reservationByIdList.forEach((room: BCReservationListItem) => {
    totalAdults += room.roomStay.adultsNumber;
    totalChildren += room.roomStay.childrenNumber;
  });

  const arrivalDay = format(new Date(firstRoom.arrivalDate), 'EEEE');
  const departureDay = format(new Date(firstRoom.departureDate), 'EEEE');

  const ancPackagesSelection: Array<UpsellsSelection[]> = [];

  bookingConfirmation?.reservationByIdList.forEach(
    (_: BCReservationListItem, roomIndex: number) => {
      const packagesPerRoom = [
        ...adultsMeals.map((meal) => ({
          code: meal?.id ?? '',
          legend: meal?.name ?? '',
          quantity: getMealQuantity(packages, meal?.id ?? '', roomIndex),
          price: meal?.price?.toFixed(2) ?? '',
          currency: meal?.currency,
          freeBreakfastCode: meal?.freeBreakfastCode,
          freeBreakfastOption: meal?.freeBreakfastOption,
        })),
        ...childrenMeals.map((meal) => ({
          code: meal?.id ?? '',
          legend: meal?.name ?? '',
          quantity: getMealQuantity(packages, meal?.id ?? '', roomIndex),
          price: '0.00',
        })),
        ...(extrasPackagesAnalyticsMap(roomIndex, noNights, packages) ?? []),
      ];
      ancPackagesSelection.push([...packagesPerRoom]);
    }
  );

  let roomTypes, roomNames;
  if (bookingConfirmation?.reservationByIdList) {
    roomTypes = getUniqueRoomProperties(bookingConfirmation?.reservationByIdList);
    roomNames = getUniqueRoomProperties(bookingConfirmation?.reservationByIdList, 'roomName');
  }

  analytics.update({
    cardNotPresent: paymentAnalyticsData?.cardNotPresent,
    cardType: paymentAnalyticsData?.cardType,
    echoID: paymentAnalyticsData?.echoID,
    validation: '',
    bookingReasonForStay: reasonForStay,
    wifiAccessAllowed: false,
    wifiOption: '',
    currencyCode: bookingConfirmation.currencyCode,
    FromToDate: {
      ArrivalDay: arrivalDay,
      DepartureDay: departureDay,
      FromToDay: `${arrivalDay}-${departureDay}`,
    },
    rateCode: firstRoom.ratePlanCode,
    productSelectedRate: firstRoom.rateExtraInfo?.rateName,
    RoomTypes: roomTypes,
    RoomNames: roomNames,
    paymentCards: '',
    paymentCardSelected: paymentAnalyticsData?.paymentCardSelected ?? '',
    paymentCardTypes: '',
    paymentTakenNow: paymentAnalyticsData?.paymentTakenNow ?? '',
    paymentSessionID: paymentAnalyticsData?.paymentSessionID ?? '',
    paymentTemplateID: paymentAnalyticsData?.paymentTemplateID ?? '',
    paymentLoadTime: paymentAnalyticsData?.paymentLoadTime ?? '',
    piba: paymentAnalyticsData?.piba ?? '',
    alcoholAllowed: paymentAnalyticsData?.alcoholAllowed ?? false,
    carParkingAllowed: paymentAnalyticsData?.carParkingAllowed ?? false,
    dinnerAllowance: paymentAnalyticsData?.dinnerAllowance ?? false,
    otherChargesAllowed: paymentAnalyticsData?.otherChargesAllowed ?? false,
    pageName: analyticsConfirmationPageName,
    paypal: paymentAnalyticsData?.paypal,
    productDetails: [
      {
        type: analyticsTrackings.HOTEL,
        quantity: bookingConfirmation.reservationByIdList.length,
        price: {
          basePrice: (
            +bookingConfirmation?.totalCost -
            calculateTotalCostRoomSelection(adultsMeals, roomSelection, noNights)
          ).toFixed(2),
        },
        productInfo: {
          sku: bookingConfirmation.hotelId,
          totalNumberOfRooms: bookingConfirmation.reservationByIdList.length.toString(),
          roomAdults: totalAdults.toString(),
          roomChildren: totalChildren.toString(),
          numberOfGuests: (totalAdults + totalChildren).toString(),
          startDate: format(new Date(firstRoom.arrivalDate), 'yyyy-MM-dd'),
          endDate: format(new Date(firstRoom.departureDate), 'yyyy-MM-dd'),
          numberOfNights: noNights.toString(),
          daysToCheckIn: differenceInDays(new Date(firstRoom.arrivalDate), new Date()).toString(),
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

    const { cdhCompanyId, sessionId, accessLevel, cdhEmployeeId } =
      getLoggedInUserInfo(idTokenCookie);

    analytics.update({
      companyID: cdhCompanyId,
      sessionId: sessionId,
      userLevel: accessLevel,
      userID: cdhEmployeeId,
    });
  }
};

const getFoodRatePlan = (
  roomUpsell: UpsellsSelection,
  totalNumberOfNights: string,
  currency: string | undefined
) => {
  return {
    code: roomUpsell.code,
    legend: roomUpsell.legend,
    price: {
      amount: roomUpsell.price,
      currency,
    },
    foodUpsell: roomUpsell.price !== '0.00',
    freeBreakfastOption: !!roomUpsell.freeBreakfastOption,
    freeBreakfastCode: roomUpsell.freeBreakfastCode ? roomUpsell.freeBreakfastCode : '',
    totalCostPerGuest: {
      amount: roomUpsell?.price ? +roomUpsell?.price * +totalNumberOfNights : 0,
      currency,
    },
  };
};

const getFoodPerRoomCode = (
  roomUpsell: UpsellsSelection,
  pricePerRoomCode: number | undefined,
  currency: string | undefined
) => {
  return {
    code: roomUpsell.code ?? '',
    adults: roomUpsell.price !== '0.00' ? roomUpsell.quantity : 0,
    children: roomUpsell.price === '0.00' ? roomUpsell.quantity : 0,
    price: {
      amount: Number(pricePerRoomCode),
      currency,
    },
  };
};

const trackFoodUpsellsRatePlan = (
  cartItems: ProductDetailsInterface[],
  foodRatePlans: FoodRatePlan[],
  foodPerRoom: FoodPerRoom[],
  totalNumberOfRooms: number,
  totalFoodQuantity: number,
  totalFoodPrice: number,
  baseCartPrice: string,
  currency: string | undefined
) => {
  cartItems.push(
    {
      type: trackingTypes.FOOD,
      quantity: totalFoodQuantity,
      price: {
        basePrice: totalFoodPrice.toFixed(2),
      },
      productInfo: { FoodPerRoom: foodPerRoom },
      AvailabilityFoodRatePlan: foodRatePlans,
    },
    {
      type: trackingTypes.RATE_PLAN,
      quantity: totalNumberOfRooms,
      price: { basePrice: baseCartPrice },
      productInfo: { sku: '' },
      rawRatePlan: {
        upsellItems: foodRatePlans,
        totalCost: {
          amount: baseCartPrice,
          currency,
        },
        totalFoodCost: {
          amount: totalFoodPrice,
          currency,
        },
      },
    }
  );
  return cartItems;
};

export const trackDailyRates = (
  cartItems: ProductDetailsInterface[],
  dailyRates: RatesPerDate[][],
  currency: string | undefined
) => {
  const dateIndex = cartItems.findIndex((obj) => {
    return obj.type === trackingTypes.DATE && !obj.productInfo?.dailyRates;
  });
  if (dateIndex > -1) {
    const ratesPerDate: Array<DailyRate[]> = [];
    dailyRates.forEach((rates: RatesPerDate[]) => {
      const dailyRate: Array<DailyRate> = [];
      rates.forEach((rate: RatesPerDate) => {
        const date: Date = new Date(rate.startDate);
        dailyRate.push({
          date: {
            day: format(date, 'EEEE'),
            date: +format(date, 'dd'),
            year: +format(date, 'yyyy'),
            month: format(date, 'MMMM'),
            shortDay: format(date, 'EEE'),
            shortMonth: format(date, 'MMM'),
          },
          backupDate: rate.startDate,
          price: {
            amount: rate.pricePerNight?.toFixed(2),
            currency,
          },
          cityTax: rate.cityTaxPerNight,
        });
      });
      ratesPerDate.push(dailyRate);
    });
    cartItems[dateIndex].productInfo.dailyRates = ratesPerDate;
    analyticsConfirmation.update({ CartItems: cartItems });
  }
};

export const trackDonationAddon = (
  cartItems: ProductDetailsInterface[],
  donationAmount: number
) => {
  const addonIndex = cartItems.findIndex((obj) => {
    return obj.type === trackingTypes.ADDON;
  });
  if (addonIndex > -1) {
    cartItems[addonIndex].price.basePrice = donationAmount.toFixed(2);
    cartItems[addonIndex].quantity = 1;
    analyticsConfirmation.update({ CartItems: cartItems });
  }
};

export default initConfirmationAnalytics;
