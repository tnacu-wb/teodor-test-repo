import { renderHook } from '@testing-library/react';
import { HotelBrand, PiCardType } from '@whitbread-eos/api';

import usePaymentData from './use-payment-data';

const mockUseQueryRequest = jest.fn();
const mockUsePackages = jest.fn();
const mockUseUpdateRateName = jest.fn();

const mockCreateReservationDetails = jest.fn();
const mockFormatUrlTermsConditions = jest.fn();
const mockGetBookingSummaryData = jest.fn();
const mockGetCityTaxMessages = jest.fn();
const mockGetIsBillingAddressDisplayed = jest.fn();
const mockGetMaxValueFromRoomStays = jest.fn();
const mockGetNightsNumber = jest.fn();
const mockGetTotalCost = jest.fn();
const mockAdultsMealsSelector = jest.fn();
const mockChildrenMealsSelector = jest.fn();
const mockFormatImportantNotes = jest.fn();
const mockMealsMapperSelector = jest.fn();

jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useQueryRequest: (...args: any[]) => mockUseQueryRequest(...args),
}));

jest.mock('./use-packages', () => ({
  __esModule: true,
  default: (...args: any[]) => mockUsePackages(...args),
}));

jest.mock('./use-discounted-rate', () => ({
  ...jest.requireActual('./use-discounted-rate'),
  useUpdateRateName: (...args: any[]) => mockUseUpdateRateName(...args),
}));

jest.mock('../formatters', () => ({
  ...jest.requireActual('../formatters'),
  createReservationDetails: (...args: any[]) => mockCreateReservationDetails(...args),
  formatUrlTermsConditions: (...args: any[]) => mockFormatUrlTermsConditions(...args),
}));

jest.mock('../getters', () => ({
  ...jest.requireActual('../getters'),
  getBookingSummaryData: (...args: any[]) => mockGetBookingSummaryData(...args),
  getCityTaxMessages: (...args: any[]) => mockGetCityTaxMessages(...args),
  getIsBillingAddressDisplayed: (...args: any[]) => mockGetIsBillingAddressDisplayed(...args),
  getMaxValueFromRoomStays: (...args: any[]) => mockGetMaxValueFromRoomStays(...args),
  getNightsNumber: (...args: any[]) => mockGetNightsNumber(...args),
}));

jest.mock('../helpers', () => ({
  ...jest.requireActual('../helpers'),
  getTotalCost: (...args: any[]) => mockGetTotalCost(...args),
}));

jest.mock('../selectors', () => ({
  ...jest.requireActual('../selectors'),
  adultsMealsSelector: (...args: any[]) => mockAdultsMealsSelector(...args),
  childrenMealsSelector: (...args: any[]) => mockChildrenMealsSelector(...args),
  formatImportantNotes: (...args: any[]) => mockFormatImportantNotes(...args),
  mealsMapperSelector: (...args: any[]) => mockMealsMapperSelector(...args),
}));

const basketReference = 'ABC123';
const language = 'en';
const country = 'gb';

const hiQueryInput = {
  hotelId: 'MANOLD',
  country,
  language,
} as any;

const pcksQueryInput = {
  adultsNumber: 2,
  childrenNumber: 1,
  hotelId: 'MANOLD',
  endDate: '2026-09-02',
  startDate: '2026-09-01',
  bookingFlowId: 'flow-1',
  nightsNumber: 1,
  channel: 'WEB',
} as any;

const selectedPaymentDetail = { type: PiCardType.PAY_NOW, order: 1, enabled: true } as any;
const selectedPaymentType = { type: 'NEW_CARD', paymentOptions: [] } as any;
const basketDetailsState = { rateDescription: 'Fallback rate', rateTags: ['tag-1'] };
const formData = { billing: {} };

const bookingInfoData = {
  bookingInformation: {
    hotelId: 'MANOLD',
    currencyCode: 'GBP',
    totalCost: '120.00',
    cityTaxTotal: '3.00',
    reservationByIdList: [
      {
        roomStay: {
          arrivalDate: '2026-09-01',
          departureDate: '2026-09-02',
          adultsNumber: 2,
          childrenNumber: 1,
          ratePlanCode: 'FLEX',
          roomExtraInfo: { roomType: 'DOUBLE' },
          rateExtraInfo: { rateDescription: 'Flexible rate' },
        },
        additionalGuestInfo: { purposeOfStay: 'LEI' },
      },
    ],
  },
};

const hotelInfoData = {
  hotelInformation: {
    brand: HotelBrand.PID,
    importantInfo: {
      infoItems: [{ content: 'Hotel message' }],
    },
  },
};

const termsData = {
  termsAndConditions: {
    text: 'Please see /terms/payment.html for details',
  },
};

