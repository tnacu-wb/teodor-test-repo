export const mockBasketResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    basket: {
      paymentOption: 'PAY_ON_ARRIVAL',
      status: 'COMPLETED',
      hotelId: 'LONEUS',
    },
  },
};

export const mockBookingConfirmationResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    bookingConfirmation: {
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
          guaranteeCode: 'NON',
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

export const mockOverrideReasonsResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    manageBooking: {
      isCancellable: false,
      isAmendable: false,
      isRuleCompliant: true,
    },
    cancellationReasons: {
      cancellationReasons: [
        {
          active: true,
          code: 'COM',
          description: 'MA Commercial Decision',
          name: 'MA Commercial Decision',
          managerApprovalNeeded: true,
        },
        {
          active: true,
          code: 'CRD',
          description: 'MA Card Expired',
          name: 'MA Card Expired',
          managerApprovalNeeded: true,
        },
        {
          active: true,
          code: 'DTH',
          description: 'Death',
          name: 'Death',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'DUP',
          description: 'Duplicate Reservation',
          name: 'Duplicate Reservation',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'GLO',
          description: 'Global Issue',
          name: 'Global Issue',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'HCV',
          description: 'MA High Call Volumes',
          name: 'MA High Call Volumes',
          managerApprovalNeeded: true,
        },
        {
          active: true,
          code: 'ILL',
          description: 'Illness',
          name: 'Illness',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'JUR',
          description: 'Jury Duty',
          name: 'Jury Duty',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'LEG',
          description: 'Legal Appointments',
          name: 'Legal Appointments',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'MED',
          description: 'Medical Appointments',
          name: 'Medical Appointments',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'MIL',
          description: 'Military/Army',
          name: 'Military/Army',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'OTH',
          description: 'Other - See Comments Below',
          name: 'Other - See Comments Below',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'OUT',
          description: 'Outbooking',
          name: 'Outbooking',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'RML',
          description: 'Rooming List/Group Block',
          name: 'Rooming List/Group Block',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'TRP',
          description: 'Transportation Issues / Delays',
          name: 'Transportation Issues / Delays',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'TSR',
          description: 'Test Reservation',
          name: 'Test Reservation',
          managerApprovalNeeded: false,
        },
        {
          active: true,
          code: 'WEA',
          description: 'Weather',
          name: 'Weather',
          managerApprovalNeeded: false,
        },
      ],
    },
  },
};

export const mockBookingConfirmationData = {
  bookingConfirmation: {
    bookingFlowId: 'booking-a1',
    hotelId: 'LONEUS',
    hotelName: 'London Euston',
    currencyCode: 'GBP',
    newTotal: 400.0,
    totalCost: 360.0,
    previousTotal: 0.0,
    reservationByIdList: [
      {
        reservationGuestList: [
          {
            givenName: 'GuestOne',
            surName: 'Test',
            nameTitle: 'Mrs',
            email: 'mailto:test@test.com',
          },
        ],
        reservationId: '1447709',
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2023-07-18',
          departureDate: '2023-07-22',
          ratePlanCode: 'FLEXRATE',
          roomExtraInfo: {
            roomName: 'Family Room',
            roomType: 'FMTRPL',
          },
          roomPrice: 45.0,
        },
      },
      {
        reservationGuestList: [
          {
            givenName: 'GuestOne',
            surName: 'Test',
            nameTitle: 'Mrs',
            email: 'mailto:test2@test.com',
          },
        ],
        reservationId: '1447707',
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2023-07-18',
          departureDate: '2023-07-22',
          ratePlanCode: 'FLEXRATE',
          roomExtraInfo: {
            roomName: 'Family Room',
            roomType: 'FMTRPL',
          },
          roomPrice: 45.0,
        },
      },
    ],
  },
};

