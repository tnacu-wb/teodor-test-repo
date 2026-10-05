import { fireEvent, render, screen } from '@testing-library/react';
import {
  COOKIE_MODAL_CLOSED_EVENT,
  ONETRUST_GROUPS_UPDATED_EVENT,
  syncDynatraceConsentFromCookie,
  syncDynatraceConsentFromOneTrust,
} from '@whitbread-eos/utils';
import React from 'react';

import ScriptsEmbed from './index';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_AMAZON_CHAT_URL: 'https://example.com/amazon-chat-script.js',
    NEXT_PUBLIC_AMAZON_CHAT_ID: 'amazon-chat-script',
  },
}));

jest.mock('next/script', () => {
  const MockNextScript = (props: any) => <script {...props} />;
  MockNextScript.displayName = 'MockNextScript';
  return MockNextScript;
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  syncDynatraceConsentFromCookie: jest.fn(),
  syncDynatraceConsentFromOneTrust: jest.fn(),
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  useRouter: () => mockUseRouter(),
}));

describe('ScriptsEmbed Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseRouter.mockReturnValue({ pathname: '/gb/en/home.html' });
  });

  it('renders Adobe script on non-opera-shared pages', () => {
    render(<ScriptsEmbed isAmazonChatBoxEnabled={true} amazonChatIcon="/amazon-chat-icon.gif" />);
    expect(screen.getByTestId('adobe-script')).toBeInTheDocument();
  });

  it('does not render Adobe script on opera-shared-page', () => {
    mockUseRouter.mockReturnValue({ pathname: '/opera-shared-page/header' });
    render(<ScriptsEmbed isAmazonChatBoxEnabled={true} />);
    expect(screen.queryByTestId('adobe-script')).not.toBeInTheDocument();
  });

  it('does not render amazon chat script when disabled', () => {
    const { container } = render(<ScriptsEmbed isAmazonChatBoxEnabled={false} language="en" />);
    expect(container.querySelector('#amazon-chat-script')).not.toBeInTheDocument();
    expect(
      container.querySelector('script[src="https://amazon-chat-gb.js"]')
    ).not.toBeInTheDocument();
  });

  it('removes the amazon chat widget from DOM when disabled', () => {
    const widget = document.createElement('div');
    widget.id = 'amazon-connect-chat-widget';
    document.body.appendChild(widget);

    expect(document.getElementById('amazon-connect-chat-widget')).toBeInTheDocument();

    render(<ScriptsEmbed isAmazonChatBoxEnabled={false} language="en" />);
    expect(document.getElementById('amazon-connect-chat-widget')).not.toBeInTheDocument();
  });
});

