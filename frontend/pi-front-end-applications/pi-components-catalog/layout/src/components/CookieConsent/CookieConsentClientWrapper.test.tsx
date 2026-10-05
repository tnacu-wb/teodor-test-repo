import { render, screen } from '@testing-library/react';
import { syncDynatraceConsentFromCookie } from '@whitbread-eos/utils';
import React from 'react';

import { CookieConsentClientWrapper } from './CookieConsentClientWrapper';

jest.mock('./CookieConsentProvider', () => ({
  CookieConsentProvider: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="provider">{children}</div>
  ),
}));
jest.mock('./CookieConsentDialog.component', () => ({
  CookieConsentDialog: () => <div data-testid="dialog">Dialog</div>,
}));
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  syncDynatraceConsentFromCookie: jest.fn(),
  useCustomLocaleAppRouter: jest.fn(() => ({ country: 'gb', language: 'en' })),
}));

describe('CookieConsentClientWrapper', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders CookieConsentProvider and CookieConsentDialog when show is true', () => {
    render(
      <CookieConsentClientWrapper
        show={true}
        shouldSyncDynatraceConsent={true}
        isDynatraceRumCookieConsentEnabled={true}
      />
    );
    expect(screen.getByTestId('provider')).toBeInTheDocument();
    expect(screen.getByTestId('dialog')).toBeInTheDocument();
    expect(syncDynatraceConsentFromCookie).toHaveBeenCalledWith({
      isEnabled: true,
      paths: ['/en-gb', '/gb'],
      domain: undefined,
    });
  });

  it('renders nothing when show is false', () => {
    const { container } = render(<CookieConsentClientWrapper show={false} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('does not sync Dynatrace consent when disabled', () => {
    render(<CookieConsentClientWrapper show={false} shouldSyncDynatraceConsent={false} />);

    expect(syncDynatraceConsentFromCookie).not.toHaveBeenCalled();
  });

  it('forwards disabled Dynatrace consent when the feature flag is disabled', () => {
    render(
      <CookieConsentClientWrapper
        show={false}
        shouldSyncDynatraceConsent={true}
        isDynatraceRumCookieConsentEnabled={false}
      />
    );

    expect(syncDynatraceConsentFromCookie).toHaveBeenCalledWith({
      isEnabled: false,
      paths: ['/en-gb', '/gb'],
      domain: undefined,
    });
  });
});
