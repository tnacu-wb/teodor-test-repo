import { AcceptedRoomCodes, AcceptedRoomTypes, ExtrasId } from '@whitbread-eos/api';

//<editor-fold desc="mocks" defaultstate="collapsed">

export const mockControlPositions = {
  BOTTOM: 11,
  BOTTOM_CENTER: 11,
  BOTTOM_LEFT: 10,
  BOTTOM_RIGHT: 12,
  CENTER: 13,
  LEFT: 5,
  LEFT_BOTTOM: 6,
  LEFT_CENTER: 4,
  LEFT_TOP: 5,
  RIGHT: 7,
  RIGHT_BOTTOM: 9,
  RIGHT_CENTER: 8,
  RIGHT_TOP: 7,
  TOP: 2,
  TOP_CENTER: 2,
  TOP_LEFT: 1,
  TOP_RIGHT: 3,
};

export const mockRoomsAndGuestsLabels = {
  roomModalLabels: {
    roomDropdownLabels: {
      single: 'Single',
      double: 'Double',
      accessible: 'Accessible',
      twin: 'Twin',
      family: 'Family',
    },
    roomDropdownRoomCodes: {
      accessible: 'DIS',
      double: 'DB',
      family: 'FAM',
      single: 'SB',
      twin: 'TWIN',
    },
    roomAvailabilityLabels: {
      adult: 'adult',
      adults: 'adults',
      child: 'child',
      children: 'children',
      addRoom: 'Add a room',
      roomAvailable: 'Room Available',
      roomsUnavailable: 'No rooms available',
      roomsUnavailableDescription:
        "Sorry, we don't have any rooms available on the dates you selected",
      checkRoomAvailability: 'Check availability',
      leadGuest: 'Lead guest',
      cancelBtn: 'Cancel',
      guests: 'Guests',
      update: 'Update',
      roomSuccessfullyAdded: 'Room [number] has been successfully added',
      roomSuccessfullyUpdated: 'Room [number] has been successfully updated',
    },
    leadGuestLabels: {
      guestTitle: "'Mr','Mrs','Ms','Miss','Master','Dr','Lord','Lady','Sir','Col','Prof','Rev'",
      title: 'Title *',
      firstName: 'First name *',
      lastName: 'Last name *',
      email: 'Email address',
    },
    leadGuestValidationLabels: {
      titleError: 'Please select your title',
      firstNameRequiredError: 'Please enter your first name (max 20 characters)',
      firstNameMinError: 'Please enter your first name in full.',
      lastNameRequiredError: 'Please enter your last name (max 30 characters)',
      firstNameInvalidError: 'Invalid characters in first name',
      lastNameInvalidError: 'Invalid characters in last name',
      emailInvalidError: 'Please enter a valid email address',
    },
    notificationLabels: {
      title: 'Your meal selection has been reset',
      description: "Please select a meal in the 'Your meals' section for room [number]",
    },
  },
  removeRoomModalLabels: {
    title: 'Remove',
    confirmLabel: 'Are you sure that you want to remove this room?',
    notificationLabel: 'This room type and rates may not be available in the future',
    removeModalRoom: 'Remove',
    cancelModalRoom: 'Cancel',
    roomSuccessfullRemoved: 'Room [number] has been successfully removed',
  },
  roomLabel: 'Room',
  edit: 'Edit',
  remove: 'Remove',
};

export const mockBookingSummaryLabels = {
  title: 'Booking summary',
  confirmChangesLabel: 'Confirm changes',
  mealsLabel: 'Meals',
  extrasLabel: 'Extras',
  previousTotalLabel: 'Previous total',
  totalCostLabel: 'Total cost',
  continueToPaymentLabel: 'Continue to payment',
};

export const mockedPrivacyPolicyData = {
  privacyPolicy: {
    description:
      'We need to collect and keep some mandatory information in order to process your booking.',
    linkLabel: 'View our Privacy Notice",',
    linkSrc: '/gb/en/terms/privacy-policy.html',
    moreInfoLabel: 'Find out more',
    name: 'We keep your personal data safe and secure.',
    moreInfo: [
      {
        description: 'test',
        image: '/content/dam/global/booking/verisign.png',
      },
      {
        description: 'test',
        image: '/content/dam/hub/app/MasterCard.jpg',
      },
      {
        description: 'test',
        image: '/content/dam/global/booking/privacy_icon_visa_verified.png',
      },
    ],
  },
};

export const mockedSecurityNoticeMoreInfoDataSelectorResponse = {
  name: 'We keep your personal data safe and secure.',
  moreInfoLabel: 'Find out more',
  linkSrc: 'https://secure2.premierinn.com/gb/en/terms/privacy-policy.html',
  linkLabel: 'View our Privacy Notice',
  description: 'test',
  moreInfo: [
    {
      description: 'test',
      image: 'https://secure2.premierinn.com/content/dam/global/booking/verisign.png',
    },
    {
      description: 'test',
      image: 'https://secure2.premierinn.com/content/dam/hub/app/MasterCard.jpg',
    },
    {
      description: 'test',
      image:
        'https://secure2.premierinn.com/content/dam/global/booking/privacy_icon_visa_verified.png',
    },
  ],
};

export const mockedStayDatesRulesData = {
  maxArrivalDateLimitation: { maxArrivalDate: 364 },
  maxNightsLimitation: { maxNights: 9 },
  globalConfig: {
    maxRoomsLim: {
      maxRooms: 4,
      maxRoomsAmend: 3,
    },
  },
};

export const mockedEmployeeStayDatesRulesData = {
  globalConfig: {
    maxRoomsLim: {
      maxRooms: 2,
      maxRoomsAmend: 5,
    },
  },
};

const billingBaseParams = {
  billing: {
    address: {
      addressLine1: '50 Wharfdale Road',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      country: 'gb',
      postalCode: 'N1 9FA',
    },
    email: 'calincic@adsa.casd',
  },
};

const flexFamilyReservationGuestList = {
  reservationGuestList: [
    {
      givenName: 'Ella',
      surName: 'test',
      nameTitle: 'Ms',
      email: 'booker.asl@mailinator.com',
    },
  ],
  roomStay: {
    adultsNumber: 1,
    childrenNumber: 1,
    arrivalDate: '2023-10-26',
    departureDate: '2023-10-29',
    ratePlanCode: 'FLEXRATE',
    roomExtraInfo: {
      roomType: 'FMTRPL',
      roomName: 'Family Room',
      groupId: 'family',
    },
    accessibleRoom: {
      phoneNumber: '0333 321 1315',
      isAccessible: false,
    },
    roomPrice: 2997,
  },
};

const employeeFamilyReservationGuestList = {
  ...flexFamilyReservationGuestList,
  roomStay: { ...flexFamilyReservationGuestList.roomStay, ratePlanCode: 'EMPLOYEE' },
};

