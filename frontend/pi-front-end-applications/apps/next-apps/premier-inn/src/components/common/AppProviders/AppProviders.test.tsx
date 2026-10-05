import { QueryClient, useQueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { AppProviders } from './AppProviders';

// Mock the external providers to simplify testing
jest.mock('@whitbread-eos/atoms', () => ({
  Fonts: () => <div data-testid="fonts-component">Fonts</div>,
}));

jest.mock('@auth0/nextjs-auth0/client', () => ({
  Auth0Provider: ({ children }) => <div data-testid="auth0-provider">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  AnalyticsProvider: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="analytics-provider">{children}</div>
  ),
  FeatureToggleContextProvider: ({
    children,
    defaultFeatureToggles,
  }: {
    children: React.ReactNode;
    defaultFeatureToggles?: Record<string, boolean>;
  }) => (
    <div data-testid="feature-toggle-provider" data-toggles={JSON.stringify(defaultFeatureToggles)}>
      {children}
    </div>
  ),
  UserContextProvider: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="user-context-provider">{children}</div>
  ),
  Auth0Provider: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="auth0-provider">{children}</div>
  ),
}));

jest.mock('@chakra-ui/react', () => ({
  ChakraProvider: ({ children, theme }: { children: React.ReactNode; theme: unknown }) => (
    <div data-testid="chakra-provider" data-theme={theme ? 'present' : 'absent'}>
      {children}
    </div>
  ),
}));

jest.mock('../../../../theme', () => ({
  theme: { colors: { primary: 'test' } },
}));

// Test component to verify QueryClient is accessible
function QueryClientConsumer() {
  const queryClient = useQueryClient();
  return <div data-testid="query-client-consumer">{queryClient ? 'has-client' : 'no-client'}</div>;
}

