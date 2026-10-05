import { ChakraProvider } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import DefaultLayout from './RestaurantLayout';

// Mock the useQueryRequest hook and its response
jest.mock('@whitbread-eos/utils/restaurants', () => ({
  ...jest.requireActual('@whitbread-eos/utils/restaurants'),
  useQueryRequest: jest.fn(() => ({
    data: null,
    isLoading: false,
    isError: false,
    error: null,
  })),
}));
const mockUseQueryRequest = jest.requireMock('@whitbread-eos/utils/restaurants');

interface Props {
  occasionId: string;
  siteId: string;
  restaurantBrandName: string;
  locationName?: string;
  subLocationName?: string;
  restaurantBrandNameForAemApi: string;
}
const mockProps: Props = {
  siteId: '123',
  occasionId: '456',
  restaurantBrandNameForAemApi: 'table-table',
  restaurantBrandName: 'tabletable',
};

const AnotherComponent = ({ children }: any) => {
  const { locationName } = children.props.pageProps;
  return (
    <div>
      <p>Location Name: {locationName}</p>
    </div>
  );
};

describe('children component within DefaultLayout', () => {
  it('renders children within defaultLayout', () => {
    mockUseQueryRequest.useQueryRequest
      .mockReturnValueOnce({
        data: {
          bookPage: {
            heroBackgroundImageSrc: '/path/to/hero-background-image.jpg',
            subtitleName: 'Subtitle',
            name: 'Book Page Name',
            heroImageSrc: '/path/to/hero-image.jpg',
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          locations: [
            {
              googleMapURL:
                'https://www.google.com/maps/place/The+Millfield+Beefeater/@53.9802955,-1.1351364,17z/data=!3m1!4b1!4m5!3m4!1s0x4879314e140566c1:0x6bed277edd338f22!8m2!3d53.9802735!4d-1.1329342',
              path: '/en-gb/locations/new-york',
              title: 'New York Location',
              contactInfo: '123 Main St, New York, NY',
            },
          ],
        },
        isLoading: false,
        isError: false,
        error: null,
      });

    const queryClient = new QueryClient();
    const { getByText } = render(
      <ChakraProvider>
        <QueryClientProvider client={queryClient}>
          <DefaultLayout>
            <AnotherComponent
              {...{
                children: { props: { pageProps: { ...mockProps, locationName: 'location' } } },
              }}
            />
          </DefaultLayout>
        </QueryClientProvider>
      </ChakraProvider>
    );
    expect(getByText('Location Name: location')).toBeInTheDocument();
  });

  it('renders children components within defaultLayout with locationName', () => {
    mockUseQueryRequest.useQueryRequest
      .mockReturnValueOnce({
        data: {
          bookPage: {
            heroBackgroundImageSrc: '/path/to/hero-background-image.jpg',
            subtitleName: 'Subtitle',
            name: 'Book Page Name',
            heroImageSrc: '/path/to/hero-image.jpg',
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          locations: [
            {
              googleMapURL:
                'https://www.google.com/maps/place/The+Millfield+Beefeater/@53.9802955,-1.1351364,17z/data=!3m1!4b1!4m5!3m4!1s0x4879314e140566c1:0x6bed277edd338f22!8m2!3d53.9802735!4d-1.1329342',
              path: '/en-gb/locations/new-york',
              title: 'New York Location',
              contactInfo: '123 Main St, New York, NY',
            },
          ],
        },
        isLoading: false,
        isError: false,
        error: null,
      });

    const queryClient = new QueryClient();
    const { getByText } = render(
      <ChakraProvider>
        <QueryClientProvider client={queryClient}>
          <DefaultLayout>
            <AnotherComponent
              {...{
                children: { props: { pageProps: { ...mockProps, locationName: 'location' } } },
              }}
            />
          </DefaultLayout>
        </QueryClientProvider>
      </ChakraProvider>
    );
    expect(getByText('Location Name: location')).toBeInTheDocument();
  });

  it('renders children components within defaultLayout with subLocationName', () => {
    mockUseQueryRequest.useQueryRequest
      .mockReturnValueOnce({
        data: {
          bookPage: {
            heroBackgroundImageSrc: '/path/to/hero-background-image.jpg',
            subtitleName: 'Subtitle',
            name: 'Book Page Name',
            heroImageSrc: '/path/to/hero-image.jpg',
          },
        },
        isLoading: false,
        isError: false,
        error: null,
      })
      .mockReturnValueOnce({
        data: {
          locations: [
            {
              googleMapURL:
                'https://www.google.com/maps/place/The+Millfield+Beefeater/@53.9802955,-1.1351364,17z/data=!3m1!4b1!4m5!3m4!1s0x4879314e140566c1:0x6bed277edd338f22!8m2!3d53.9802735!4d-1.1329342',
              path: '/en-gb/locations/los-angeles',
              title: 'Los Angeles Location',
              contactInfo: '456 Elm St, Los Angeles, CA',
            },
          ],
        },
        isLoading: false,
        isError: false,
        error: null,
      });

    const queryClient = new QueryClient();
    const { container } = render(
      <ChakraProvider>
        <QueryClientProvider client={queryClient}>
          <DefaultLayout>
            <AnotherComponent
              {...{
                children: {
                  props: { pageProps: { ...mockProps, subLocationName: 'subLocation' } },
                },
              }}
            />
          </DefaultLayout>
        </QueryClientProvider>
      </ChakraProvider>
    );
    expect(container).toBeTruthy();
  });
});
