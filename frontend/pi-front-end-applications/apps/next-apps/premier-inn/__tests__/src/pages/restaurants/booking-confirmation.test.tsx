import '@testing-library/jest-dom';
import { GetServerSidePropsContext } from 'next';

import ConfirmationPage, {
  getServerSideProps,
} from '~pages/restaurants/[location]/[sublocation]/booking-confirmation';
import { render, screen } from '~utils/test-utils';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

jest.mock('~page-helper/restaurants/getBookingConfirmationDetails.data', () => ({
  getBookingConfirmationDetailsDatFn: jest.fn(),
}));

const mockUseQueryRequest = jest.requireMock(
  '~page-helper/restaurants/getBookingConfirmationDetails.data'
);

const commonProps = {
  eventId: '1234',
  enquiryId: '5678',
};

const renderConfirmationPage = (props: any) => {
  render(<ConfirmationPage {...props} />);
  expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
};

const getServerSidePropsWithContext = async (context: GetServerSidePropsContext) => {
  const mockGetBookingConfirmationDetailsDatFn = jest.fn();
  mockUseQueryRequest.getBookingConfirmationDetailsDatFn.mockImplementation(
    mockGetBookingConfirmationDetailsDatFn
  );

  return getServerSideProps(context);
};

describe('ConfirmationPage page with restaurantState', () => {
  it('should render booking confimation page with eventId', () => {
    renderConfirmationPage({ ...commonProps, enquiryId: '' });
    const loadingSpinner = screen.getByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();
  });
  it('should render booking confimation page with enquiryId', () => {
    renderConfirmationPage({ ...commonProps, eventId: '' });
    const loadingSpinner = screen.getByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();
  });
  it('should render booking confimation page without enquiryId and eventId', () => {
    renderConfirmationPage({ eventId: null, enquiryId: null });
    const loadingSpinner = screen.getByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();
  });

  it('should fetch data and return props if brandName is allowed', async () => {
    const context = {
      query: {
        event: '1234',
        enquiry: '5678',
        brandName: 'restaurant-premierinn',
        restaurantState: '',
        restaurantArea: '',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSidePropsWithContext(context);
    expect(result.props).toEqual({
      ...commonProps,
      restaurantBrandName: 'restaurant-premierinn',
      restaurantBrandNameForAemApi: 'restaurant-premierinn',
      locationName: '',
      subLocationName: '',
    });

    expect(result.props).toEqual({
      ...commonProps,
      restaurantBrandName: 'restaurant-premierinn',
      restaurantBrandNameForAemApi: 'restaurant-premierinn',
      locationName: '',
      subLocationName: '',
    });
  });
  it('should return expected props if eventId, enquiryId,location and subLocation is null', async () => {
    const context = {
      query: {
        event: '',
        enquiry: '',
        brandName: 'restaurant-premierinn',
        restaurantState: '',
        restaurantArea: '',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSidePropsWithContext(context);
    expect(result.props).toEqual({
      eventId: '',
      enquiryId: '',
      restaurantBrandName: 'restaurant-premierinn',
      restaurantBrandNameForAemApi: 'restaurant-premierinn',
      locationName: '',
      subLocationName: '',
    });
  });
  it('should return expected props if eventIdand enquiryId is null', async () => {
    const context = {
      query: {
        brandName: 'restaurant-premierinn',
        restaurantState: '',
        restaurantArea: '',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSidePropsWithContext(context);
    expect(result.props).toEqual({
      eventId: null,
      enquiryId: null,
      restaurantBrandName: 'restaurant-premierinn',
      restaurantBrandNameForAemApi: 'restaurant-premierinn',
      locationName: '',
      subLocationName: '',
    });
  });
  it('should return expected props if eventId, enquiryId,location and subLocation is not null', async () => {
    const context = {
      query: {
        event: '1234',
        enquiry: '5678',
        brandName: 'restaurant-premierinn',
        restaurantState: '',
        restaurantArea: '',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSidePropsWithContext(context);
    expect(result.props).toEqual({
      ...commonProps,
      restaurantBrandName: 'restaurant-premierinn',
      restaurantBrandNameForAemApi: 'restaurant-premierinn',
      locationName: '',
      subLocationName: '',
    });
  });
  it('should return expected props if same brandName as an array', async () => {
    const context = {
      query: {
        event: '1234',
        enquiry: '5678',
        brandName: ['restaurant-premierinn', 'restaurant-premierinn'],
        restaurantState: 'location',
        restaurantArea: 'subLocation',
      },
      req: {} as any,
      res: {} as any,
      resolvedUrl: '',
    };

    const result = await getServerSidePropsWithContext(context);
    expect(result.props).toEqual({
      ...commonProps,
      restaurantBrandName: 'restaurant-premierinn',
      restaurantBrandNameForAemApi: 'restaurant-premierinn',
      locationName: '',
      subLocationName: '',
    });
  });
});