export const mockedBookingConfirmationAmendData = {
  bookingConfirmation: {
    bookingFlowId: 'booking-a1',
    hotelId: 'MANOLD',
    hotelName: 'Manchester Old Trafford',
    infoMessages: ['<p>Free cancellation up to 1pm on the day of arrival</p>\n'],
    currencyCode: 'GBP',
    totalCost: 6101.88,
    previousTotal: 0,
    newTotal: 6101.88,
    brand: 'PI',
    reservationByIdList: [
      {
        ...billingBaseParams,
        reservationId: '2292308',
        reservationGuestList: [
          {
            givenName: 'Test Orange',
            surName: 'Guest Orange',
            nameTitle: 'Lady',
            email: 'tekn@mgm.comm',
          },
        ],
        roomStay: {
          adultsNumber: 2,
          childrenNumber: 1,
          arrivalDate: '2023-10-26',
          departureDate: '2023-10-29',
          ratePlanCode: 'FLEXRATE',
          roomExtraInfo: {
            roomType: 'FMTRPL',
            roomName: 'Family Room',
            groupId: 'family',
          },
          accessibleRoom: {
            phoneNumber: '0333 321 1315',
            isAccessible: false,
          },
          roomPrice: 2997,
        },
      },
      {
        ...billingBaseParams,
        reservationId: '2292104',
        ...flexFamilyReservationGuestList,
      },
      {
        ...billingBaseParams,
        reservationId: '2292103',
        ...flexFamilyReservationGuestList,
      },
      {
        ...billingBaseParams,
        reservationId: '2292100',
        ...flexFamilyReservationGuestList,
      },
    ],
  },
};

export const mockedBookingConfirmationAmendDataEmployee = {
  bookingConfirmation: {
    bookingFlowId: 'booking-a1',
    hotelId: 'MANOLD',
    hotelName: 'Manchester Old Trafford',
    infoMessages: ['<p>Free cancellation up to 1pm on the day of arrival</p>\n'],
    currencyCode: 'GBP',
    totalCost: 6101.88,
    previousTotal: 0,
    newTotal: 6101.88,
    brand: 'PI',
    reservationByIdList: [
      {
        ...billingBaseParams,
        reservationId: '2292103',
        ...employeeFamilyReservationGuestList,
      },
      {
        ...billingBaseParams,
        reservationId: '2292100',
        ...employeeFamilyReservationGuestList,
      },
    ],
  },
};

const acceptedRoomTypes: Record<AcceptedRoomTypes, AcceptedRoomCodes> = {
  family: 'FAM',
  single: 'SB',
  double: 'DB',
  twin: 'TWIN',
  accessible: 'DIS',
};

export const mockedRoomOccupancyLimitationsData = {
  roomOccupancyLimitations: {
    roomOccupancies: [
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 2,
        childrenNumber: 2,
      },
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 2,
        childrenNumber: 1,
      },
      {
        acceptedRoomTypes: [
          acceptedRoomTypes.double,
          acceptedRoomTypes.twin,
          acceptedRoomTypes.accessible,
        ],
        adultsNumber: 2,
        childrenNumber: 0,
      },
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 1,
        childrenNumber: 2,
      },
      {
        acceptedRoomTypes: [acceptedRoomTypes.family],
        adultsNumber: 1,
        childrenNumber: 1,
      },
      {
        acceptedRoomTypes: [
          acceptedRoomTypes.single,
          acceptedRoomTypes.double,
          acceptedRoomTypes.accessible,
        ],
        adultsNumber: 1,
        childrenNumber: 0,
      },
    ],
  },
};

export const mockedMealPackagesData = {
  packages: {
    packages: {
      meals: [
        {
          allergyInfoLabel: 'Allergy & nutrition info',
          allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
          bartId: '11',
          currency: 'GBP',
          description:
            '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
          freeBreakfastCode: 'BFCHDF',
          freeBreakfastMaxPerMeal: 2,
          freeBreakfastOption: true,
          id: 'BFADBF',
          imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
          name: 'Premier Inn Breakfast',
          menu: {
            imageSrc: null,
            description: null,
            disclaimer: null,
            menuLabel: null,
            menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
            name: 'Breakfast menu',
          },
          order: 1,
          price: 9.99,
          basePrice: 9.99,
          isFree: false,
        },
        {
          allergyInfoLabel: 'Allergy & nutrition info',
          allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
          bartId: '12',
          currency: 'GBP',
          description:
            '<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n',
          freeBreakfastCode: '',
          freeBreakfastMaxPerMeal: 2,
          freeBreakfastOption: false,
          id: 'BFADCT',
          imageSrc: '/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
          name: 'Continental Breakfast',
          menu: {
            imageSrc: null,
            description: null,
            disclaimer: null,
            menuLabel: null,
            menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
            name: 'Breakfast menu',
          },
          order: 2,
          price: 7.99,

          // NEW
          basePrice: 7.99,
          isFree: false,
        },
        {
          allergyInfoLabel: 'Allergy & nutrition info',
          allergyInfoSrc: '/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
          bartId: '17',
          currency: 'GBP',
          description: '<p>Save up to 20% off your bill with our Meal Deal offer!</p>\r\n',
          freeBreakfastCode: 'BFCHDF',
          freeBreakfastMaxPerMeal: 2,
          freeBreakfastOption: true,
          id: 'MDP',
          imageSrc: '/content/dam/global/restaurants/Global/meal-deal-booking.png',
          name: 'Meal Deal',
          menu: {
            imageSrc: null,
            description: null,
            disclaimer: null,
            menuLabel: null,
            menuSrc: '/content/dam/global/restaurants/Global/pi-dinner-menu-band2.pdf',
            name: 'Dinner menu',
          },
          order: 3,
          price: 24.99,

          // NEW
          basePrice: 24.99,
          isFree: false,
        },
      ],

      mealsKids: [
        {
          allergyInfoLabel: null,
          allergyInfoSrc: null,
          currency: null,
          description: '<p>Up to two kids eat breakfast for free.</p>\r\n',
          id: 'BFCHDF',
          imageSrc: '/content/dam/global/restaurants/Global/child-breakfast.jpg',
          menu: null,
          name: 'Free breakfast for kids',
          order: 0,
          price: null,
          totalPrice: null,
        },
      ],

      extrasItems: [
        {
          currency: 'GBP',
          description: 'Check out any time until 2pm (normal check-out time is 12pm).',
          id: 'HSCKIN',
          imageSrc: '/content/dam/global/extras/early-check-in.png',
          name: 'Early check-in',
          order: 1,
          price: 10,
          available: 10,

          // NEW
          basePrice: 10,
          isFree: false,
        },
        {
          currency: 'GBP',
          description: 'Check out any time until 2pm (normal check-out time is 12pm).',
          id: 'HSCOU2',
          imageSrc: '/content/dam/global/extras/late-checkout.png',
          name: 'Late check-out',
          order: 2,
          price: 10,
          available: 10,

          // NEW
          basePrice: 10,
          isFree: false,
        },
        {
          currency: 'GBP',
          description: 'Enjoy an ice-cold bottle of Prosecco during your stay.',
          id: 'DBPROS',
          imageSrc: '/content/dam/global/extras/prosecco.png',
          name: 'Bottle of prosecco',
          order: 4,
          price: 20,
          available: null,

          // NEW
          basePrice: 20,
          isFree: false,
        },
      ],

      roomSelection: [
        {
          reservationId: '2292308',
          packagesSelection: [
            { id: 'BFCHDF', noOfSelections: 1 },
            { id: 'BFADCT', noOfSelections: 1 },
            { id: 'BFADBF', noOfSelections: 1 },
            { id: ExtrasId.ULTIMATE_WIFI, noOfSelections: 1 },
            { id: ExtrasId.BOTTLE_OF_PROSECCO, noOfSelections: 1 },
          ],
        },
        {
          reservationId: '2292104',
          packagesSelection: [
            { id: 'BFADCT', noOfSelections: 1 },
            { id: 'BFCHDF', noOfSelections: 1 },
            { id: 'BFADBF', noOfSelections: 1 },
          ],
        },
      ],
    },

    privacyPolicy: {
      description: 'description',
      linkLabel: 'View our Privacy Notice',
      linkSrc: '/gb/en/terms/privacy-policy.html',
      moreInfo: [
        {
          description: 'description',
          image: '/content/dam/global/booking/verisign.png',
        },
      ],
      moreInfoLabel: 'Find out more',
      name: 'We keep your personal data safe and secure.',
    },

    restaurant: {
      logoSrc: '/content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg',
      menus: [],
      messageDescription: null,
      messageHeader: null,
      noMealsFound: false,
      restaurantNotFound: false,
    },

    hotelHasCityTaxForBusiness: false,
    hotelHasCityTaxForLeisure: false,
  },
};

