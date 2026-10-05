import { PageName, SEOInformation, TripAdvisorReviews } from '@whitbread-eos/api';
import React from 'react';

import { render } from '../../utils/test-utils';
import SEOContainer from './SEO.container';

const mockData = {
  pageTitle: 'Premier Inn hotels | Book direct',
  pageDescription:
    "From booking to bed, we're here to help you rest easy. Whether it's our choice of rooms across 800+ hotels, beds you won't want to leave, our super tasty food, flexible rates that have you covered whatever the weather or our friendly team members who genuinely care about you, these are just some of the reasons we're one of the most-loved hotels in the UK and beyond",
  cardImageUrl:
    '/content/dam/pi/websites/hotelimages/gb/en/M/MANOLD/xMANOLD,P201.jpg.pagespeed.ic.wVArVr-i1-.webp',
  faviconUrl: '/content/dam/pi/websites/desktop/icons/favicons/favicon.ico',
  icons: [
    {
      rel: 'icon',
      sizes: '228x228',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xfavicon-228x228.png.pagespeed.ic.AhL0MMwPlZ.webp',
    },
    {
      rel: 'icon',
      sizes: '192x192',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xfavicon-192x192.png.pagespeed.ic.Zk_eY9WhyO.webp',
    },
    {
      rel: 'apple-touch-icon',
      sizes: '192x192',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-precomposed.png.pagespeed.ic.bMeNN1UiMU.webp',
    },
    {
      rel: 'apple-touch-icon',
      sizes: '76x76',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-76x76-precomposed.png.pagespeed.ic.jvouXmNgx-.webp',
    },
    {
      rel: 'apple-touch-icon',
      sizes: '120x120',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-120x120-precomposed.png.pagespeed.ic.-IQFrblwkR.webp',
    },
    {
      rel: 'apple-touch-icon',
      sizes: '152x152',
      href: '/content/dam/pi/websites/desktop/icons/favicons/xapple-touch-icon-152x152-precomposed.png.pagespeed.ic.vgnfkWZNQ3.webp',
    },
  ],
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
  faq: {
    title: 'titleFaq',
    faqItems: [
      {
        question: 'q',
        answer: 'a',
      },
    ],
  },
  hreflangs: [
    {
      hreflang: 'en-gb',
      href: 'https://www.premierinn.com/gb/en/search.html',
    },
    {
      hreflang: 'de-de',
      href: 'https://www.premierinn.com/de/de/search.html',
    },
  ],
} as SEOInformation;

const mockComponentProps = {
  data: mockData,
  isLoading: false,
  isError: false,
  error: null,
};

const mockCustomLocale = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useQueryRequest: () => mockComponentProps,
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
  }),
}));

describe('SEO', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    jest.clearAllMocks();
  });

  const mockComponentProps = {
    page: PageName.HOME,
    hotelId: 'PremierInn',
    bookingFlowId: 'default',
  };

  const mockComponentPropsHDP = {
    page: PageName.HDP,
    hotelId: 'PremierInn',
    bookingFlowId: 'default',
    displayMeta: true,
    breadcrumbs: [
      {
        position: 1,
        title: 'Home',
        link: '/',
      },
      {
        position: 2,
        title: 'Hotels',
        link: '/hotels',
      },
      {
        position: 3,
        title: 'Hotel Details',
        link: '/hotels/hotel-details',
      },
    ],
    tripAdvisorReviews: {
      rating: 4.0,
      subRatings: [],
      name: 'Premier Inn London Euston hotel',
      address: '1 Dukes Road, London WC1H 9PJ England',
      reviews: [
        {
          publishedDate: '2025-03-20T01:30:45-0400',
          rating: 1.0,
          tripType: 'Business',
          title: 'Just poor',
          text: "Probably the worst Premier Inn I've stayed at, avoid. Expensive, too hot, too noisy, TV didnt work, WiFi was slow, food was overpriced and of poor quality, staff not bothered and unwelcoming. If you must stay at a Premier Inn then try the St. Pancras Premier Inn just down the road because the Euston one is crap.",
          user: {
            username: 'ajays81',
            location: 'Lancashire, UK',
          },
        },
        {
          publishedDate: '2025-03-18T00:56:15-0400',
          rating: 3.0,
          tripType: 'Family',
          title: 'bed and shower below expectations',
          text: "The bed sheet had a stain and the shower was jumping from cold to warm making it uncomfortable to shower. Outside of that, everything was good. I usually stay on Premier Inn when I travel, it is a safe option. But when you can't shower or if you don't find the sheet in your bed spotless, this is troublesome.",
          user: {
            username: 'Conrado d',
            location: null,
          },
        },
        {
          publishedDate: '2025-03-08T04:58:17-0500',
          rating: 5.0,
          tripType: 'Family',
          title: 'Tayler is amazing!!!!',
          text: 'Tayler takes five star because she had the best service she could ever have and she very kind and gave me a little bag full of goodies so everybody come here because all the look for Tayler cause they are Tayler is there? Tayler is the best worker that could ever be so look out for her Take five done because she had the best service she could. Love Luke age 6',
          user: {
            username: 'wygells',
            location: null,
          },
        },
        {
          publishedDate: '2025-03-03T07:20:30-0500',
          rating: 1.0,
          tripType: 'Couples',
          title: 'Dire',
          text: 'Not usual Premier Inn standard. Breakfast was bedlam no orange juice no muesli no staff so queuing to be seated and we had a train to catch. Tables are so close together you are literally eating with the other people. Noise in room from 5.30 pipes banging. No other rooms available. Dark room hardly any natural light. Grotty overall.',
          user: {
            username: 'LJE19671967',
            location: 'Oswestry, United Kingdom',
          },
        },
        {
          publishedDate: '2025-02-27T05:54:17-0500',
          rating: 5.0,
          tripType: 'Family',
          title: 'London experience',
          text: 'Good position in London near tube links. Rooms good and beds very comfy famiy room. Quiet  even though on main street. Breakfast good with quick access to table. Staff really accomidating on arrival as unable to have early check in. Able to leave luggage for day and pick up on way home',
          user: {
            username: 'susan h',
            location: null,
          },
        },
      ],
      numberOfReviews: 6302,
      writeReview:
        'https://www.tripadvisor.com/UserReview-g186338-d215522-Premier_Inn_London_Euston_hotel-London_England.html?m=67640',
      webUrl:
        'https://www.tripadvisor.com/Hotel_Review-g186338-d215522-Reviews-Premier_Inn_London_Euston_hotel-London_England.html?m=67640',
      locationId: '215522',
      hotelCode: 'LONEUS',
    },
  };

  it('should render SEO container', () => {
    render(<SEOContainer {...mockComponentProps} />);
  });
  it('should return SEO DLP specific component if the pageName is DLP and dlpData is defined', () => {
    render(
      <SEOContainer
        {...mockComponentProps}
        page={PageName.DLP}
        dlpData={mockData}
        breadcrumbs={mockComponentPropsHDP.breadcrumbs}
      />
    );
  });
  it('should return SEO component with breadcrumbs & tripAdvisorReviews for HDP page', () => {
    render(
      <SEOContainer
        {...mockComponentPropsHDP}
        page={PageName.HDP}
        breadcrumbs={mockComponentPropsHDP.breadcrumbs}
        tripAdvisorReviews={mockComponentPropsHDP.tripAdvisorReviews as TripAdvisorReviews}
      />
    );
  });
});
