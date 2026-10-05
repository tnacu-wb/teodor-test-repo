import '@testing-library/jest-dom';
import { SEOInformation, TripAdvisorReviews } from '@whitbread-eos/api';
import { containsPlaceLD } from '@whitbread-eos/utils';
import React from 'react';

import { render } from '../../utils/test-utils';
import SEOComponent, { faqFormatter } from './SEO.component';

const mockData = {
  pageTitle: 'Premier Inn hotels | Book direct',
  pageDescription:
    "From booking to bed, we're here to help you rest easy. Whether it's our choice of rooms across 800+ hotels, beds you won't want to leave, our super tasty food, flexible rates that have you covered whatever the weather or our friendly team members who genuinely care about you, these are just some of the reasons we're one of the most-loved hotels in the UK and beyond",
  cardImageUrl:
    '/content/dam/pi/websites/hotelimages/gb/en/M/MANOLD/xMANOLD,P201.jpg.pagespeed.ic.wVArVr-i1-.webp',
  faviconUrl: '/content/dam/pi/websites/desktop/icons/favicons/favicon.ico',
  geoJsonLd: '',
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
};

const mockTripAdvisorReviews = {
  awards: [],
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
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'gb',
  }),
}));

jest.mock('next/head', () => ({
  __esModule: true,
  default: ({ children }: { children?: any }) => {
    const doc = (global as any).document;
    if (!doc || children == null) return null;

    const flatten = (node: any): any[] => {
      if (node == null || node === false) return [];
      if (Array.isArray(node)) return node.flatMap(flatten);

      // React element-like object: { type, props }
      if (typeof node === 'object') {
        // If it's a fragment or a component, it won't have a string type.
        // We can't "render" components here, but fragments contain children we can traverse.
        if (node.props?.children != null && typeof node.type !== 'string') {
          return flatten(node.props.children);
        }
        return [node];
      }

      // string/number etc (ignore)
      return [];
    };

    for (const child of flatten(children)) {
      if (!child) continue;
      if (typeof child.type !== 'string') continue;

      const tag = child.type;
      const props = child.props ?? {};

      // Special-case <title> to keep document.title in sync
      if (tag === 'title') {
        const titleText = props.children == null ? '' : String(props.children);
        doc.title = titleText;
        // also create a <title> element for completeness
        const titleEl = doc.createElement('title');
        titleEl.textContent = titleText;
        doc.head.appendChild(titleEl);
        continue;
      }

      const el = doc.createElement(tag);

      for (const [key, value] of Object.entries(props)) {
        if (key === 'children') {
          if (value == null) continue;
          el.textContent = Array.isArray(value) ? value.join('') : String(value);
          continue;
        }

        // Skip react-only props
        if (key === 'dangerouslySetInnerHTML') {
          const html = (value as any)?.__html;
          if (html != null) el.innerHTML = String(html);
          continue;
        }

        if (value != null) el.setAttribute(key, String(value));
      }

      doc.head.appendChild(el);
    }

    return null;
  },
}));

const mockUseFeatureSwitch = jest.fn();
const mockCustomLocale = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn().mockReturnValue({ release_pi_hreflang_attribute: true }),
  useCustomLocale: () => mockCustomLocale(),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
  useQueryRequest: () => mockComponentProps,
  formatAssetsUrl: (value: string) => value,
  containsPlaceLD: jest.fn(),
}));

