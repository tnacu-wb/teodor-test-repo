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

export const mockBookingConfirmationAuthenticatedResponse = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  error: { message: '' },
  data: {
    bookingConfirmationAuthenticated: {
      reservationByIdList: [
        {
          billing: {
            address: {
              addressLine1: 'Dental Surgery',
              postalCode: 'GU16 7HF',
            },
            title: 'Mr',
            telephone: '+440123123123',
            firstName: 'Catalin',
            lastName: 'Iosif',
            email: 'catalin.iosif@mailinator.com',
          },
          reservationGuestList: [
            {
              givenName: 'Catalin',
              surName: 'Iosif',
            },
          ],
          gdsReferenceNumber: null,
          roomStay: {
            checkInTime: '15:00',
            checkOutTime: '12:00',
            ratePlanCode: 'FLEXRATE',
            arrivalDate: '2023-06-15',
            departureDate: '2023-06-17',
            bookingChannel: 'PI.com',
            roomPrice: 1998,
            cot: false,
            adultsNumber: 1,
            roomExtraInfo: {
              roomName: 'Premier Plus Room',
            },
            childrenNumber: 0,
          },
          paymentCard: {
            cardNumberMasked: 'XXXXXXXXXXXX0017',
          },
          reservationOverrideReasons: {
            reasonCode: 'DTH',
            callerName: 'test',
            managerName: '',
            reasonName: 'Death',
          },
          reservationOverridden: true,
          guaranteeCode: 'CC',
          reservationStatus: 'Reserved',
          additionalGuestInfo: {
            purposeOfStay: 'LEI',
          },
        },
      ],
      balanceOutstanding: 1998,
      currencyCode: 'GBP',
      newTotal: 1998,
      policyCode: 'D1A',
      previousTotal: 0,
      totalCost: 1998,
      hotelId: 'MANOLD',
      hotelName: 'Manchester Old Trafford',
      bookingFlowId: 'booking-a1',
      rateMessage: '<p>Amend or cancel up to 1pm on arrival day</p>\n',
    },
  },
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
  messageDescription: null,
  messageHeader: null,
  noMealsFound: false,
  restaurantNotFound: false,
};

export const mockedMealPackagesData = {
  packages: {
    packages: {
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
        },
        {
          currency: 'GBP',
          description: 'Check out any time until 2pm (normal check-out time is 12pm).',
          id: 'FI24HR',
          imageSrc: '/content/dam/global/extras/late-checkout.png',
          name: 'Late check-out',
          order: 2,
          price: 10,
          available: null,
        },
      ],
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
        },
        {
          allergyInfoLabel: 'Allergy & nutrition info',
          allergyInfoSrc: '/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
          bartId: '17',
          currency: 'GBP',
          description:
            '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
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
        },
      ],
      mealsKids: [
        {
          allergyInfoLabel: null,
          allergyInfoSrc: null,
          currency: null,
          description:
            '<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\r\n',
          id: 'BFCHDF',
          imageSrc: '/content/dam/global/restaurants/Global/child-breakfast.jpg',
          menu: null,
          name: 'Free breakfast for kids',
          order: 0,
          price: null,
          totalPrice: null,
        },
      ],
      roomSelection: [
        {
          reservationId: '2292308',
          packagesSelection: [
            {
              id: 'BFCHDF',
              noOfSelections: 1,
            },
            {
              id: 'BFADCT',
              noOfSelections: 1,
            },
            {
              id: 'BFADBF',
              noOfSelections: 1,
            },
          ],
        },
        {
          reservationId: '2292104',
          packagesSelection: [
            {
              id: 'BFADCT',
              noOfSelections: 1,
            },
            {
              id: 'BFCHDF',
              noOfSelections: 1,
            },
            {
              id: 'BFADBF',
              noOfSelections: 1,
            },
          ],
        },
      ],
      roomSelectionAmendExtras: [
        {
          reservationId: '2292308',
          packagesSelection: [
            {
              id: 'HSCKIN',
              noOfSelections: 1,
            },
            {
              id: 'HSCOU2',
              noOfSelections: 1,
            },
          ],
        },
        {
          reservationId: '2292104',
          packagesSelection: [
            {
              id: 'HSCKIN',
              noOfSelections: 1,
            },
            {
              id: 'HSCOU2',
              noOfSelections: 1,
            },
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
        {
          description: 'description',
          image: '/content/dam/hub/app/MasterCard.jpg',
        },
        {
          description: 'description',
          image: '/content/dam/global/booking/privacy_icon_visa_verified.png',
        },
      ],
      moreInfoLabel: 'Find out more',
      name: 'We keep your personal data safe and secure.',
    },
    restaurant: {
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
        {
          description: null,
          disclaimer: null,
          imageSrc: null,
          menuLabel: null,
          menuSrc: '/content/dam/global/restaurants/Global/pi-band2.pdf',
          name: 'Dinner',
        },
        {
          description: null,
          disclaimer: null,
          imageSrc: null,
          menuLabel: null,
          menuSrc: '/content/dam/global/restaurants/Global/pi-band2.pdf',
          name: 'Meal Deal',
        },
      ],
      messageDescription: null,
      messageHeader: null,
      noMealsFound: false,
      restaurantNotFound: false,
    },
    hotelHasCityTaxForBusiness: false,
    hotelHasCityTaxForLeisure: false,
  },
};

