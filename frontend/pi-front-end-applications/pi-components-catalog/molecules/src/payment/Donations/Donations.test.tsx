import '@testing-library/jest-dom';
import { GET_DONATIONS_QUERY } from '@whitbread-eos/api';
import { formatDataTestId, useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import getConfig from 'next/config';

import { render, fireEvent } from '../../utils/test-utils';
import Donations from './Donations';

const baseDataTestId = 'Donation';

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: jest.fn(),
  useQueryRequest: jest.fn(),
}));

const mockedUseCustomLocale = useCustomLocale as jest.Mock;
const mockedUseQueryRequest = useQueryRequest as jest.Mock;

const mockBookingInformation = {
  hotelId: '12345',
  ratePlanCode: 'RATE123',
};

const donationPackages = [
  { code: 'A', currency: 'GBP', unitPrice: 5 },
  { code: 'B', currency: 'GBP', unitPrice: 10 },
];

const donationsData = {
  donations: {
    name: 'Support Charity',
    imageSrc: '/img.png',
    description: '<b>desc</b>',
    informationBox: '',
    donationPackages,
  },
};

const mockedProps = {
  selectedDonation: { code: 'ZCHRY3', currency: 'GBP', unitPrice: 5 },
  onDonationChange: jest.fn(),
  bookingInformation: mockBookingInformation,
  bookingChannel: 'PI',
};

describe('Payment - Donations', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockedUseCustomLocale.mockReturnValue({ language: 'en', country: 'GB' });
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        NEXT_IMAGE_UNOPTIMIZED: 'true',
      },
    }));
  });

  it('should display the Donations component', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: donationsData,
      isLoading: false,
      isError: false,
      error: undefined,
    });

    const { getByTestId, getByText } = render(<Donations {...mockedProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    expect(getByText(donationsData.donations.name)).toBeInTheDocument();
  });

  it('should invoke query with correct key and variables', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: donationsData,
      isLoading: false,
      isError: false,
      error: undefined,
    });

    render(<Donations {...mockedProps} />);

    expect(mockedUseQueryRequest).toHaveBeenCalledWith(
      [
        'GetDonations',
        mockBookingInformation.hotelId,
        'GB',
        'en',
        mockBookingInformation.ratePlanCode,
        'PI',
      ],
      GET_DONATIONS_QUERY,
      {
        country: 'GB',
        language: 'en',
        hotelId: mockBookingInformation.hotelId,
        rateCode: mockBookingInformation.ratePlanCode,
        bookingChannel: 'PI',
      }
    );
  });

  it('should display message if loading', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
      error: undefined,
    });

    const { getByTestId } = render(<Donations {...mockedProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'LoadingMessage'))).toBeInTheDocument();
  });

  it('should render nothing if donations is undefined', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: {},
      isLoading: false,
      isError: false,
      error: undefined,
    });

    const { container } = render(<Donations {...mockedProps} />);
    expect(container.firstChild).toBeNull();
  });

  it('should display error message if query fails', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      error: new Error('Query failed'),
    });

    const { getByTestId } = render(<Donations {...mockedProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Error'))).toBeInTheDocument();
    expect(getByTestId(formatDataTestId(baseDataTestId, 'ErrorMessage'))).toBeInTheDocument();
  });

  it('should call onDonationChange when "No donation" is clicked', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: donationsData,
      isLoading: false,
      isError: false,
      error: undefined,
    });

    const onDonationChange = jest.fn();
    const { getByTestId } = render(
      <Donations
        {...mockedProps}
        onDonationChange={onDonationChange}
        selectedDonation={donationPackages[0]}
      />
    );
    fireEvent.click(getByTestId(`${baseDataTestId}-DonationRadio-0`));
    expect(onDonationChange).toHaveBeenCalledWith({
      code: '',
      currency: '',
      unitPrice: 0,
    });
  });

  it('should call onDonationChange with correct donation package when clicked', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: donationsData,
      isLoading: false,
      isError: false,
      error: undefined,
    });

    const onDonationChange = jest.fn();
    const { getByTestId } = render(
      <Donations
        {...mockedProps}
        onDonationChange={onDonationChange}
        selectedDonation={donationPackages[0]}
      />
    );
    fireEvent.click(getByTestId(`${baseDataTestId}-DonationRadio-1`));
    expect(onDonationChange).toHaveBeenCalledWith(donationPackages[0]);
  });

  it('should render all donation options', () => {
    mockedUseQueryRequest.mockReturnValue({
      data: donationsData,
      isLoading: false,
      isError: false,
      error: undefined,
    });

    const { getByTestId } = render(<Donations {...mockedProps} />);
    expect(getByTestId(`${baseDataTestId}-DonationRadio-0`)).toBeInTheDocument();
    expect(getByTestId(`${baseDataTestId}-DonationRadio-1`)).toBeInTheDocument();
    expect(getByTestId(`${baseDataTestId}-DonationRadio-2`)).toBeInTheDocument();
  });
});