describe('OneTrust Cookie Consent scripts', () => {
  const originalEnv = process.env;

  beforeEach(() => {
    mockUseRouter.mockReturnValue({ pathname: '/gb/en/home.html' });
    jest.resetModules();
    process.env = {
      ...originalEnv,
      NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL:
        'https://cdn-ukwest.onetrust.com/scripttemplates/otSDKStub.js',
      NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB: 'test-domain-gb',
      NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE: 'test-domain-de',
    };
  });

  afterAll(() => {
    process.env = originalEnv;
  });

  it('renders OneTrust scripts when enabled and env vars are set', () => {
    render(<ScriptsEmbed isOneTrustCookieConsentEnabled={true} />);

    const script = screen.getByTestId('one-trust-cookie-consent');
    expect(script).toBeInTheDocument();
    expect(script).toHaveAttribute(
      'src',
      'https://cdn-ukwest.onetrust.com/scripttemplates/otSDKStub.js'
    );
    expect(script).toHaveAttribute('data-domain-script', 'test-domain-gb');
    expect(script).toHaveAttribute('data-language', 'en');
  });

  it('renders DE locale OneTrust config when locale is de-de', () => {
    render(<ScriptsEmbed isOneTrustCookieConsentEnabled={true} locale="de-de" />);

    const script = screen.getByTestId('one-trust-cookie-consent');
    expect(script).toHaveAttribute('data-domain-script', 'test-domain-de');
    expect(script).toHaveAttribute('data-language', 'de');
  });

  it('renders an empty OptanonWrapper placeholder required by OneTrust', () => {
    const { container } = render(<ScriptsEmbed isOneTrustCookieConsentEnabled={true} />);

    const inlineScript = container.querySelector('script#optanon-wrapper');
    expect(inlineScript).toBeInTheDocument();
    expect(inlineScript).toHaveTextContent('function OptanonWrapper() {}');
  });

  it('syncs current OneTrust consent and listens for updates', () => {
    render(
      <ScriptsEmbed
        isOneTrustCookieConsentEnabled={true}
        isDynatraceRumCookieConsentEnabled={true}
      />
    );

    expect(syncDynatraceConsentFromOneTrust).toHaveBeenCalledWith({ isEnabled: true });
    jest.mocked(syncDynatraceConsentFromOneTrust).mockClear();

    fireEvent(window, new Event(ONETRUST_GROUPS_UPDATED_EVENT));
    expect(syncDynatraceConsentFromOneTrust).toHaveBeenCalledTimes(1);
  });

  it('syncs custom consent and listens for the legacy modal event', () => {
    render(
      <ScriptsEmbed
        isOneTrustCookieConsentEnabled={false}
        isDynatraceRumCookieConsentEnabled={true}
        locale="en-gb"
      />
    );

    expect(syncDynatraceConsentFromCookie).toHaveBeenCalledWith({
      isEnabled: true,
      paths: ['/en-gb', '/gb'],
      domain: undefined,
    });
    jest.mocked(syncDynatraceConsentFromCookie).mockClear();

    fireEvent(window, new Event(COOKIE_MODAL_CLOSED_EVENT));
    expect(syncDynatraceConsentFromCookie).toHaveBeenCalledTimes(1);
  });

  it('does not render OneTrust scripts when isOneTrustCookieConsentEnabled is false', () => {
    render(<ScriptsEmbed isOneTrustCookieConsentEnabled={false} />);

    expect(screen.queryByTestId('one-trust-cookie-consent')).not.toBeInTheDocument();
  });

  it('does not render OneTrust scripts when NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL is not set', () => {
    delete process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL;
    render(<ScriptsEmbed isOneTrustCookieConsentEnabled={true} />);

    expect(screen.queryByTestId('one-trust-cookie-consent')).not.toBeInTheDocument();
  });

  it('does not render OneTrust scripts when NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB is not set', () => {
    delete process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB;
    render(<ScriptsEmbed isOneTrustCookieConsentEnabled={true} />);

    expect(screen.queryByTestId('one-trust-cookie-consent')).not.toBeInTheDocument();
  });
});

describe('Dynatrace script', () => {
  const originalEnv = process.env;

  beforeEach(() => {
    mockUseRouter.mockReturnValue({ pathname: '/gb/en/home.html' });
    jest.resetModules();
    process.env = {
      ...originalEnv,
      NEXT_PUBLIC_DYNATRACE_GB:
        'https://js-cdn.dynatrace.com/jstag/16a48f5d352/bf33584hsx/43c9a9e1e80e5a9a_complete-gb.js',
      NEXT_PUBLIC_DYNATRACE_DE:
        'https://js-cdn.dynatrace.com/jstag/16a48f5d352/bf33584hsx/43c9a9e1e80e5a9a_complete-de.js',
    };
  });

  afterAll(() => {
    process.env = originalEnv;
  });

  it('renders Dynatrace script for English language', () => {
    render(<ScriptsEmbed language="en" />);

    expect(screen.getByTestId('dynatrace-script')).toBeInTheDocument();
    expect(screen.getByTestId('dynatrace-script')).toHaveAttribute(
      'src',
      'https://js-cdn.dynatrace.com/jstag/16a48f5d352/bf33584hsx/43c9a9e1e80e5a9a_complete-gb.js'
    );
  });

  it('renders Dynatrace script for German language', () => {
    render(<ScriptsEmbed language="de" />);

    expect(screen.getByTestId('dynatrace-script')).toBeInTheDocument();
    expect(screen.getByTestId('dynatrace-script')).toHaveAttribute(
      'src',
      'https://js-cdn.dynatrace.com/jstag/16a48f5d352/bf33584hsx/43c9a9e1e80e5a9a_complete-de.js'
    );
  });
});
