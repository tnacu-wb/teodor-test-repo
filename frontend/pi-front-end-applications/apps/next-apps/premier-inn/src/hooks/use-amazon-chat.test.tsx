import { ACTIVE_CHAT_COOKIE, CHAT_BOT_STATUS } from '~utils/chat-constants';
import { render, screen, waitFor } from '~utils/test-utils';

import { useAmazonChat } from './use-amazon-chat';

const mockGetCookie = jest.fn();
const mockInitializeAmazonConnectChat = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getCookie: (...args: any[]) => mockGetCookie(...args),
}));

jest.mock('~utils/chatBot', () => ({
  initializeAmazonConnectChat: (...args: any[]) => mockInitializeAmazonConnectChat(...args),
}));

// Helper function to create mock pageProps with dehydrated state
const createMockPageProps = (chatBotStatus: string, webChatEnabledPages: string[] = []) => ({
  dehydratedState: {
    queries: [
      {
        state: {
          data: {
            headerInformation: {
              config: {
                amazonChat: {
                  chatBotStatus,
                  webChatEnabledPages,
                },
              },
            },
          },
        },
      },
    ],
  },
});

// Test component that uses the hook
function TestComponent({ pageProps, language }: { pageProps: any; language: string }) {
  const { isAmazonChatBoxEnabled } = useAmazonChat({ pageProps, language });
  return <div data-testid="chat-enabled">{isAmazonChatBoxEnabled ? 'enabled' : 'disabled'}</div>;
}