export const mockedStayDatesLabels = {
  arrivalDate: 'Arrival Date',
  nightsLabel: 'Nights',
  nightOption: 'night',
  nightsOption: 'nights',
  checkOut: 'Check out:',
  hotel: 'Hotel',
  yourStayDatesTitle: 'Your stay dates',
  numberOfNightsErrorMessage: 'Number of nights cannot be more than 364',
  invalidNights: 'Please enter a valid number of nights',
};

export const mockedRestaurantData = {
  logoSrc: '/content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg',
  menus: [
    {
      description: null,
      disclaimer: null,
      imageSrc: null,
      menuLabel: null,
      menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
      name: 'Breakfast',
    },
  ],
  messageDescription: null as string | null,
  messageHeader: null as string | null,
  noMealsFound: false,
  restaurantNotFound: false,
};

export const mockedConfirmAmendResponse = {
  data: {
    confirmAmend: {
      basketReference: 'AKU2338900',
      reservations: [
        {
          createDateTime: '2023-04-25T08:48:24.891438173Z',
          roomStay: {
            adultsNumber: 1,
            arrivalDate: '2023-07-28',
            childrenNumber: 1,
            departureDate: '2023-08-01',
            ratePlanCode: 'FLEXRATE',
            pmsRoomType: 'FMTRPL',
          },
        },
        {
          createDateTime: '2023-04-25T08:48:24.891482705Z',
          roomStay: {
            adultsNumber: 1,
            arrivalDate: '2023-07-28',
            childrenNumber: 0,
            departureDate: '2023-08-01',
            ratePlanCode: 'FLEXRATE',
            pmsRoomType: 'FMTRPL',
          },
        },
      ],
      hotelId: 'LONEUS',
      totalCost: 360.0,
      currencyCode: 'GBP',
    },
  },
};

export const mockedConfirmAmendResponseEmployee = {
  data: {
    confirmAmend: {
      basketReference: 'AKU2338900',
      reservations: [
        {
          createDateTime: '2023-04-25T08:48:24.891438173Z',
          roomStay: {
            adultsNumber: 1,
            arrivalDate: '2023-07-28',
            childrenNumber: 1,
            departureDate: '2023-08-01',
            ratePlanCode: 'EMPLOYEE',
            pmsRoomType: 'FMTRPL',
          },
        },
        {
          createDateTime: '2023-04-25T08:48:24.891482705Z',
          roomStay: {
            adultsNumber: 1,
            arrivalDate: '2023-07-28',
            childrenNumber: 0,
            departureDate: '2023-08-01',
            ratePlanCode: 'EMPLOYEE',
            pmsRoomType: 'FMTRPL',
          },
        },
      ],
      hotelId: 'LONEUS',
      totalCost: 360.0,
      currencyCode: 'GBP',
    },
  },
};

export const mockedTranslations = {
  'amend.roomSuccessfullyAdded': 'Room successfully added',
  'amend.roomSuccessfullyUpdated': 'Room successfully updated',
  'amend.roomSuccessfullRemoved': 'Room successfully removed',
  'amend.stayDate': 'Stay dates',
  'amend.roomAndGuests': 'Room and guests',
  'amend.yourMeals': 'Your meals',
  'headerInformationData.headerInformation.results.notifications.groupBookingMessage':
    'If you’d like to book more rooms, please call us and we’ll be happy to help.',
  'headerInformationData.headerInformation.results.notifications.emp01groupBookingMessage':
    'You can book a maximum of 2 rooms using the employee rate',
};

export const mockedSummaryOfPaymentsData = {
  charitable: 5,
  previousTotal: 1778,
  balancePaid: 0,
  payOnArrival: 1788.5,
  refund: -5,
  nonRefundable: 5,
  totalCost: 1788.5,
  balanceAuthorised: 0,
  paymentOptions: {
    payNow: true,
    payOnArrival: true,
  },
  paymentCardDetails: {
    cardNumberMasked: 'XXXXXXXXXXXX1100',
    token: '5667855671183870034',
    expirationDate: '2023-12-31',
    cardType: 'AT',
    cardHolderName: 'Monica W',
    cardNumberLast4Digits: '1100',
    cardName: 'Mastercard Credit',
    cardLogoSrc: '',
  },
};

export const mockedSummaryOfPaymentsLabels = {
  balancePaid: 'Balance paid',
  payOnArrival: 'Pay on arrival',
  totalCost: 'Total cost',
  refund: 'Refund',
  refundTerms: 'Your card will be refunded within 3 working days',
  nonRefundable: 'Non-refundable',
  balanceAuthorised: 'Balance authorised',
  donation: 'On-line charitable pledge',
  additionalAmount: 'Additional amount to PN',
};

export const mockedSelectedPaymentType = {
  name: 'PIBA',
  type: 'SAVED_CARD',
  order: 1,
  card: {
    token: '5667855671183870034',
    expiryMonth: '12',
    expiryYear: '23',
    type: 'AT',
    logoSrc: '',
    cardHolderName: 'Monica W',
    cardType: 'LEISURE_STORED_CARD',
    cnpRequired: false,
    cardNumber: 'XXXXXXXXXXXX1100',
  },
  paymentOptions: [
    {
      type: 'PAY_NOW',
      order: 1,
      enabled: true,
    },
    {
      type: 'PAY_ON_ARRIVAL',
      order: 2,
      enabled: true,
    },
  ],
  enabled: true,
  cnpPreSelected: false,
  cnpOptionAvailable: false,
  reasons: [],
};

