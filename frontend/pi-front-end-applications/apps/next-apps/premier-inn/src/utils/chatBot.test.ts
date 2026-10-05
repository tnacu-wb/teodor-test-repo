import { analytics, getCookie } from '@whitbread-eos/utils';

import { initializeAmazonConnectChat } from './chatBot';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_GB: 'snippet-gb',
    NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_DE: 'snippet-de',
    NEXT_PUBLIC_AMAZON_CHAT_AUTH_URL: 'https://mock-auth-url/token',
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  GLOBALS: {
    language: {
      DE: 'de',
      EN: 'en',
    },
  },
  formatAssetsUrl: (url: string) => url,
  analytics: { update: jest.fn() },
  getCookie: jest.fn(),
}));

declare global {
  interface Window {
    amazon_connect: jest.Mock;
    fetch: jest.Mock<
      Promise<{
        json: () => Promise<{ token: string }>;
      }>,
      []
    >;
  }
}

describe('initializeAmazonConnectChat', () => {
  beforeEach(() => {
    global.window = Object.create(window);
    global.window.amazon_connect = jest.fn();
    global.window.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve({ token: 'mockToken' }),
      } as any)
    );
    global.document.cookie = '';
    (window as any).analyticsData = {};
    sessionStorage.clear();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should initialize amazon_connect with correct configurations for EN', () => {
    initializeAmazonConnectChat('en');

    expect(window.amazon_connect).toHaveBeenCalledWith('styles', {
      iconType: 'CHAT',
      openChat: { color: '#ffffff', backgroundColor: '#521e5a' },
      closeChat: { color: '#ffffff', backgroundColor: '#521e5a' },
    });

    expect(window.amazon_connect).toHaveBeenCalledWith('snippetId', 'snippet-gb');
  });

  it('should initialize amazon_connect with correct configurations for DE', () => {
    initializeAmazonConnectChat('de');

    expect(window.amazon_connect).toHaveBeenCalledWith('snippetId', 'snippet-de');
  });

  it('should call authenticate and fetch token', async () => {
    initializeAmazonConnectChat('en');

    const authenticateCall = window.amazon_connect.mock.calls.find(
      ([key]) => key === 'authenticate'
    );
    const authenticateCallback = authenticateCall[1];

    await new Promise((resolve) => {
      authenticateCallback((token: string) => {
        expect(token).toBe('mockToken');
        resolve(undefined);
      });
    });

    expect(window.fetch).toHaveBeenCalledWith('https://mock-auth-url/token');
  });

  it('should set and clear cookies on CONNECTION_ESTABLISHED and CHAT_ENDED', () => {
    Object.defineProperty(document, 'cookie', {
      writable: true,
      value: '',
    });

    initializeAmazonConnectChat('en');

    const registerCallbackCall = window.amazon_connect.mock.calls.find(
      ([key]) => key === 'registerCallback'
    );
    const callbacks = registerCallbackCall[1];

    sessionStorage.setItem('persistedChatSession', 'mockSession');
    callbacks.CONNECTION_ESTABLISHED();
    expect(document.cookie).toContain('activeChat=mockSession');
    callbacks.CHAT_ENDED();
    expect(document.cookie).toContain('activeChat=');
  });

  it('should restore persisted chat session from cookies', () => {
    document.cookie = 'activeChat=mockSession';
    sessionStorage.setItem('persistedChatSession', 'mockSession');
    (getCookie as jest.Mock).mockReturnValue('mockSession');
    initializeAmazonConnectChat('en');
    expect(sessionStorage.getItem('persistedChatSession')).toBe('mockSession');
  });

  it('should initialize amazon_connect if not already defined', () => {
    global.window.amazon_connect = null as any;
    document.cookie = 'activeChat=mockSession';
    initializeAmazonConnectChat('en');

    expect(global.window.amazon_connect).not.toBeNull();
  });

  it('should call analytics.update with available: true', () => {
    initializeAmazonConnectChat('en');
    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({ liveChat: expect.objectContaining({ available: true }) })
    );
  });

  it('should call analytics.update with opened: true on CONNECTION_ESTABLISHED', () => {
    initializeAmazonConnectChat('en');
    const registerCallbackCall = window.amazon_connect.mock.calls.find(
      ([key]) => key === 'registerCallback'
    );
    const callbacks = registerCallbackCall[1];
    sessionStorage.setItem('persistedChatSession', 'mockSession');
    callbacks.CONNECTION_ESTABLISHED();
    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({ liveChat: expect.objectContaining({ opened: true }) })
    );
  });

  it('should call analytics.update with endChat: true on CHAT_ENDED', () => {
    initializeAmazonConnectChat('en');
    const registerCallbackCall = window.amazon_connect.mock.calls.find(
      ([key]) => key === 'registerCallback'
    );
    const callbacks = registerCallbackCall[1];
    callbacks.CHAT_ENDED();
    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({ liveChat: expect.objectContaining({ endChat: true }) })
    );
  });
});
