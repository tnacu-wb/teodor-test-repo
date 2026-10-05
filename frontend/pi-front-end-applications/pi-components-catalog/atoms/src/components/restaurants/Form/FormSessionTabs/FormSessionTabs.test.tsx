import '@testing-library/jest-dom';
import { useForm } from 'react-hook-form';

import { fireEvent, render, screen } from '../../../../utils/test-utils';
import FormSessionTabs from './FormSessionTabs.component';

const onChangeMock = jest.fn();

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

// Mock the useQueryRequest hook and its response
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequestRestaurants: jest.fn(),
}));

// Custom type definition for the mock
const mockUseQueryRequestRestaurants = jest.requireMock('@whitbread-eos/utils');

const GetSessionComponentProps = (isEnquiry: boolean) => {
  const { control, clearErrors } = useForm();
  return {
    control: control,
    field: { onChange: onChangeMock },
    formField: {
      name: 'time',
      testid: 'TableBooking-SessionTabs',
    },
    clearErrors: clearErrors,
    errors: {},
    setValue: jest.fn(),
    getValues: jest.fn(),
    isEnquiry: isEnquiry,
  };
};

const SessionComponentWithIsEnquiry = () => {
  const props = GetSessionComponentProps(true);
  return <FormSessionTabs {...props} />;
};

const SessionComponentWithoutIsEnquiry = () => {
  const props = GetSessionComponentProps(false);
  return <FormSessionTabs {...props} />;
};

describe('Session Form Component', () => {
  beforeEach(() => {
    mockUseQueryRequestRestaurants.useQueryRequestRestaurants.mockReturnValue({
      data: {
        slots: {
          dates: [
            {
              breakFastAvailable: false,
              lunchAvailable: true,
              dinnerAvailable: true,
              date: '2023-09-25',
              sessionDto: {
                dinner: [
                  {
                    time: '19:00',
                    available: true,
                    totalCapacity: 30,
                    remainingCapacity: 30,
                    canEnquire: true,
                    closed: false,
                  },
                ],
                lunch: [
                  {
                    time: '12:00',
                    available: false,
                    totalCapacity: 20,
                    remainingCapacity: 17,
                    canEnquire: true,
                    closed: false,
                  },
                ],
                breakFast: [
                  {
                    time: '06:30',
                    available: false,
                    totalCapacity: 28,
                    remainingCapacity: 28,
                    canEnquire: true,
                    closed: false,
                  },
                ],
              },
            },
          ],
        },
      },
      isLoading: false,
      isError: false,
      error: null,
    });
  });
  it('should render loader', () => {
    mockUseQueryRequestRestaurants.useQueryRequestRestaurants.mockReturnValue({
      data: {
        slots: {
          dates: [],
        },
      },
      isLoading: true,
      isError: false,
      error: null,
    });
    render(<SessionComponentWithIsEnquiry />);
    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });
  it('should render the component with isEnquiry', () => {
    render(<SessionComponentWithIsEnquiry />);
    expect(screen.getByTestId('TableBooking-SessionTabs')).toBeInTheDocument();
  });
  it('should render the component without isEnquiry', () => {
    render(<SessionComponentWithoutIsEnquiry />);
    expect(screen.getByTestId('TableBooking-SessionTabs')).toBeInTheDocument();
  });
  it('should render the component with null data', () => {
    mockUseQueryRequestRestaurants.useQueryRequestRestaurants.mockReturnValue({
      data: {
        slots: {
          dates: [],
        },
      },
      isLoading: false,
      isError: false,
      error: null,
    });
    render(<SessionComponentWithoutIsEnquiry />);
    expect(screen.getByTestId('TableBooking-SessionTabs')).toBeInTheDocument();
  });
  it('should click on the session button', async () => {
    const { getByRole } = render(<SessionComponentWithIsEnquiry />);
    const breakFastBtn = getByRole('heading', { name: /Dinner/i });
    fireEvent.click(breakFastBtn);

    const timeBtn = getByRole('button', { name: /19:00/i });
    fireEvent.click(timeBtn);
    expect(screen.getByTestId('TableBooking-SessionTabs')).toBeInTheDocument();
  });
});