describe('SEO', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    jest.clearAllMocks();
    (global as any).document.head.innerHTML = '';
    (global as any).document.title = '';
  });

  it('should render SEO component', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(document.title).toEqual(mockData.pageTitle);
  });

  it('should not render SEO component if isLoading is true', async () => {
    mockComponentProps.isLoading = true;
    render(<SEOComponent {...mockComponentProps} isLoading />);

    expect(document.title).toEqual('');
  });

  it('should not render SEO component if isError is true', async () => {
    mockComponentProps.isLoading = false;
    mockComponentProps.isError = true;
    render(<SEOComponent {...mockComponentProps} isError />);

    expect(document.title).toEqual('');
  });

  it('should set head page description', () => {
    mockComponentProps.isError = false;

    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('meta[name=description]')?.attributes?.getNamedItem('content')
        ?.value
    ).toEqual(mockData.pageDescription);
  });

  it('should set head viewport', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('meta[name=viewport]')?.attributes?.getNamedItem('content')
        ?.value
    ).toEqual('width=device-width, initial-scale=1');
  });

  it('should set head twitter card', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=twitter\\:card]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual('summary');
  });

  it('should set head twitter url', () => {
    window.location.href = 'http://localhost/';

    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=twitter\\:url]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual('http://localhost/');
  });

  it('should set head twitter title', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=twitter\\:title]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual(mockData.pageTitle);
  });

  it('should set head twitter description', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=twitter\\:description]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual(mockData.pageDescription);
  });

  it('should set head twitter image', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=twitter\\:image]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual(mockData.cardImageUrl);
  });

  it('should set head twitter site', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=twitter\\:site]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual('Premier Inn');
  });

  it('should set head twitter creator', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(document.head?.querySelector('meta[property=twitter\\:creator]')).toBeDefined();
  });

  it('should set head og type', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('meta[property=og\\:type]')?.attributes?.getNamedItem('content')
        ?.value
    ).toEqual('website');
  });

  it('should set head og url', () => {
    window.location.href = 'http://localhost/';

    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('meta[property=og\\:url]')?.attributes?.getNamedItem('content')
        ?.value
    ).toEqual('http://localhost/');
  });

  it('should set head og title', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('meta[property=og\\:title]')?.attributes?.getNamedItem('content')
        ?.value
    ).toEqual(mockData.pageTitle);
  });

  it('should set head og description', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=og\\:description]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual(mockData.pageDescription);
  });

  it('should set head og image', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('meta[property=og\\:image]')?.attributes?.getNamedItem('content')
        ?.value
    ).toEqual(mockData.cardImageUrl);
  });

  it('should set head og site_name', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[property=og\\:site_name]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual('Premier Inn');
  });

  it('should set head fb:admins', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(document.head?.querySelector('meta[property=fb\\:admins]')).toBeDefined();
  });

  it('should set favicons image/x-icon link', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('link[type=image\\/x-icon]')?.attributes?.getNamedItem('href')
        ?.value
    ).toEqual(mockData.faviconUrl);
  });

  it('should display hreflang link', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('link[rel="alternate"]')?.attributes?.getNamedItem('hreflang')
        ?.value
    ).toEqual(mockData.hreflangs[0].hreflang);
  });

  it('should display canonical link', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head?.querySelector('link[rel="canonical"]')?.attributes?.getNamedItem('href')?.value
    ).toEqual(mockData.hreflangs[0].href);
  });

  it('should set head msapplication tag', () => {
    render(<SEOComponent {...mockComponentProps} />);

    expect(
      document.head
        ?.querySelector('meta[name=msapplication-TileColor]')
        ?.attributes?.getNamedItem('content')?.value
    ).toEqual('#552462');
  });

  it('should not render seo breadcrumbs in markup when feature flag is false', async () => {
    mockUseFeatureSwitch.mockReturnValue(false);
    render(<SEOComponent {...mockComponentProps} />);

    const googleSEOScript = document.head?.querySelector('script[data-testid="seoGraph"]');
    expect(googleSEOScript?.textContent?.includes('BreadcrumbList')).not.toBe(true);
  });

  it('should render seo breadcrumbs in markup when feature flag is true', async () => {
    mockUseFeatureSwitch.mockReturnValue(true);
    mockComponentProps.breadcrumbs[2].link = '';
    render(<SEOComponent {...mockComponentProps} />);

    const googleSEOScript = document.head?.querySelector('script[data-testid="seoGraph"]');
    expect(googleSEOScript).toBeInTheDocument();
    expect(googleSEOScript?.textContent?.includes('BreadcrumbList')).toBe(true);
  });

  it('should not render seo rating in markup if there are no reviews', async () => {
    render(
      <SEOComponent
        tripAdvisorReviews={{ ...mockTripAdvisorReviews, reviews: [] }}
        {...mockComponentProps}
      />
    );

    const googleSEOScript = document.head?.querySelector('script[data-testid="seoRating"]');
    expect(googleSEOScript).not.toBeInTheDocument();
  });

  it('should not render seo hotelsList in markup if there are no hotels in DLP', async () => {
    render(
      <SEOComponent
        showBreadcrumbs={true}
        tripAdvisorReviews={{ ...mockTripAdvisorReviews, reviews: [] }}
        hotelsList={[]}
        {...mockComponentProps}
      />
    );

    const graphContent =
      document.head?.querySelector('script[data-testid="seoGraph"]')?.textContent ?? '';
    expect(graphContent.includes('ItemList')).toBe(false);
  });

  it('should render seo hotelsList in markup if there are hotels in DLP', async () => {
    render(
      <SEOComponent
        {...mockComponentProps}
        showBreadcrumbs={true}
        tripAdvisorReviews={mockTripAdvisorReviews as TripAdvisorReviews}
        hotelsList={[{ name: 'London Euston' }]}
      />
    );

    const googleSEOScript = document.head?.querySelector('script[data-testid="seoGraph"]');
    expect(googleSEOScript).toBeInTheDocument();
    expect(googleSEOScript?.textContent?.includes('ItemList')).toBe(true);
  });
  it('should render hotel schema in markup for HDP', async () => {
    const hotel = {
      coordinates: {
        latitude: 51.5287718,
        longitude: -0.1337724,
      },
      hotelDescription:
        "Country or city? Culture or adventure? There's no need to choose at Premier Inn Hotel Lincoln Canwick. Set in a peaceful rural location, you'll also be well placed to explore all the business and leisure attractions of Lincoln city centre. Discover historic Lincoln at the Cathedral and Castle, show your support at Sincil Bank football stadium or visit the popular antiques fair at Lincolnshire Showground. Then come back to our hotel to relax with a meal in our tasty restaurant and a great night's sleep in your extra-comfy bed.",
      address: {
        addressLine1: 'Lincoln Road',
        addressLine2: 'Canwick Hill',
        addressLine3: 'Lincoln',
        postalCode: 'LN4 2RF',
        country: 'United Kingdom (the)',
      },
      links: {
        detailsPage: '/england/lincolnshire/lincoln/lincoln-canwick',
      },
      hotelId: 'LINMIL',
      hotelName: 'Premier Inn Lincoln Canwick',
      tripAdvisorReviews: mockTripAdvisorReviews as TripAdvisorReviews,
    };
    render(
      <SEOComponent
        tripAdvisorReviews={mockTripAdvisorReviews as TripAdvisorReviews}
        hotel={hotel}
        {...mockComponentProps}
      />
    );

    const googleSEOScript = document.head?.querySelector('script[data-testid="seoGraph"]');
    expect(googleSEOScript).toBeInTheDocument();
    expect(googleSEOScript?.textContent?.includes('Hotel')).toBe(true);
  });
});

