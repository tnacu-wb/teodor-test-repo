import { AnalyticsData, AnalyticsDataCartConfirmation } from '@whitbread-eos/api';

import analytics, { analyticsConfirmation, analyticsTrackings as trackingTypes } from './analytics';
import initConfirmationAnalytics, {
  updateConfirmationPageAnalytics,
  trackDonationAddon,
} from './confirmationPageAnalytics';

const analyticsUpdateSpy = jest.spyOn(analyticsConfirmation, 'update').mockReturnValue(undefined);

const analyticsConfirmationUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

const productDetailsTestData = [
  {
    type: 'Hotel',
    quantity: 1,
    price: {
      basePrice: '999.00',
    },
    productInfo: {
      sku: 'MANOLD',
      totalNumberOfRooms: '1',
      roomAdults: '1',
      roomChildren: '0',
      numberOfGuests: '1',
      startDate: '2023-10-01',
      endDate: '2023-10-02',
      numberOfNights: '1',
      daysToCheckIn: '60',
    },
  },
];
const upsellsTestData = {
  rooms: [
    [
      {
        code: 'BFADBF',
        legend: 'Premier Inn Breakfast',
        quantity: 1,
        price: '9.99',
        currency: 'GBP',
        freeBreakfastCode: 'BFCHDF',
        freeBreakfastOption: true,
      },
    ],
  ],
};

