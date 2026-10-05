import '@testing-library/jest-dom';
import { act, fireEvent, waitFor } from '@testing-library/react';
import { ScreenSizeValues } from '@whitbread-eos/api';
import { format } from 'date-fns';

import { render } from '../../../utils/test-utils';
import BookingHistoryFilter from './BookingHistoryFilter';
import { setFindButtonStyles } from './BookingHistoryFilter.style';

const mockClearButton = jest.fn();
const mockFilterProps = {
  t: jest.fn(),
  onFind: jest.fn(),
  onClear: mockClearButton,
  baseTestId: 'booking-history',
};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

enum FILTER_OPTIONS {
  ARRIVAL = 'ARRIVAL_DATE',
  SURNAME = 'NAME',
  BOOKING_REFERENCE = 'CONFIRM_NUMBER',
}

const screenSize: ScreenSizeValues = {
  isLessThanMobile: false,
  isLessThanXs: false,
  isLessThanSm: false,
  isLessThanMd: false,
  isLessThanLg: false,
  isLessThanXl: false,
};

describe('Unit test style', () => {
  it('should return for w: lg 10.625rem on en lang', () => {
    expect(setFindButtonStyles('en').w.lg).toBe('10.625rem');
  });

  it('should return for w: lg 9.625rem on en lang', () => {
    expect(setFindButtonStyles('de').w.lg).toBe('9.625rem');
  });
});

