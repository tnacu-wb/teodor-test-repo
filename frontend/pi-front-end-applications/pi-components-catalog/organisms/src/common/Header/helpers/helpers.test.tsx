import '@testing-library/jest-dom';
import { act, renderHook } from '@testing-library/react';
import { getSecureTwoURL } from '@whitbread-eos/utils';
import React from 'react';

import { render } from '../../../utils/test-utils';
import { getListOfLanguagesForSwitcher, useLogoutWithRedirect } from './helpers';

const SECURE_TWO_URL = 'https://secure2.premierinn.com';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (url: string) => url,
  getSecureTwoURL: jest.fn(() => 'https://secure2.premierinn.com'),
}));

const mockCountries = [
  { language: 'English', flagUrl: '/images/british-round.svg' },
  { language: 'German', flagUrl: '/images/germany-round.svg' },
];

describe('getListOfLanguagesForSwitcher', () => {
  it('should return English and German locale entries', () => {
    const result = getListOfLanguagesForSwitcher(mockCountries as any);
    expect(result[0].locale).toBe('en');
    expect(result[1].locale).toBe('de');
  });

  it('should set alt text to the language name on the English flag icon', () => {
    const result = getListOfLanguagesForSwitcher(mockCountries as any);
    const { getByAltText } = render(<>{result[0].icon}</>);
    expect(getByAltText('English')).toBeInTheDocument();
  });

  it('should set alt text to the language name on the German flag icon', () => {
    const result = getListOfLanguagesForSwitcher(mockCountries as any);
    const { getByAltText } = render(<>{result[1].icon}</>);
    expect(getByAltText('German')).toBeInTheDocument();
  });

  it('should set the correct languageName on the English locale entry', () => {
    const result = getListOfLanguagesForSwitcher(mockCountries as any);
    expect(result[0].languageName).toBe('English');
  });

  it('should set the correct languageName on the German locale entry', () => {
    const result = getListOfLanguagesForSwitcher(mockCountries as any);
    expect(result[1].languageName).toBe('German');
  });
});