const bookingConfirmationData = {
  hotelName: 'Manchester Old Trafford',
  previousTotal: '',
  upgradeToFlex: {},
  balanceOutstanding: '200',
  bookingFlowId: 'booking-ct-a1',
  currencyCode: 'EUR',
  hotelId: 'FRAMTI',
  infoMessages: ['<p>Free cancellation up to 6pm on the day of arrival</p>\n'],
  newTotal: '200',
  policyCode: 'DAX',
  totalCost: '200',
  bookingReference: 'GAA6419665',
  bookingSpinnerConfig: [
    {
      order: '1',
      seconds: '10',
      text: 'One moment...',
    },
    {
      order: '2',
      seconds: '20',
      text: 'Hold tight we’re processing your order',
    },
    {
      order: '3',
      seconds: '270',
      text: 'Sorry for the delay - please bear with us',
    },
  ],
  reservationByIdList: [
    {
      additionalGuestInfo: {
        purposeOfStay: 'LEI',
      },
      reservationPackageList: [],
      depositPolicies: [
        {
          amountDue: {
            amount: 65,
            currencyCode: null,
          },
          amountPaid: {
            amount: 0,
            currencyCode: null,
          },
          policyCode: 'DAX',
        },
      ],
      reservationGuestList: [
        {
          givenName: 'asdas',
          surName: 'bbbbbbbss',
          nameTitle: 'Mr',
        },
      ],
      roomStay: {
        roomPrice: '69',
        adultsNumber: 2,
        arrivalDate: '2024-08-01',
        childrenNumber: 0,
        departureDate: '2024-08-02',
        roomType: 'WETDBL',
        cot: false,
        ratePlanCode: 'FLEXRATE',
        ratesPerNight: [
          {
            startDate: '2024-08-01',
            pricePerNight: '65',
            cityTaxPerNight: '4',
          },
        ],
        rateExtraInfo: {
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
          rateLongDescription: '',
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomDescription:
            'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
          roomType: 'WETDBL',
          roomName: 'Accessible double bedroom with level access shower room',
        },
      },
      billing: {
        address: {
          addressLine1: '5 Graham Road',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'HARROW',
          country: 'GB',
          postalCode: 'HA3 5RP',
        },
        title: 'Mr',
        telephone: '+4407760223333',
        lastName: 'Test',
        firstName: 'Test',
        email: 'testprod22@mailinator.com',
      },
    },
    {
      additionalGuestInfo: {
        purposeOfStay: 'LEI',
      },
      reservationPackageList: [],
      depositPolicies: [
        {
          amountDue: {
            amount: 70,
            currencyCode: null,
          },
          amountPaid: {
            amount: 0,
            currencyCode: null,
          },
          policyCode: 'DAX',
        },
      ],
      reservationGuestList: [
        {
          givenName: 'wqeqeqe',
          surName: 'adada',
          nameTitle: 'Mr',
        },
      ],
      roomStay: {
        roomPrice: '74',
        adultsNumber: 2,
        arrivalDate: '2024-08-01',
        childrenNumber: 0,
        departureDate: '2024-08-02',
        roomType: 'TWINRM',
        cot: false,
        ratePlanCode: 'FLEXRATE',
        ratesPerNight: [
          {
            startDate: '2024-08-01',
            pricePerNight: 70,
            cityTaxPerNight: 4,
          },
        ],
        rateExtraInfo: {
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
          rateLongDescription: '',
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomDescription:
            'Our twin rooms feature two super-comfy single beds, toasty duvets and and a choice of firm and soft pillows to help you get a great night’s sleep.',
          roomType: 'TWINRM',
          roomName: 'Twin room',
        },
      },
      billing: {
        address: {
          addressLine1: '5 Graham Road',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'HARROW',
          country: 'GB',
          postalCode: 'HA3 5RP',
        },
        title: 'Mr',
        telephone: '+4407760223333',
        lastName: 'Test',
        firstName: 'Test',
        email: 'testprod22@mailinator.com',
      },
    },
    {
      additionalGuestInfo: {
        purposeOfStay: 'LEI',
      },
      reservationPackageList: [],
      depositPolicies: [
        {
          amountDue: {
            amount: 55,
            currencyCode: null,
          },
          amountPaid: {
            amount: 0,
            currencyCode: null,
          },
          policyCode: 'DAX',
        },
      ],
      reservationGuestList: [
        {
          givenName: 'asdasd',
          surName: 'asdasdada',
          nameTitle: 'Mr',
        },
      ],
      roomStay: {
        roomPrice: 57,
        adultsNumber: 1,
        arrivalDate: '2024-08-01',
        childrenNumber: 0,
        departureDate: '2024-08-02',
        roomType: 'DOUBLE',
        cot: false,
        ratePlanCode: 'FLEXRATE',
        ratesPerNight: [
          {
            startDate: '2024-08-01',
            pricePerNight: 55,
            cityTaxPerNight: 2,
          },
        ],
        rateExtraInfo: {
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
          rateLongDescription: '',
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomDescription:
            'Our double rooms feature a super-comfy double bed, a toasty duvet, and a choice of firm and soft pillows to help you get a great night’s sleep.',
          roomType: 'DOUBLE',
          roomName: 'Double room',
        },
      },
      billing: {
        address: {
          addressLine1: '5 Graham Road',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'HARROW',
          country: 'GB',
          postalCode: 'HA3 5RP',
        },
        title: 'Mr',
        telephone: '+4407760223333',
        lastName: 'Test',
        firstName: 'Test',
        email: 'testprod22@mailinator.com',
      },
    },
  ],
};

