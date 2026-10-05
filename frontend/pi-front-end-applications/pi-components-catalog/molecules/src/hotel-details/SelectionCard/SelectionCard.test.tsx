import { render, screen, fireEvent } from '@testing-library/react';
import { Currency } from '@whitbread-eos/api';

import SelectionCard from './SelectionCard.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const mockData = {
  roomTypeCode: 'DB',
  roomLabel: 'Standard Room',
  roomDescription: 'A super-comfy bed, a power shower and free Wi-Fi',
  roomImage: '/test-image.jpg',
};

const mockPrice = {
  currency: Currency.GBP_NAME,
  amount: 75,
};

const defaultProps = {
  data: mockData,
  activePmsRoomType: '',
  onHandleClick: jest.fn(),
  price: mockPrice,
};

describe('RoomSelectionRateCard', () => {
  it('renders the room image', () => {
    render(<SelectionCard {...defaultProps} />);
    const image = screen.getByAltText('Standard room');
    expect(image).toBeInTheDocument();
  });

  it('renders the room name', () => {
    render(<SelectionCard {...defaultProps} />);
    expect(screen.getByText('Standard Room')).toBeInTheDocument();
  });

  it('renders the description', () => {
    render(<SelectionCard {...defaultProps} />);
    expect(
      screen.getByText(/A super-comfy bed, a power shower and free Wi-Fi/i)
    ).toBeInTheDocument();
  });

  it('renders the price and nights', () => {
    render(<SelectionCard {...defaultProps} />);
    expect(screen.getByText('1 night')).toBeInTheDocument();
    expect(screen.getByText(/£ 75/)).toBeInTheDocument();
  });

  it('renders the select room button', () => {
    render(<SelectionCard {...defaultProps} />);
    expect(screen.getByTestId('Room-Selection-Rate-Card-SelectRoomButton')).toBeInTheDocument();
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  it('select room button calls onHandleClick with correct code', () => {
    render(<SelectionCard {...defaultProps} />);
    const button = screen.getByTestId('Room-Selection-Rate-Card-SelectRoomButton');
    fireEvent.click(button);
    expect(defaultProps.onHandleClick).toHaveBeenCalledWith('DB');
  });

  it('shows selected state when activePmsRoomType matches roomTypeCode', () => {
    render(<SelectionCard activePmsRoomType="DB" {...defaultProps} />);
    expect(screen.getByText('Selected')).toBeInTheDocument();
    expect(screen.getByTestId('Room-Selection-Rate-Card-SelectRoomButton')).toHaveClass(
      'chakra-button'
    );
  });

  it('renders with empty description', () => {
    const emptyDescData = { ...mockData, roomDescription: '' };
    render(<SelectionCard data={emptyDescData} {...defaultProps} />);
    expect(screen.getByText('Standard Room')).toBeInTheDocument();
  });

  it('renders with EUR currency', () => {
    render(<SelectionCard {...defaultProps} price={{ currency: Currency.EUR_NAME, amount: 99 }} />);
    expect(screen.getByText(/€ 99/)).toBeInTheDocument();
  });
});