describe('useLogoutWithRedirect', () => {
  const PAGE_ORIGIN = 'http://localhost';
  let assignMock: jest.Mock;
  let navigateToLogout: jest.Mock;

  beforeEach(() => {
    assignMock = jest.fn();
    navigateToLogout = jest.fn();
    delete (window as any).location;
    (window as any).location = {
      origin: PAGE_ORIGIN,
      protocol: 'http:',
      hostname: 'localhost',
      assign: assignMock,
    };
  });

  afterEach(() => {
    jest.clearAllMocks();
    (getSecureTwoURL as jest.Mock).mockReturnValue(SECURE_TWO_URL);
  });

  it('sends logout to SecureTwo iframe when the returned logout function is called', () => {
    const postMessageMock = jest.fn();
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: postMessageMock },
    });

    const { result } = renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      result.current();
    });

    expect(postMessageMock).toHaveBeenCalledWith('{"action":"logout"}', SECURE_TWO_URL);
  });

  it('redirects to home once SecureTwo confirms logout after the button was clicked', () => {
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: jest.fn() },
    });

    const { result } = renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      result.current();
      window.dispatchEvent(
        new MessageEvent('message', {
          data: 'userLoggedOut',
          origin: SECURE_TWO_URL,
        })
      );
    });

    expect(assignMock).toHaveBeenCalledWith('/gb/en/home.html');
  });

  it('does not redirect when userLoggedOut arrives without the logout button having been clicked', () => {
    renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      window.dispatchEvent(
        new MessageEvent('message', {
          data: 'userLoggedOut',
          origin: SECURE_TWO_URL,
        })
      );
    });

    expect(assignMock).not.toHaveBeenCalled();
  });

  it('calls navigateToLogout with the locale-aware returnTo instead of posting to the iframe when Auth0 is active', () => {
    const postMessageMock = jest.fn();
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: postMessageMock },
    });

    const { result } = renderHook(() => useLogoutWithRedirect('gb', 'en', true, navigateToLogout));

    act(() => {
      result.current();
    });

    expect(navigateToLogout).toHaveBeenCalledTimes(1);
    expect(navigateToLogout).toHaveBeenCalledWith(`${window.location.origin}/gb/en/home.html`);
    expect(postMessageMock).not.toHaveBeenCalled();
  });

  it('builds the Auth0 returnTo as an absolute URL using the given country and language', () => {
    const { result } = renderHook(() => useLogoutWithRedirect('de', 'de', true, navigateToLogout));

    act(() => {
      result.current();
    });

    expect(navigateToLogout).toHaveBeenCalledWith(`${window.location.origin}/de/de/home.html`);
  });

  it('does not redirect on userLoggedOut when isAuth0Active is true', () => {
    renderHook(() => useLogoutWithRedirect('gb', 'en', true, navigateToLogout));

    act(() => {
      window.dispatchEvent(
        new MessageEvent('message', {
          data: 'userLoggedOut',
          origin: SECURE_TWO_URL,
        })
      );
    });

    expect(assignMock).not.toHaveBeenCalled();
  });

  it('redirects when getSecureTwoURL returns a bare hostname without protocol (non-www domains)', () => {
    (getSecureTwoURL as jest.Mock).mockReturnValue('secure2.dev.premierinn.com');
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: jest.fn() },
    });

    const { result } = renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      result.current();
      window.dispatchEvent(
        new MessageEvent('message', {
          data: 'userLoggedOut',
          origin: `${window.location.protocol}//secure2.dev.premierinn.com`,
        })
      );
    });

    expect(assignMock).toHaveBeenCalledWith('/gb/en/home.html');
  });

  it('sends logout to SecureTwo iframe when an external USER_LOG_OUT message is received', () => {
    const postMessageMock = jest.fn();
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: postMessageMock },
    });

    renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      window.dispatchEvent(
        new MessageEvent('message', {
          data: { action: 'USER_LOG_OUT' },
          origin: PAGE_ORIGIN,
        })
      );
    });

    expect(postMessageMock).toHaveBeenCalledWith('{"action":"logout"}', SECURE_TWO_URL);
  });

  it('redirects to home when an external USER_LOG_OUT is followed by userLoggedOut', () => {
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: jest.fn() },
    });

    renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      window.dispatchEvent(
        new MessageEvent('message', {
          data: { action: 'USER_LOG_OUT' },
          origin: PAGE_ORIGIN,
        })
      );
      window.dispatchEvent(
        new MessageEvent('message', {
          data: 'userLoggedOut',
          origin: SECURE_TWO_URL,
        })
      );
    });

    expect(assignMock).toHaveBeenCalledWith('/gb/en/home.html');
  });

  it('appends sessionExpired=true when the external USER_LOG_OUT carries that reason', () => {
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: jest.fn() },
    });

    renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      window.dispatchEvent(
        new MessageEvent('message', {
          data: { action: 'USER_LOG_OUT', reason: 'SESSION_EXPIRED' },
          origin: PAGE_ORIGIN,
        })
      );
      window.dispatchEvent(
        new MessageEvent('message', {
          data: 'userLoggedOut',
          origin: SECURE_TWO_URL,
        })
      );
    });

    expect(assignMock).toHaveBeenCalledWith('/gb/en/home.html?sessionExpired=true');
  });

  it('does not carry over a stale SESSION_EXPIRED reason into a later manual logout', () => {
    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: { postMessage: jest.fn() },
    });

    const { result } = renderHook(() => useLogoutWithRedirect('gb', 'en', false, navigateToLogout));

    act(() => {
      // Reason gets armed but never confirmed (userLoggedOut is dropped/never arrives).
      window.dispatchEvent(
        new MessageEvent('message', {
          data: { action: 'USER_LOG_OUT', reason: 'SESSION_EXPIRED' },
          origin: PAGE_ORIGIN,
        })
      );
    });

    act(() => {
      // User then manually clicks the header's own Log out link.
      result.current();
      window.dispatchEvent(
        new MessageEvent('message', {
          data: 'userLoggedOut',
          origin: SECURE_TWO_URL,
        })
      );
    });

    expect(assignMock).toHaveBeenCalledWith('/gb/en/home.html');
  });

  it('removes event listener on unmount', () => {
    const removeSpy = jest.spyOn(window, 'removeEventListener');
    const { unmount } = renderHook(() =>
      useLogoutWithRedirect('gb', 'en', false, navigateToLogout)
    );

    unmount();

    expect(removeSpy).toHaveBeenCalledWith('message', expect.any(Function));
  });
});
