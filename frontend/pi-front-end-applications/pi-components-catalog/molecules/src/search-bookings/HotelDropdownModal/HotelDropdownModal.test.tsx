import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import HotelDropdownModal from './HotelDropdownModal.component';

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  isSuccess: true,
  data: {
    hotelsLocations: [
      {
        brand: 'PI',
        distance: '0.73',
        hotelId: 'LONLEI',
        name: 'London Leicester Square',
      },
      {
        brand: 'HUB',
        distance: '0.91',
        hotelId: 'LONSTM',
        name: 'hub London Covent Garden',
      },
      {
        brand: 'HUB',
        distance: '1.35',
        hotelId: 'LONWAB',
        name: 'hub London Westminster Abbey',
      },
      {
        brand: 'HUB',
        distance: '1.37',
        hotelId: 'LONSOH',
        name: 'hub London Soho',
      },
      {
        brand: 'HUB',
        distance: '1.51',
        hotelId: 'LONWES',
        name: "hub London Westminster, St James's Park",
      },
      {
        brand: 'PI',
        distance: '1.83',
        hotelId: 'LONWAT',
        name: 'London Waterloo (Westminster Bridge)',
      },
      {
        brand: 'PI',
        distance: '1.99',
        hotelId: 'LONCOU',
        name: 'London County Hall',
      },
      {
        brand: 'HUB',
        distance: '2.14',
        hotelId: 'LONGOO',
        name: 'hub London Goodge Street',
      },
      {
        brand: 'PI',
        distance: '2.22',
        hotelId: 'LONBLA',
        name: 'London Blackfriars (Fleet Street)',
      },
    ],
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockResponse,
}));

const mockProps = {
  queryClient: new QueryClient(),
  isOpen: true,
  onClose: jest.fn(),
  paramsForQuery: {
    location: 'ChIJ2_UmUkxNekgRqmv-BDgUvtk',
    locationFormat: 'PLACEID',
    radius: '50',
    radiusUnit: 'mi',
  },
  handleSetValue: jest.fn(),
  onHotelSelected: jest.fn(),
};

describe('HotelDropdownModal', () => {
  it('should render HotelDropdownModal', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByTestId } = render(<HotelDropdownModal {...mockProps} />);
    expect(getByTestId('HotelDropdownModal-Container-ModalContent')).toBeInTheDocument();
  });

  it('should render the HotelDropdownModal title', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByTestId } = render(<HotelDropdownModal {...mockProps} />);
    expect(getByTestId('HotelDropdownModal-Container-ModalHeader')).toBeInTheDocument();
  });

  it('should have a Close button', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByRole } = render(<HotelDropdownModal {...mockProps} />);
    expect(getByRole('button', { name: 'Close' })).toBeInTheDocument();
  });

  it('should display a list of hotels', () => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });

    const { getByText, getByTestId } = render(<HotelDropdownModal {...mockProps} />);

    expect(getByTestId('HotelDropdownModal-ListContent')).toBeInTheDocument();
    expect(getByText('London Leicester Square')).toBeInTheDocument();
    expect(getByText('hub London Westminster Abbey')).toBeInTheDocument();
  });

  it('should close HotelDropdownModal when pressing Close btn', async () => {
    const { getByRole } = render(<HotelDropdownModal {...mockProps} />);
    const cancelBtn = getByRole('button', { name: 'Close' });
    fireEvent.click(cancelBtn);
    expect(mockProps.onClose).toBeCalledTimes(1);
  });
});