export const mockedRestaurantDataWithClosure = {
  logoSrc: '/content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg',
  menus: [
    {
      description: 'null',
      disclaimer: null,
      imageSrc: null,
      menuLabel: null,
      menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
      name: 'Breakfast',
    },
  ],
  messageDescription: 'Description-Closed',
  messageTitle: 'Title-Closed',
  messageHeader: null,
  noMealsFound: false,
  restaurantNotFound: false,
};

export const mockRequestGetSearchRules = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  error: { message: '' },
  data: {
    maxNightsLimitation: {
      maxNights: 9,
    },
    globalConfig: {
      maxRoomsLim: {
        maxRooms: 4,
      },
    },
    maxArrivalDateLimitation: {
      maxArrivalDate: 364,
    },
    roomOccupancyLimitations: {
      roomOccupancies: [
        {
          adultsNumber: 2,
          childrenNumber: 2,
          acceptedRoomTypes: ['FAM'],
        },
        {
          adultsNumber: 2,
          childrenNumber: 1,
          acceptedRoomTypes: ['FAM'],
        },
        {
          adultsNumber: 2,
          childrenNumber: 0,
          acceptedRoomTypes: ['DB', 'TWIN', 'DIS'],
        },
        {
          adultsNumber: 1,
          childrenNumber: 2,
          acceptedRoomTypes: ['FAM'],
        },
        {
          adultsNumber: 1,
          childrenNumber: 1,
          acceptedRoomTypes: ['FAM'],
        },
        {
          adultsNumber: 1,
          childrenNumber: 0,
          acceptedRoomTypes: ['SB', 'DB', 'DIS'],
        },
      ],
    },
  },
};

export const mockResponseSuggestions = {
  properties: [
    {
      code: 'BECWIN',
      brand: 'PI',
      suggestion: 'London Beckton',
      geometry: {
        type: 'Point',
        coordinates: [0.060897, 51.516141],
      },
    },
    {
      code: 'LONBRI',
      brand: 'PI',
      suggestion: 'London Brixton',
      geometry: {
        type: 'Point',
        coordinates: [-0.11462, 51.461785],
      },
    },
    {
      code: 'LONEAL',
      brand: 'PI',
      suggestion: 'London Ealing',
      geometry: {
        type: 'Point',
        coordinates: [-0.309854, 51.512673],
      },
    },
    {
      code: 'LONEUS',
      brand: 'PI',
      suggestion: 'London Euston',
      geometry: {
        type: 'Point',
        coordinates: [-0.129068, 51.527736],
      },
    },
    {
      code: 'LONSUT',
      brand: 'PI',
      suggestion: 'London Sutton',
      geometry: {
        type: 'Point',
        coordinates: [-0.194185, 51.363412],
      },
    },
  ],
  managedPlaces: [],
  places: [
    {
      suggestion: 'London, UK',
      placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    },
    {
      suggestion: 'London Bridge, London, UK',
      placeId: 'ChIJxRO7WVEDdkgRrGM1fCYoHqY',
    },
    {
      suggestion: 'London Eye, London, UK',
      placeId: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
    },
    {
      suggestion: 'London Bridge Station, London, UK',
      placeId: 'ChIJ___OyFADdkgRkjYaWF6n5h0',
    },
    {
      suggestion: 'London Euston station, Euston Square, London, UK',
      placeId: 'ChIJX3IGxcwbdkgRjh45tuTmuTs',
    },
  ],
};

export const mockSearchErrorKeysData = {
  invalidLocation: 'invalidLocation',
  invalidDate: 'invalidDate',
  arrivalDateInThePast: 'arrivalDateInThePast',
  arrivalDateInTheFuture: 'arrivalDateInTheFuture',
  invalidNights: 'invalidNights',
  invalidOccupancy: 'invalidOccupancy',
};

export const mockSearchErrorFieldsData = {
  invalidLocation: 'location',
  invalidDate: 'datepicker',
  arrivalDateInThePast: 'datepicker',
  arrivalDateInTheFuture: 'datepicker',
  invalidNights: 'numberOfNights',
  invalidOccupancy: 'occupancy',
};

export const mockMapSearchParamsData = {
  searchTerm: 'London, UK',
  ARRdd: 1,
  ARRmm: 11,
  ARRyyyy: 2022,
  nights: 1,
  roomsNumber: 1,
  rooms: [
    {
      id: '_Wk-hmtNWMkBUcsnjmi-O',
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: 'Double',
    },
  ],
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  ADULT1: 1,
  CHILD1: 0,
  COT1: 0,
};

export const mockSearchContainerData = {
  searchLocation: undefined,
  defaultLocation: 'London Eye, London, UK',
  defaultRooms: [
    {
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: 'DB',
    },
  ],
  ARRdd: 5,
  ARRmm: 11,
  ROOMS: 1,
  ARRyyyy: 2022,
  NIGHTS: 1,
  showNoHotelsWarning: true,
  PROMOID: 'ST10R',
};