describe('SEO Hotel JSON-LD schema', () => {
  const baseHotel = {
    coordinates: { latitude: 51.5287718, longitude: -0.1337724 },
    hotelDescription: 'A nice hotel.',
    address: {
      addressLine1: 'Lincoln Road',
      addressLine2: 'Canwick Hill',
      addressLine3: 'Lincoln',
      postalCode: 'LN4 2RF',
      country: 'United Kingdom (the)',
    },
    links: { detailsPage: '/england/lincolnshire/lincoln/lincoln-canwick' },
    hotelId: 'LINMIL',
    name: 'Premier Inn Lincoln Canwick',
    contactDetails: {
      hotelNationalPhone: '01522 560000',
      phone: '01522 000000',
    },
    tripAdvisorReviews: mockTripAdvisorReviews as TripAdvisorReviews,
  };

  function getHotelSchema(hotel: any) {
    render(
      <SEOComponent {...mockComponentProps} isLoading={false} isError={false} hotel={hotel} />
    );
    const script = document.head?.querySelector('script[data-testid="seoGraph"]');
    const json = JSON.parse(script?.textContent ?? '{}');
    return json['@graph']?.find((item: any) => item['@type'] === 'Hotel') ?? json;
  }

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({ language: 'en', country: 'gb' });
    mockUseFeatureSwitch.mockReturnValue(false);
    jest.clearAllMocks();
    document.head.innerHTML = '';
  });

  describe('description', () => {
    it('strips HTML tags from hotelDescription', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        hotelDescription: '<p>For <strong>comfy</strong> stays in Lincoln.</p>',
      });
      expect(schema.description).toBe('For comfy stays in Lincoln.');
    });

    it('replaces newlines with a space in hotelDescription', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        hotelDescription: 'First line\nSecond line',
      });
      expect(schema.description).toBe('First line Second line');
    });

    it('strips mixed HTML and newlines from hotelDescription', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        hotelDescription: '<p>Line one.</p>\r\n<p>Line two.</p>',
      });
      expect(schema.description).toBe('Line one. Line two.');
    });

    it('handles unclosed tags without backtracking (ReDoS safety)', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        hotelDescription: '<p>Good content</p><unclosed',
      });
      expect(schema.description).toBe('Good content');
    });

    it('returns undefined when hotelDescription is absent', () => {
      const schema = getHotelSchema({ ...baseHotel, hotelDescription: undefined });
      expect(schema.description).toBeUndefined();
    });
  });

  describe('streetAddress', () => {
    it('does not produce double commas when addressLine3 is an empty string', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        address: { ...baseHotel.address, addressLine3: '' },
      });
      expect(schema.address.streetAddress).not.toMatch(/, ,|,,/);
    });

    it('does not produce double commas when addressLine3 is undefined', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        address: {
          addressLine1: 'Shepiston Lane',
          addressLine2: 'Middlesex',
          postalCode: 'UB3 1RW',
          country: 'United Kingdom (the)',
        },
      });
      expect(schema.address.streetAddress).not.toMatch(/(^, )|(, $)|(, ,)|,,/);
    });

    it('includes all non-empty address parts joined by commas', () => {
      const schema = getHotelSchema(baseHotel);
      expect(schema.address.streetAddress).toContain('Lincoln Road');
      expect(schema.address.streetAddress).toContain('Lincoln');
      expect(schema.address.streetAddress).toContain('Canwick Hill');
    });
  });

  describe('telephone', () => {
    it('uses hotelNationalPhone when it is present', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        contactDetails: { hotelNationalPhone: '01522 560000', phone: '01522 000000' },
      });
      expect(schema.telephone).toBe('01522 560000');
    });

    it('falls back to phone when hotelNationalPhone is absent', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        contactDetails: { phone: '01234 567890' },
      });
      expect(schema.telephone).toBe('01234 567890');
    });

    it('returns an empty string when no phone numbers are available', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        contactDetails: {},
      });
      expect(schema.telephone).toBe('');
    });
  });

  describe('review', () => {
    it('returns a flat array of Review objects (not double-nested)', () => {
      const schema = getHotelSchema(baseHotel);
      expect(Array.isArray(schema.review)).toBe(true);
      expect(Array.isArray(schema.review[0])).toBe(false);
      expect(schema.review[0]['@type']).toBe('Review');
    });

    it('each review contains required schema.org Review properties', () => {
      const schema = getHotelSchema(baseHotel);
      const review = schema.review[0];
      expect(review).toHaveProperty('reviewRating');
      expect(review.reviewRating).toHaveProperty('@type', 'Rating');
      expect(review.reviewRating).toHaveProperty('ratingValue');
      expect(review).toHaveProperty('author');
      expect(review.author).toHaveProperty('@type', 'Person');
      expect(review).toHaveProperty('datePublished');
    });

    it('returns an empty array when there are no reviews', () => {
      const schema = getHotelSchema({
        ...baseHotel,
        tripAdvisorReviews: { ...mockTripAdvisorReviews, reviews: [] },
      });
      expect(schema.review).toEqual([]);
    });
  });
});

