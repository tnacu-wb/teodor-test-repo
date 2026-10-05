import { CountryEnum, paymentSteps } from '@whitbread-eos/api';

import {
  getBillingAddress,
  getBookingSummaryData,
  getIsBBCardDetailsDisplayed,
  getIsBillingAddressDisplayed,
  getIsDonationInfoBoxDisplayed,
  getIsDonationsDisplayed,
  getPaymentError,
  setBusinessAllowancesSections,
} from './payment';

const bkngMockData = {
  bookingInformation: {
    hotelId: 'MANOLD',
    totalCost: 59,
    currencyCode: 'GBP',
    bookingFlowId: 'booking-a1',
    infoMessages: [
      '<p>Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
    ],
    reservationByIdList: [
      {
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2022-08-29',
          departureDate: '2022-08-30',
          ratePlanCode: 'STANDARD',
          rateExtraInfo: { rateName: 'standard' },
          roomExtraInfo: { roomType: 'Double', roomName: 'Premier Plus double' },
          accessibleRoom: { isAccessible: false, phoneNumber: '0333 321 1315' },
        },
      },
    ],
    upgradeToFlex: { amount: 10, currency: 'EUR', flexRateCode: 'GB' },
  },
};

const mockOnclickBillingFormHandler = jest.fn();

const mockBookingData = {
  hiData: {
    hotelInformation: {
      name: 'hotelNameText',
      address: { addressLine1: 'addr1' },
      hotelBrand: 'PI',
    },
  },
  bkngData: bkngMockData,
  selectedDonation: { unitPrice: 0, code: '' },
  termsAndConditionsData: {
    termsAndConditions: {
      text: 'Terms and conditions text',
    },
  },
  onclickBillingFormHandler: mockOnclickBillingFormHandler,
  onSubmitBtnText: 'Submit',
  firstRoom: {
    roomStay: {
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2022-09-27',
      departureDate: '2022-09-28',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: {
        rateName: 'Flex',
      },
      roomExtraInfo: {
        roomType: 'DB',
        roomName: 'Double Room',
      },
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
      },
    },
  },
  noNights: 2,
  selectedMeals: {
    adultsMeals: [],
    childrenMeals: [],
  },
  adultsMeals: [],
  childrenMeals: [],
  roomSelection: [
    {
      packagesSelection: [],
    },
  ],
  paymentStepState: paymentSteps.CARD_DETAILS,
  updatedTotalCost: { name: 'ASD', totalCost: { amount: 59, currency: 'GBP' } },
};

const t = (key: string) => {
  switch (key) {
    case 'errors.booking.fraud.leisure.pi':
      return 'errors.booking.fraud.leisure.pi';
    case 'errors.booking.fraud.business.pi':
      return 'errors.booking.fraud.business.pi';
    case 'errors.booking.fraud.leisure.hub':
      return 'errors.booking.fraud.leisure.hub';
    case 'errors.booking.fraud.business.hub':
      return 'errors.booking.fraud.business.hub';
    case 'errors.payment.generic':
      return 'errors.payment.generic';
    case 'errors.sorry':
      return 'errors.sorry';
    case 'errors.paypal.generic.error':
      return 'errors.paypal.generic.error';
    default:
      return 'default';
  }
};