export const mockedGetStaticContent = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  error: { message: '' },
  data: {
    headerInformation: {
      announcement: {
        browserCompatibilityMessage:
          '<p>Looks like your web browser isn’t supported. Try downloading <a href="https://www.google.com/chrome/">Google Chrome</a>, <a href="https://www.microsoft.com/en-us/edge">Microsoft Edge</a> or <a href="https://www.mozilla.org/en-CA/firefox/new/">Firefox</a> for a better online experience with us.</p>',
        text: '<p>Test</p>',
        type: null,
      },
      config: {
        api: {
          bookingChannel: {
            business: 'CBT',
            leisure: 'WEB',
          },
        },
        authentication: {
          accountLinks: [
            {
              icon: 'bookings',
              title: 'Bookings',
              url: 'https://www.qapink.premierinn.digital/gb/en/account/dashboard.html',
            },
            {
              icon: 'settings',
              title: 'Settings',
              url: 'https://www.qapink.premierinn.digital/gb/en/account/profile.html',
            },
          ],
          business: {
            businessAccountLinks: null,
          },
        },
        bookingSearch: {
          show: true,
          dashboardRedirect: {
            bookingReference: 'bookingReference',
            cookie: {
              domain: '.premierinn.digital',
              minutesTillExpiry: '30',
              name: 'pi.single-booking',
            },
            url: '/gb/en/account/dashboard.html',
          },
        },
        roomCodes: {
          accessible: 'DIS',
          double: 'DB',
          family: 'FAM',
          single: 'SB',
          twin: 'TWIN',
        },
      },
      content: {
        authentication: {
          forgottenPassword: {
            business: {
              emailPlaceholder: 'Email address',
              formLabel: 'We will send you an email with instructions to reset your password',
              formTitle: 'Reset your password',
              submitButton: 'Submit',
            },
            cancel: 'Cancel',
            leisure: {
              emailPlaceholder: 'Email address',
              formLabel: 'We will send you an email with instructions to reset your password',
              formTitle: 'Reset your password',
              submitButton: 'Submit',
            },
          },
          login: {
            business: {
              formLabel: 'Log into Business Booker',
              emailPlaceholder: 'Business email address',
              loginButton: 'Log into Business Booker',
            },
            forgotPassword: 'Forgotten password?',
            leisure: {
              emailPlaceholder: 'Email address',
              formLabel: 'Log into your Premier Inn account',
              loginButton: 'Log in',
            },
            passwordPlaceholder: 'Password',
            signupLink: 'Sign up here',
            signupMessage: "Don't have an account yet?",
          },
        },
        countries: [
          {
            flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
            language: 'English',
          },
          {
            flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
            language: 'German',
          },
        ],
        global: {
          accessible: 'Accessible',
          addRoom: 'Add another room',
          adult: 'adult',
          adultsLabel: 'Adults',
          adults: 'adults',
          brand: {
            hub: 'Hub by Premier Inn',
            hubBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg',
            hubLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-hub.svg',
            piLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg',
            pid: 'Premier Inn',
            pi: 'Premier Inn Rest Easy',
            pidLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg',
            zip: 'ZIP by Premier Inn',
            zipBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg',
            zipLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-zip.svg',
          },
          child: 'child',
          children: 'children',
          childrenLabel: 'Children',
          done: 'Done',
          double: 'Double',
          family: 'Family',
          night: 'night',
          room: 'room',
          roomLabel: 'Room',
          rooms: 'rooms',
          tomorrow: 'Tomorrow',
          today: 'Today',
          single: 'Single',
          twin: 'Twin',
          offers: [
            {
              cellCode: 'EMP01',
              maxRooms: 2,
              numberOfNights: 9,
              page: 'employee-offer',
            },
            {
              cellCode: '',
              maxRooms: 2,
              numberOfNights: 9,
              page: 'travel-industry-rate',
              ratePlanCode: 'FCDNLR30',
              corpId: '15010601',
              PROMOID: 'ST10R',
            },
          ],
        },
        header: {
          image: '/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg',
        },
        menu: {
          agentMemo: 'Agent memo',
          bookHotel: 'Search for a hotel',
          business: 'Business',
          changeLogs: 'Change logs',
          discoverPI: 'Discover Premier Inn',
          findBooking: 'Manage booking',
          guestAccount: 'Guest account',
          language: 'Language',
          languageButton: 'English',
          logIn: 'Log in',
          mobileMenuButton: 'Menu',
          tick: '/etc/clientlibs/pi-header/resources/images/tick.svg',
        },
        subNav: [
          {
            navOptions: [
              {
                title: 'Business home',
                url: '/gb/en/business.html',
              },
              {
                title: 'Business Booker ',
                url: '/gb/en/business/business-booker.html',
              },
              {
                title: 'Business Account',
                url: '/gb/en/business/business-account.html',
              },
            ],
            title: 'Business',
          },
          {
            navOptions: [
              {
                title: 'Short breaks ',
                url: '/gb/en/short-breaks.html',
              },
              {
                title: 'Hidden treasures',
                url: '/gb/en/short-breaks/hidden-treasures.html',
              },
              {
                title: 'Coastal breaks ',
                url: '/gb/en/short-breaks/coastal-breaks.html',
              },
            ],
            title: 'Short breaks ',
          },
          {
            navOptions: [
              {
                title: 'Book a hotel ',
                url: '/gb/en/book-a-hotel.html',
              },
              {
                title: 'Hotel directory ',
                url: '/gb/en/hotels.html',
              },
              {
                title: 'Local guides ',
                url: '/gb/en/short-breaks/city-breaks.html',
              },
            ],
            title: 'Locations ',
          },
          {
            navOptions: [
              {
                title: "Why we're Premier ",
                url: '/gb/en/why.html',
              },
              {
                title: 'Seriously tasty food ',
                url: '/gb/en/why/food.html',
              },
              {
                title: 'Totally kid-friendly ',
                url: '/gb/en/why/family.html',
              },
            ],
            title: "Why we're Premier",
          },
        ],
      },
      datePicker: {
        checkOut: 'Check out',
        invalidDate: 'Invalid Date',
        reset: 'Reset',
      },
      form: {
        adultsHelperText: 'Max 2 per room',
        arrivalDateLabel: 'Arrival date *',
        bookingInvalid:
          'Some of the details are missing or invalid. Please provide correct details. Stays at our Dresden City Centre hotel will not show in manage booking. To amend or cancel your booking, please call us on +49 35146566047.',
        bookingReferenceLabel: 'Booking reference *',
        bookingSurnameLabel: 'Booking surname *',
        checkout: 'Check out:',
        childrenHelperText: '2-15 years',
        cotLimit: '0-2 years',
        findBookingDescription:
          "Please enter your details below. You'll find your booking reference in your confirmation email.",
        findBookingTitle: 'Amend or cancel a booking',
        includeCot: 'Include a cot?',
        invalidFutureDate:
          'Unable to book over a year in advance, rates below are for availability today',
        invalidDate: 'Please enter a valid date',
        invalidLocation: 'Please enter a location or a hotel',
        invalidNights: 'Please enter a valid number of nights',
        invalidPastDate:
          'Your selected date is in the past, rates below are for availability today',
        invalidReference: 'Invalid reference',
        invalidRooms: 'Please enter a valid room composition',
        invalidSurname:
          'Must be at least 2 characters long – any letters and special characters (except :;~)',
        removeRoom: 'Remove room',
        roomType: 'Room type',
        searchBookingError:
          'We are unable to retrieve your booking at the moment. Please try again later.',
        snowdropError: 'Oh dear, something went wrong when we tried to load the suggestions.',
        snowdropErrorRetry: 'Retry',
        where: 'Enter place, postcode or hotel',
      },
      results: {
        notifications: {
          availabilitiesErrorMessage: 'Something went wrong when we tried to load the results',
          ccuiGroupBookingMessage:
            'If caller wishes to add more than 9 rooms then please ask them to contact the Groups Team at group.enquiries@whitbread.com',
          errorTitle: 'Oh dear..',
          groupBookingHeader: 'Unable to add more rooms',
          groupBookingMessage:
            'If you’d like to book more rooms, please call us and we’ll be happy to help.',
          emp01groupBookingMessage: 'You can book a maximum of 2 rooms using the employee rate',
          noResults:
            'Sorry, we couldn’t find any hotels with available rooms. Try changing your search area.',
          groupBookingFormPageMessage:
            'For a group booking of 5 to 9 rooms, call us on 0333 003 8101. To book 10 rooms or more, please complete the <a href=“/why/groups/form” class="wb-a">group booking form</a> and we will be in contact to discuss your enquiry.',
        },
      },
    },
    footer: {
      copyrightInfo: '&copy; 2023 Premier Inn',
      newsletterSignup: {
        introViewText:
          'Simply fill in your details below to be the first to hear about all our latest news and getaway innspiration!',
        introViewTitle: 'Signing up is easy',
        signUpButtonText: 'Sign up',
      },
      socialMediaIcons: [
        {
          iconSrc: '/content/dam/icons/resources/social/facebook-dark-square-small.svg',
          label: 'Facebook icon',
          linkSrc: 'https://www.facebook.com/premierinn',
        },
        {
          iconSrc: '/content/dam/icons/resources/social/twitter-dark-square-small.svg',
          label: 'Twitter icon',
          linkSrc: 'https://twitter.com/premierinn',
        },
        {
          iconSrc: '/content/dam/icons/resources/social/instagram-dark-square-small.svg',
          label: 'Instagram icon',
          linkSrc: 'https://www.instagram.com/premierinn',
        },
      ],
      tabs: [
        {
          columns: [
            {
              linkItems: [
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/contact-us.html',
                  name: 'Contact us',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/faq.html',
                  name: 'FAQs',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why/groups.html',
                  name: 'Group bookings',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/premier-inn-affiliate-programme.html',
                  name: 'Affiliates',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/business/international-development.html',
                  name: 'International development ',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.whitbreadcareers.com/our-brands/premier-inn/',
                  name: 'Careers',
                  openInNewTab: true,
                },
              ],
              name: 'Get in touch',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/sleep/good-night-guarantee.html',
                  name: 'Good Night Guarantee',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/terms/booking-terms-and-conditions.html',
                  name: 'Terms and conditions',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/terms/terms-of-use.html',
                  name: 'Terms of use ',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/terms/privacy-policy.html',
                  name: 'Privacy policy',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/terms/how-we-use-cookies.html',
                  name: 'Cookies notice',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/sitemap.html',
                  name: 'Sitemap',
                  openInNewTab: false,
                },
              ],
              name: 'Legal',
            },
            {
              linkItems: [
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/hotels.html',
                  name: 'Hotel directory',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/why/locations/new-hotels.html',
                  name: 'New hotels ',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/hotels/best-hotels.html',
                  name: 'Best hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/short-breaks/city-breaks.html',
                  name: 'Local guides',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/short-breaks.html',
                  name: 'Short breaks',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/hotels/middle-east.html',
                  name: 'Dubai and beyond',
                  openInNewTab: true,
                },
              ],
              name: 'Locations',
            },
            {
              linkItems: [
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/sleep/our-rooms.html',
                  name: 'Our rooms',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why/family.html',
                  name: 'Family friendly ',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/sleep.html',
                  name: 'Sleep',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why/food.html',
                  name: 'Food & drink',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/hub.html',
                  name: 'hub by Premier Inn',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/zip.html',
                  name: 'ZIP by Premier Inn',
                  openInNewTab: false,
                },
              ],
              name: 'Our hotels',
            },
            {
              linkItems: [
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why/rates.html',
                  name: 'Our rates',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/offers.html',
                  name: 'Offers',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/sleep/buy-our-bed.html',
                  name: 'Buy our bed',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why/apps.html',
                  name: 'Mobile apps ',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/terms/diversity-and-inclusion.html',
                  name: 'We value difference',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://cdn.whitbread.co.uk/media/2021/02/03141057/whitbread-modern-slavery-statement-2019-20-1.pdf',
                  name: 'Modern Slavery Act statement',
                  openInNewTab: true,
                },
              ],
              name: 'Everything else',
            },
            {
              linkItems: [
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why.html',
                  name: 'About us',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/resteasy.html',
                  name: 'Rest easy',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why/cleanliness.html',
                  name: 'Premier Inn CleanProtect™',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/why/force-for-good.html',
                  name: 'Force for Good',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/terms/disabled-access.html',
                  name: 'Disabled access',
                  openInNewTab: false,
                },
                {
                  linkSrc: 'https://www.qapink.premierinn.digital/gb/en/news.html',
                  name: 'News',
                  openInNewTab: false,
                },
              ],
              name: 'Find out more',
            },
          ],
          intro: {
            description:
              '<p>Is it our Hypnos beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice AND flexibility.</p>\r\n',
            name: '',
          },
          name: 'About us',
        },
        {
          columns: [
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/kensington.html',
                  name: 'London - Kensington hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/covent-garden.html',
                  name: 'London - Covent Garden hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/southbank-london.html',
                  name: 'London - South Bank hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/kings-cross-euston-london.html',
                  name: 'London - Kings Cross hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/westminster.html',
                  name: 'London - Westminster hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london.html',
                  name: 'Hotels in Central London',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/republic-of-ireland/dublin.html',
                  name: 'Dublin hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/lothian/edinburgh.html',
                  name: 'Edinburgh hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-manchester/manchester.html',
                  name: 'Manchester hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/merseyside/liverpool.html',
                  name: 'Liverpool hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/strathclyde/glasgow.html',
                  name: 'Glasgow hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/tyne-and-wear/newcastle.html',
                  name: 'Newcastle hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/west-yorkshire/leeds.html',
                  name: 'Leeds hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/south-yorkshire/sheffield.html',
                  name: 'Sheffield hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/north-yorkshire/york.html',
                  name: 'York hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/northern-ireland/antrim/belfast.html',
                  name: 'Belfast hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/county-durham/durham.html',
                  name: 'Durham hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/north-yorkshire/harrogate.html',
                  name: 'Harrogate hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/west-midlands/birmingham.html',
                  name: 'Birmingham hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/bristol.html',
                  name: 'Bristol hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/wales/glamorgan/cardiff.html',
                  name: 'Cardiff hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/nottinghamshire/nottingham.html',
                  name: 'Nottingham hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/cambridgeshire/cambridge.html',
                  name: 'Cambridge hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/oxfordshire/oxford.html',
                  name: 'Oxford hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/grampian/aberdeen.html',
                  name: 'Aberdeen hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/somerset/bath.html',
                  name: 'Bath hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/cheshire/chester.html',
                  name: 'Chester hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/hampshire/southampton.html',
                  name: 'Southampton hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/hampshire/portsmouth.html',
                  name: 'Portsmouth hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/highland/inverness.html',
                  name: 'Inverness hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
          ],
          intro: {
            description:
              '<p>There are so many exciting things to do in the UK, so whether it’s last-minute weekend breaks or fun filled family holidays, we’ve got it all. No matter where your next adventure takes you, you can rest easy knowing you’ll get the same great-value rooms and friendly service at any of our 800+ hotels across the UK.</p>\r\n',
            name: '',
          },
          name: 'City breaks',
        },
        {
          columns: [
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/kensington.html',
                  name: 'Hotels in Kensington',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/cambridgeshire/cambridge.html',
                  name: 'Hotels in Cambridge',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/somerset/bath.html',
                  name: 'Hotels in Bath',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/hertfordshire/tring.html',
                  name: 'Hotels in Chiltern Hills',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/soho.html',
                  name: 'Hotels near Soho',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/lothian/edinburgh.html',
                  name: 'Hotels in Edinburgh',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/cumbria/lake-district.html',
                  name: 'Hotels in the Lake District',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/southbank-london.html',
                  name: 'Hotels on the South Bank',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/north-yorkshire/york.html',
                  name: 'Hotels in York',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/lincolnshire/lincoln.html',
                  name: 'Hotels in Lincoln',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/republic-of-ireland/dublin.html',
                  name: 'Hotels in Dublin',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/dorset/weymouth/weymouth-seafront.html',
                  name: 'Hotels in Weymouth',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/county-durham/durham.html',
                  name: 'Hotels in Durham',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/warwickshire/stratford-upon-avon.html',
                  name: 'Hotels in Stratford-upon-Avon',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/oxfordshire/oxford.html',
                  name: 'Hotels in Oxford',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/cornwall/camborne/camborne.html',
                  name: 'Hotels in Camborne',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/warwickshire/warwick.html',
                  name: 'Hotels in Warwick',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/gloucestershire.html',
                  name: 'Hotels in Cotswolds',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/cornwall.html',
                  name: 'Hotels in Cornwall',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/west-yorkshire/huddersfield.html',
                  name: 'Hotels in Huddersfield',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/derbyshire/peak-district.html',
                  name: 'Hotels near Peak District',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/highland.html',
                  name: 'Hotels in Scottish Highlands',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/norfolk.html',
                  name: 'Hotels in Norfolk',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/worcestershire.html',
                  name: 'Hotels in Worcestershire',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
          ],
          intro: {
            description:
              '<p>What better way to treat a loved one than with a romantic weekend in the UK? Whether you fancy escaping to the countryside to enjoy some peace and quiet or exploring somewhere new on scenic UK city breaks, our great-value Premier Inn hotels are an ideal base for romantic days out on last-minute weekend getaways.&nbsp;</p>\r\n',
            name: '',
          },
          name: 'Romantic getaways',
        },
        {
          columns: [
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/london-stratford.html',
                  name: 'Best London Stratford hotel',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/kensington.html',
                  name: 'Best Kensington hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/london-hammersmith-ravenscourt-park.html',
                  name: 'Best Hammersmith hotel',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/london-leicester-square.html',
                  name: 'Best Leicester Square hotel',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/southbank-london.html',
                  name: 'Best South Bank hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-london/london/kings-cross-euston-london.html',
                  name: 'Best Kings Cross hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/west-midlands/birmingham.html',
                  name: 'Best Birmingham hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/lothian/edinburgh.html',
                  name: 'Best Edinburgh hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/greater-manchester/manchester.html',
                  name: 'Best Manchester hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/merseyside/liverpool.html',
                  name: 'Best Liverpool hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/scotland/strathclyde/glasgow.html',
                  name: 'Best Glasgow hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/tyne-and-wear/newcastle.html',
                  name: 'Best Newcastle hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/bristol.html',
                  name: 'Best Bristol hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/wales/glamorgan/cardiff.html',
                  name: 'Best Cardiff hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/hampshire/portsmouth.html',
                  name: 'Best Portsmouth hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/norfolk/norwich.html',
                  name: 'Best Norwich hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/lincolnshire/lincoln.html',
                  name: 'Best Lincoln hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/republic-of-ireland/dublin.html',
                  name: 'Best Dublin hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/northern-ireland/antrim/belfast.html',
                  name: 'Best Belfast hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/west-yorkshire/leeds.html',
                  name: 'Best Leeds hotels',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
            {
              linkItems: [
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/oxfordshire/oxford.html',
                  name: 'Best Oxford hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/somerset/bath.html',
                  name: 'Best Bath hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/hampshire/portsmouth.html',
                  name: 'Best Portsmouth hotels',
                  openInNewTab: false,
                },
                {
                  linkSrc:
                    'https://www.qapink.premierinn.digital/gb/en/hotels/england/east-riding-of-yorkshire/bridlington/bridlington-seafront.html',
                  name: 'Best Bridlington Seafront hotel',
                  openInNewTab: false,
                },
              ],
              name: '',
            },
          ],
          intro: {
            description:
              '<p>Get more out of your wonderful winter getaways with family and friends when you book great-value rooms at our UK hotels. From Christmas shopping breaks to New Year’s Eve parties, we’re wherever you need to be this winter. Our cosiest spots for winter staycations are ready and waiting – all that’s left to do is book!&nbsp;</p>\r\n',
            name: '',
          },
          name: 'Winter holidays ',
        },
      ],
    },
  },
};

