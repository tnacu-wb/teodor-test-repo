import { ROOM_TYPE } from '@whitbread-eos/api';

import {
  getAdultMealDescription,
  getChildrenMealDescription,
  getMealPrice,
  getRoomExtrasTotal,
  getRoomGroups,
  getRoomTotalprice,
} from './confirmation';

const mockReservationListWithStringComputedPrice = {
  additionalGuestInfo: {
    purposeOfStay: 'vacation',
  },
  roomStay: {
    adultsNumber: 1,
    arrivalDate: '2022-10-19',
    childrenNumber: 1,
    departureDate: '2022-10-28',
    ratePlanCode: 'FLEXRATE',
    roomType: 'ST',
    cot: false,
    roomPrice: 330.99,
    ratesPerNight: [
      {
        pricePerNight: 40.99,
        arrivalDate: '2022-10-19',
      },
    ],
    rateExtraInfo: {
      rateName: 'Flex',
      rateDescription: 'Our flex rates are the best',
      rateLongDescription: 'Our flex rates are the best long description',
    },
    roomExtraInfo: {
      roomType: ROOM_TYPE.STANDARD,
      roomName: 'Double Twin',
      roomDescription: 'Room description',
    },
    accessibleRoom: {
      isAccessible: false,
      phoneNumber: '+447292718293',
    },
    checkInTime: '10:00am',
    checkOutTime: '10:00pm',
  },
  reservationGuestList: [
    {
      givenName: 'Poppins',
      surName: 'Mary',
      nameTitle: 'Mr.',
    },
  ],
  reservationPackageList: [
    {
      computedPrice: 'text',
      description: 'Price for entire room',
      totalQuantity: 1,
      unitPrice: 200,
    },
  ],
  depositPolicies: [
    {
      policyCode: 'AB9',
      amountPaid: {
        amount: 250,
        currencyCode: 'GBP',
      },
      amountDue: {
        amount: 80,
        currencyCode: 'GBP',
      },
    },
  ],
  billing: {
    address: {
      addressLine1: 'Mary Street',
      addressLine2: '22 London',
      addressLine3: '',
      addressLine4: '',
      country: 'London',
      postalCode: '3RP 04M',
    },
    email: 'john.doe@gmail.com',
    firstName: 'John',
    lastName: 'Doe',
    telephone: '+44271927491',
    title: 'Mr.',
  },
  paymentCard: {
    cardNumberMasked: '1111-1111-1111-1111',
  },
  reservationOverrideReasons: {
    reasonCode: '103Override',
    reasonName: 'Override reason name',
    callerName: 'John',
    managerName: 'Lucy',
  },
  reservationOverridden: 'false',
  guaranteeCode: 'guarantee_code',
  reservationStatus: 'CONFIRMED',
};

const mockReservationListItem = {
  additionalGuestInfo: {
    purposeOfStay: 'vacation',
  },
  roomStay: {
    adultsNumber: 1,
    arrivalDate: '2022-10-19',
    childrenNumber: 1,
    departureDate: '2022-10-28',
    ratePlanCode: 'FLEXRATE',
    roomType: 'ST',
    cot: false,
    roomPrice: 330.99,
    ratesPerNight: [
      {
        pricePerNight: 40.99,
        arrivalDate: '2022-10-19',
      },
    ],
    rateExtraInfo: {
      rateName: 'Flex',
      rateDescription: 'Our flex rates are the best',
      rateLongDescription: 'Our flex rates are the best long description',
    },
    roomExtraInfo: {
      roomType: ROOM_TYPE.STANDARD,
      roomName: 'Double Twin',
      roomDescription: 'Room description',
    },
    accessibleRoom: {
      isAccessible: false,
      phoneNumber: '+447292718293',
    },
    checkInTime: '10:00am',
    checkOutTime: '10:00pm',
  },
  reservationGuestList: [
    {
      givenName: 'Poppins',
      surName: 'Mary',
      nameTitle: 'Mr.',
    },
  ],
  reservationPackageList: [
    {
      computedPrice: 330,
      description: 'Price for entire room',
      totalQuantity: 1,
      unitPrice: 200,
    },
    {
      description: 'Early Check In',
      unitPrice: 10,
      totalQuantity: 1,
      computedPrice: 10,
    },
    {
      description: 'Late Check Out 2pm',
      unitPrice: 10,
      totalQuantity: 1,
      computedPrice: 10,
    },
  ],
  depositPolicies: [
    {
      policyCode: 'AB9',
      amountPaid: {
        amount: 250,
        currencyCode: 'GBP',
      },
      amountDue: {
        amount: 80,
        currencyCode: 'GBP',
      },
    },
  ],
  billing: {
    address: {
      addressLine1: 'Mary Street',
      addressLine2: '22 London',
      addressLine3: '',
      addressLine4: '',
      country: 'London',
      postalCode: '3RP 04M',
    },
    email: 'john.doe@gmail.com',
    firstName: 'John',
    lastName: 'Doe',
    telephone: '+44271927491',
    title: 'Mr.',
  },
  paymentCard: {
    cardNumberMasked: '1111-1111-1111-1111',
  },
  reservationOverrideReasons: {
    reasonCode: '103Override',
    reasonName: 'Override reason name',
    callerName: 'John',
    managerName: 'Lucy',
  },
  reservationOverridden: 'false',
  guaranteeCode: 'guarantee_code',
  reservationStatus: 'CONFIRMED',
};