describe('AppProviders', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
        },
      },
    });
  });

  afterEach(() => {
    queryClient.clear();
  });

  describe('Provider rendering', () => {
    it('should render children correctly', () => {
      render(
        <AppProviders queryClient={queryClient}>
          <div data-testid="child-content">Test Content</div>
        </AppProviders>
      );

      expect(screen.getByTestId('child-content')).toBeInTheDocument();
      expect(screen.getByText('Test Content')).toBeInTheDocument();
    });

    it('should render Fonts component', () => {
      render(
        <AppProviders queryClient={queryClient}>
          <div>Content</div>
        </AppProviders>
      );

      expect(screen.getByTestId('fonts-component')).toBeInTheDocument();
    });

    it('should render all required providers', () => {
      render(
        <AppProviders queryClient={queryClient}>
          <div>Content</div>
        </AppProviders>
      );

      expect(screen.getByTestId('analytics-provider')).toBeInTheDocument();
      expect(screen.getByTestId('feature-toggle-provider')).toBeInTheDocument();
      expect(screen.getByTestId('user-context-provider')).toBeInTheDocument();
      expect(screen.getByTestId('chakra-provider')).toBeInTheDocument();
    });
  });

  describe('QueryClient integration', () => {
    it('should provide QueryClient to children', () => {
      render(
        <AppProviders queryClient={queryClient}>
          <QueryClientConsumer />
        </AppProviders>
      );

      expect(screen.getByTestId('query-client-consumer')).toHaveTextContent('has-client');
    });
  });

  describe('Feature toggles', () => {
    it('should pass feature toggles to FeatureToggleContextProvider', () => {
      const featureToggles = {
        featureA: true,
        featureB: false,
      };

      render(
        <AppProviders queryClient={queryClient} featureToggles={featureToggles}>
          <div>Content</div>
        </AppProviders>
      );

      const provider = screen.getByTestId('feature-toggle-provider');
      expect(provider).toHaveAttribute('data-toggles', JSON.stringify(featureToggles));
    });

    it('should handle undefined feature toggles', () => {
      render(
        <AppProviders queryClient={queryClient}>
          <div>Content</div>
        </AppProviders>
      );

      const provider = screen.getByTestId('feature-toggle-provider');
      // When featureToggles is undefined, JSON.stringify returns undefined (not the string)
      // so the data-toggles attribute won't be set
      expect(provider).not.toHaveAttribute('data-toggles');
    });

    it('should handle empty feature toggles', () => {
      render(
        <AppProviders queryClient={queryClient} featureToggles={{}}>
          <div>Content</div>
        </AppProviders>
      );

      const provider = screen.getByTestId('feature-toggle-provider');
      expect(provider).toHaveAttribute('data-toggles', '{}');
    });
  });

  describe('ChakraProvider', () => {
    it('should provide theme to ChakraProvider', () => {
      render(
        <AppProviders queryClient={queryClient}>
          <div>Content</div>
        </AppProviders>
      );

      const chakraProvider = screen.getByTestId('chakra-provider');
      expect(chakraProvider).toHaveAttribute('data-theme', 'present');
    });
  });

  describe('Provider hierarchy', () => {
    it('should nest providers in correct order', () => {
      render(
        <AppProviders queryClient={queryClient}>
          <div data-testid="child">Content</div>
        </AppProviders>
      );

      // Verify the nesting order by checking parent-child relationships
      const featureToggle = screen.getByTestId('feature-toggle-provider');
      const userContext = screen.getByTestId('user-context-provider');
      const analytics = screen.getByTestId('analytics-provider');
      const chakra = screen.getByTestId('chakra-provider');
      const child = screen.getByTestId('child');

      // ChakraProvider should contain the child
      expect(chakra).toContainElement(child);
      // AnalyticsProvider should contain ChakraProvider
      expect(analytics).toContainElement(chakra);
      // UserContextProvider should contain AnalyticsProvider
      expect(userContext).toContainElement(analytics);
      // FeatureToggleProvider should contain UserContextProvider
      expect(featureToggle).toContainElement(userContext);
    });
  });

  describe('Dehydrated state', () => {
    it('should accept dehydratedState prop without errors', () => {
      const dehydratedState = {
        mutations: [],
        queries: [
          {
            queryKey: ['test'],
            queryHash: 'test-hash',
            state: { data: 'test-data' },
          },
        ],
      };

      expect(() => {
        render(
          <AppProviders queryClient={queryClient} dehydratedState={dehydratedState as any}>
            <div>Content</div>
          </AppProviders>
        );
      }).not.toThrow();
    });

    it('should handle undefined dehydratedState', () => {
      expect(() => {
        render(
          <AppProviders queryClient={queryClient}>
            <div>Content</div>
          </AppProviders>
        );
      }).not.toThrow();
    });
  });

  describe('Multiple renders', () => {
    it('should handle re-renders correctly', () => {
      const { rerender } = render(
        <AppProviders queryClient={queryClient}>
          <div data-testid="content">First</div>
        </AppProviders>
      );

      expect(screen.getByTestId('content')).toHaveTextContent('First');

      rerender(
        <AppProviders queryClient={queryClient}>
          <div data-testid="content">Second</div>
        </AppProviders>
      );

      expect(screen.getByTestId('content')).toHaveTextContent('Second');
    });

    it('should handle feature toggle updates on re-render', () => {
      const { rerender } = render(
        <AppProviders queryClient={queryClient} featureToggles={{ feature: true }}>
          <div>Content</div>
        </AppProviders>
      );

      expect(screen.getByTestId('feature-toggle-provider')).toHaveAttribute(
        'data-toggles',
        JSON.stringify({ feature: true })
      );

      rerender(
        <AppProviders queryClient={queryClient} featureToggles={{ feature: false }}>
          <div>Content</div>
        </AppProviders>
      );

      expect(screen.getByTestId('feature-toggle-provider')).toHaveAttribute(
        'data-toggles',
        JSON.stringify({ feature: false })
      );
    });
  });
});