describe('faqFormatter', () => {
  it('should format FAQ items correctly', () => {
    const faqItems = [
      { question: 'What is check-in time?', answer: 'Check-in is from 2pm.' },
      { question: 'Can I bring pets?', answer: 'No pets allowed.' },
    ];
    const result = faqFormatter(faqItems);
    expect(result).toContain('"@type":"Question"');
    expect(result).toContain('"name":"What is check-in time?"');
    expect(result).toContain('"text":"Check-in is from 2pm."');
    expect(result).toContain('"name":"Can I bring pets?"');
    expect(result).toContain('"text":"No pets allowed."');
  });

  it('should escape quotes and backslashes in FAQ items', () => {
    const faqItems = [
      { question: 'Can I use "special" characters?', answer: 'Yes, like \\ and "' },
    ];
    const result = faqFormatter(faqItems);
    expect(result).toContain('\\"special\\"');
    expect(result).toContain('\\\\');
  });

  it('should remove newlines from FAQ items', () => {
    const faqItems = [{ question: 'Line1\nLine2', answer: 'Answer\r\nwith newlines' }];
    const result = faqFormatter(faqItems);
    expect(result).not.toContain('\n');
    expect(result).not.toContain('\r');
  });

  it('should return an empty string for empty FAQ items', () => {
    const result = faqFormatter([]);
    expect(result).toBe('');
  });

  it('should handle undefined question and answer gracefully', () => {
    const faqItems = [{ question: undefined, answer: undefined }];
    const result = faqFormatter(faqItems as any);
    expect(result).toContain('"name":""');
    expect(result).toContain('"text":""');
  });

  it('should strip HTML tags from FAQ answers', () => {
    const faqItems = [
      {
        question: 'What facilities are available?',
        answer:
          '<p>We offer <strong>free WiFi</strong> and a <a href="/restaurant">restaurant</a>.</p>',
      },
    ];
    const result = faqFormatter(faqItems);
    expect(result).toContain('"text":"We offer free WiFi and a restaurant."');
    expect(result).not.toContain('<p>');
    expect(result).not.toContain('<strong>');
    expect(result).not.toContain('<a href=');
  });
});

