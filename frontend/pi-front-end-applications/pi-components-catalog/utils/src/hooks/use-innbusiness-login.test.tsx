import { renderHook, act } from '@testing-library/react';
import { z } from 'zod';

import useCookieWatcher from './use-cookie-watcher';
import useInnBusinessLogin from './use-innbusiness-login';

jest.mock('./use-cookie-watcher');

const mockUseCookieWatcher = useCookieWatcher as jest.Mock;

const mockRouter = jest.fn();
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    replace: mockRouter,
    refresh: jest.fn(),
  }),
}));

describe('useInnBusinessLogin', () => {
  const redirectTo = '/dashboard';
  const iframeSecureUrl = 'https://secure.example.com';
  const schema = z.object({
    email: z.string().email(),
    password: z.string().min(6),
  });

  beforeEach(() => {
    mockUseCookieWatcher.mockReturnValue(false);
    jest.spyOn(window, 'addEventListener');
    jest.spyOn(window, 'removeEventListener');
    Object.defineProperty(window, 'location', {
      writable: true,
      value: { replace: jest.fn() },
    });
    window.location.replace = jest.fn();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should initialize with default values', () => {
    const { result } = renderHook(() => useInnBusinessLogin(redirectTo, iframeSecureUrl, schema));

    expect(result.current.isError).toBe(false);
    expect(result.current.isSubmitting).toBe(false);
    expect(typeof result.current.handleLogin).toBe('function');
  });

  it('should handle login success message', () => {
    const { result } = renderHook(() => useInnBusinessLogin(redirectTo, iframeSecureUrl, schema));

    const messageEvent = new MessageEvent('message', {
      origin: iframeSecureUrl,
      data: JSON.stringify({ action: 'userLoggedIn' }),
    });

    act(() => {
      window.dispatchEvent(messageEvent);
    });

    expect(result.current.isError).toBe(false);
  });

  it('should handle login error message', () => {
    const { result } = renderHook(() => useInnBusinessLogin(redirectTo, iframeSecureUrl, schema));

    const messageEvent = new MessageEvent('message', {
      origin: iframeSecureUrl,
      data: JSON.stringify({ action: 'loginError' }),
    });

    act(() => {
      window.dispatchEvent(messageEvent);
    });

    expect(result.current.isError).toBe(true);
    expect(result.current.isSubmitting).toBe(false);
  });

  it('should handle login submission', async () => {
    const { result } = renderHook(() => useInnBusinessLogin(redirectTo, iframeSecureUrl, schema));

    const mockIframe = document.createElement('iframe');
    mockIframe.id = 'authIframe';
    document.body.appendChild(mockIframe);

    const data = { email: 'test@example.com', password: 'password123' };

    await act(async () => {
      result.current.handleLogin(data);
    });

    expect(result.current.isSubmitting).toBe(true);

    document.body.removeChild(mockIframe);
  });

  it('should handle login submission error when iframe is missing', async () => {
    const { result } = renderHook(() => useInnBusinessLogin(redirectTo, iframeSecureUrl, schema));

    const data = { email: 'test@example.com', password: 'password123' };

    await act(async () => {
      result.current.handleLogin(data);
    });

    expect(result.current.isError).toBe(true);
  });

  it('should clean up event listeners on unmount', () => {
    const { unmount } = renderHook(() => useInnBusinessLogin(redirectTo, iframeSecureUrl, schema));

    unmount();

    expect(window.removeEventListener).toHaveBeenCalledWith('message', expect.any(Function));
  });

  it('should set hasLoggedIn to true on LOGIN_SUCCESS and redirect when token is set', () => {
    mockUseCookieWatcher.mockReturnValueOnce(false).mockReturnValueOnce(true);

    const { result, rerender } = renderHook(() =>
      useInnBusinessLogin(redirectTo, iframeSecureUrl, schema)
    );

    const messageEvent = new MessageEvent('message', {
      origin: iframeSecureUrl,
      data: JSON.stringify({ action: 'userLoggedIn' }),
    });

    act(() => {
      window.dispatchEvent(messageEvent);
    });

    expect(result.current.isError).toBe(false);

    rerender();

    expect(mockRouter).toHaveBeenCalledWith(redirectTo);
  });

  it('should not handle messages from unknown origins', () => {
    const { result } = renderHook(() => useInnBusinessLogin(redirectTo, iframeSecureUrl, schema));

    const messageEvent = new MessageEvent('message', {
      origin: 'https://unknown-origin.com',
      data: JSON.stringify({ action: 'userLoggedIn' }),
    });

    act(() => {
      window.dispatchEvent(messageEvent);
    });

    expect(result.current.isError).toBe(false);
    expect(result.current.isSubmitting).toBe(false);
  });
});
