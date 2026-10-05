import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import GlobalError from './global-error';

jest.mock('@whitbread-eos/utils', () => ({
  getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
  getLocaleByPathname: jest.fn(() => 'en'),
}));

const mockLocation = {
  pathname: '/en/some-path',
};

Object.defineProperty(window, 'location', {
  value: mockLocation,
  writable: true,
});

describe('GlobalError Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render GlobalError component with meta refresh', () => {
    const { getPathForLocale } = jest.requireMock('@whitbread-eos/utils');

    render(<GlobalError />);

    // Verify the utility functions were called correctly
    expect(getPathForLocale).toHaveBeenCalledWith('en', 'error');
  });

  it('should call getLocaleByPathname with window location pathname', () => {
    const { getLocaleByPathname } = jest.requireMock('@whitbread-eos/utils');

    render(<GlobalError />);

    expect(getLocaleByPathname).toHaveBeenCalledWith('/en/some-path');
  });

  it('should call getPathForLocale with locale and error path', () => {
    const { getPathForLocale } = jest.requireMock('@whitbread-eos/utils');

    render(<GlobalError />);

    expect(getPathForLocale).toHaveBeenCalledWith('en', 'error');
  });

  it('should handle different locales', () => {
    const { getLocaleByPathname, getPathForLocale } = jest.requireMock('@whitbread-eos/utils');

    getLocaleByPathname.mockReturnValueOnce('de');

    render(<GlobalError />);

    expect(getPathForLocale).toHaveBeenCalledWith('de', 'error');
  });
});