export const mockedGetStaticContentData = {
  headerInformation: {
    announcement: {
      browserCompatibilityMessage:
        '<p>Looks like your web browser isn’t supported. Try downloading <a href="https://www.google.com/chrome/">Google Chrome</a>, <a href="https://www.microsoft.com/en-us/edge">Microsoft Edge</a> or <a href="https://www.mozilla.org/en-CA/firefox/new/">Firefox</a> for a better online experience with us.</p>\n',
      text: null,
      type: 'info',
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
            url: 'https://www.qaorange.premierinn.digital/gb/en/account/dashboard.html',
          },
          {
            icon: 'settings',
            title: 'Settings',
            url: 'https://www.qaorange.premierinn.digital/gb/en/account/profile.html',
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
            domain: 'premierinn.digital',
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
            formLabel: 'Log in to Business Booker',
            emailPlaceholder: 'Business email address',
            loginButton: 'Log in to Business Booker',
          },
          forgotPassword: 'Forgotten password?',
          leisure: {
            emailPlaceholder: 'Email address',
            formLabel: 'Log in to your Premier Inn account',
            loginButton: 'Log in',
          },
          badCredentialsError: 'Your email address or password is incorrect.',
          invalidEmail: 'Please enter a valid e-mail address.',
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
              url: '/gb/en/business.html?INTCMP=topNav',
            },
            {
              title: 'Business Booker ',
              url: '/gb/en/business/business-booker.html?INTCMP=topNav',
            },
            {
              title: 'Business Account',
              url: '/gb/en/business/business-account.html?INTCMP=topNav',
            },
            {
              title: 'Business blog',
              url: '/gb/en/business-blog.html?INTCMP=topNav',
            },
          ],
          title: 'Business',
        },
        {
          navOptions: [
            {
              title: 'Short breaks',
              url: '/gb/en/short-breaks.html?INTCMP=topNav',
            },
            {
              title: 'City breaks',
              url: '/gb/en/short-breaks/city-breaks.html?INTCMP=topNav',
            },
            {
              title: 'Beach breaks',
              url: '/gb/en/short-breaks/coastal-breaks.html?INTCMP=topNav',
            },
            {
              title: 'National parks',
              url: '/gb/en/hotels/national-parks.html?INTCMP=topNav',
            },
            {
              title: 'Family breaks',
              url: '/gb/en/short-breaks/family-breaks.html?INTCMP=topNav',
            },
          ],
          title: 'Short breaks ',
        },
        {
          navOptions: [
            {
              title: 'Book a hotel',
              url: '/gb/en/book-a-hotel.html?INTCMP=topNav',
            },
            {
              title: 'Hotel directory',
              url: '/gb/en/hotels.html?INTCMP=topNav',
            },
            {
              title: 'Best hotels',
              url: '/gb/en/hotels/best-hotels.html?INTCMP=topNav',
            },
            {
              title: 'Germany hotels',
              url: '/gb/en/hotels/germany.html?INTCMP=topNav',
            },
            {
              title: 'Local guides',
              url: '/gb/en/short-breaks/city-breaks.html?INTCMP=topNav',
            },
          ],
          title: 'Locations ',
        },
        {
          navOptions: [
            {
              title: 'About us',
              url: '/gb/en/why.html?INTCMP=topNav',
            },
            {
              title: 'Our rates',
              url: '/gb/en/why/rates.html?INTCMP=topNav',
            },
            {
              title: 'Food and drink',
              url: '/gb/en/why/food.html?INTCMP=topNav',
            },
            {
              title: 'Our rooms',
              url: '/gb/en/sleep/our-rooms.html?INTCMP=topNav',
            },
            {
              title: 'Families',
              url: '/gb/en/why/family.html?INTCMP=topNav',
            },
          ],
          title: 'About us',
        },
        {
          navOptions: [
            {
              title: ' ',
              url: ' ',
            },
            {
              title: ' ',
              url: ' ',
            },
          ],
          title: ' ',
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
        'Some of these details are missing or invalid. Please provide correct details',
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
      invalidPastDate: 'Your selected date is in the past, rates below are for availability today',
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
          'If you’d like to book five rooms or more, please call us and we’ll be happy to help.',
        emp01groupBookingMessage: 'You can book a maximum of 2 rooms using the employee rate',
        noResults:
          'Sorry, we couldn’t find any hotels with available rooms. Try changing your search area.',
      },
    },
  },
};