describe('Booking History Filter tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('Should find the input, datepicker and the find and clear buttons', () => {
    const { getByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    expect(getByTestId('input-bookingFilter')).toBeInTheDocument();
    expect(getByTestId('booking-history-datePicker')).toBeInTheDocument();
    expect(getByTestId('booking-history-findButton')).toBeInTheDocument();
    expect(getByTestId('booking-history-clearButton')).toBeInTheDocument();
  });

  it('Should find the search button in initial state as disabled', () => {
    const { getByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    expect(getByTestId('booking-history-findButton')).toBeDisabled();
  });

  it('Should enable the find button when input value has at least 1 character and make datepicker disabled', async () => {
    const { getByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    act(() => {
      fireEvent.change(getByTestId('input-bookingFilter'), { target: { value: 'AWB1234' } });
    });
    expect(getByTestId('booking-history-findButton')).not.toBeDisabled();
    expect(getByTestId('SingleDatePicker')).toBeDisabled();
  });

  it('Should enable the datepicker if the input value is empty', async () => {
    const { getByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    act(() => {
      fireEvent.change(getByTestId('input-bookingFilter'), { target: { value: '' } });
    });

    expect(getByTestId('SingleDatePicker')).toBeEnabled();
  });

  it('Should enable the find button when a date is selected from datepicker and make the bookingRef input disabled', async () => {
    const { getByTestId } = render(<BookingHistoryFilter {...mockFilterProps} />);
    const input = getByTestId('SingleDatePicker');
    act(() => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });
    waitFor(() => {
      fireEvent.mouseDown(document.body);
    });
    expect(getByTestId('booking-history-findButton')).not.toBeDisabled();
    expect(getByTestId('input-bookingFilter')).toBeDisabled();
  });

  it('Should clear the booking ref input when clear button is clicked', async () => {
    const { getByTestId } = render(<BookingHistoryFilter {...mockFilterProps} />);
    const inputBookingRef = getByTestId('input-bookingFilter') as HTMLInputElement;
    act(() => {
      fireEvent.change(inputBookingRef, { target: { value: 'AWB1234' } });
      fireEvent.click(getByTestId('booking-history-clearButton'));
    });
    expect(inputBookingRef.value).toBe('');
  });

  it('Should clear the datepicker when clear button is clicked', async () => {
    const { getByTestId } = render(<BookingHistoryFilter {...mockFilterProps} />);
    const inputDatepicker = getByTestId('SingleDatePicker') as HTMLInputElement;

    act(() => {
      fireEvent.click(inputDatepicker);
      fireEvent.change(inputDatepicker, { target: { value: new Date() } });
    });
    await waitFor(async () => {
      fireEvent.mouseDown(document.body);
      fireEvent.click(getByTestId('booking-history-clearButton'));
      expect(inputDatepicker.value).toBe('');
    });
  });

  it('Should find the input error when value is a number', async () => {
    const t = (key: string) => {
      return key === 'dashboard.bookings.invalidBookingReference'
        ? 'Invalid booking reference'
        : 'Regular text label';
    };
    const { getByTestId } = render(
      <BookingHistoryFilter
        t={t}
        onFind={jest.fn()}
        onClear={jest.fn()}
        screenSize={screenSize}
        baseTestId={'booking-history'}
      />
    );

    fireEvent.change(getByTestId('input-bookingFilter'), { target: { value: '1234' } });
    act(() => {
      fireEvent.click(getByTestId('booking-history-findButton'));
    });

    expect(getByTestId('input-bookingFilter-FormErrorMessage')).toBeInTheDocument();
    expect(getByTestId('input-bookingFilter-FormErrorMessage')).toHaveTextContent(
      'Invalid booking reference'
    );
  });

  it('Should find the input error when value have more than 30 characters', async () => {
    const t = (key: string) => {
      return key === 'dashboard.bookings.maximumCharactersPermitted'
        ? 'A maximum of 30 characters are permitted. Please try again.'
        : 'Regular text label';
    };
    const { getByTestId } = render(
      <BookingHistoryFilter
        t={t}
        onFind={jest.fn()}
        onClear={jest.fn()}
        screenSize={screenSize}
        baseTestId={'booking-history'}
      />
    );

    fireEvent.change(getByTestId('input-bookingFilter'), {
      target: { value: 'a123456789012345678901234567890' },
    });
    act(() => {
      fireEvent.click(getByTestId('booking-history-findButton'));
    });

    expect(getByTestId('input-bookingFilter-FormErrorMessage')).toBeInTheDocument();
    expect(getByTestId('input-bookingFilter-FormErrorMessage')).toHaveTextContent(
      'A maximum of 30 characters are permitted. Please try again.'
    );
  });

  it('Should find the input error when value contains invalid characters such as @', async () => {
    const t = (key: string) => {
      return key === 'dashboard.bookings.invalidCharacters'
        ? 'Invalid characters. Please try again.'
        : 'Regular text label';
    };
    const { getByTestId } = render(
      <BookingHistoryFilter
        t={t}
        onFind={jest.fn()}
        onClear={jest.fn()}
        screenSize={screenSize}
        baseTestId={'booking-history'}
      />
    );

    fireEvent.change(getByTestId('input-bookingFilter'), {
      target: { value: 'abc@' },
    });
    act(() => {
      fireEvent.click(getByTestId('booking-history-findButton'));
    });

    expect(getByTestId('input-bookingFilter-FormErrorMessage')).toBeInTheDocument();
    expect(getByTestId('input-bookingFilter-FormErrorMessage')).toHaveTextContent(
      'Invalid characters. Please try again.'
    );
  });

  it('Should not find the input error when value is a valid string', async () => {
    const { getByTestId, queryByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    act(() => {
      fireEvent.change(getByTestId('input-bookingFilter'), { target: { value: 'A1234' } });
    });

    act(() => {
      fireEvent.click(getByTestId('booking-history-findButton'));
    });

    expect(queryByTestId('input-bookingFilter-FormErrorMessage')).not.toBeInTheDocument();
  });

  it('should not allow chosing a date thats its not in minDate and maxDate interval', async () => {
    const { getByTestId, container } = render(<BookingHistoryFilter {...mockFilterProps} />);
    const inputDatepicker = getByTestId('SingleDatePicker') as HTMLInputElement;

    act(() => {
      fireEvent.click(inputDatepicker);
      fireEvent.change(inputDatepicker, { target: { value: new Date(2022, 7, 10) } });
      fireEvent.click(inputDatepicker);
    });
    const selected = container.querySelector('.react-datepicker__day--selected');
    expect(selected).toBe(null);
  });

  it('should call onFind function with argument filterType: NAME', async () => {
    const { getByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    const mockCallObject = {
      filterType: FILTER_OPTIONS.SURNAME,
      filterValue: 'AAA',
    };

    act(() => {
      fireEvent.change(getByTestId('input-bookingFilter'), { target: { value: 'AAA' } });
    });

    act(() => {
      fireEvent.click(getByTestId('booking-history-findButton'));
    });
    expect(mockFilterProps.onFind).toBeCalledWith(mockCallObject);
  });

  it('should call onFind function with argument filterType: CONFIRM_NUMBER', async () => {
    const { getByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    const mockCallObject = {
      filterType: FILTER_OPTIONS.BOOKING_REFERENCE,
      filterValue: 'ABC123',
    };

    act(() => {
      fireEvent.change(getByTestId('input-bookingFilter'), { target: { value: 'ABC123' } });
    });

    act(() => {
      fireEvent.click(getByTestId('booking-history-findButton'));
    });
    expect(mockFilterProps.onFind).toBeCalledWith(mockCallObject);
  });

  it('should call onFind function with argument filterType: ARRIVAL_DATE', async () => {
    const { getByTestId } = render(
      <BookingHistoryFilter {...mockFilterProps} screenSize={screenSize} />
    );
    const mockCallObject = {
      filterType: FILTER_OPTIONS.ARRIVAL,
      filterValue: format(new Date(), 'yyyy-MM-dd'),
    };

    const input = getByTestId('SingleDatePicker');
    act(() => {
      fireEvent.click(input);
      fireEvent.change(input, { target: { value: new Date() } });
    });
    waitFor(() => {
      fireEvent.mouseDown(document.body);
    });

    act(() => {
      fireEvent.click(getByTestId('booking-history-findButton'));
    });
    expect(mockFilterProps.onFind).toBeCalledWith(mockCallObject);
  });
});