export const mockGetPackagesData = {
  packages: {
    packages: {
      roomSelection: [{ packagesSelection: [] }],
      meals: [
        {
          allergyInfoLabel: 'Allergy & nutrition info',
          allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
          currency: 'GBP',
          description:
            '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
          id: 'BFADBF',
          bartId: '11',
          imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
          name: 'Premier Inn Breakfast',
          price: 9.5,
          order: 1,
          freeBreakfastOption: true,
          freeBreakfastCode: 'BFCHDF',
          freeBreakfastMaxPerMeal: 2,
          menu: {
            menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
            name: 'Breakfast menu',
          },
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
          name: 'Free breakfast for kids',
          order: 0,
          menu: null,
        },
      ],
    },
    restaurant: {
      logoSrc: '/content/dam/global/restaurants/HDE/lounge logo.jpg',
      messageDescription: null,
      messageHeader: null,
      noMealsFound: false,
      restaurantNotFound: false,
    },
    privacyPolicy: {
      description:
        '<p>We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE.</p>\n',
      linkLabel: 'View our Privacy Notice',
      linkSrc: '/gb/en/terms/privacy-policy.html',
      moreInfoLabel: 'Find out more',
      moreInfo: [
        {
          description:
            '<p><b>VeriSign</b><br>\n<br>\n</p>\n<p>Premier Inn takes the security of any information we hold very seriously, and will always implement security measures that are in line with, or exceed current best practices and recommendations. Where necessary, and in common with other websites, we use SSL (Secure Sockets Layer) encryption to ensure that information provided to us is not visible to anybody else when in transit between your computer and our servers. You can tell when SSL is in use by the presence of a small &quot;padlock&quot; symbol in the status bar or next to the address bar of your web browser. In addition, our web servers are housed behind a secure firewall that prevents access to our databases from unauthorised users. All of our servers are housed in a secure environment with high levels of physical security, and access is only permitted to a handful of security screened staff.</p>\n',
          image: '/content/dam/global/booking/verisign.png',
        },
        {
          description:
            '<p><b>MasterCard</b><br>\n<br>\n</p>\n<p>MasterCard SecureCode is a service to enhance your existing MasterCard account. A private code means added protection against unauthorized use of your card when you shop at participating online retailers. Once youve registered and created your own private SecureCode, you will be automatically prompted by your financial institution at checkout to provide your SecureCode each time you make a purchase with a participating online merchant. Your SecureCode is quickly confirmed by your financial institution and then your purchase is completed. Your SecureCode will never be shared with the merchant. Its just like entering your PIN at an ATM. When you correctly enter your SecureCode during a purchase at a participating online merchant, you confirm that you are the authorized cardholder and your purchase is then completed. If an incorrect SecureCode is entered, the purchase will not be completed. Even if someone knows your credit or debit card number, the purchase cannot be completed without your SecureCode at a participating merchant. How do I sign up for MasterCard?<br>\n<br>\n</p>\n<p>Choosing your own private SecureCode is quick and easy. When shopping online at a participating merchant, you will be prompted to create your own SecureCode prior to checkout. When this happens, a pop up window will appear and you will be guided through the simple enrolment process before your purchase is completed. Once you have created your private SecureCode, you will use it for future purchases at participating online merchants.</p>\n',
          image: '/content/dam/hub/app/MasterCard.jpg',
        },
        {
          description:
            '<p><b>Verified by Visa</b><br>\n<br>\n</p>\n<p>Verified by Visa is a new security service that tells on-line retailers and banks that you are a genuine cardholder when you shop on-line. It allows you to use a personal password to confirm your identity and protect your Visa card when you use your card on the Internet, providing greater reassurance and security. Through a simple checkout process, Verified by Visa confirms your identity when you make purchases in participating online stores. Its convenient and it works with your existing Visa Card. Verified by Visa is easy to use. You register your card just once and create your own password. Then, when you make purchases at participating online stores, a Verified by Visa window will appear. Simply enter your password and click submit. Your identity is verified and your purchase is secure.<br>\n<br>\n</p>\n<p><b>How do I sign up for Verified by Visa?</b><br>\n<br>\n</p>\n<p>Visit the Verified by Visa website to register your Visa Card online, alternatively contact your bank who can register your card for Verified by Visa for you. Once your bank has activated your card, Verified by Visa protects you at every participating on-line store. When you shop at a participating on-line store, your card will be automatically recognized as protected by Verified by Visa. When you are completing your purchase, your issuing bank will verify your password.</p>\n',
          image: '/content/dam/global/booking/privacy_icon_visa_verified.png',
        },
      ],
      name: 'We keep your personal data safe and secure.',
    },
    hotelHasCityTaxForLeisure: false,
    hotelHasCityTaxForBusiness: false,
  },
};

