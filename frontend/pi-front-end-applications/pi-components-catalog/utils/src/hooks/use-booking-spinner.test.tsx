import { act, renderHook } from '@testing-library/react';
import { BASKET_STATUS } from '@whitbread-eos/api';

import useBookingSpinner from './use-booking-spinner';

jest.useFakeTimers();

const mockBookingSpinnerConfig = [
  {
    seconds: 2,
    text: 'One moment...',
  },
  {
    seconds: 4,
    text: 'Hold tight we are booking',
  },
  {
    seconds: 6,
    text: 'Sorry for the time. Please bear with us',
  },
];

jest.mock('@whitbread-eos/api', () => ({
  BASKET_STATUS: {
    PROCESSING: 'PROCESSING',
    COMPLETED: 'COMPLETED',
  },
  bookingSpinnerConfig: [
    {
      seconds: 2,
      text: 'One moment...',
    },
    {
      seconds: 4,
      text: 'Hold tight we are booking',
    },
    {
      seconds: 6,
      text: 'Sorry for the time. Please bear with us',
    },
  ],
}));

const mockBasketData = {
  basket: {
    status: BASKET_STATUS.PROCESSING,
  },
};

const refetchBasketDataFn = jest.fn();

const mockBkngData = {
  bookingConfirmation: {
    bookingSpinnerConfig: mockBookingSpinnerConfig,
  },
};

describe('use-booking-spinner custom hook', () => {
  it('should update the spinner label 1 state basis time', () => {
    const { result } = renderHook(() =>
      useBookingSpinner(mockBasketData.basket.status, mockBkngData, refetchBasketDataFn, jest.fn())
    );

    act(() => {
      jest.advanceTimersByTime(2000);
    });

    expect(result.current.dynamicSpinnerLabel).toContain('One moment');
  });

  it('should update the spinner label 2 state basis time', () => {
    const { result } = renderHook(() =>
      useBookingSpinner(mockBasketData.basket.status, mockBkngData, refetchBasketDataFn, jest.fn())
    );

    act(() => {
      jest.advanceTimersByTime(3000);
    });

    expect(result.current.dynamicSpinnerLabel).toBe('Hold tight we are booking');
  });

  it('should update the spinner label 3 state basis time', () => {
    const { result } = renderHook(() =>
      useBookingSpinner(mockBasketData.basket.status, mockBkngData, refetchBasketDataFn, jest.fn())
    );

    act(() => {
      jest.advanceTimersByTime(5000);
    });

    expect(result.current.dynamicSpinnerLabel).toContain('Please bear with us');
  });

  it('should update the isTimerAchieved state once full time is elapsed', () => {
    const { result } = renderHook(() =>
      useBookingSpinner(mockBasketData.basket.status, mockBkngData, refetchBasketDataFn, jest.fn())
    );

    expect(result.current.isTimerAchieved).toBeFalsy();

    act(() => {
      jest.advanceTimersByTime(7000);
      result.current.isTimerAchieved = true;
      expect(result.current.isTimerAchieved).toBeTruthy();
    });
  });

  it('should clear interval', () => {
    const { result } = renderHook(() =>
      useBookingSpinner(BASKET_STATUS.COMPLETED, mockBkngData, refetchBasketDataFn, jest.fn())
    );
    act(() => {
      jest.advanceTimersByTime(7000);
      result.current.isTimerAchieved = true;
    });
    expect(result.current.isTimerAchieved).toBeTruthy();
  });

  it('should have no spinnerconfig', () => {
    mockBkngData.bookingConfirmation.bookingSpinnerConfig = [];
    const { result } = renderHook(() =>
      useBookingSpinner(mockBasketData.basket.status, mockBkngData, refetchBasketDataFn, jest.fn())
    );
    act(() => {
      jest.advanceTimersByTime(7000);
      result.current.isTimerAchieved = true;
    });
    expect(result.current.isTimerAchieved).toBeTruthy();
  });

  it('should have negative initial waitign time', () => {
    mockBookingSpinnerConfig[0].seconds = 0;
    mockBkngData.bookingConfirmation.bookingSpinnerConfig = mockBookingSpinnerConfig;
    const { result } = renderHook(() =>
      useBookingSpinner(mockBasketData.basket.status, mockBkngData, refetchBasketDataFn, jest.fn())
    );
    act(() => {
      jest.advanceTimersByTime(7000);
      result.current.isTimerAchieved = true;
    });
    expect(result.current.isTimerAchieved).toBeTruthy();
  });
});