describe('containsPlace JSON-LD', () => {
  const baseHotel = {
    coordinates: { latitude: 51.5287718, longitude: -0.1337724 },
    hotelDescription: 'A nice hotel.',
    address: {
      addressLine1: 'Lincoln Road',
      addressLine2: 'Canwick Hill',
      addressLine3: 'Lincoln',
      postalCode: 'LN4 2RF',
      country: 'United Kingdom (the)',
    },
    links: { detailsPage: '/england/lincolnshire/lincoln/lincoln-canwick' },
    hotelId: 'LINMIL',
    name: 'Premier Inn Lincoln Canwick',
    contactDetails: {
      hotelNationalPhone: '01522 560000',
      phone: '01522 000000',
    },
    tripAdvisorReviews: mockTripAdvisorReviews as TripAdvisorReviews,
  };

  const mockContainsPlace = [
    {
      '@type': 'HotelRoom',
      name: 'Standard Double',
    },
  ];

  const mockAvailability = {
    rates: [],
  } as any;

  const mockRoomTypeInformation = {
    roomTypes: [],
  } as any;

  beforeEach(() => {
    jest.clearAllMocks();

    (containsPlaceLD as jest.Mock).mockReturnValue(mockContainsPlace);

    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    mockUseFeatureSwitch.mockReturnValue(false);

    document.head.innerHTML = '';
  });

  it('should add containsPlace to hotel schema when availability and room type data exist', () => {
    render(
      <SEOComponent
        {...mockComponentProps}
        hotel={{
          ...baseHotel,
          seo: {
            geoJsonLd: 'test data',
          },
          brand: 'PI',
        }}
        hotelAvailability={mockAvailability}
        roomTypeInformation={mockRoomTypeInformation}
      />
    );

    const script = document.head?.querySelector('script[data-testid="seoGraph"]');

    const json = JSON.parse(script?.textContent ?? '{}');

    const hotelSchema = json['@graph']?.find((item: any) => item['@type'] === 'Hotel');

    expect(hotelSchema.containsPlace).toEqual(mockContainsPlace);

    expect(containsPlaceLD).toHaveBeenCalledWith(mockAvailability, mockRoomTypeInformation, 'PI');
  });

  it('should not add containsPlace when availability data is missing', () => {
    render(
      <SEOComponent
        {...mockComponentProps}
        hotel={{
          ...baseHotel,
          brand: 'PI',
        }}
        roomTypeInformation={mockRoomTypeInformation}
      />
    );

    const script = document.head?.querySelector('script[data-testid="seoGraph"]');

    const json = JSON.parse(script?.textContent ?? '{}');

    const hotelSchema = json['@graph']?.find((item: any) => item['@type'] === 'Hotel');

    expect(hotelSchema.containsPlace).toBeUndefined();
  });

  it('should not add containsPlace when room type information is missing', () => {
    render(
      <SEOComponent
        {...mockComponentProps}
        hotel={{
          ...baseHotel,
          brand: 'PI',
        }}
        hotelAvailability={mockAvailability}
      />
    );

    const script = document.head?.querySelector('script[data-testid="seoGraph"]');

    const json = JSON.parse(script?.textContent ?? '{}');

    const hotelSchema = json['@graph']?.find((item: any) => item['@type'] === 'Hotel');

    expect(hotelSchema.containsPlace).toBeUndefined();
  });
});