describe('payment methods', () => {
  describe('getBookingSummaryData Method', () => {
    it('should return the correct booking summary data', () => {
      const bookingSummaryData = getBookingSummaryData(mockBookingData);
      const expectedBookingSummaryData = {
        hotelInformation: {
          hotelAddress: ['addr1'],
          hotelCountry: undefined,
          hotelName: 'hotelNameText',
          hotelBrand: undefined,
        },
        onSubmitBtnText: 'Submit',
        onclickBillingFormHandler: mockOnclickBillingFormHandler,
        paymentStepState: 'CARD_DETAILS',
        rateInformation: {
          noNights: 2,
          noRooms: 1,
          rate: 'Flex',
        },
        roomInformation: [
          {
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
            nrAdults: 1,
            nrChildren: 0,
            roomName: 'Premier Plus double',
            roomType: 'Double',
            selectedExtrasList: {
              packagesSelection: [],
              price: 0,
              reservationId: undefined,
            },
            selectedMeals: {
              adultsMeals: [],
              childrenMeals: [],
            },
          },
        ],
        stayDatesInformation: {
          arrivalDate: '2022-09-27',
          departureDate: '2022-09-28',
          noNights: 2,
        },
        termsAndConditionsText: 'Terms and conditions text',
        totalCost: {
          currency: 'GBP',
          donations: 0,
          initialTotalCost: 59,
          meals: [],
        },
      };
      expect(bookingSummaryData).toEqual(expectedBookingSummaryData);
    });

    it('should return null arrival and departure dates if none are provided in the roomStay prop', () => {
      const mockBookingDataCopy = Object.assign({}, mockBookingData);
      mockBookingDataCopy.firstRoom.roomStay.arrivalDate = '';
      mockBookingDataCopy.firstRoom.roomStay.departureDate = '';
      const bookingSummaryData = getBookingSummaryData(mockBookingDataCopy);
      const expectedBookingSummaryData = {
        hotelInformation: {
          hotelAddress: ['addr1'],
          hotelBrand: undefined,
          hotelCountry: undefined,
          hotelName: 'hotelNameText',
        },
        onSubmitBtnText: 'Submit',
        onclickBillingFormHandler: mockOnclickBillingFormHandler,
        paymentStepState: 'CARD_DETAILS',
        rateInformation: {
          noNights: 2,
          noRooms: 1,
          rate: 'Flex',
        },
        roomInformation: [
          {
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
            nrAdults: 1,
            nrChildren: 0,
            roomName: 'Premier Plus double',
            roomType: 'Double',
            selectedExtrasList: {
              packagesSelection: [],
              price: 0,
              reservationId: undefined,
            },
            selectedMeals: {
              adultsMeals: [],
              childrenMeals: [],
            },
          },
        ],
        stayDatesInformation: {
          arrivalDate: null,
          departureDate: null,
          noNights: 2,
        },
        termsAndConditionsText: 'Terms and conditions text',
        totalCost: {
          currency: 'GBP',
          donations: 0,
          initialTotalCost: 59,
          meals: [],
        },
      };
      expect(bookingSummaryData).toEqual(expectedBookingSummaryData);
    });
  });

  describe('getIsBillingAddressDisplayed Method', () => {
    it('should return true if brand is different from PID, selectedPaymentType is different from SAVED_CARD and selectedPaymentDetail is different from RESERVE_WITHOUT_CARD', () => {
      const testInput = {
        selectedPaymentType: { type: 'NEW_PIBA' },
        hiData: mockBookingData.hiData,
        selectedPaymentDetail: { type: 'PAY_NOW' },
      };
      expect(getIsBillingAddressDisplayed(testInput)).toEqual(true);
    });

    it('should return false if selectedPaymentType is SAVED_CARD', () => {
      const testInput = {
        selectedPaymentType: { type: 'SAVED_CARD' },
        hiData: mockBookingData.hiData,
        selectedPaymentDetail: { type: 'PAY_NOW' },
      };
      expect(getIsBillingAddressDisplayed(testInput)).toEqual(false);
    });

    it('should return false if selectedPaymentDetail is RESERVE_WITHOUT_CARD', () => {
      const testInput = {
        selectedPaymentType: { type: 'NEW_PIBA' },
        hiData: mockBookingData.hiData,
        selectedPaymentDetail: { type: 'RESERVE_WITHOUT_CARD' },
      };
      expect(getIsBillingAddressDisplayed(testInput)).toEqual(false);
    });

    it('should return false if isBillingAddressDisplayed is true', () => {
      const localStorageData = {
        isBillingAddressDisplayed: true,
      };
      const testInput = {
        selectedPaymentType: { type: 'NEW_PIBA' },
        formData: localStorageData,
        selectedPaymentDetail: { type: 'PAY_NOW' },
      };
      expect(getIsBillingAddressDisplayed(testInput)).toEqual(false);
    });

    it('should return true if brand is PID, but the user is on BB', () => {
      const testHiData = {
        hotelInformation: { ...mockBookingData.hiData.hotelInformation, brand: 'PID' },
      };
      const testInput = {
        selectedPaymentType: { type: 'NEW_PIBA' },
        hiData: testHiData,
        selectedPaymentDetail: { type: 'PAY_NOW' },
        isBb: true,
      };
      expect(getIsBillingAddressDisplayed(testInput)).toEqual(true);
    });
  });

  describe('getIsDonationsDisplayed Method', () => {
    it('should return true if language is en and the hotel is not in Ireland', () => {
      const testInput = {
        currentLang: 'en',
        hiData: {
          hotelInformation: {
            ...mockBookingData.hiData.hotelInformation,
            address: { country: 'Germany' },
          },
        },
      };
      expect(getIsDonationsDisplayed(testInput)).toEqual(true);
    });

    it('should return false if language is de', () => {
      const testInput = {
        currentLang: 'de',
        hiData: {
          hotelInformation: {
            ...mockBookingData.hiData.hotelInformation,
            address: { country: 'Germany' },
          },
        },
      };
      expect(getIsDonationsDisplayed(testInput)).toEqual(false);
    });

    it('should return false if the hotel is in Ireland', () => {
      const testInput = {
        currentLang: 'en',
        hiData: {
          hotelInformation: {
            ...mockBookingData.hiData.hotelInformation,
            address: { country: 'Ireland' },
          },
        },
      };
      expect(getIsDonationsDisplayed(testInput)).toEqual(false);
    });
  });

  describe('getIsDonationInfoBoxDisplayed Method', () => {
    it('should return the donation information if language is en, payment step is PAYMENT_DETAILS and the hotel is not in Ireland', () => {
      const testInput = {
        currentLang: 'en',
        paymentStepState: paymentSteps.PAYMENT_DETAILS,
        hiData: {
          hotelInformation: {
            ...mockBookingData.hiData.hotelInformation,
            address: { country: 'Germany' },
          },
        },
        paymentSteps: paymentSteps,
        donationsData: { donations: { informationBox: 'donation info' } },
      };
      expect(getIsDonationInfoBoxDisplayed(testInput)).toEqual('donation info');
    });

    it('should return false if language is de', () => {
      const testInput = {
        currentLang: 'de',
        paymentStepState: paymentSteps.PAYMENT_DETAILS,
        hiData: {
          hotelInformation: {
            ...mockBookingData.hiData.hotelInformation,
            address: { country: 'Germany' },
          },
        },
        paymentSteps: paymentSteps,
        donationsData: { donations: { informationBox: 'donation info' } },
      };
      expect(getIsDonationInfoBoxDisplayed(testInput)).toEqual(false);
    });

    it('should return false if payment step is not PAYMENT_DETAILS', () => {
      const testInput = {
        currentLang: 'en',
        paymentStepState: paymentSteps.CARD_DETAILS,
        hiData: {
          hotelInformation: {
            ...mockBookingData.hiData.hotelInformation,
            address: { country: 'Germany' },
          },
        },
        paymentSteps: paymentSteps,
        donationsData: { donations: { informationBox: 'donation info' } },
      };
      expect(getIsDonationInfoBoxDisplayed(testInput)).toEqual(false);
    });

    it('should return false if the hotel is in Ireland', () => {
      const testInput = {
        currentLang: 'en',
        paymentStepState: paymentSteps.PAYMENT_DETAILS,
        hiData: {
          hotelInformation: {
            ...mockBookingData.hiData.hotelInformation,
            address: { country: 'Ireland' },
          },
        },
        paymentSteps: paymentSteps,
        donationsData: { donations: { informationBox: 'donation info' } },
      };
      expect(getIsDonationInfoBoxDisplayed(testInput)).toEqual(false);
    });
  });

  describe('setBusinessAllowancesSections Method', () => {
    it('should set business allowances sections for greater london county', () => {
      const testDisplayedSections = { businessAllowancesSections: {} };
      const testInput = {
        displayedSections: testDisplayedSections,
        selectedPaymentType: {
          type: 'NEW_PIBA',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 200 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
        },
        hotelCounty: 'greater-london',
        hiData: {
          hotelInformation: { address: { country: CountryEnum.GB } },
        },
      };
      const expectedOutput = {
        businessAllowancesSections: {
          allowAlcohol: true,
          allowCarParking: true,
          amount: 200,
          allowDinner: true,
        },
      };
      setBusinessAllowancesSections(testInput);
      expect(testDisplayedSections).toEqual(expectedOutput);
    });

    it('should set business allowances sections for non greater london county', () => {
      const testDisplayedSections = { businessAllowancesSections: {} };
      const testInput = {
        displayedSections: testDisplayedSections,
        selectedPaymentType: {
          type: 'NEW_PIBA',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 50 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
        },
        hiData: {
          hotelInformation: { address: { country: CountryEnum.GB } },
        },
        hotelCounty: 'london',
      };
      const expectedOutput = {
        businessAllowancesSections: {
          allowAlcohol: true,
          allowCarParking: true,
          amount: 50,
          allowDinner: true,
        },
      };
      setBusinessAllowancesSections(testInput);
      expect(testDisplayedSections).toEqual(expectedOutput);
    });

    it('should set business allowances sections amount to 0 and allowDinner to false if greaterLondon amount is 0', () => {
      const testDisplayedSections = { businessAllowancesSections: {} };
      const testInput = {
        displayedSections: testDisplayedSections,
        selectedPaymentType: {
          type: 'NEW_PIBA',
          bookingAllowances: {
            maxDinnerBudgets: { greaterLondon: { amount: 0 }, ukWide: { amount: 50 } },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
        },
        hotelCounty: 'greater-london',
      };
      const expectedOutput = {
        businessAllowancesSections: {
          allowAlcohol: true,
          allowCarParking: true,
          allowDinner: false,
          amount: 0,
        },
      };
      setBusinessAllowancesSections(testInput);
      expect(testDisplayedSections).toEqual(expectedOutput);
    });

    it('should set default allowAlcohol and allowCarParking to false if bookingAllowances do not allow it for the selected payment type', () => {
      const testDisplayedSections = { businessAllowancesSections: {} };
      const testInput = {
        displayedSections: testDisplayedSections,
        selectedPaymentType: {
          type: 'NEW_PIBA',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
          },
        },
        hotelCounty: 'greater-london',
      };
      const expectedOutput = {
        businessAllowancesSections: {
          allowAlcohol: false,
          allowCarParking: false,
          allowDinner: true,
          amount: 100,
        },
      };
      setBusinessAllowancesSections(testInput);
      expect(testDisplayedSections).toEqual(expectedOutput);
    });
  });

  describe('getIsBBCardDetailsDisplayed Method', () => {
    it('should return default displayedSections if no bookingAllowances are allowed', () => {
      const testInput = {
        selectedPaymentType: {
          bookingAllowances: null,
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: false,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return default displayedSections if hotelCountry is not United Kingdom (the)', () => {
      const testInput = {
        selectedPaymentType: {
          bookingAllowances: null,
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'gb',
      };
      const expectedOutput = {
        referenceDetails: false,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for NEW_PIBA selected payment type', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'NEW_PIBA',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: true,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 100,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - PIBA Centrally stored card type AT', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'AT',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: true,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 100,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - PIBA Centrally stored card type PI', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'PI',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: true,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 100,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - PIBA Centrally stored card cnp not required', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: { greaterLondon: { amount: 100 }, ukWide: { amount: 50 } },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'PI',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: false,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - PIBA Personal stored card type AT', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'AT',
            cardType: 'BUSINESS_PERSONAL_STORED_CARD',
            cnpRequired: true,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 100,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - PIBA Personal stored card type PI', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'PI',
            cardType: 'BUSINESS_PERSONAL_STORED_CARD',
            cnpRequired: true,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 100,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - PIBA Personal stored card cnp not required', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'PI',
            cardType: 'BUSINESS_PERSONAL_STORED_CARD',
            cnpRequired: false,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: true,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 100,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - Centrally Stored Normal card type not AT or PI', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 100 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'Visa',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: true,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 100,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - Centrally Stored Normal card type not AT or PI and cnp not required', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: { greaterLondon: { amount: 100 }, ukWide: { amount: 50 } },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'Visa',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: false,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - Centrally Stored Normal card type AT or PI', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: { greaterLondon: { amount: 100 }, ukWide: { amount: 50 } },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'AT',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: false,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for selected payment type different from SAVED_CARD or NEW_PIBA', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'DIFFERENT_TYPE',
          bookingAllowances: {
            maxDinnerBudgets: { greaterLondon: { amount: 100 }, ukWide: { amount: 50 } },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'AT',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: false,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: false,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return displayedSections for SAVED_CARD selected payment type - PIBA Centrally stored card non Greater London county', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: {
              greaterLondon: { amount: 100 },
              ukWide: { amount: 50 },
              ireland: { amount: 50 },
            },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'PI',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: true,
          },
        },
        hotelCounty: 'london',
        hotelCountry: 'United Kingdom (the)',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: true,
        businessAllowancesSections: {
          amountDisabled: true,
          amount: 50,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return amountDisabled as false if accessLevel is BOOKER', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: { greaterLondon: { amount: 100 }, ukWide: { amount: 50 } },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'PI',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: false,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
        accessLevel: 'BOOKER',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: false,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });

    it('should return amountDisabled as false if accessLevel is SUPER', () => {
      const testInput = {
        selectedPaymentType: {
          type: 'SAVED_CARD',
          bookingAllowances: {
            maxDinnerBudgets: { greaterLondon: { amount: 100 }, ukWide: { amount: 0 } },
            allowAlcohol: true,
            allowCarParking: true,
            allowDinner: true,
          },
          card: {
            type: 'PI',
            cardType: 'BUSINESS_CENTRALLY_STORED_CARD',
            cnpRequired: false,
          },
        },
        hotelCounty: 'greater-london',
        hotelCountry: 'United Kingdom (the)',
        accessLevel: 'BOOKER',
      };
      const expectedOutput = {
        referenceDetails: true,
        paymentAuth: false,
        businessAllowances: false,
        businessAllowancesSections: {
          amountDisabled: false,
          amount: 0,
          allowDinner: true,
          allowAlcohol: false,
          allowCarParking: false,
          allowWiFi: false,
        },
      };
      const displayedSections = getIsBBCardDetailsDisplayed(testInput);
      expect(displayedSections).toEqual(expectedOutput);
    });
  });

  describe('getBillingAddress Method', () => {
    it('should return billingAddress if isBillingAddressDisplayed is true and country is gb', () => {
      const testInput = {
        countryRouter: 'gb',
        isBillingAddressDisplayed: true,
        billingAddress: {
          addressLine1: 'address',
          countryCode: 'countryCode',
          country: 'country',
          postalCode: 'postalCode',
        },
        billing: {
          address: {
            addressLine1: '24 Fitzroy Court',
            postalCode: 'CR0 2AX',
          },
          telephone: '34534534543',
          firstName: 'Test',
          lastName: 'German',
          email: 'testgerman1@mailinator.com',
        },
      };
      const expectedOutput = {
        addressLine1: 'address',
        companyName: undefined,
        country: 'countryCode',
        countryCode: undefined,
        postalCode: 'postalCode',
        addressType: 'BUSINESS',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });

    it('should return billing address if country is de', () => {
      const testInput = {
        countryRouter: 'de',
        isBillingAddressDisplayed: true,
        billingAddress: {
          addressLine1: 'address',
          countryCode: 'countryCode',
          country: 'country',
          postalCode: 'postalCode',
        },
        billing: {
          address: {
            addressLine1: '24 Fitzroy Court',
            postalCode: 'CR0 2AX',
          },
          telephone: '34534534543',
          firstName: 'Test',
          lastName: 'German',
          email: 'testgerman1@mailinator.com',
        },
      };
      const expectedOutput = {
        addressLine1: '24 Fitzroy Court',
        postalCode: 'CR0 2AX',
        addressType: 'BUSINESS',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });

    it('should return billing address if isBillingAddressDisplayed is false', () => {
      const testInput = {
        countryRouter: 'gb',
        isBillingAddressDisplayed: false,
        billingAddress: {
          addressLine1: 'address',
          countryCode: 'countryCode',
          country: 'country',
          postalCode: 'postalCode',
        },
        billing: {
          address: {
            addressLine1: '24 Fitzroy Court',
            postalCode: 'CR0 2AX',
          },
          telephone: '34534534543',
          firstName: 'Test',
          lastName: 'German',
          email: 'testgerman1@mailinator.com',
        },
      };
      const expectedOutput = {
        addressLine1: '24 Fitzroy Court',
        postalCode: 'CR0 2AX',
        addressType: 'BUSINESS',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });

    it('should return billingAddress using country instead of countryCode, if not is provided', () => {
      const testInput = {
        countryRouter: 'gb',
        isBillingAddressDisplayed: true,
        billingAddress: {
          addressLine1: 'address',
          country: 'country',
          postalCode: 'postalCode',
        },
        billing: {
          address: {
            addressLine1: '24 Fitzroy Court',
            postalCode: 'CR0 2AX',
          },
          telephone: '34534534543',
          firstName: 'Test',
          lastName: 'German',
          email: 'testgerman1@mailinator.com',
        },
      };
      const expectedOutput = {
        addressLine1: 'address',
        companyName: undefined,
        country: 'country',
        countryCode: undefined,
        postalCode: 'postalCode',
        addressType: 'BUSINESS',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });
  });

  describe('getPaymentError Method', () => {
    it('should return undefined if no error is present', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: false,
        isErrorBasketConfirmation: false,
        initiatePaymentMutationError: {
          response: 'payment mutation error',
        },
        basketConfirmationError: {
          response: 'basket confirmation error',
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = undefined;
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.booking.fraud.leisure.pi', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                errorInfo: { globalErrTextTemplate: 'fraud_check_failed' },
              },
            ],
          },
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.booking.fraud.leisure.pi';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.booking.fraud.business.pi', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                errorInfo: { globalErrTextTemplate: 'fraud_check_failed' },
              },
            ],
          },
        },
        currentReasonForStay: 'BUS',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.booking.fraud.business.pi';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.booking.fraud.leisure.hub', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                errorInfo: { globalErrTextTemplate: 'fraud_check_failed' },
              },
            ],
          },
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'HUB',
        t,
      };
      const expectedOutput = 'errors.booking.fraud.leisure.hub';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.booking.fraud.business.hub', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                errorInfo: { globalErrTextTemplate: 'fraud_check_failed' },
              },
            ],
          },
        },
        currentReasonForStay: 'BUS',
        hotelBrand: 'HUB',
        t,
      };
      const expectedOutput = 'errors.booking.fraud.business.hub';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.payment.generic', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                errorInfo: { globalErrTextTemplate: 'errors.payment.generic' },
              },
            ],
          },
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.payment.generic';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.sorry', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                errorInfo: { globalErrTextTemplate: 'errors.other.kind.of.error' },
              },
            ],
          },
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.sorry';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should check Paypal return generic error', () => {
      const testInput = {
        isErrorInitiatePaypalPaymentMutation: true,
        initiatePaypalPaymentMutationError: {
          response: {
            errors: [
              {
                errorInfo: { globalErrTextTemplate: 'paypal.generic.error' },
              },
            ],
          },
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.paypal.generic.error';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.booking.fraud.leisure.pi (Apollo)', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                message: '{"globalErrTextTemplate":"fraud_check_failed"}',
              },
            ],
          },
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.booking.fraud.leisure.pi';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should return errors.booking.fraud.business.pi (Apollo)', () => {
      const testInput = {
        isErrorInitiatePaymentMutation: true,
        initiatePaymentMutationError: {
          response: {
            errors: [
              {
                message: '{"globalErrTextTemplate":"fraud_check_failed"}',
              },
            ],
          },
        },
        currentReasonForStay: 'BUS',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.booking.fraud.business.pi';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });

    it('should check Paypal return generic error (Apollo)', () => {
      const testInput = {
        isErrorInitiatePaypalPaymentMutation: true,
        initiatePaypalPaymentMutationError: {
          response: {
            errors: [
              {
                message: '{"globalErrTextTemplate":"paypal.generic.error"}',
              },
            ],
          },
        },
        currentReasonForStay: 'LEI',
        hotelBrand: 'PI',
        t,
      };
      const expectedOutput = 'errors.paypal.generic.error';
      expect(getPaymentError(testInput)).toEqual(expectedOutput);
    });
  });

  describe('getBillingAddress Method', () => {
    it('should return the billing address for GB country router when billing address is displayed', () => {
      const testInput = {
        countryRouter: 'gb',
        isBillingAddressDisplayed: true,
        billingAddress: {
          addressLine1: 'Line 1',
          addressLine2: 'Line 2',
          addressLine3: 'Line 3',
          addressLine4: 'Line 4',
          postalCode: '12345',
          countryCode: 'GB',
          addressType: 'Type',
        },
        billing: {
          address: {
            addressLine1: 'Billing Line 1',
            addressLine2: 'Billing Line 2',
            addressLine3: 'Billing Line 3',
            addressLine4: 'Billing Line 4',
            cityName: 'City',
            countryCode: 'GB',
            postalCode: '54321',
            companyName: 'Company',
          },
        },
      };
      const expectedOutput = {
        addressLine1: 'Line 1',
        addressLine2: 'Line 2',
        addressLine3: 'Line 3',
        addressLine4: 'Line 4',
        postalCode: '12345',
        country: 'GB',
        countryCode: undefined,
        companyName: undefined,
        addressType: 'Type',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });

    it('should return the billing address for DE country router when billing address is not displayed', () => {
      const testInput = {
        countryRouter: 'us',
        isBillingAddressDisplayed: false,
        billingAddress: {
          addressLine1: 'Line 1',
          addressLine2: 'Line 2',
          addressLine3: 'Line 3',
          addressLine4: 'Line 4',
          postalCode: '12345',
          countryCode: 'DE',
        },
        billing: {
          address: {
            addressLine1: 'Billing Line 1',
            addressLine2: 'Billing Line 2',
            addressLine3: 'Billing Line 3',
            addressLine4: 'Billing Line 4',
            cityName: 'City',
            countryCode: 'DE',
            postalCode: '54321',
            companyName: 'Company',
            addressType: 'Type',
          },
        },
      };
      const expectedOutput = {
        addressLine1: 'Billing Line 1',
        addressLine2: 'Billing Line 2',
        addressLine3: 'Billing Line 3',
        addressLine4: 'Billing Line 4',
        cityName: 'City',
        country: 'DE',
        postalCode: '54321',
        companyName: 'Company',
        addressType: 'Type',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });

    it('should return the billing address for GB country router when billing address is not displayed', () => {
      const testInput = {
        countryRouter: 'gb',
        isBillingAddressDisplayed: false,
        billingAddress: {
          addressLine1: 'Line 1',
          addressLine2: 'Line 2',
          addressLine3: 'Line 3',
          addressLine4: 'Line 4',
          postalCode: '12345',
          countryCode: 'GB',
        },
        billing: {
          address: {
            addressLine1: 'Billing Line 1',
            addressLine2: 'Billing Line 2',
            addressLine3: 'Billing Line 3',
            addressLine4: 'Billing Line 4',
            cityName: 'City',
            countryCode: 'GB',
            postalCode: '54321',
            companyName: 'Company',
            addressType: 'Type',
          },
        },
      };
      const expectedOutput = {
        addressLine1: 'Billing Line 1',
        addressLine2: 'Billing Line 2',
        addressLine3: 'Billing Line 3',
        addressLine4: 'Billing Line 4',
        cityName: 'City',
        country: 'GB',
        postalCode: '54321',
        companyName: 'Company',
        addressType: 'Type',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });

    it('should return the billing address for DE country router when billing address is displayed', () => {
      const testInput = {
        countryRouter: 'us',
        isBillingAddressDisplayed: true,
        billingAddress: {
          addressLine1: 'Line 1',
          addressLine2: 'Line 2',
          addressLine3: 'Line 3',
          addressLine4: 'Line 4',
          postalCode: '12345',
          countryCode: 'DE',
        },
        billing: {
          address: {
            addressLine1: 'Billing Line 1',
            addressLine2: 'Billing Line 2',
            addressLine3: 'Billing Line 3',
            addressLine4: 'Billing Line 4',
            cityName: 'City',
            countryCode: 'DE',
            postalCode: '54321',
            companyName: 'Company',
            addressType: 'Type',
          },
        },
      };
      const expectedOutput = {
        addressLine1: 'Billing Line 1',
        addressLine2: 'Billing Line 2',
        addressLine3: 'Billing Line 3',
        addressLine4: 'Billing Line 4',
        cityName: 'City',
        country: 'DE',
        postalCode: '54321',
        companyName: 'Company',
        addressType: 'Type',
      };
      expect(getBillingAddress(testInput)).toEqual(expectedOutput);
    });
  });
});
