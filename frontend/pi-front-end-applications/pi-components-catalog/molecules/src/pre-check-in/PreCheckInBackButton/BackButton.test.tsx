import '@testing-library/jest-dom';
import { useRouter } from 'next/router';
import React from 'react';

import { render, waitFor, act, userEvent } from '../../utils/test-utils';
import BackButton from './BackButton';

jest.mock('next/router', () => {
  const mockPush = jest.fn();
  const mockReplace = jest.fn();
  return {
    ...jest.requireActual('next/router'),
    useRouter: jest.fn(() => ({
      query: { bookingReference: 'GBH9630479' },
      pathname: '/pre-check-in',
      push: mockPush,
      replace: mockReplace,
      events: {
        on: jest.fn(),
        off: jest.fn(),
      },
      back: jest.fn(),
    })),
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn(() => ({ t: (key) => key })),
}));

describe('BackButton', () => {
  let mockPush;
  let mockReplace;
  let mockBack;
  let mockRouter;

  beforeEach(() => {
    mockPush = jest.fn();
    mockReplace = jest.fn();
    mockBack = jest.fn();
    mockRouter = {
      query: { bookingReference: 'GBH9630479' },
      pathname: '/pre-check-in',
      push: mockPush,
      replace: mockReplace,
      events: {
        on: jest.fn(),
        off: jest.fn(),
      },
      back: mockBack,
    };

    (useRouter as jest.Mock).mockReturnValue(mockRouter);
    jest.spyOn(window.history, 'replaceState');
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should render the back button with text', () => {
    const { getByTestId } = render(<BackButton />);
    expect(getByTestId('reg-form-back-btn')).toBeInTheDocument();
  });

  it('should set pageLoaderStatus and isNavigating to true on route change start', async () => {
    render(<BackButton />);
    act(() => {
      mockRouter.events.on.mock.calls.forEach(([event, handler]) => {
        if (event === 'routeChangeStart') handler();
      });
    });
  });

  it('should set pageLoaderStatus and isNavigating to false after route change completes', async () => {
    render(<BackButton />);
    act(() => {
      mockRouter.events.on.mock.calls.forEach(([event, handler]) => {
        if (event === 'routeChangeStart') handler();
      });
    });
    act(() => {
      mockRouter.events.on.mock.calls.forEach(([event, handler]) => {
        if (event === 'routeChangeComplete') handler();
      });
    });
  });

  it('should not proceed with navigation if isNavigating is true', async () => {
    const isNavigating = true;
    jest.spyOn(React, 'useState').mockImplementation(() => [isNavigating, jest.fn()]);
    const { getByTestId } = render(<BackButton />);
    const backButton = getByTestId('reg-form-back-btn');
    act(() => {
      userEvent.click(backButton);
    });
    await waitFor(() => {
      expect(mockPush).not.toHaveBeenCalled();
      expect(mockReplace).not.toHaveBeenCalled();
    });
  });
});
