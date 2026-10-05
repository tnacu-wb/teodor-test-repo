import { SingleHotelAvailability, SRPartialTranslationsType } from '@whitbread-eos/api';

export const mockedItem = [
  {
    hotelAvailability: {
      lowestRoomRate: {
        currencyCode: 'GBP',
        netTotal: 48.0,
      },
      distance: 9.99,
      unit: 'mile',
      available: true,
      limitedAvailability: false,
    },
    name: 'London Edmonton',
    hotelId: 'LONEDM',
    hotelInformation: {
      hotelOpeningDate: '',
      brand: 'PI',
      coordinates: {
        latitude: 51.61348,
        longitude: -0.0466,
      },
      thumbnailImages: [
        {
          imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONEDM/London Edmonton 001.jpg',
          tags: ['exterior'],
        },
      ],
      hotelFacilities: [
        {
          code: 'HAR',
          description: 'Accessible Room',
          icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/HAR.svg',
          isVisible: true,
          name: 'Accessible Room',
          weight: 1,
        },
        {
          code: 'CPF',
          description: 'Free parking',
          icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/CPF.svg',
          isVisible: true,
          name: 'Free parking',
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
      links: {
        detailsPage: '/england/greater-london/london/london-edmonton',
      },
    },
  },
];

export const mockedItems = [
  ...mockedItem,
  {
    ...mockedItem,
    hotelInformation: {
      ...mockedItem[0].hotelInformation,
      coordinates: {
        latitude: mockedItem[0].hotelInformation.coordinates.latitude,
        longitude: -0.0566,
      },
    },
  },
];

export const mockedMultiSearchParams = {
  arrivalDay: 25,
  arrivalMonth: 11,
  arrivalYear: 2023,
  location: 'London',
  numberOfNights: 4,
  rooms: [{ adultsNumber: 2, childrenNumber: 0, type: 'Double' }],
  bookingChannel: 'WEB',
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  sort: 'DISTANCE',
};

export const mockedPartialTranslations = {
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
          freeParking: 'Free parking',
          parking: 'Parking',
        },
      },
      results: {
        menu: {
          listLong: 'List view',
          mapLong: 'Map view',
        },
        notifications: {
          fullyBooked:
            'This hotel is fully booked on your chosen dates. Here are some nearby hotels with available rooms.',
          openingSoon:
            'This hotel will be opening soon. Here are some nearby hotels with available rooms.',
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
    },
  },
};

export const mockedHeaderInformation = {
  headerInformation: {
    announcement: {
      text: 'Get all the latest updates on our response to COVID-19',
      type: 'info',
    },
    content: {
      global: {
        brand: {
          hubBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg',
          zipBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg',
        },
      },
    },
    results: {
      notifications: {
        errorTitle: 'Oh dear..',
        availabilitiesErrorMessage: 'Something went wrong when we tried to load the results',
      },
    },
  },
};

export const mockedThumbnailImages = [
  {
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/D/DUBSOU/dublin-cc-external.jpg',
    tags: ['exterior'],
  },
];

export const mockedFacilities = [
  {
    code: 'DIS',
    description: 'Accessible',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
    isVisible: true,
    name: 'Accessible',
    weight: 1,
  },
  {
    code: 'COP',
    description: 'Chargeable offsite parking',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/COP.svg',
    isVisible: true,
    name: 'Chargeable offsite parking',
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
];

export const mockedLowestRate = {
  currencyCode: 'GBP',
  netTotal: 124,
};

export const mockedSlug = {
  detailsPage: '/england/greater-london/london/hub-london-tower-bridge',
};

export const mockedHotelInformation: NonNullable<SingleHotelAvailability['hotelInformation']> = {
  coordinates: { latitude: 0, longitude: 0 },
  thumbnailImages: mockedThumbnailImages,
  hotelFacilities: mockedFacilities,
  brand: 'PI',
  links: mockedSlug,
  hotelOpeningDate: '',
  messagingFlag: {
    text: '',
    color: '',
  },
};

export const mockedDataHotelCard: SingleHotelAvailability = {
  hotelId: '',
  name: 'hub London Tower Bridge',
  hotelInformation: mockedHotelInformation,
  hotelAvailability: {
    distance: 5,
    unit: 'mile',
    lowestRoomRate: mockedLowestRate,
    available: false,
    limitedAvailability: true,
    pmsSource: 'OPERA',
  },
};

export const mockedDataHotelCardWithHotelFlagBanner: SingleHotelAvailability = {
  ...mockedDataHotelCard,
  hotelId: 'hotel-123',
  hotelInformation: {
    ...mockedHotelInformation,
    hotelFlags: {
      isEnabled: true,
      flagBanner: {
        text: 'New restaurant now open',
        textColour: 'baseWhite',
        backgroundImage: '/content/dam/pi/restaurants/banner-icon.png',
      },
    },
  },
};

export const mockedDataHotelCardWithDisabledHotelFlagBanner: SingleHotelAvailability = {
  ...mockedDataHotelCard,
  hotelInformation: {
    ...mockedHotelInformation,
    hotelFlags: {
      isEnabled: false,
      flagBanner: {
        text: 'New restaurant now open',
      },
    },
  },
};

export const mockedDataHotelCardWithEmptyHotelFlagBanner: SingleHotelAvailability = {
  ...mockedDataHotelCard,
  hotelInformation: {
    ...mockedHotelInformation,
    hotelFlags: {
      isEnabled: true,
      flagBanner: {
        text: '',
      },
    },
  },
};

export const mockedPartialTranslationsHotelCard: SRPartialTranslationsType = {
  searchInformation: {
    config: {
      api: {
        initialPageSize: '40',
        lazyLoadPageSize: '10',
        radius: '50',
      },
    },
    content: {
      global: {
        brand: {
          hubBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg',
          zipBadge: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg',
          hub: '',
          hubLogo: '',
          hubLogoWhite: '',
          pi: '',
          piLogo: '',
          zip: '',
          zipLogo: '',
          zipLogoWhite: '',
        },
      },
      map: {
        controlText: 'Map view',
        list: 'List view',
      },
      totalHotels: '',
      filter: {
        label: {
          freeParking: '',
          parking: '',
          restaurant: '',
          airCon: '',
          header: '',
          chargeableOffsiteParking: '',
          chargeableOnsiteParking: '',
          lift: '',
          meet: '',
          apply: '',
          reset: '',
          facilities: '',
        },
        info: { lift: '' },
        code: {
          restaurant: '',
          airCon: '',
          chargeableOnsiteParking: '',
          chargeableOffsiteParking: '',
          freeParking: [''],
          lift: '',
          meet: '',
        },
      },
      results: {
        menu: {
          listLong: 'List view',
          mapLong: 'Map view',
          distance: '',
          price: '',
          filtersLong: '',
        },
        notifications: { fullyBooked: '', openingSoon: '', noFilteredHotels: '' },
        result: {
          viewDetails: 'View details',
          priceFrom: 'From',
          openingSoon: 'Open soon',
          openingOn: 'Opening on',
          fullyBooked: 'Sold out',
          fromLocation: 'from your search',
          distanceUnitPlural: 'miles',
          availabilityWarning: 'Last few rooms',
          facilities: {
            businessRoom: 'Business rooms',
            premierPlusRoom: 'Premier Plus',
            standardExtraRoom: 'Premier Plus rooms',
            freeParking: '',
            noParking: '',
            parking: '',
          },
          promotions: {
            discountApplied: 'Discount applied',
            discountUnavailable: 'Discount unavailable',
          },
        },
      },
    },
  },
};