export const mockGetDonationPackages = {
  donations: {
    donationPackages: [
      {
        code: 'ZCHRY3',
        currency: 'GBP',
        unitPrice: 5,
      },
      {
        code: 'ZCHRY4',
        currency: 'GBP',
        unitPrice: 3,
      },
      {
        code: 'ZCHRY5',
        currency: 'GBP',
        unitPrice: 1,
      },
    ],
  },
};

export const mockBookingConfirmationAuthenticatedMock = {
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
          email: 'mailto:catalin.iosif@mailinator.com',
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
};

export const mockRatesInformationDiscountRate = {
  ratesInformation: {
    rateClassifications: [
      {
        additionalDescription: 'Additional description',
        ratePlanCode: 'STDDIS10',
        rateOrder: '0',
        rateNotes: '<p>This booking cannot be amended or cancelled</p>\n',
        rateName: 'Standard',
        rateLongDescription: '',
        rateDescription: 'Save 10% on our Standard rate',
        rateClassification: 'STDDIS10',
        rateCategory: 'D',
        rateTags: ['10% discount'],
      },
    ],
  },
};

export const mockHotelInformation = {
  address: 'Shepiston Lane, Middlesex, UB3 1RW',
  hotelId: 'HEAPTI',
  hotelOpeningDate: '',
  name: 'London Heathrow Airport (M4/J4)',
  brand: 'PI',
  parkingDescription:
    '<p>Our London Heathrow Airport (M4/J4) hotel has 121 on-site parking spaces in total, chargeable at £7 for 24 hours. Parking availability can vary, if you have any questions please contact the hotel prior to your stay.</p>\r\n<p>Airport parking is available through our partners Holiday Extras at great prices (see frequently asked questions section for more details).</p>\r\n<p><i>&nbsp;</i></p>\r\n',
  directions:
    'From M4 exit Jtn 4 then follow signs to Uxbridge remaining in left hand lane. Bear left following signs for other routes and Hayes. At give way point, use as a roundabout and take the 4th exit off (sign posted Hayes). The hotel is 200 yards away on the right hand side. (SAT NAV - UB3 1RW) Parking costs 7 GBP per night for Premier Inn guests.',
  county: 'greater-london',
  contactDetails: {
    phone: '0333 003 1715',
    hotelNationalPhone: '0333 003 1715',
    email: '',
  },
  coordinates: {
    latitude: 51.496015,
    longitude: -0.447979,
  },
  links: {
    detailsPage: '/england/greater-london/london/london-heathrow-airport-m4j4',
  },
  galleryImages: [
    {
      alt: '',
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/H/HEAPTI/HEAPTI 1.jpg',
    },
  ],
  announcement: {
    endDate: '21/07/2022',
    showAnnouncement: 'true',
    startDate: '08/01/2021',
    text: 'Get all the latest updates on our response to&nbsp;<a href="/gb/en/covid-19.html" target="_blank"><u>COVID-19</u></a>&nbsp;and see how we’re keeping guests safe with our&nbsp;<a href="/gb/en/why/cleanliness.html" target="_blank"><u>Premier Inn CleanProtect<sup>TM</sup></u></a>&nbsp;promise.&nbsp;<br>\r\n',
    title: '',
    type: 'info',
  },
  importantInfo: {
    title: '',
    infoItems: [
      {
        text: 'There is limited parking at this hotel which is allocated on a first come, first served basis and is chargeable.',
        priority: '',
        startDate: '11/10/2023',
        endDate: '31/12/2045',
      },
    ],
  },
  ancillaryCloseout: {
    items: [
      {
        endDate: '10/02/2024',
        serviceCode: 'BREAKFAST_NA',
        startDate: '01/02/2024',
        text: 'Premier Inn Breakfast is not available',
        upsellCodes: 'BFADBF,BBIB',
      },
    ],
  },
};