describe('DLP geoJsonLd handling', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    document.head.innerHTML = '';
  });

  it('should include parsed geoJsonLd fragment in DLP graph', () => {
    const dataWithGeo = {
      ...mockData,
      geoJsonLd: '"@type":"Place","name":"London"',
    };

    render(<SEOComponent {...mockComponentProps} data={dataWithGeo} showBreadcrumbs={true} />);

    const script = document.head?.querySelector('script[data-testid="seoGraph"]');

    expect(script).toBeInTheDocument();

    const json = JSON.parse(script?.textContent ?? '{}');

    expect(
      json['@graph'].some((item: any) => item['@type'] === 'Place' && item.name === 'London')
    ).toBe(true);
  });

  it('should warn when geoJsonLd fragment is invalid', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation();

    const dataWithInvalidGeo = {
      ...mockData,
      geoJsonLd: '""\\"@type\\":\\"Place\\",invalid-json""',
    };

    render(
      <SEOComponent {...mockComponentProps} data={dataWithInvalidGeo} showBreadcrumbs={true} />
    );

    expect(warnSpy).toHaveBeenCalledWith(
      'Invalid geoJsonLd fragment',
      dataWithInvalidGeo.geoJsonLd,
      expect.any(Error)
    );

    warnSpy.mockRestore();
  });

  it('should ignore empty geoJsonLd', () => {
    const dataWithEmptyGeo = {
      ...mockData,
      geoJsonLd: '   ',
    };

    render(<SEOComponent {...mockComponentProps} data={dataWithEmptyGeo} showBreadcrumbs={true} />);

    const script = document.head?.querySelector('script[data-testid="seoGraph"]');

    const json = JSON.parse(script?.textContent ?? '{}');

    expect(
      json['@graph'].find((item: any) => item['@type'] === 'Place' && item.name === 'London')
    ).toBeUndefined();
  });
});
