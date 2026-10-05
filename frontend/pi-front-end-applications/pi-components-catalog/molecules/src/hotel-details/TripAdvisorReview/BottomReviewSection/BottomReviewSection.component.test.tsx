import { fireEvent, render } from '../../../utils/test-utils';
import BottomReviewSection from './BottomReviewSection.component';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockUseTranslation = jest.fn();
const mockUseTranslationResponse = () => {
  return {
    t: (key: string) => {
      switch (key) {
        case 'search.new.premierInn':
          return 'Premier Inn';
        case 'hoteldetails.tripadvisorreviews.title':
          return 'Reviews for';
        case 'tripadvisor.show.reviews':
          return 'See reviews';
        case 'tripadvisor.hide.reviews':
          return 'Hide reviews';
        default:
          return '';
      }
    },
  };
};

const mockTripAdvisorData = {
  numberOfReviews: 2,
  webUrl: 'https://tripadvisor.com/reviews',
  writeReview: 'https://tripadvisor.com/write-review',
  reviews: [
    {
      publishedDate: '2023-01-01',
      rating: 4.5,
      user: { username: 'John Doe', location: 'London' },
      tripType: 'Business',
      title: 'Great stay',
      text: 'Very nice hotel!',
    },
  ],
  subRatings: [
    {
      localisedName: 'Cleanliness',
      ratingImageUrl: '/cleanliness.png',
    },
  ],
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => mockUseTranslation(),
}));

describe('TripAdvisorReviews', () => {
  beforeEach(() => {
    mockUseTranslation.mockReturnValue(mockUseTranslationResponse());
  });
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders nothing if tripAdvisorData is null', () => {
    const { container } = render(
      <BottomReviewSection tripAdvisorData={null} brand="pi" title="Hotel" />
    );
    expect(container).toBeEmptyDOMElement();
  });

  it('renders nothing if tripAdvisorData is empty object', () => {
    const { container } = render(
      <BottomReviewSection tripAdvisorData={{}} brand="pi" title="Hotel" />
    );
    expect(container).toBeEmptyDOMElement();
  });

  it('renders gracefully with no reviews or subRatings', () => {
    const data = { ...mockTripAdvisorData, reviews: [], subRatings: [] };
    const { getByTestId } = render(
      <BottomReviewSection tripAdvisorData={data} brand="pi" title="Hotel" />
    );
    expect(getByTestId('tripadvisor-user-review-section')).toBeInTheDocument();
    expect(getByTestId('subrating-section')).toBeInTheDocument();
  });

  it('renders with minimal tripAdvisorData', () => {
    const data = { numberOfReviews: 1 };
    const { getByTestId } = render(
      <BottomReviewSection tripAdvisorData={data} brand="pi" title="Hotel" />
    );
    expect(getByTestId('tripadvisor-user-review-section')).toBeInTheDocument();
  });

  it('renders the section and heading if tripAdvisorData is present', () => {
    const { getByTestId, getByRole } = render(
      <BottomReviewSection tripAdvisorData={mockTripAdvisorData} brand="pi" title="Hotel" />
    );
    expect(getByTestId('tripadvisor-bottom-review-section-hdp-Section')).toBeInTheDocument();
    expect(getByRole('heading', { level: 3 })).toHaveTextContent('Reviews for Hotel');
  });

  it('renders user review section', () => {
    const { getByTestId, getByText } = render(
      <BottomReviewSection tripAdvisorData={mockTripAdvisorData} brand="pi" title="Hotel" />
    );
    expect(getByTestId('tripadvisor-user-review-section')).toBeInTheDocument();
    expect(getByText(/John Doe/)).toBeInTheDocument();
    expect(getByText(/Great stay/)).toBeInTheDocument();
    expect(getByText(/Very nice hotel!/)).toBeInTheDocument();
  });

  it('renders subratings', () => {
    const { getByTestId, getByText } = render(
      <BottomReviewSection tripAdvisorData={mockTripAdvisorData} brand="pi" title="Hotel" />
    );
    expect(getByTestId('subrating-section')).toBeInTheDocument();
    expect(getByText('Cleanliness')).toBeInTheDocument();
  });

  it('toggles accordion title on section toggle', () => {
    const { getByTestId } = render(
      <BottomReviewSection tripAdvisorData={mockTripAdvisorData} brand="pi" title="Hotel" />
    );
    const accordionButton = getByTestId('Button-See_reviews');
    expect(accordionButton).toBeInTheDocument();

    fireEvent.click(accordionButton);

    expect(getByTestId('Button-Hide_reviews')).toBeInTheDocument();

    fireEvent.click(getByTestId('Button-Hide_reviews'));
    expect(getByTestId('Button-See_reviews')).toBeInTheDocument();
  });

  it('calls window.open when clicking the see reviews link', () => {
    window.open = jest.fn();
    const { getByTestId } = render(
      <BottomReviewSection tripAdvisorData={mockTripAdvisorData} brand="pi" title="Hotel" />
    );
    const seeReviewsLinks = getByTestId('tripadvisor-write-review-link');
    fireEvent.click(seeReviewsLinks);
    expect(window.open).toHaveBeenCalledWith(
      mockTripAdvisorData.writeReview,
      '_blank',
      'noopener,noreferrer'
    );
  });

  it('calls window.open when clicking the see reviews link inside accordion', () => {
    window.open = jest.fn();
    const { getByTestId } = render(
      <BottomReviewSection tripAdvisorData={mockTripAdvisorData} brand="pi" title="Hotel" />
    );

    fireEvent.click(getByTestId('Button-See_reviews'));
    fireEvent.click(getByTestId('tripadvisor-see-reviews-link'));
    expect(window.open).toHaveBeenCalledWith(
      mockTripAdvisorData.webUrl,
      '_blank',
      'noopener,noreferrer'
    );
  });
});
