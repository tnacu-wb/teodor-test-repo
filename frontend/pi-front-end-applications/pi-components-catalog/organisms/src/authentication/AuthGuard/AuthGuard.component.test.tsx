import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { LOCALES } from '@whitbread-eos/api';

import { render, screen } from '../../utils/test-utils';
import AuthGuard from './AuthGuard.component';

const mockUseRouter = jest.fn();
const mockIsInnBusinessApp = jest.fn();
const mockGetPathForLocale = jest.fn();
const mockUseCustomLocale = jest.fn();

// Mock window.location.href
delete (window as any).location;
window.location = { href: '' } as any;

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  isInnBusinessApp: () => mockIsInnBusinessApp(),
  getPathForLocale: (locale: LOCALES, path: string) => mockGetPathForLocale(locale, path),
  useCustomLocale: () => mockUseCustomLocale(),

  PIB_MANUAL_LOGOUT_FLAG: 'pib_manual_logout',
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQuery: () => ({
    isLoading: false,
    isError: false,
    isSuccess: true,
  }),
  useMutation: () => ({}),
}));

const props = {
  queryClient: new QueryClient(),
  hasRegisteredSuccessfully: false,
};

describe('AuthGuard', () => {
  let sessionStorageMock: { [key: string]: string };

  beforeEach(() => {
    jest.clearAllMocks();
    sessionStorageMock = {};

    // Mock sessionStorage
    Object.defineProperty(window, 'sessionStorage', {
      value: {
        getItem: jest.fn((key: string) => sessionStorageMock[key] || null),
        setItem: jest.fn((key: string, value: string) => {
          sessionStorageMock[key] = value;
        }),
        removeItem: jest.fn((key: string) => {
          delete sessionStorageMock[key];
        }),
        clear: jest.fn(() => {
          sessionStorageMock = {};
        }),
      },
      writable: true,
      configurable: true,
    });

    mockUseRouter.mockReturnValue({
      query: { reservationId: 'AWM323321321' },
      locale: 'en',
      route: '/some-route',
    });
    mockIsInnBusinessApp.mockReturnValue(false);
    mockUseCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    mockGetPathForLocale.mockReturnValue('en-gb/account/login?timeout=true');
    window.location.href = '';
  });

  it('should render a AuthGuard with default props ', function () {
    render(<AuthGuard {...props} />);
    const modal = screen.getByTestId('BB-Header-Auth-ModalContent');
    expect(modal).toBeDefined();
  });

  it('should not render AuthContentManagerBBVariant if route is inn business', () => {
    mockIsInnBusinessApp.mockReturnValue(true);

    const { container } = render(<AuthGuard {...props} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('redirects to login page if not authenticated and route is inn business', () => {
    mockIsInnBusinessApp.mockReturnValue(true);
    mockGetPathForLocale.mockReturnValue('en-gb/account/login?timeout=true');

    const { container } = render(<AuthGuard {...props} />);
    expect(container).toBeEmptyDOMElement();
    expect(mockGetPathForLocale).toHaveBeenCalledTimes(1);
    expect(mockGetPathForLocale).toHaveBeenCalledWith('en-gb', 'account/login?timeout=true');
    expect(window.location.href).toBe('en-gb/account/login?timeout=true');
  });

  it('redirects to login page without timeout if manual logout flag is set', () => {
    mockIsInnBusinessApp.mockReturnValue(true);
    mockGetPathForLocale.mockReturnValue('en-gb/account/login');
    sessionStorageMock['pib_manual_logout'] = 'true';

    const { container } = render(<AuthGuard {...props} />);
    expect(container).toBeEmptyDOMElement();
    expect(mockGetPathForLocale).toHaveBeenCalledTimes(1);
    expect(mockGetPathForLocale).toHaveBeenCalledWith('en-gb', 'account/login');
    expect(window.location.href).toBe('en-gb/account/login');
    expect(window.sessionStorage.removeItem).toHaveBeenCalledWith('pib_manual_logout');
  });

  it('should not redirect if route is a public route', () => {
    mockIsInnBusinessApp.mockReturnValue(true);
    mockUseRouter.mockReturnValue({
      query: {},
      locale: 'en',
      route: '/404',
    });

    const { container } = render(<AuthGuard {...props} />);
    expect(container).toBeEmptyDOMElement();
    expect(mockGetPathForLocale).not.toHaveBeenCalled();
    expect(window.location.href).toBe('');
  });

  it('should render AuthContentManagerBBVariant if route is not inn business', () => {
    render(<AuthGuard {...props} />);
    const modal = screen.getByTestId('BB-Header-Auth-ModalContent');
    expect(modal).toBeDefined();
  });
});
