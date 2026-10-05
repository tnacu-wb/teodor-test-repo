import '@testing-library/jest-dom';
import { getStaticContent } from '@whitbread-eos/api';
import { formatAssetsUrl, useQueryRequest } from '@whitbread-eos/utils';

import { render, screen } from '../../utils/test-utils';
import HomeBanner from './HomeBanner.component';

jest.mock('@chakra-ui/react', () => ({
  Box: ({ children, ...props }: any) => <div {...props}>{children}</div>,
  Heading: ({ children, as: As = 'h1', ...props }: any) => {
    const Component = As as any;
    return <Component {...props}>{children}</Component>;
  },
  Text: ({ children, ...props }: any) => <p {...props}>{children}</p>,
}));

jest.mock('@chakra-ui/icons', () => ({
  ArrowDownIcon: () => <svg data-testid="arrow-down-icon" />,
}));

jest.mock('@whitbread-eos/api', () => ({
  getStaticContent: jest.fn(() => 'mock-query'),
  SITE_LEISURE: 'leisure',
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Icon: () => <span data-testid="home-banner-arrow-icon" />,
}));

jest.mock('@whitbread-eos/utils', () => ({
  useCustomLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
  useQueryRequest: jest.fn(() => ({ data: undefined })),
  formatAssetsUrl: jest.fn((url: string) => `formatted-${url}`),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: function MockImage({ src, alt }: { src: string; alt: string }) {
    return <img data-testid="home-banner-image" src={src} alt={alt} />;
  },
}));

const MockSearchComponent = () => <div data-testid="search-component" />;

const createStaticContent = (heroOverrides: Record<string, unknown> = {}) => ({
  headerInformation: {
    content: {
      hero: {
        backgroundImage: 'test-bg.jpg',
        backgroundColor: '5B2E66',
        headingTextShort: 'Short Heading',
        headingTextLong: 'Long Heading Text',
        headingFontColor: 'FFFFFF',
        headingFontWeight: '700',
        headingFontShadow: true,
        subHeadingShow: true,
        subHeadingText: 'Test subtitle',
        subHeadingFontColor: 'FFFFFF',
        subHeadingFontWeight: '400',
        subHeadingFontShadow: false,
        bottomCaptionShow: false,
        bottomCaptionText: 'Scroll down',
        bottomCaptionShadow: false,
        bottomCaptionArrowShow: false,
        ...heroOverrides,
      },
    },
  },
});

describe('<HomeBanner />', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useQueryRequest as jest.Mock).mockReturnValue({ data: undefined });
  });

  describe('when no hero data is available', () => {
    it('renders empty title when no data provided', () => {
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-title')).toHaveTextContent('');
    });

    it('renders empty subtitle when no data provided', () => {
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-subtitle')).toHaveTextContent('');
    });

    it('does not render background image', () => {
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.queryByTestId('home-banner-image')).not.toBeInTheDocument();
    });

    it('does not render bottom caption', () => {
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.queryByText('Scroll down')).not.toBeInTheDocument();
    });

    it('renders the search component', () => {
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('search-component')).toBeInTheDocument();
    });
  });

  describe('title text priority', () => {
    it('renders headingTextShort when both short and long are available', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({
          headingTextShort: 'Short Title',
          headingTextLong: 'Long Title',
        }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-title')).toHaveTextContent('Short Title');
    });

    it('renders headingTextLong as fallback when headingTextShort is missing', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ headingTextShort: '', headingTextLong: 'Long Title' }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-title')).toHaveTextContent('Long Title');
    });

    it('renders empty title when both headingTextShort and headingTextLong are missing', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ headingTextShort: '', headingTextLong: '' }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-title')).toHaveTextContent('');
    });
  });

  describe('subtitle visibility', () => {
    it('renders subtitle when subHeadingShow is true', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ subHeadingShow: true, subHeadingText: 'My Subtitle' }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-subtitle')).toHaveTextContent('My Subtitle');
    });

    it('renders subtitle when subHeadingShow is not defined', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ subHeadingShow: undefined, subHeadingText: 'Visible Sub' }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-subtitle')).toHaveTextContent('Visible Sub');
    });

    it('hides subtitle when subHeadingShow is false', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ subHeadingShow: false }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.queryByTestId('home-banner-subtitle')).not.toBeInTheDocument();
    });
  });

  describe('background image', () => {
    it('renders background image when backgroundImage is provided', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ backgroundImage: 'hero-bg.jpg' }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-image')).toBeInTheDocument();
    });

    it('calls formatAssetsUrl with the background image URL', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ backgroundImage: 'hero-bg.jpg' }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(formatAssetsUrl).toHaveBeenCalledWith('hero-bg.jpg');
    });

    it('does not render background image when backgroundImage is empty', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ backgroundImage: '' }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.queryByTestId('home-banner-image')).not.toBeInTheDocument();
    });

    it('does not render background image when backgroundImage is null', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({ backgroundImage: null }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.queryByTestId('home-banner-image')).not.toBeInTheDocument();
    });
  });

  describe('bottom caption', () => {
    it('renders bottom caption text when bottomCaptionShow is true', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({
          bottomCaptionShow: true,
          bottomCaptionText: 'Explore more',
        }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByText('Explore more')).toBeInTheDocument();
    });

    it('does not render bottom caption when bottomCaptionShow is false', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({
          bottomCaptionShow: false,
          bottomCaptionText: 'Explore more',
        }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.queryByText('Explore more')).not.toBeInTheDocument();
    });

    it('renders arrow icon when bottomCaptionArrowShow is true', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({
          bottomCaptionShow: true,
          bottomCaptionArrowShow: true,
          bottomCaptionText: 'Explore more',
        }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.getByTestId('home-banner-arrow-icon')).toBeInTheDocument();
    });

    it('does not render arrow icon when bottomCaptionArrowShow is false', () => {
      (useQueryRequest as jest.Mock).mockReturnValue({
        data: createStaticContent({
          bottomCaptionShow: true,
          bottomCaptionArrowShow: false,
          bottomCaptionText: 'Explore more',
        }),
      });
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(screen.queryByTestId('home-banner-arrow-icon')).not.toBeInTheDocument();
    });
  });

  describe('data fetching', () => {
    it('calls getStaticContent with false', () => {
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(getStaticContent).toHaveBeenCalledWith(false);
    });

    it('calls useQueryRequest with correct parameters', () => {
      render(<HomeBanner searchComponent={<MockSearchComponent />} />);
      expect(useQueryRequest).toHaveBeenCalledWith(['GetStaticContent', 'en', 'gb'], 'mock-query', {
        country: 'gb',
        language: 'en',
        site: 'leisure',
        businessBooker: false,
      });
    });
  });
});
