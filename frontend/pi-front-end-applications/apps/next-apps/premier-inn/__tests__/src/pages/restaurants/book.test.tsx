import '@testing-library/jest-dom';
import { GetServerSidePropsContext } from 'next';

import { getRestaurantInitialContentDataFn } from '~page-helper/restaurants/getRestaurantInitialContentData.data';
import BookingDetailsPage, {
  getServerSideProps,
} from '~pages/restaurants/[location]/[sublocation]/book';
import { render, screen } from '~utils/test-utils';

jest.mock('~page-helper/restaurants/getRestaurantInitialContentData.data', () => ({
  getRestaurantInitialContentDataFn: jest.fn(),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

describe('Booking page with restaurantState', () => {
  it('should render booking details page', () => {
    const props = {
      restaurantBrandName: 'restaurant-premierinn',
      restaurantBrandNameForAemApi: 'restaurant-premierinn',
      restaurantBrandNameForMarketing: 'Restaurant Premier Inn',
      locationName: '',
      subLocationName: 'the-acorn',
      occasionId: '30b545b7-bd1b-43ae-bc96-4520b04be8e6',
      siteId: 'b62be154-a301-49bb-9f00-edcee8a6fe9f',
    };
    render(<BookingDetailsPage pageProps={props} />);
    const loadingSpinner = screen.getByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();
  });

  it('should return expected props if brandName is allowed', async () => {
    (getRestaurantInitialContentDataFn as jest.Mock).mockResolvedValueOnce({
      occasionId: 'occasionId',
      siteId: 'siteId',
    });

    const context: GetServerSidePropsContext = {
      query: {
        brandName: 'restaurant-premierinn',
        restaurantState: 'location',
        restaurantArea: 'subLocation',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSideProps(context);

    expect(result).toEqual({
      props: {
        restaurantBrandName: 'restaurant-premierinn',
        restaurantBrandNameForAemApi: 'restaurant-premierinn',
        restaurantBrandNameForMarketing: 'Restaurant Premier Inn',
        locationName: '',
        subLocationName: '',
        occasionId: 'occasionId',
        siteId: 'siteId',
        dehydratedState: undefined,
      },
    });
  });

  it('should return expected props if brandName is allowed withiut location and sublocation', async () => {
    (getRestaurantInitialContentDataFn as jest.Mock).mockResolvedValueOnce({
      occasionId: 'occasionId',
      siteId: 'siteId',
    });

    const context: GetServerSidePropsContext = {
      query: {
        brandName: 'restaurant-premierinn',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSideProps(context);

    expect(result).toEqual({
      props: {
        restaurantBrandName: 'restaurant-premierinn',
        restaurantBrandNameForAemApi: 'restaurant-premierinn',
        restaurantBrandNameForMarketing: 'Restaurant Premier Inn',
        locationName: '',
        subLocationName: '',
        occasionId: 'occasionId',
        siteId: 'siteId',
      },
    });
  });

  it('should return expected props if same brandName as an array', async () => {
    (getRestaurantInitialContentDataFn as jest.Mock).mockResolvedValueOnce({
      occasionId: 'occasionId',
      siteId: 'siteId',
    });

    const context: GetServerSidePropsContext = {
      query: {
        brandName: ['restaurant-premierinn', 'restaurant-premierinn'],
        restaurantState: 'location',
        restaurantArea: 'subLocation',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSideProps(context);

    expect(result).toEqual({
      props: {
        restaurantBrandName: 'restaurant-premierinn',
        restaurantBrandNameForAemApi: 'restaurant-premierinn',
        restaurantBrandNameForMarketing: 'Restaurant Premier Inn',
        locationName: '',
        subLocationName: '',
        dehydratedState: undefined,
        occasionId: 'occasionId',
        siteId: 'siteId',
      },
    });
  });

  it('should set notFound if resourceNotFound true', async () => {
    (getRestaurantInitialContentDataFn as jest.Mock).mockResolvedValueOnce({
      occasionId: 'occasionId',
      siteId: 'siteId',
      resourceNotFound: true,
    });
    const context: GetServerSidePropsContext = {
      query: {
        brandName: 'restaurant-premierinn',
        restaurantState: 'location',
        restaurantArea: 'subLocation',
      },
      req: {} as any, // Mock the request object
      res: {} as any, // Mock the response object
      resolvedUrl: '', // Mock the resolved URL
    };

    const result = await getServerSideProps(context);

    expect(result).toEqual({
      notFound: true,
    });
  });
});