const packagesData = {
  meals: [
    {},
    {
      allergyInfoLabel: 'Allergy & nutrition info',
      allergyInfoSrc:
        '/content/dam/pi/websites/desktop/de/restaurants/pi_info_allergene_zusatzstoffe_additives_allergens.pdf',
      bartId: '11',
      currency: 'EUR',
      description:
        '<p>Enjoy our all-you-can-eat Premier Inn Breakfast buffet made from high-quality local produce. Tuck into freshly cooked sausages, bacon and eggs cooked your way, plus freshly baked bread, savoury cold cuts and cheese, mueslis and yoghurt, fresh fruit, vegan alternatives. There’s plenty of hot drinks and fruit juices to choose from, too.</p>\r\n',
      freeBreakfastCode: 'BFCHDF',
      freeBreakfastMaxPerMeal: 2,
      freeBreakfastOption: true,
      id: 'BBIB',
      imageSrc: '/content/dam/global/restaurants/PID/pi-germany-breakfast-booking-334-189.jpg',
      name: 'Breakfast',
      menu: null,
      order: 1,
      price: 18,
    },
  ],
  mealsKids: [
    {},
    {
      allergyInfoLabel: null,
      allergyInfoSrc: null,
      currency: null,
      description:
        '<p>Great news – up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast. This means little ones get full access to our Premier Inn buffet breakfast – tuck in!</p>\r\n',
      id: 'BFCHDF',
      imageSrc: '/content/dam/global/restaurants/Global/de-kids-free-breakfast.png',
      menu: null,
      name: 'Free breakfast for kids',
      order: 1,
      price: null,
      totalPrice: null,
    },
  ],
  extrasItems: [
    {},
    {
      currency: 'GBP',
      description:
        '<p>Enjoy our all-you-can-eat Premier Inn Breakfast buffet made from high-quality local produce. Tuck into freshly cooked sausages, bacon and eggs cooked your way, plus freshly baked bread, savoury cold cuts and cheese, mueslis and yoghurt, fresh fruit, vegan alternatives. There’s plenty of hot drinks and fruit juices to choose from, too.</p>\r\n',
      id: 'HSCOU2',
      imageSrc: '/content/dam/global/restaurants/PID/pi-germany-breakfast-booking-334-189.jpg',
      name: 'Late check-out',
      order: 2,
      price: 10,
    },
  ],
  roomSelection: [
    {
      reservationId: '1988350',
      packagesSelection: [
        {
          id: 'CITYTAX',
          noOfSelections: 2,
        },
      ],
    },
    {
      reservationId: '1988349',
      packagesSelection: [
        {
          id: 'CITYTAX',
          noOfSelections: 2,
        },
        {
          id: 'HSATWN',
          noOfSelections: 1,
        },
      ],
    },
    {
      reservationId: '1988106',
      packagesSelection: [
        {
          id: 'CITYTAX',
          noOfSelections: 1,
        },
      ],
    },
  ],
};

const paymentAnalyticsData = {
  basketReference: 'GAA-5fece868-2fc2-4e2d-8e32-93995482f2ae',
  piba: '',
  paymentCardSelected: 'NEW_CARD',
  paymentTakenNow: '',
  cardNotPresent: true,
};

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    analyticsDataCartConfirmation: AnalyticsDataCartConfirmation;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