const paymentInfoData = {
  paymentInfoMessages: [
    { paymentType: PiCardType.PAY_NOW, messages: ['Pay now message', 'Card reminder'] },
    { paymentType: PiCardType.RESERVE_WITHOUT_CARD, messages: ['Reserve message'] },
  ],
};

const packagesResponse = {
  isLoading: false,
  packages: {
    meals: [{ id: 'meal' }],
    mealsKids: [{ id: 'kids' }],
    roomSelection: [{ packagesSelection: [{ id: 'meal', noOfSelections: 1 }] }],
  },
  hotelHasCityTaxForBusiness: false,
  hotelHasCityTaxForLeisure: true,
};

describe('usePaymentData', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUsePackages.mockReturnValue(packagesResponse);
    mockUseQueryRequest
      .mockReturnValueOnce({ isLoading: false, data: bookingInfoData })
      .mockReturnValueOnce({ isLoading: false, data: hotelInfoData })
      .mockReturnValueOnce({ isLoading: false, data: termsData })
      .mockReturnValueOnce({ isLoading: false, data: paymentInfoData });

    mockGetNightsNumber.mockReturnValue(1);
    mockGetMaxValueFromRoomStays.mockReturnValueOnce(2).mockReturnValueOnce(1);
    mockCreateReservationDetails.mockReturnValue({ currency: 'GBP', dummy: true });
    mockGetTotalCost.mockReturnValue('GBP120.00');
    mockGetBookingSummaryData.mockReturnValue({ total: 'summary' });
    mockGetCityTaxMessages.mockReturnValue({ summaryText: 'City tax applies' });
    mockGetIsBillingAddressDisplayed.mockReturnValue(true);
    mockMealsMapperSelector.mockReturnValue([{ id: 'meal', quantity: 1 }]);
    mockAdultsMealsSelector.mockReturnValue([{ id: 'adult-meal' }]);
    mockChildrenMealsSelector.mockReturnValue([{ id: 'kids-meal' }]);
    mockFormatImportantNotes.mockReturnValue('Important hotel note');
    mockFormatUrlTermsConditions.mockReturnValue('Formatted terms text');
  });

  it('returns derived values and message structures from payment data sources', () => {
    const onClickBillingFormHandler = jest.fn();
    const t = jest.fn((key: string) => key);

    const { result } = renderHook(() =>
      usePaymentData({
        hiQueryInput,
        pcksQueryInput,
        basketReference,
        language,
        country,
        selectedPaymentDetail,
        selectedPaymentType,
        paymentStepState: 'PAYMENT_DETAILS',
        basketDetailsState,
        formData,
        onclickBillingFormHandler: onClickBillingFormHandler,
        t,
      })
    );

    expect(result.current.bookingInformation).toEqual({
      hotelId: 'MANOLD',
      adults: 2,
      children: 1,
      nrNights: 1,
      ratePlanCode: 'FLEX',
      totalCost: '120.00',
    });
    expect(result.current.reservationDetails).toEqual({ currency: 'GBP', dummy: true });
    expect(result.current.bookingSummaryData).toEqual({ total: 'summary' });
    expect(result.current.cityTaxMessages).toEqual({ summaryText: 'City tax applies' });
    expect(result.current.infoMessages).toEqual(['Important hotel note']);
    expect(result.current.orderedInfoMessages).toEqual([
      { indexOrder: 0, infoMsg: 'Important hotel note' },
    ]);
    expect(result.current.orderedListOfMessagesPaymentType).toEqual([
      { index: 0, messagesNotif: 'Pay now message' },
      { index: 1, messagesNotif: 'Card reminder' },
    ]);
    expect(result.current.termsAndConditionsText).toBe('Formatted terms text');
    expect(result.current.isGermanHotel).toBe(true);
    expect(result.current.isBillingAddressDisplayed).toBe(true);
    expect(result.current.isLoading).toBe(false);

    expect(mockUseUpdateRateName).toHaveBeenCalledWith(
      bookingInfoData.bookingInformation,
      language,
      country
    );
  });

  it('sets isLoading=true when any underlying query is still loading', () => {
    mockUseQueryRequest.mockReset();
    mockUseQueryRequest
      .mockReturnValueOnce({ isLoading: true, data: undefined })
      .mockReturnValueOnce({ isLoading: false, data: hotelInfoData })
      .mockReturnValueOnce({ isLoading: false, data: termsData })
      .mockReturnValueOnce({ isLoading: false, data: paymentInfoData });

    const { result } = renderHook(() =>
      usePaymentData({
        hiQueryInput,
        pcksQueryInput,
        basketReference,
        language,
        country,
        selectedPaymentDetail,
        selectedPaymentType,
        paymentStepState: 'PAYMENT_DETAILS',
        basketDetailsState,
        formData,
        onclickBillingFormHandler: jest.fn(),
        t: (key: string) => key,
      })
    );

    expect(result.current.isLoading).toBe(true);
  });
});
