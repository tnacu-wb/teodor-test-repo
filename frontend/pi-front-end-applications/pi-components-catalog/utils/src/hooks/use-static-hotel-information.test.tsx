import useStaticHotelInformation from './use-static-hotel-information';

const mockCustomLocale = jest.fn();
jest.mock('./use-custom-locale', () => () => mockCustomLocale());

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockQueryRequest = jest.fn();
jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useQueryRequest: (...args: unknown[]) => mockQueryRequest(...args),
}));

const mockQueryRequestResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    hotelInformationBySlug: {
      bookingFlow: {
        bookingFlowItems: [
          {
            bookingId: 'booking-a1',
            rateCode: 'FLEX',
          },
        ],
      },
      galleryImages: [
        {
          alt: 'alt',
          caption: 'caption',
          iconSrc: 'iconSrc.png',
          imageSrc: 'image.png',
          thumbnailSrc: 'thumbnail.png',
        },
      ],
      hotelFacilities: [
        {
          code: 'WIFI',
          description: 'Free Wi-fi',
          icon: 'wifi.png',
          isVisible: true,
          name: 'Wifi',
          weight: 4,
        },
      ],
      name: 'Manchester Old Trafford',
      brand: 'PI',
      headline: 'Manchester Old Trafford headline',
      address: {
        addressLine1: 'Manchester',
        addressLine2: 'John Lane, 59',
        addressLine3: '',
        postalCode: 'E11 AB3',
      },
      satNavDirections: 'satNavDirections',
      directions: 'directions',
      transportInformation: 'transportInformation',
      parkingDescription: 'parkingDescription',
      roomConfiguration: {
        tabGroups: [
          {
            groupId: '1',
            groupName: 'Double',
          },
        ],
        tabItems: [
          {
            roomDescription: 'Double room',
            roomName: 'Double room',
            roomType: 'DBL',
            images: [
              {
                alt: 'alt',
                caption: 'caption',
                iconSrc: 'iconSrc.png',
                imageSrc: 'imageSrc.png',
                thumbnailSrc: 'thumbnail.png',
              },
            ],
            facilities: [
              {
                code: 'WIFI',
                description: 'Free Wi-fi',
                icon: 'wifi.png',
                isVisible: true,
                name: 'Wifi',
                weight: 4,
              },
            ],
          },
        ],
      },
      restaurant: {
        name: 'Premier Inn Restaurant',
        description: 'Restaurant description',
        logoSrc: 'logoSrc.png',
        menus: [
          {
            name: 'Breakfast',
            description: 'Breakfast menu',
            imageSrc: 'breakfast.png',
            menuSrc: 'menuSrc',
            menuLabel: 'Breakfast',
            disclaimer: 'Disclaimer message',
          },
        ],
      },
      coordinates: {
        latitude: 25,
        longitude: 94,
      },
      whatThreeWords: 'what_3_words',
      hotelId: 'MANOLD',
      hotelOpeningDate: '',
      messagingFlag: {
        color: 'purple',
        description: 'Premier Plus',
        text: 'Premier Plus Hotel',
      },
      announcement: {
        endDate: '2023-05-12',
        showAnnouncement: false,
        startDate: '2023-04-12',
        text: 'Some announcement text',
        title: 'Covid 19 announcement',
        type: 'info',
      },
      importantInfo: {
        title: 'Some important info',
        infoItems: [
          {
            text: 'Info #1',
            priority: 1,
            startDate: '2023-05-12',
            endDate: '2023-05-16',
          },
        ],
      },
      accessibilityInfo: {
        header: 'Accessibility info header',
        linkText: 'linkUrl',
        phoneNumber: '+447777777777',
        text: 'Accessibility info',
      },
      contactDetails: {
        phone: '+442222222222',
        hotelNationalPhone: '+443333333333',
        email: 'manold@premierinn.com',
      },
      hotelDescription: 'hotelDescription',
      faq: {
        title: 'FAQs',
        faqItems: [
          {
            question: 'first FAQ',
            answer: 'First FAQ answer',
          },
        ],
      },

      facts: {
        factItems: [
          {
            title: 'Free parking',
            description: '',
          },
        ],
      },
    },
  },
};

