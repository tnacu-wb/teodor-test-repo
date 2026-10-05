export const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    headerInformation: {
      content: {
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
          promoCode: 'promo code',
        },
        countries: [
          {
            language: 'English',
            flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
          },
          {
            language: 'German',
            flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
          },
        ],
        header: {
          image: '/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg',
        },
        subNav: [
          {
            title: 'Business',
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
              { title: 'Business blog', url: '/gb/en/business-blog.html?INTCMP=topNav' },
            ],
          },
          {
            title: 'Short breaks ',
            navOptions: [
              { title: 'Short breaks ', url: '/gb/en/short-breaks.html?INTCMP=topNav' },
              { title: 'City breaks', url: '/gb/en/short-breaks/city-breaks.html?INTCMP=topNav' },
              {
                title: 'Beach breaks',
                url: '/gb/en/short-breaks/coastal-breaks.html?INTCMP=topNav',
              },
              { title: 'National parks', url: '/gb/en/hotels/national-parks.html?INTCMP=topNav' },
              {
                title: 'Family breaks',
                url: '/gb/en/short-breaks/family-breaks.html?INTCMP=topNav',
              },
            ],
          },
          {
            title: 'Locations ',
            navOptions: [
              { title: 'Book a hotel', url: '/gb/en/book-a-hotel.html?INTCMP=topNav' },
              { title: 'Hotel directory', url: '/gb/en/hotels.html?INTCMP=topNav' },
              { title: 'Best hotels', url: '/gb/en/hotels/best-hotels.html?INTCMP=topNav' },
              { title: 'Germany hotels', url: '/gb/en/hotels/germany.html?INTCMP=topNav' },
              { title: 'Local guides', url: '/gb/en/short-breaks/city-breaks.html?INTCMP=topNav' },
            ],
          },
          {
            title: 'About us',
            navOptions: [
              { title: 'About us', url: '/gb/en/why.html?INTCMP=topNav' },
              { title: 'Our rates', url: '/gb/en/why/rates.html?INTCMP=topNav' },
              { title: 'Food and drink', url: '/gb/en/why/food.html?INTCMP=topNav' },
              { title: 'Our rooms', url: '/gb/en/sleep/our-rooms.html?INTCMP=topNav' },
              { title: 'Families', url: '/gb/en/why/family.html?INTCMP=topNav' },
            ],
          },
        ],
      },
    },
  },
};

export const mockUseRestQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {},
};

export const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    HeaderLeisureInformation: {
      content: {
        countries: [
          {
            flagUrl: '/etc/clientlibs/pi-HeaderLeisure/resources/images/british-round.svg',
            language: 'English',
          },
          {
            flagUrl: '/etc/clientlibs/pi-HeaderLeisure/resources/images/germany-round.svg',
            language: 'German',
          },
        ],
        menu: {
          guestAccount: 'Guest account',
          changeLogs: 'Change logs',
          agentMemo: 'Agent memo',
        },
        HeaderLeisure: {
          image: '/etc/clientlibs/pi-HeaderLeisure/resources/images/pi-refresh-logo.svg',
        },
      },
    },
  },
};
