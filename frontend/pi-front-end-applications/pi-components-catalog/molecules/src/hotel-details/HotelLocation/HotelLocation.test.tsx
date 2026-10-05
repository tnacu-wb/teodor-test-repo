import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelLocation from './HotelLocation';

const mockUseCustomLocale = jest.fn();
const mockUseLocalStorage = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockUseCustomLocale(),
  useLocalStorage: (...args: unknown[]) => mockUseLocalStorage(...args),
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

function setupMocks({
  language = 'en',
  distance = 1.82,
  referrer = 'search.html',
}: { language?: string; distance?: number | null; referrer?: string } = {}) {
  mockUseCustomLocale.mockReturnValue({ language });
  mockUseLocalStorage.mockImplementation((key: string) => {
    if (key === 'DistanceFromSearch') {
      return [distance != null ? { data: { distance } } : {}];
    }
    if (key === 'SearchReferrer') {
      return [{ data: { referrer } }];
    }
    return [{}];
  });
}

describe('HotelLocation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    setupMocks();
  });

  it('should render the HotelLocationContainer', () => {
    setupMocks({ distance: null, referrer: '' });
    const { queryByTestId } = render(<HotelLocation />);
    expect(queryByTestId('hdp_hotelDistanceFromSearch')).toBeNull();
  });

  it('should render the hotel distance in miles for en language', () => {
    const { queryByText } = render(<HotelLocation />);
    expect(queryByText(/1.8 miles/)).toBeInTheDocument();
    expect(queryByText(/1.8 km/)).not.toBeInTheDocument();
  });

  it('should render the hotel distance in kilometres for de locale', () => {
    setupMocks({ language: 'de', distance: 2.9 });
    const { queryByText } = render(<HotelLocation />);
    expect(queryByText(/2.9 km/)).toBeInTheDocument();
    expect(queryByText(/2.9 miles/)).not.toBeInTheDocument();
  });

  it('should round the hotel distance in miles to 1 decimal place', () => {
    setupMocks({ distance: 1.82 });
    const { queryByText } = render(<HotelLocation />);
    expect(queryByText(/1.8 miles/)).toBeInTheDocument();
    expect(queryByText(/1.82 miles/)).not.toBeInTheDocument();
  });

  it('should render null if no distance data prop is passed', () => {
    setupMocks({ distance: null });
    const { queryByTestId } = render(<HotelLocation />);
    expect(queryByTestId('hdp_hotelDistanceFromSearch')).toBeNull();
  });

  it('should render null if referrer is not from search (srp)', () => {
    setupMocks({ referrer: 'home.html' });
    const { queryByTestId } = render(<HotelLocation />);
    expect(queryByTestId('hdp_hotelDistanceFromSearch')).toBeNull();
  });
});