export const mockedAmendPaymentData = {
  discount: 10,
  paymentOption: {
    payNow: false,
    payOnArrival: true,
  },
  paymentType: [
    {
      acceptedCardTypes: [
        {
          logoSrc: '/content/dam/global/booking/Mastercard.jpg',
          name: 'Mastercard Credit',
          type: 'MC',
        },
        {
          logoSrc: '/content/dam/global/booking/AX.jpg',
          name: 'American Express',
          type: 'AX',
        },
        {
          logoSrc: '/content/dam/global/booking/Visa_Debit.jpg',
          name: 'Visa Debit',
          type: 'VS',
        },
        {
          logoSrc: '/content/dam/global/booking/MD.jpg',
          name: 'Mastercard Debit',
          type: 'MC',
        },
        {
          logoSrc: '/content/dam/global/booking/VC.jpg',
          name: 'Visa Credit',
          type: 'VS',
        },
      ],
      cnpOptionAvailable: false,
      cnpPreSelected: false,
      enabled: true,
      name: 'CARD',
      subType: undefined,
      order: 1,
      paymentOptions: [
        {
          enabled: true,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'NEW_CARD',
    },
    {
      acceptedCardTypes: [],
      cnpOptionAvailable: true,
      cnpPreSelected: false,
      enabled: false,
      name: 'PIBA UK',
      subType: 'PIBAGB',
      order: 2,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: false,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: ['PIBA_UK_ALLOWED_ONLY_IN_UK'],
      type: 'NEW_PIBA',
    },
    {
      acceptedCardTypes: [],
      cnpOptionAvailable: true,
      cnpPreSelected: false,
      enabled: true,
      name: 'PIBA EU',
      subType: 'PIBADE',
      order: 3,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'NEW_PIBA',
    },
    {
      acceptedCardTypes: undefined,
      cnpOptionAvailable: false,
      cnpPreSelected: false,
      enabled: true,
      name: 'Account to company',
      subType: undefined,
      order: 4,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'ACCOUNT_COMPANY',
    },
    {
      acceptedCardTypes: undefined,
      cnpOptionAvailable: false,
      cnpPreSelected: false,
      enabled: true,
      name: 'Non-guaranteed booking',
      subType: undefined,
      order: 5,
      paymentOptions: [
        {
          enabled: false,
          order: 1,
          type: 'PAY_NOW',
        },
        {
          enabled: true,
          order: 2,
          type: 'PAY_ON_ARRIVAL',
        },
      ],
      reasons: [],
      type: 'RESERVE_WITHOUT_CARD',
    },
  ],
  cardPresent: {
    display: false,
    value: false,
  },
  eckoh: {
    diplay: false,
    enabled: false,
  },
  cardHolderName: null,
  billingAddress: {
    diplay: true,
  },
  emailPreference: {
    diplay: true,
    send: true,
    emailAddress: 'abc@zzz.com',
  },
  allowances: {
    diplay: true,
    values: ['ultimate wifi', 'meal deal'],
    dinnerBudget: 100,
  },
  purchaseOrder: null,
  companyRef: {
    diplay: true,
    value: 'Ref999',
  },
  a2cDetails: {
    diplay: true,
    number: '99887',
    name: 'Company',
    address: 'Street ACB, no 62',
    postcode: 'FG567',
  },
  preAuthCharges: {
    display: true,
    charges: ['carParking'],
  },
  hotelName: 'London Euston',
  hotelCode: 'LONEUS',
  brand: 'PI',
};
