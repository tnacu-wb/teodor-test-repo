import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import { setCookie, syncDynatraceConsent } from '@whitbread-eos/utils';
import React from 'react';

import { CookieConsentDialog } from './CookieConsentDialog.component';
import { useCookieConsent } from './CookieConsentProvider';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  setCookie: jest.fn(),
  syncDynatraceConsent: jest.fn(),
  getNoOfDaysInYear: jest.fn().mockReturnValue(365),
}));

jest.mock('./CookieConsentProvider', () => ({
  ...jest.requireActual('./CookieConsentProvider'),
  useCookieConsent: jest.fn(),
}));

const mockCookiePolicies = {
  introView: {
    title: 'Cookie Consent',
    description: '<p>Intro description</p>',
    manageButtonText: 'Manage Cookies',
    necessaryOnlyButtonText: 'Necessary Only',
    acceptAllButtonText: 'Accept All',
  },
  manageView: {
    title: 'Manage Cookies',
    description: '<p>Manage description</p>',
    alwaysActiveText: 'Always Active',
    saveSettingsButtonText: 'Save Settings',
    cookieGroup: [
      {
        cookieName: 'permissionEssential',
        title: 'Essential',
        description: 'Essential cookies',
        isAlwaysActive: true,
      },
      {
        cookieName: 'permissionMarketing',
        title: 'Marketing',
        description: 'Marketing cookies',
        isAlwaysActive: false,
      },
      {
        cookieName: 'permissionPerformance',
        title: 'Performance',
        description: 'Performance cookies',
        isAlwaysActive: false,
      },
    ],
  },
};

const mockCookieConsentData = {
  cookieConsent: {
    cookiePolicies: mockCookiePolicies,
  },
};

const mockCountryLanguage = { country: 'gb', language: 'en' };

beforeEach(() => {
  (useCookieConsent as jest.Mock).mockReturnValue({
    countryLanguage: mockCountryLanguage,
    cookieConsentData: mockCookieConsentData,
  });
  jest.clearAllMocks();
  // Simulate iframe loaded by dispatching message event
  window.postMessage(
    {
      type: 'COOKIES_IFRAME_LOADED',
      consentCookies: {
        consentGiven: false,
      },
    },
    window.location.origin
  );
});

describe('CookieConsentDialog', () => {
  it('renders null if data is missing', () => {
    (useCookieConsent as jest.Mock).mockReturnValue({});
    const { container } = render(<CookieConsentDialog />);
    expect(container.firstChild).toBeNull();
  });

  it('renders iframe initially and then loads dialog', async () => {
    render(<CookieConsentDialog />);
    expect(screen.getByTitle('BusinessBooker cookies')).toBeInTheDocument();

    // Simulate iframe loaded
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );

    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-Container')).toBeInTheDocument()
    );
  });

  it('renders intro dialog with correct texts', async () => {
    render(<CookieConsentDialog />);
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );
    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-Container')).toBeInTheDocument()
    );
    expect(screen.getByText('Cookie Consent')).toBeInTheDocument();
    expect(screen.getByText('Manage Cookies')).toBeInTheDocument();
    expect(screen.getByText('Necessary Only')).toBeInTheDocument();
    expect(screen.getByText('Accept All')).toBeInTheDocument();
    expect(screen.getByTestId('CookieConsentDialog-Description')).toBeInTheDocument();
  });

  it('opens manage cookies dialog when Manage Cookies is clicked', async () => {
    render(<CookieConsentDialog />);
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );
    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-ManageButton')).toBeInTheDocument()
    );
    fireEvent.click(screen.getByTestId('CookieConsentDialog-ManageButton'));
    expect(screen.getByTestId('ManageCookiesDialog-Container')).toBeInTheDocument();
    expect(screen.getByText('Manage Cookies')).toBeInTheDocument();
    expect(screen.getByText('Save Settings')).toBeInTheDocument();
    expect(screen.getByText('Essential')).toBeInTheDocument();
    expect(screen.getByText('Marketing')).toBeInTheDocument();
    expect(screen.getByText('Performance')).toBeInTheDocument();
    expect(screen.getByText('Always Active')).toBeInTheDocument();
  });

  it('calls setCookiePolicies when Accept All is clicked', async () => {
    render(<CookieConsentDialog isDynatraceRumCookieConsentEnabled={true} />);
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );
    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-AcceptAllButton')).toBeInTheDocument()
    );
    fireEvent.click(screen.getByTestId('CookieConsentDialog-AcceptAllButton'));
    await waitFor(() => expect(setCookie).toHaveBeenCalled());
    expect(syncDynatraceConsent).toHaveBeenCalledWith({
      hasConsent: true,
      isEnabled: true,
      expiryMinutes: 525600,
      paths: ['/en-gb', '/gb'],
      domain: undefined,
    });
  });

  it('calls setCookiePolicies when Necessary Only is clicked', async () => {
    render(<CookieConsentDialog isDynatraceRumCookieConsentEnabled={true} />);
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );
    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-NecessaryOnlyButton')).toBeInTheDocument()
    );
    fireEvent.click(screen.getByTestId('CookieConsentDialog-NecessaryOnlyButton'));
    await waitFor(() => expect(setCookie).toHaveBeenCalled());
    expect(syncDynatraceConsent).toHaveBeenCalledWith({
      hasConsent: false,
      isEnabled: true,
      expiryMinutes: 525600,
      paths: ['/en-gb', '/gb'],
      domain: undefined,
    });
  });

  it('toggles cookie permission switch in manage dialog', async () => {
    render(<CookieConsentDialog />);
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );
    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-ManageButton')).toBeInTheDocument()
    );
    fireEvent.click(screen.getByTestId('CookieConsentDialog-ManageButton'));
    await waitFor(() =>
      expect(
        screen.getByTestId('ManageCookiesDialog-permissionMarketing-Switch')
      ).toBeInTheDocument()
    );
    const switchEl = screen.getByTestId('ManageCookiesDialog-permissionMarketing-Switch');
    fireEvent.click(switchEl);
    // No error, state updated internally
  });

  it('saves settings in manage dialog', async () => {
    render(<CookieConsentDialog />);
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );
    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-ManageButton')).toBeInTheDocument()
    );
    fireEvent.click(screen.getByTestId('CookieConsentDialog-ManageButton'));
    await waitFor(() =>
      expect(screen.getByTestId('ManageCookiesDialog-Save Settings')).toBeInTheDocument()
    );
    fireEvent.click(screen.getByTestId('ManageCookiesDialog-Save Settings'));
    await waitFor(() => expect(setCookie).toHaveBeenCalled());
  });

  it('prevents default on escape and pointer down', async () => {
    render(<CookieConsentDialog />);
    window.dispatchEvent(
      new MessageEvent('message', {
        origin: window.location.origin,
        data: {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: { consentGiven: false },
        },
      })
    );
    await waitFor(() =>
      expect(screen.getByTestId('CookieConsentDialog-Container')).toBeInTheDocument()
    );
    const dialogContent = screen.getByTestId('CookieConsentDialog-Container');
    fireEvent.keyDown(dialogContent, { key: 'Escape' });
    fireEvent.pointerDown(dialogContent);
    // No error, preventDefault called internally
  });
});
