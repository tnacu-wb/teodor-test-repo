import { renderSanitizedHtml } from '@whitbread-eos/utils';

export const mockDlpInformation = {
  breadcrumbs: [
    { link: '#', title: 'Home' },
    { link: '#', title: 'Locations' },
    { link: '#', title: 'Newcastle' },
  ],
  title: 'Hotels in Newcastle',
  description: renderSanitizedHtml(
    `Our hotels in Newcastle are close to some of the city’s most famous landmarks, including the Millennium Bridge that stretches across the River Tyne, Newcastle racecourse, boutique shops in nearby Jesmond and Newcastle Airport. There’s great places to relax and unwind along the river on The Quayside or in refreshing green spaces like Leazes Park near the city centre, next door to the home of Newcastle FC, St. James’ Park. Our hotels in Newcastle are close to some of the city’s most famous landmarks, including the Millennium Bridge that stretches across the River Tyne, Newcastle racecourse, boutique shops in nearby Jesmond and Newcastle Airport. There’s great places to relax and unwind along the river on The Quayside or in refreshing green spaces like Leazes Park near the city centre, next door to the home of Newcastle FC, St. James’ Park.`
  ),
  picture:
    'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
  coordinates: {
    latitude: 54.9783,
    longitude: 1.6178,
  },
  hotels: [
    {
      code: 'NEWPTI',
      order: 1,
    },
    {
      code: 'NEWMTI',
      order: 2,
    },
    {
      code: 'NEWTEA',
      order: 3,
    },
    {
      code: 'NEWTHY',
      order: 4,
    },
    {
      code: 'BANBRI',
      order: 5,
    },
    {
      code: 'HEAPTI',
      order: 6,
    },
    {
      code: 'LONSTM',
      order: 7,
    },
    {
      code: 'LONKIN',
      order: 8,
    },
    {
      code: 'HEIBAH',
      order: 9,
    },
    {
      code: 'BURSTA',
      order: 10,
    },
    {
      code: 'LUTREG',
      order: 11,
    },
    {
      code: 'LONEUS',
      order: 12,
    },
    {
      code: 'MANOLD',
      order: 13,
    },
    {
      code: 'BOLBAR',
      order: 14,
    },
    {
      code: 'FRAMTI',
      order: 15,
    },
    {
      code: 'FRESUD',
      order: 16,
    },
    {
      code: 'DUBSOU',
      order: 17,
    },
    {
      code: 'STUAIR',
      order: 18,
    },
    {
      code: 'BRILEW',
      order: 19,
    },
    {
      code: 'BRIQUI',
      order: 20,
    },
  ],
  why: {
    title: 'Why Premier Inn?',
    description:
      'Is it our bed of dreams, our lip smackingly tasty food, our great value or our amazing team members that guests love so much? We reckon it’s a bit of everything.',
    picture:
      'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
    whyItems: [
      {
        itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
        itemTitle: 'Happy guests',
        itemDescription: 'Our hotels are rated consistently highly by those who stay with us.',
      },
      {
        itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
        itemTitle: 'Here to serve',
        itemDescription: 'Our famous unlimited breakfast and evening meals at every hotel.',
      },
      {
        itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
        itemTitle: 'Good night guarantee',
        itemDescription: 'A great night’s sleep, or your money back!',
      },
      {
        itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
        itemTitle: 'Zimmerreinigung auf Wunsch',
        itemDescription: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
      },
      {
        itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
        itemTitle: 'Lorem ipsum dolor',
        itemDescription: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
      },
      {
        itemIcon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
        itemTitle: 'Lorem ipsum dolor',
        itemDescription: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
      },
    ],
  },
  faqs: [
    {
      title: 'About Newcastle',
      faqItems: [
        {
          question: 'What is someone from Newcastle called?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'What is the population of Newcastle?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
      ],
    },
    {
      title: 'Visiting Newcastle',
      faqItems: [
        {
          question: 'What is someone from Newcastle called?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'What is the population of Newcastle?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
      ],
    },
    {
      title: 'Premier Inn',
      faqItems: [
        {
          question: 'What is someone from Newcastle called?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'What is the population of Newcastle?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
        {
          question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
          answer:
            'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
        },
      ],
    },
  ],
  dlps: {
    title: 'Explore other destinations',
    dlpItems: [
      {
        picture:
          'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
        title: 'Hotels in London Wembley Stadium',
        link: '/hotels/gb/en/london/london-wembley-stadium',
        order: 1,
      },
      {
        picture:
          'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
        title: 'Hotels in London Wembley Stadium',
        link: '/hotels/gb/en/london/london-wembley-stadium',
        order: 2,
      },
      {
        picture:
          'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
        title: 'Hotels in London Wembley Stadium',
        link: '/hotels/gb/en/london/london-wembley-stadium',
        order: 3,
      },
      {
        picture:
          'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
        title: 'Hotels in London Wembley Stadium',
        link: '/hotels/gb/en/london/london-wembley-stadium',
        order: 4,
      },
      {
        picture:
          'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
        title: 'Hotels in London Wembley Stadium',
        link: '/hotels/gb/en/london/london-wembley-stadium',
        order: 5,
      },
    ],
  },
  thingsToDo: {
    title: 'Things To Do',
    items: [
      {
        picture:
          '/content/dam/pi/websites/desktop/homepage/brand-promos/xcookhouse-pub-dinner-1000x640.jpg.pagespeed.ic.Np2dFYsLBS.webp',
        title: 'Interesting restaurants',
        link: 'http://www.premierinn.com/gb/en/hotels/scotland/lothian/edinburgh.html',
        order: 1,
      },
      {
        picture:
          '/content/dam/pi/websites/desktop/why/premier-everywhere/airports/airports-family-suitcase-500x320.jpg',
        title: 'Travel around',
        link: 'https://www.premierinn.com/gb/en/why/locations/airport-hotels.html',
        order: 2,
      },
      {
        picture:
          '/content/dam/pi/websites/desktop/News/2021/cities-by-night/cities-by-night-london-1000x640.jpg',
        title: 'Night life',
        link: 'https://www.premierinn.com/gb/en/things-to-do/london.html',
        order: 3,
      },
      {
        picture:
          '/content/dam/pi/websites/desktop/guides/bracknell/bracknell-legoland-01-500x320.jpg',
        title: 'Theme parks',
        link: 'https://www.premierinn.com/gb/en/short-breaks/days-out.html',
        order: 4,
      },
    ],
  },
};

export const mockHotelInformationObject = {
  name: 'Newcastle City Centre (The Gate)',
  brand: 'PI',
  distanceFromReference: 128.29555929579615,
  topSectionImages: [
    {
      alt: '',
      caption: '',
      iconSrc: '',
      imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/N/NEWTHY/NEWTHY1-min.jpg',
      thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/N/NEWTHY/NEWTHY1-min.jpg',
    },
  ],
  hotelFacilities: [
    {
      code: 'COC',
      description: '',
      icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/COC.svg',
      isVisible: true,
      name: 'Chargeable offsite parking',
      weight: 0,
    },
  ],
  links: {
    detailsPage: '/england/tyne-and-wear/newcastle/newcastle-city-centre-the-gate',
  },
};

export const mockGetHotelsInformation = Array.from({ length: 20 }).map(
  () => mockHotelInformationObject
);

export const newList = Array.from({ length: 8 }).map(() => mockHotelInformationObject);

export const mockStaticContentFavicon = {
  faviconUrl: '/content/dam/pi/websites/desktop/icons/favicons/favicon.ico',
  msIcons: [
    {
      name: 'msapplication-TileImage',
      content: '/content/dam/pi/websites/desktop/icons/favicons/favicon-ie10-144x144.png',
    },
    {
      name: 'msapplication-square70x70logo',
      content: '/content/dam/pi/websites/desktop/icons/favicons/favicon-ie11-70x70.png',
    },
    {
      name: 'msapplication-square150x150logo',
      content: '/content/dam/pi/websites/desktop/icons/favicons/favicon-ie11-150x150.png',
    },
    {
      name: 'msapplication-wide310x150logo',
      content: '/content/dam/pi/websites/desktop/icons/favicons/favicon-ie11-310x150.png',
    },
    {
      name: 'msapplication-square310x310logo',
      content: '/content/dam/pi/websites/desktop/icons/favicons/favicon-ie11-310x310.png',
    },
  ],
  icons: [
    {
      sizes: '228x228',
      rel: 'icon',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xfavicon-228x228.png.pagespeed.ic.AhL0MMwPlZ.webp',
    },
    {
      sizes: '192x192',
      rel: 'icon',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xfavicon-192x192.png.pagespeed.ic.Zk_eY9WhyO.webp',
    },
    {
      sizes: '',
      rel: 'apple-touch-icon',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-precomposed.png.pagespeed.ic.bMeNN1UiMU.webp',
    },
    {
      sizes: '76x76',
      rel: 'apple-touch-icon',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-76x76-precomposed.png.pagespeed.ic.jvouXmNgx-.webp',
    },
    {
      sizes: '120x120',
      rel: 'apple-touch-icon',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-120x120-precomposed.png.pagespeed.ic.-IQFrblwkR.webp',
    },
    {
      sizes: '152x152',
      rel: 'apple-touch-icon',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-152x152-precomposed.png.pagespeed.ic.vgnfkWZNQ3.webp',
    },
  ],
};

export const mockGetStaticContent = {
  isLoading: false,
  isError: false,
  error: null,
  data: {
    headerInformation: {
      content: {
        favicon: mockStaticContentFavicon,
      },
    },
    getPageData: {
      commonIconsEndpoint: JSON.stringify({
        'icon.pin-city': '/content/dam/global/icons/common/pin-city.svg',
        'icon.pin-default': '/content/dam/global/icons/common/pin-purple.svg',
      }),
    },
  },
};
