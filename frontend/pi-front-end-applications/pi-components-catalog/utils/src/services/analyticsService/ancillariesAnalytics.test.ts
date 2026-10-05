import analytics from './analytics';
import updateAncillariesAnalytics from './ancillariesAnalytics';

const analyticsConfirmationUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

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

const mockBookingInformation = {
  bookingFlowId: 'booking-a1',
  hotelId: 'LONEUS',
  hotelName: 'London Euston',
  currencyCode: 'GBP',
  totalCost: 199.95,
  previousTotal: 0,
  newTotal: 199.95,
  channel: 'PI',
  companyId: 2455921,
  reservationByIdList: [
    {
      reservationId: '720981',
      reservationGuestList: [
        {
          givenName: 'Tilica',
          surName: 'Franaru',
          nameTitle: 'Prof',
          email: '',
        },
      ],
      billing: {
        address: {
          addressLine1: 'London Road North',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'LOWESTOFT',
          companyName: 'United Reformed Church',
          country: 'GB',
          postalCode: 'NR32 1HB',
        },
        email: 'cristiadrian@mailinator.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-04-01',
        departureDate: '2024-04-06',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'DOUBLE',
          roomName: 'Double room',
          groupId: 'double',
        },
        accessibleRoom: {
          phoneNumber: '0333 321 1262',
          isAccessible: true,
        },
        roomPrice: 75,
      },
    },
    {
      reservationId: '721541',
      reservationGuestList: [
        {
          givenName: 'Cristi',
          surName: 'Normal',
          nameTitle: 'Mr',
          email: 'cristiadrian@mailinator.com',
        },
      ],
      billing: {
        address: {
          addressLine1: 'London Road North',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'LOWESTOFT',
          companyName: 'United Reformed Church',
          country: 'GB',
          postalCode: 'NR32 1HB',
        },
        email: 'cristiadrian@mailinator.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-04-01',
        departureDate: '2024-04-06',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'LOWDBL',
          roomName: 'Accessible double bedroom with a lowered bath',
          groupId: 'accessible',
        },
        accessibleRoom: {
          phoneNumber: '0333 321 1262',
          isAccessible: true,
        },
        roomPrice: 75,
      },
    },
  ],
};

describe('ancillariesAnalytics', () => {
  describe('ancillariesAnalytics Method', () => {
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
    it('should run updateAncillariesAnalytics', () => {
      updateAncillariesAnalytics(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        mockBookingInformation,
        packagesData,
        'pi'
      );
      expect(analyticsConfirmationUpdateSpy).toHaveBeenCalledTimes(1);
    });
    it('should run updateAncillariesAnalytics when extrasItems objects are empty', () => {
      updateAncillariesAnalytics(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        mockBookingInformation,
        packagesData,
        'pi'
      );
      expect(analyticsConfirmationUpdateSpy).toHaveBeenCalledTimes(1);
    });
    it('should run updateAncillariesAnalytics for bb', () => {
      updateAncillariesAnalytics(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        mockBookingInformation,
        packagesData,
        'bb'
      );
      expect(analyticsConfirmationUpdateSpy).toHaveBeenCalledTimes(3);
    });
  });
});