export const mockedPartialTranslations = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    searchInformation: {
      config: {
        api: {
          initialPageSize: '40',
          lazyLoadPageSize: '10',
          radius: '50',
        },
      },
      content: {
        map: {
          controlText: 'Map View',
          list: 'List view',
        },
        filter: {
          label: {
            restaurant: 'Restaurant',
            airCon: 'Air conditioning',
            chargeableOffsiteParking: 'Chargeable off-site parking',
            freeParking: 'Free parking',
            parking: 'Parking',
            chargeableOnsiteParking: 'Chargeable on-site parking',
            header: 'Filters applied when selected',
            lift: 'Lift access',
            meet: 'Meeting rooms',
            apply: 'Apply filters',
            reset: 'Reset filters',
            facilities: 'Facilities',
          },
          info: {
            lift: 'Some hotels are ground floor only. Please check directly with the hotel (local rate)',
          },
          code: {
            restaurant: 'EAT',
            airCon: 'ACO',
            chargeableOffsiteParking: 'COP',
            freeParking: ['CPF'],
            chargeableOnsiteParking: 'CPP',
            lift: 'LFT',
            meet: 'MEE',
          },
        },
        results: {
          menu: {
            listLong: 'List view',
            mapLong: 'Map view',
            distance: 'Distance',
            price: 'Price',
            filtersLong: 'Filter by',
          },
          notifications: {
            fullyBooked:
              'This hotel is fully booked on your chosen dates. Here are some nearby hotels with available rooms.',
            openingSoon:
              'This hotel will be opening soon. Here are some nearby hotels with available rooms.',
            noFilteredHotels: "We couldn't find any hotels that matched your criteria.",
          },
          result: {
            availabilityWarning: 'Last few rooms',
            distanceUnitPlural: 'miles',
            facilities: {
              businessRoom: 'Standard Extra rooms',
              freeParking: 'Free parking',
              noParking: 'No parking available',
              parking: 'Parking',
              premierPlusRoom: 'Premier Plus',
              standardExtraRoom: 'Premier Plus rooms',
            },
            fromLocation: 'from your search',
            fullyBooked: 'Sold out',
            openingOn: 'Opening on',
            openingSoon: 'Open soon',
            priceFrom: 'From',
            viewDetails: 'View details',
          },
        },
        totalHotels: 'Hotels found',
        dynamicFilters: [
          {
            groupOperator: 'OR',
            groupTitle: 'Parking',
            groupItems: [
              {
                info: '',
                label: 'Free parking',
                queryParam: 'free-parking',
                codes: 'CPF',
              },
            ],
          },
        ],
      },
    },
  },
};

