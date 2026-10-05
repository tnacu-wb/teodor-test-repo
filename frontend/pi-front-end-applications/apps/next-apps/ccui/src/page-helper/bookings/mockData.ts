export const searchRulesMockData = {
  maxNightsLimitation: {
    maxNights: 364,
  },
  globalConfig: {
    maxRoomsLim: {
      maxRooms: 9,
    },
  },
  maxArrivalDateLimitation: {
    maxArrivalDate: 365,
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
        acceptedRoomTypes: ['DIS', 'FAM'],
      },
      {
        adultsNumber: 2,
        childrenNumber: 0,
        acceptedRoomTypes: ['DB', 'TWIN', 'DIS', 'FAM'],
      },
      {
        adultsNumber: 1,
        childrenNumber: 2,
        acceptedRoomTypes: ['DIS', 'FAM'],
      },
      {
        adultsNumber: 1,
        childrenNumber: 1,
        acceptedRoomTypes: ['DB', 'TWIN', 'DIS', 'FAM'],
      },
      {
        adultsNumber: 1,
        childrenNumber: 0,
        acceptedRoomTypes: ['SB', 'DB', 'TWIN', 'DIS', 'FAM'],
      },
    ],
  },
};

export const staticContentMockData = {
  headerInformation: {
    content: {
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
          hubHdpLogo: null,
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
        ],
      },
    },
  },
};

export const bookingsResultsMockData = {
  searchBookings: {
    bookings: [
      {
        bookingReference: 'MAH7346157',
        currencyCode: 'GBP',
        hotelId: 'LONKIN',
        sourcePms: 'OPERA',
        status: 'UPCOMING',
        hotelName: 'London Kings Cross - hub by Premier Inn',
        stayingGuests: [
          {
            firstName: 'gyytd',
            lastName: 'room',
            title: 'Prof',
          },
          {
            firstName: 'testing',
            lastName: 'rooms',
            title: 'Lady',
          },
          {
            firstName: 'tester',
            lastName: 'testerqa',
            title: 'Mrs',
          },
        ],
        totalCost: 0,
        booker: {
          firstName: 'tester',
          lastName: 'testerqa',
          title: 'Mrs',
        },
        arrivalDate: '2023-09-13',
        departureDate: '2023-09-15',
        sourceSystem: 'test',
      },
    ],
    limit: 10,
    hasMore: false,
    offset: 10,
    totalPages: 1,
    totalResults: 1,
    responseLimitExceeded: false,
  },
};

export const mockedGetStaticContent = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    headerInformation: {
      announcement: {
        browserCompatibilityMessage:
          '<p>Looks like your web browser isn’t supported. Try downloading <a href="https://www.google.com/chrome/">Google Chrome</a>, <a href="https://www.microsoft.com/en-us/edge">Microsoft Edge</a> or <a href="https://www.mozilla.org/en-CA/firefox/new/">Firefox</a> for a better online experience with us.</p>',
        text: null,
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
            'If you’d like to book five rooms or more, please call us and we’ll be happy to help.',
          noResults:
            'Sorry, we couldn’t find any hotels with available rooms. Try changing your search area.',
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