describe('confirmationPageAnalytics', () => {
  describe('initConfirmationAnalytics Method', () => {
    beforeEach(() => {
      Object.defineProperty(window, 'analyticsData', {
        value: {
          paymentTakenNow: 'paymentTakenNow',
          productSelectedRate: 'productSelectedRate',
          bookingReasonForStay: 'bookingReasonForStay',
          productDetails: 'productDetails',
          browserTimeZone: 'browserTimeZone',
          pageName: 'booking confirmation',
        },
        writable: true,
      });
    });

    afterEach(() => {
      analyticsUpdateSpy.mockReset();
    });

    it('should call analytics update one time with provided data', () => {
      initConfirmationAnalytics(true);
      expect(analyticsUpdateSpy).toHaveBeenCalledTimes(1);
    });

    it('should call analytics update 2 times if window.analyticsData contains cardType', () => {
      window.analyticsData.cardType = 'cardType';
      initConfirmationAnalytics(true);
      expect(analyticsUpdateSpy).toHaveBeenCalledTimes(2);
    });

    it('should call analytics update 3 times if window.analyticsData contains cardType, paymentTemplateId', () => {
      window.analyticsData.cardType = 'cardType';
      window.analyticsData.paymentTemplateID = 'paymentTemplateID';
      initConfirmationAnalytics(true);
      expect(analyticsUpdateSpy).toHaveBeenCalledTimes(3);
    });

    it('should call analytics update 4 times if window.analyticsData contains cardType, paymentTemplateId, paymentSessionID', () => {
      window.analyticsData.cardType = 'cardType';
      window.analyticsData.paymentTemplateID = 'paymentTemplateID';
      window.analyticsData.paymentSessionID = 'paymentSessionID';
      initConfirmationAnalytics(true);
      expect(analyticsUpdateSpy).toHaveBeenCalledTimes(4);
    });

    it('should call analytics update 5 times if window.analyticsData contains cardType, paymentTemplateId, paymentSessionID, validation', () => {
      window.analyticsData.cardType = 'cardType';
      window.analyticsData.paymentTemplateID = 'paymentTemplateID';
      window.analyticsData.paymentSessionID = 'paymentSessionID';
      window.analyticsData.validation = 'validation';
      initConfirmationAnalytics(true);
      expect(analyticsUpdateSpy).toHaveBeenCalledTimes(5);
    });

    it('should call analytics update 6 times if window.analyticsData contains cardType, paymentTemplateId, paymentSessionID, validation, upsells', () => {
      window.analyticsData.cardType = 'cardType';
      window.analyticsData.paymentTemplateID = 'paymentTemplateID';
      window.analyticsData.paymentSessionID = 'paymentSessionID';
      window.analyticsData.validation = 'validation';
      window.analyticsData.productDetails = productDetailsTestData;
      window.analyticsData.upsells = upsellsTestData;
      initConfirmationAnalytics(true);
      expect(analyticsUpdateSpy).toHaveBeenCalledTimes(6);
    });

    it('should sattelite track the booking flow completion', () => {
      window.__satelliteLoaded = true;
      window._satellite = {
        track: jest.fn(),
      };
      initConfirmationAnalytics(true);
      expect(window._satellite.track).toHaveBeenCalledWith('bookingflowComplete');
    });

    it('should sattelite track the booking flow completion, even if it was not finished', () => {
      window.__satelliteLoaded = true;
      window._satellite = {
        track: jest.fn(),
      };
      initConfirmationAnalytics(false);
      expect(window._satellite.track).toHaveBeenCalledWith('operaConfirmationFailed');
    });
  });

  describe('updateConfirmationPageAnalytics Method', () => {
    beforeEach(() => {
      Object.defineProperty(window, 'analyticsData', {
        value: {
          paymentTakenNow: 'paymentTakenNow',
          productSelectedRate: 'productSelectedRate',
          bookingReasonForStay: 'bookingReasonForStay',
          productDetails: 'productDetails',
          browserTimeZone: 'browserTimeZone',
          pageName: 'booking confirmation',
        },
        writable: true,
      });
    });
    afterEach(() => {
      analyticsConfirmationUpdateSpy.mockReset();
    });

    it('should run updateConfirmationPageAnalytics', () => {
      updateConfirmationPageAnalytics(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        bookingConfirmationData,
        packagesData,
        paymentAnalyticsData,
        'pi'
      );
      expect(analyticsConfirmationUpdateSpy).toHaveBeenCalledTimes(1);
    });
    it('should run updateConfirmationPageAnalytics', () => {
      updateConfirmationPageAnalytics(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        bookingConfirmationData,
        packagesData,
        paymentAnalyticsData,
        'bb'
      );
      expect(analyticsConfirmationUpdateSpy).toHaveBeenCalledTimes(3);
    });
  });
});
describe('trackDonationAddon', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should update the donation addon and call analyticsConfirmation.update', () => {
    const cartItems = [
      {
        type: trackingTypes.ADDON,
        price: {
          basePrice: '0.00',
        },
        quantity: 0,
      },
    ];

    const donationAmount = 50;

    trackDonationAddon(cartItems, donationAmount);

    expect(cartItems[0].price.basePrice).toEqual('50.00');
    expect(cartItems[0].quantity).toEqual(1);

    expect(analyticsConfirmation.update).toHaveBeenCalledWith({
      CartItems: cartItems,
    });
  });

  it('should not update cartItems if there is no ADDON type', () => {
    const cartItems = [
      {
        type: trackingTypes.DATE,
        price: {
          basePrice: '0.00',
        },
        quantity: 0,
      },
    ];

    const donationAmount = 50;

    trackDonationAddon(cartItems, donationAmount);

    expect(analyticsConfirmation.update).not.toHaveBeenCalled();
  });
});