describe('useStaticHotelInformation', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      asPath: '/hotels/england/greater-london/london/london-beckton.html',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
    });
    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
  });

  afterEach(() => {
    mockQueryRequest.mockReset();
  });

  it('should return hotel information data, error obj, isLoading and isError flags', () => {
    const {
      isLoading,
      isError,
      error,
      bookingFlow,
      galleryImages,
      hotelFacilities,
      name,
      brand,
      headline,
      address,
      satNavDirections,
      directions,
      transportInformation,
      parkingDescription,
      roomConfiguration,
      restaurant,
      coordinates,
      whatThreeWords,
      hotelId,
      hotelOpeningDate,
      messagingFlag,
      announcement,
      importantInfo,
      accessibilityInfo,
      contactDetails,
      hotelDescription,
      faq,
      facts,
    } = useStaticHotelInformation();

    expect(isLoading).toBe(false);
    expect(isError).toBe(false);
    expect(error).toEqual({ message: '' });
    expect(bookingFlow).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.bookingFlow);
    expect(galleryImages).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.galleryImages
    );
    expect(hotelFacilities).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.hotelFacilities
    );
    expect(name).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.name);
    expect(brand).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.brand);
    expect(headline).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.headline);
    expect(address).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.address);
    expect(satNavDirections).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.satNavDirections
    );
    expect(directions).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.directions);
    expect(transportInformation).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.transportInformation
    );
    expect(parkingDescription).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.parkingDescription
    );
    expect(roomConfiguration).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.roomConfiguration
    );
    expect(restaurant).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.restaurant);
    expect(whatThreeWords).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.whatThreeWords
    );
    expect(coordinates).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.coordinates);
    expect(hotelId).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.hotelId);
    expect(hotelOpeningDate).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.hotelOpeningDate
    );
    expect(messagingFlag).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.messagingFlag
    );
    expect(announcement).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.announcement);
    expect(importantInfo).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.importantInfo
    );
    expect(accessibilityInfo).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.accessibilityInfo
    );
    expect(contactDetails).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.contactDetails
    );
    expect(hotelDescription).toEqual(
      mockQueryRequestResponse.data.hotelInformationBySlug.hotelDescription
    );
    expect(faq).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.faq);
    expect(facts).toEqual(mockQueryRequestResponse.data.hotelInformationBySlug.facts);
  });

  it('should call useQueryRequest when retrieving hotel information', () => {
    useStaticHotelInformation();

    expect(mockQueryRequest).toHaveBeenCalledTimes(1);
  });

  it('should pass stay dates in query key and variables when URL contains dates', () => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      asPath:
        '/hotels/england/greater-london/london/london-beckton.html?ARRyyyy=2024&ARRmm=08&ARRdd=15&NIGHTS=3',
      query: {
        slug: ['england', 'greater-london', 'london', 'london-beckton.html'],
        ARRyyyy: '2024',
        ARRmm: '08',
        ARRdd: '15',
        NIGHTS: '3',
      },
    });

    useStaticHotelInformation();

    expect(mockQueryRequest).toHaveBeenCalledWith(
      [
        'staticHotelInformation',
        'en',
        'gb',
        '/hotels/england/greater-london/london/london-beckton.html',
        '2024-08-15',
        '2024-08-18',
      ],
      expect.anything(),
      {
        slug: '/hotels/england/greater-london/london/london-beckton.html',
        language: 'en',
        country: 'gb',
        stayStartDate: '2024-08-15',
        stayEndDate: '2024-08-18',
      }
    );
  });

  it('should omit stay dates when URL date params are missing', () => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      asPath: '/hotels/england/greater-london/london/london-beckton.html',
      query: { slug: ['england', 'greater-london', 'london', 'london-beckton.html'] },
    });

    useStaticHotelInformation();

    expect(mockQueryRequest).toHaveBeenCalledWith(
      [
        'staticHotelInformation',
        'en',
        'gb',
        '/hotels/england/greater-london/london/london-beckton.html',
      ],
      expect.anything(),
      {
        slug: '/hotels/england/greater-london/london/london-beckton.html',
        language: 'en',
        country: 'gb',
      }
    );
  });
});