export const mockedPromotionsInformation = {
  showPromo: true,
  isWithinPromoWindow: true,
  promotionCode: 'SAVE20',
  landingPage: '/offers/save20',
  promoBannerColour: '#005EB8',
  promoBannerIcon: 'offer',
  promoBannerTitle: 'Save 20%',
  promoBannerSubtitle: 'Applicable on selected stays',
  promoInvalidMessage: 'Invalid promo code',
  promoExpiredMessage: 'Promo has expired',
  promoAmendMessage: 'Promo cannot be applied to amended bookings',
  promoBookingInfo: {
    ratePlanCode: 'FLEXRATE',
    promotionCode: 'SAVE20',
  },
  promoBox: {
    enabled: true,
    placeholder: 'Enter promo code',
  },
  promoKind: 'PUBLIC',
  genericPromocode: 'SAVE20',
};

export const mockedHotelAvailabilities = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    promotionsInformation: mockedPromotionsInformation,
    multiHotelAvailabilities: [
      {
        hotelId: 'LKEBAR',
        name: 'London Victoria',
        hotelAvailability: {
          available: true,
          distance: 1.52,
          limitedAvailability: false,
          lowestRoomRate: {
            currencyCode: 'GBP',
            netTotal: 316.5,
          },
          unit: 'mile',
          pmsSource: 'Opera',
        },
        hotelInformation: {
          brand: 'PI',
          coordinates: {
            latitude: 51.49306081188715,
            longitude: -0.14288663864134826,
          },
          hotelFacilities: [
            {
              code: 'PBI',
              description: '',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PBI.svg',
              isVisible: true,
              name: 'Public Transport Info',
              weight: 0,
            },
            {
              code: 'DIS',
              description: 'Accessible',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
              isVisible: true,
              name: 'Accessible',
              weight: 1,
            },
            {
              code: 'WET',
              description: 'Accessible Wetroom',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/WET.svg',
              isVisible: true,
              name: 'Accessible Wetroom',
              weight: 1,
            },
            {
              code: 'BKF',
              description: '',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/BKF.svg',
              isVisible: true,
              name: 'Breakfast',
              weight: 1,
            },
            {
              code: 'COC',
              description: '',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/COC.svg',
              isVisible: true,
              name: 'Chargeable offsite parking',
              weight: 1,
            },
            {
              code: 'FAM',
              description: 'Family rooms',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/FAM.svg',
              isVisible: true,
              name: 'Family rooms',
              weight: 1,
            },
            {
              code: 'WIA',
              description: 'Free Wi-Fi',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/WIA.svg',
              isVisible: true,
              name: 'Free Wi-Fi',
              weight: 1,
            },
            {
              code: 'LFT',
              description: 'Lift access',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
              isVisible: true,
              name: 'Lift',
              weight: 1,
            },
            {
              code: 'LUG',
              description: 'Luggage facilities',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LUG.svg',
              isVisible: true,
              name: 'Luggage facilities',
              weight: 1,
            },
            {
              code: 'PAY',
              description: '',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PAY.svg',
              isVisible: true,
              name: 'Payment',
              weight: 1,
            },
            {
              code: 'PRR',
              description: 'Premier Plus room',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PRR.svg',
              isVisible: true,
              name: 'Premier Plus rooms',
              weight: 1,
            },
            {
              code: 'DIN',
              description: '',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIN.svg',
              isVisible: true,
              name: 'Restaurant',
              weight: 1,
            },
            {
              code: 'TBM',
              description: '',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/TBM.svg',
              isVisible: true,
              name: 'Touch Base',
              weight: 1,
            },
            {
              code: 'ACO',
              description: 'Air conditioning',
              icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
              isVisible: true,
              name: 'Air conditioning',
              weight: 5,
            },
          ],
          hotelOpeningDate: '',
          links: {
            detailsPage: '/england/greater-london/london/london-victoria',
          },
          messagingFlag: {
            color: '',
            description: '',
            text: '',
          },
          thumbnailImages: [
            {
              imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LKEBAR/LKEBAR 3.jpg',
              tags: ['exterior', 'surrounding-area'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room1.jpg',
              tags: ['bedroom', 'ID4', 'standard-room', 'double-room'],
            },
            {
              imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LKEBAR/LKEBAR 11.jpg',
              tags: ['restaurant'],
            },
            {
              imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LKEBAR/LKEBAR 6.jpg',
              tags: ['reception'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
              tags: ['bedroom', 'ID4', 'standard-room', 'double-room'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-3.jpg',
              tags: ['bedroom', 'standard-room', 'ID4', 'twin-room'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-4.jpg',
              tags: ['standard-room', 'bedroom', 'twin-room'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Bathroom.jpg',
              tags: ['standard-bathroom', 'bath', 'ID4'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
              tags: ['ultimate', 'bedroom', 'double-room'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-2.jpg',
              tags: ['ultimate', 'bedroom'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-3.jpg',
              tags: ['ultimate'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-5.jpg',
              tags: ['ultimate'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Bedroom-6.jpg',
              tags: ['ultimate'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Bedroom-7.jpg',
              tags: ['ultimate'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Shower.jpg',
              tags: ['ultimate', 'shower'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg',
              tags: ['accessible-room', 'ID4'],
            },
            {
              imageSrc:
                '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Bathroom.jpg',
              tags: ['accessible-bathroom', 'accessible-lowered-bathroom', 'ID4'],
            },
            {
              imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LKEBAR/LKEBAR 13.jpg',
              tags: ['restaurant'],
            },
            {
              imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LKEBAR/LKEBAR 14.jpg',
              tags: ['meeting-room'],
            },
            {
              imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LKEBAR/LKEBAR 2.jpg',
              tags: ['exterior'],
            },
          ],
        },
      },
    ],
    page: 2,
    pageSize: 5,
    total: 110,
  },
};

export const mockCompanyData = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  error: { message: '' },
  data: {
    companyProfile: {
      name: 'Travel Industry Rate ',
      address: {
        addressLine1: '',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        country: 'DE',
        postalCode: '12526',
      },
      telephoneNumber: '999999999999',
      corpId: '15010601',
      PROMOID: 'FX10R',
      companyId: '8986523',
      profileType: 'Company',
      language: 'DE',
      active: true,
      negotiatedRateEnabled: true,
    },
  },
};

export const mockSearchCompanyByIdOrCorpId = {
  isError: false,
  isFetching: false,
  error: { message: '' },
  data: {
    companyProfileById: {
      name: 'Test company',
      address: {
        addressLine1: '4th floor',
        addressLine2: '120 Holborn',
        addressLine3: 'London',
        country: 'UK',
        postalCode: 'EC1N 2TD',
      },
      telephoneNumber: '020 7806 5480',
      corpId: '15001003',
      PROMOID: 'FX10R',
      companyId: '2569623',
      active: true,
      profileType: 'Business',
      negotiatedRateEnabled: true,
      language: 'EN',
    },
  },
  refetch: jest.fn(),
};
//</editor-fold>
