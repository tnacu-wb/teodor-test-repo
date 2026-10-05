import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import {
  mockedHotelAvailabilityParams,
  mockedRoomRules,
  mockedRoomsAndGuestsData,
  mockedRoomsAndGuestsLabels,
} from '../utilities/mockResponse';
import RoomsAndGuests from './RoomsAndGuests.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useGetDiscountRateComapnyId: () => jest.fn(),
}));

const Component = () => {
  const initialProps = {
    baseDataTestId: 'amend',
    labels: mockedRoomsAndGuestsLabels,
    language: 'en',
    data: mockedRoomsAndGuestsData,
    roomRules: mockedRoomRules,
    hotelAvailabilityParams: mockedHotelAvailabilityParams,
    maxRooms: 4,
    onSaveNewRoom: jest.fn(),
    onUpdateRoom: jest.fn(),
    onRemoveRoom: jest.fn(),
    variant: 'default',
    isCancellable: true,
    hotelCountry: 'United Kingdom (the)',
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <RoomsAndGuests {...initialProps} />
    </QueryClientProvider>
  );
};

describe('Rooms and Guests section', () => {
  it('should render the Rooms and Guests section', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId('amend-rooms-and-guests-section')).toBeInTheDocument();
  });
});