describe('useAmazonChat', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetCookie.mockReturnValue(null);
  });

  describe('Priority 1: CHAT_BOT_STATUS.HIDE', () => {
    it('should always hide chat when status is HIDE, even with active chat cookie', async () => {
      mockGetCookie.mockReturnValue('active-chat-session');
      const pageProps = createMockPageProps(CHAT_BOT_STATUS.HIDE);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });

    it('should hide chat when status is HIDE regardless of page configuration', async () => {
      const pageProps = createMockPageProps(CHAT_BOT_STATUS.HIDE, [
        '/gb/en/account/dashboard.html',
      ]);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });
  });

  describe('Priority 2: ACTIVE_CHAT_COOKIE', () => {
    it('should show chat on all pages when active chat cookie exists', async () => {
      mockGetCookie.mockReturnValue('active-chat-session');
      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, [
        '/gb/en/different-page.html',
      ]);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockGetCookie).toHaveBeenCalledWith(ACTIVE_CHAT_COOKIE);
      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('en');
    });

    it('should bypass page restrictions when cookie is present', async () => {
      mockGetCookie.mockReturnValue('active-chat-session');
      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, []);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('en');
    });
  });

  describe('Priority 3: CHAT_BOT_STATUS.SHOW_ALL_PAGES', () => {
    it('should show chat on all pages when status is SHOW_ALL_PAGES', async () => {
      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_ALL_PAGES);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('en');
    });

    it('should initialize chat with correct language for DE locale', async () => {
      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_ALL_PAGES);

      render(<TestComponent pageProps={pageProps} language="de" />);

      await waitFor(() => {
        expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('de');
      });
    });
  });

  describe('Priority 4: CHAT_BOT_STATUS.SHOW_LIMITED_PAGES', () => {
    it('should show chat when window.location.pathname exactly matches an enabled page', async () => {
      delete (window as any).location;
      window.location = { ...window.location, pathname: '/gb/en/account/dashboard.html' };

      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, [
        '/gb/en/account/dashboard.html',
      ]);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('en');
    });

    it('should show chat when enabled path is contained within window.location.pathname', async () => {
      delete (window as any).location;
      window.location = { ...window.location, pathname: '/gb/en/account/dashboard.html' };

      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, [
        '/account/dashboard',
      ]);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('en');
    });

    it('should hide chat when window.location.pathname is not in webChatEnabledPages', async () => {
      delete (window as any).location;
      window.location = { ...window.location, pathname: '/gb/en/account/dashboard.html' };

      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, [
        '/gb/en/different-page.html',
      ]);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });

    it('should hide chat when webChatEnabledPages is empty', async () => {
      delete (window as any).location;
      window.location = { ...window.location, pathname: '/gb/en/account/dashboard.html' };

      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, []);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });

    it('should handle multiple pages in webChatEnabledPages', async () => {
      delete (window as any).location;
      window.location = { ...window.location, pathname: '/gb/en/account/dashboard.html' };

      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, [
        '/gb/en/home.html',
        '/gb/en/account/dashboard.html',
        '/gb/en/search.html',
      ]);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('en');
    });
  });

  describe('Path matching logic', () => {
    it('should match using the real browser pathname from window.location', async () => {
      delete (window as any).location;
      window.location = { ...window.location, pathname: '/de/de/konto/dashboard.html' };

      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, [
        '/de/de/konto/dashboard.html',
      ]);

      render(<TestComponent pageProps={pageProps} language="de" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('de');
    });

    it('should use window.location.pathname not Next.js router pathname', async () => {
      // Simulate microfrontend: Next.js route is opera-shared-page but real page is contact-us
      delete (window as any).location;
      window.location = { ...window.location, pathname: '/gb/en/contact-us.html' };

      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_LIMITED_PAGES, [
        '/gb/en/contact-us.html',
      ]);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });

      expect(mockInitializeAmazonConnectChat).toHaveBeenCalledWith('en');
    });
  });

  describe('Missing or invalid data', () => {
    it('should handle missing headerInformation gracefully', async () => {
      const pageProps = {
        dehydratedState: {
          queries: [],
        },
      };

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });

    it('should handle missing amazonChat config gracefully', async () => {
      const pageProps = {
        dehydratedState: {
          queries: [
            {
              state: {
                data: {
                  headerInformation: {
                    config: {},
                  },
                },
              },
            },
          ],
        },
      };

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });

    it('should handle missing webChatEnabledPages gracefully', async () => {
      const pageProps = {
        dehydratedState: {
          queries: [
            {
              state: {
                data: {
                  headerInformation: {
                    config: {
                      amazonChat: {
                        chatBotStatus: CHAT_BOT_STATUS.SHOW_LIMITED_PAGES,
                      },
                    },
                  },
                },
              },
            },
          ],
        },
      };

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });

    it('should handle empty pageProps', async () => {
      const pageProps = {};

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      expect(mockInitializeAmazonConnectChat).not.toHaveBeenCalled();
    });
  });

  describe('Re-render behavior', () => {
    it('should update when pageProps changes', async () => {
      const initialPageProps = createMockPageProps(CHAT_BOT_STATUS.HIDE);
      const updatedPageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_ALL_PAGES);

      const { rerender } = render(<TestComponent pageProps={initialPageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });

      rerender(<TestComponent pageProps={updatedPageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });
    });
  });

  describe('Cookie checking', () => {
    it('should call getCookie with correct parameter', async () => {
      const pageProps = createMockPageProps(CHAT_BOT_STATUS.SHOW_ALL_PAGES);

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        expect(mockGetCookie).toHaveBeenCalledWith(ACTIVE_CHAT_COOKIE);
      });
    });
  });

  describe('Helper function: getHeaderInformationFromDehydratedState', () => {
    it('should extract headerInformation from correct query structure', async () => {
      const pageProps = {
        dehydratedState: {
          queries: [
            {
              state: {
                data: {
                  someOtherData: 'value',
                },
              },
            },
            {
              state: {
                data: {
                  headerInformation: {
                    config: {
                      amazonChat: {
                        chatBotStatus: CHAT_BOT_STATUS.SHOW_ALL_PAGES,
                      },
                    },
                  },
                },
              },
            },
          ],
        },
      };

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('enabled');
      });
    });

    it('should return undefined when queries array is empty', async () => {
      const pageProps = {
        dehydratedState: {
          queries: [],
        },
      };

      render(<TestComponent pageProps={pageProps} language="en" />);

      await waitFor(() => {
        screen.getByText('disabled');
      });
    });
  });
});