const mockTranslationFn = (t: string) => t;

const mockMealSelections = {
  adultsMeals: [
    {
      title: 'Breakfast',
      id: '1',
      price: 30,
      noSelections: 1,
    },
  ],
  childrenMeals: [
    {
      title: 'Breakfast',
      id: '1',
      price: 15,
      noSelections: 1,
    },
  ],
};

describe('confirmation getters', () => {
  describe('getRoomGroups Method', () => {
    it('should return adult,child string', () => {
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('1 account.dashboard.adult, 1 account.dashboard.child');
    });

    it('should return adults,child string', () => {
      mockReservationListItem.roomStay.adultsNumber = 2;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('2 account.dashboard.adults, 1 account.dashboard.child');
    });

    it('should return adults,children string', () => {
      mockReservationListItem.roomStay.adultsNumber = 2;
      mockReservationListItem.roomStay.childrenNumber = 2;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('2 account.dashboard.adults, 2 account.dashboard.children');
    });

    it('should return adult,children string', () => {
      mockReservationListItem.roomStay.adultsNumber = 1;
      mockReservationListItem.roomStay.childrenNumber = 2;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('1 account.dashboard.adult, 2 account.dashboard.children');
    });

    it('should return adult string', () => {
      mockReservationListItem.roomStay.childrenNumber = 0;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('1 account.dashboard.adult');
    });

    it('should return adults string', () => {
      mockReservationListItem.roomStay.adultsNumber = 2;
      mockReservationListItem.roomStay.childrenNumber = 0;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('2 account.dashboard.adults');
    });

    it('should return child string', () => {
      mockReservationListItem.roomStay.adultsNumber = 0;
      mockReservationListItem.roomStay.childrenNumber = 1;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('1 account.dashboard.child');
    });

    it('should return children string', () => {
      mockReservationListItem.roomStay.adultsNumber = 0;
      mockReservationListItem.roomStay.childrenNumber = 2;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('2 account.dashboard.children');
    });

    it('should return empty string', () => {
      mockReservationListItem.roomStay.adultsNumber = 0;
      mockReservationListItem.roomStay.childrenNumber = 0;
      const result = getRoomGroups(mockReservationListItem, mockTranslationFn);
      expect(result).toEqual('');
    });
  });

  describe('getRoomTotalprice Method', () => {
    it('should return room total price', () => {
      mockReservationListItem.roomStay.adultsNumber = 1;
      mockReservationListItem.roomStay.childrenNumber = 1;
      const result = getRoomTotalprice(mockReservationListItem);
      expect(result).toEqual(680.99);
    });
  });

  describe('getRoomExtrasTotal Method', () => {
    it('should return room total extras price', () => {
      const result = getRoomExtrasTotal(mockReservationListItem);
      expect(result).toEqual(350);
    });

    it('should return 0 if there are no reservation packages selected', () => {
      mockReservationListItem.reservationPackageList = [];
      const result = getRoomExtrasTotal(mockReservationListItem);
      expect(result).toEqual(0);
    });

    it('should return 0 if the total is not a number', () => {
      const result = getRoomExtrasTotal(mockReservationListWithStringComputedPrice);
      expect(result).toEqual(0);
    });

    it('should omit charity and city packages when calculating the extras total', () => {
      mockReservationListItem.reservationPackageList = [
        {
          computedPrice: 330,
          description: 'Price for entire room',
          totalQuantity: 1,
          unitPrice: 200,
        },
        {
          computedPrice: 150,
          description: 'Charity donation',
          totalQuantity: 1,
          unitPrice: 150,
        },
        {
          computedPrice: 50,
          description: 'City tax for sightseeing',
          totalQuantity: 1,
          unitPrice: 10,
        },
      ];
      const result = getRoomExtrasTotal(mockReservationListItem);
      expect(result).toEqual(330);
    });
  });

  describe('getMealPrice Method', () => {
    it('should return meal price total', () => {
      const result = getMealPrice(mockMealSelections);
      expect(result).toEqual(45);
    });

    it('should return meal price 0 if no menus were selected', () => {
      const result = getMealPrice();
      expect(result).toEqual(0);
    });
  });

  describe('getAdultMealDescription Method', () => {
    it('should return an array with "1 adult meal Breakfast"', () => {
      const result = getAdultMealDescription(mockMealSelections.adultsMeals, mockTranslationFn);
      expect(result).toEqual(['1 account.dashboard.adult Breakfast']);
    });

    it('should return an array with "2 adults meals Breakfast"', () => {
      mockMealSelections.adultsMeals[0].noSelections = 2;
      const result = getAdultMealDescription(mockMealSelections.adultsMeals, mockTranslationFn);
      expect(result).toEqual(['2 account.dashboard.adults Breakfast']);
    });
  });

  describe('getChildrenMealDescription Method', () => {
    it('should return an array with "1 child meal Breakfast"', () => {
      const result = getChildrenMealDescription(
        mockMealSelections.childrenMeals,
        mockTranslationFn
      );
      expect(result).toEqual(['1 account.dashboard.child Breakfast']);
    });

    it('should return an array with "2 children meals Breakfast"', () => {
      mockMealSelections.childrenMeals[0].noSelections = 2;
      const result = getChildrenMealDescription(
        mockMealSelections.childrenMeals,
        mockTranslationFn
      );
      expect(result).toEqual(['2 account.dashboard.children Breakfast']);
    });
  });
});
