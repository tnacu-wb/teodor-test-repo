import '@testing-library/jest-dom';
import { useStaticHotelInformation } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import { HotelBreadcrumb } from './HotelBreadcrumb';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: jest.fn(),
}));

const mockUseStaticHotelInformation = useStaticHotelInformation as jest.Mock;

const breadcrumbItems = [
  { title: 'Home', link: '/gb/en/home.html' },
  { title: 'Hotel Directory', link: '/gb/en/hotels.html' },
  { title: 'England', link: '/gb/en/hotels/england.html' },
  { title: 'West Midlands', link: '/gb/en/hotels/england/west-midlands.html' },
  { title: 'Birmingham', link: '/gb/en/hotels/england/west-midlands/birmingham.html' },
  { title: 'Birmingham South Hall Green', link: '' },
];

describe('Hotel Breadcrumb', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue({
      breadcrumb: breadcrumbItems,
      isLoading: false,
      isError: false,
      error: undefined,
    });
  });

  it('Should render the breadcrumb in HDP', () => {
    const { getByTestId, getByText } = render(<HotelBreadcrumb />);
    expect(getByTestId('breadcrumbs-hdp')).toBeInTheDocument();
    expect(getByText('Home')).toBeInTheDocument();
    expect(getByText('Hotel Directory')).toBeInTheDocument();
    expect(getByText('England')).toBeInTheDocument();
    expect(getByText('West Midlands')).toBeInTheDocument();
    expect(getByText('Birmingham')).toBeInTheDocument();
  });

  it('Should not render breadcrumb if breadcrumb prop is empty', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      breadcrumb: [],
      isLoading: false,
      isError: false,
      error: undefined,
    });
    const { queryByTestId } = render(<HotelBreadcrumb />);
    expect(queryByTestId('breadcrumbs-hdp')).not.toBeInTheDocument();
  });

  it('should render a loading message, if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      breadcrumb: undefined,
      isLoading: true,
      isError: false,
      error: undefined,
    });
    const { getByText } = render(<HotelBreadcrumb />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render error message, if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      breadcrumb: undefined,
      isLoading: false,
      isError: true,
      error: { message: 'Error' },
    });
    const { getByText } = render(<HotelBreadcrumb />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('Should render Breadcrumbs', async () => {
    const { getByTestId } = render(<HotelBreadcrumb />);
    expect(getByTestId('breadcrumbs-hdp')).toBeInTheDocument();
  });

  it('Should not render breadcrumb if breadcrumb prop is null', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      breadcrumb: null,
      isLoading: false,
      isError: false,
      error: undefined,
    });
    const { queryByTestId } = render(<HotelBreadcrumb />);
    expect(queryByTestId('breadcrumbs-hdp')).not.toBeInTheDocument();
  });
});
