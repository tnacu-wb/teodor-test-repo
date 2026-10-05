import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import {
  mockedAmendReservation,
  mockedHotelAvailabilityParams,
  mockednullishTitleAmendReservation,
  mockedRoomsAndGuestsLabels,
} from '../utilities/mockResponse';
import RoomInfoCard from './RoomInfoCard.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useGetDiscountRateComapnyId: () => jest.fn(),
}));

enum Area {
  PI = 'pi',
  CCUI = 'ccui',
  BB = 'bb',
}

const initialProps = {
  currencyCode: 'GBP',
  index: 1,
  labels: mockedRoomsAndGuestsLabels,
  language: 'en',
  data: mockedAmendReservation,
  roomRules: {
    roomOccupancyLimitations: {
      roomOccupancies: [],
    },
  },
  hotelAvailabilityParams: mockedHotelAvailabilityParams,
  baseDataTestId: 'amend',
  reservationsNumber: 2,
  isCancellable: true,
  isAmendable: true,
  onUpdateRoom: jest.fn(),
  onRemoveRoom: jest.fn(),
  onRoomTypeChange: jest.fn(),
  hotelCountry: 'United Kingdom (the)',
};

const Component = () => {
  return (
    <QueryClientProvider client={new QueryClient()}>
      <RoomInfoCard {...initialProps} />
    </QueryClientProvider>
  );
};

describe('Room Info Card', () => {
  it('should render the RoomInfoCard component', () => {
    const { getByText } = render(<Component />);

    expect(getByText('£45.00')).toBeInTheDocument();
  });
  it('should render the nameTitle uppercaseFirst', () => {
    const { getByText } = render(<Component />);

    expect(getByText('Mrs. Guest One Test')).toBeInTheDocument();
  });
  it('should not render the nameTitle if it is undefined or null', () => {
    const { getByText } = render(
      <RoomInfoCard {...initialProps} data={mockednullishTitleAmendReservation} />
    );

    expect(getByText('Guest One Test')).toBeInTheDocument();
  });
  it('should display the Edit modal', async () => {
    const { getByRole } = render(<Component />);
    const editBtn = getByRole('button', { name: 'Edit' });
    fireEvent.click(editBtn);
    await waitFor(() => {
      expect(getByRole('dialog')).toBeVisible();
    });
  });
  it('should close the Edit modal if the user clicks outside the modal', async () => {
    const { getByRole } = render(<Component />);
    const editBtn = getByRole('button', { name: 'Edit' });
    fireEvent.click(editBtn);
    await waitFor(() => {
      const editModal = getByRole('dialog');
      fireEvent.click(window);
      expect(editModal).not.toBeVisible();
    });
  });
  it('should close the Edit modal if the user clicks on the Cancel button', async () => {
    const { getByRole } = render(<Component />);
    const editBtn = getByRole('button', { name: 'Edit' });
    fireEvent.click(editBtn);
    await waitFor(() => {
      const editModal = getByRole('dialog');
      const cancelBtn = getByRole('button', { name: 'Cancel' });
      fireEvent.click(cancelBtn);
      expect(editModal).not.toBeVisible();
    });
  });
  it('should display the Remove button if the reservationsNumber is higher than 1', async () => {
    const { getByRole } = render(<Component />);
    const removeBtn = getByRole('button', { name: 'Remove' });
    fireEvent.click(removeBtn);
    await waitFor(() => {
      const removeModal = getByRole('dialog');
      const cancelBtn = getByRole('button', { name: 'Cancel' });
      fireEvent.click(cancelBtn);
      expect(removeModal).not.toBeVisible();
    });
  });

  it('should display the Remove button disabled when isCancellable is false', async () => {
    const { getByRole } = render(
      <QueryClientProvider client={new QueryClient()}>
        <RoomInfoCard {...initialProps} isCancellable={false} />
      </QueryClientProvider>
    );
    const removeBtn = getByRole('button', { name: 'Remove' });
    expect(removeBtn).toHaveAttribute('disabled');
  });

  it('should display the Edit button disabled when isCancellable is false', async () => {
    const { getByRole } = render(
      <QueryClientProvider client={new QueryClient()}>
        <RoomInfoCard {...initialProps} isCancellable={false} />
      </QueryClientProvider>
    );
    const removeBtn = getByRole('button', { name: 'Edit' });
    expect(removeBtn).toHaveAttribute('disabled');
  });

  it('should display the Edit button when isAmendable is true in CCUI', async () => {
    const { getByRole } = render(
      <QueryClientProvider client={new QueryClient()}>
        <RoomInfoCard {...initialProps} variant={Area.CCUI} />
      </QueryClientProvider>
    );
    const removeBtn = getByRole('button', { name: 'Edit' });
    expect(removeBtn).not.toHaveAttribute('disabled');
  });

  it('should display the Edit button disabled when isAmendable is false in CCUI', async () => {
    const { getByRole } = render(
      <QueryClientProvider client={new QueryClient()}>
        <RoomInfoCard {...initialProps} isAmendable={false} variant={Area.CCUI} />
      </QueryClientProvider>
    );
    const removeBtn = getByRole('button', { name: 'Edit' });
    expect(removeBtn).toHaveAttribute('disabled');
  });

  it('should display the Edit button disabled when isAmendable is true and not in CCUI', async () => {
    const { getByRole } = render(
      <QueryClientProvider client={new QueryClient()}>
        <RoomInfoCard {...initialProps} isCancellable={false} variant={Area.PI} />
      </QueryClientProvider>
    );
    const removeBtn = getByRole('button', { name: 'Edit' });
    expect(removeBtn).toHaveAttribute('disabled');
  });
});
