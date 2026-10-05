import React from 'react';

import { render, waitFor, act, fireEvent } from '../utils/test-utils';
import useScrollVisibility from './use-scroll-visibility';

let mockIsVisible = true;
let mockDefaultValue: boolean | undefined = false;

const Component = () => {
  mockIsVisible = useScrollVisibility(mockDefaultValue);
  return null;
};

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useSearchParams: jest.fn().mockReturnValue({
    get: (key: string) => key,
  }),
}));

describe('useScrollVisibility', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'scroll', { value: () => true, writable: true });
  });

  it('should return false when not scrolling and defaultValue = false', () => {
    render(<Component />);
    expect(mockIsVisible).toEqual(false);
  });

  it('should return true when not scrolling and defaultValue = undefined', () => {
    mockDefaultValue = undefined;
    render(<Component />);
    expect(mockIsVisible).toEqual(true);
  });

  it('should return false when scrolling down', async () => {
    render(<Component />);

    await act(async () => {
      Object.defineProperty(window, 'scrollY', { value: 1000, writable: true });
      fireEvent.scroll(document);
    });

    await waitFor(() => {
      expect(mockIsVisible).toEqual(false);
    });
  });

  it('should return true when scrolling up', async () => {
    render(<Component />);

    await act(async () => {
      Object.defineProperty(window, 'scrollY', { value: 1000, writable: true });
      fireEvent.scroll(document);
    });

    await act(async () => {
      Object.defineProperty(window, 'scrollY', { value: 500, writable: true });
      fireEvent.scroll(document);
    });

    await waitFor(() => {
      expect(mockIsVisible).toEqual(true);
    });
  });
});
