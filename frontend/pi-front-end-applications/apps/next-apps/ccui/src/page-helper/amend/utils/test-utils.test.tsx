import { useQuery } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import React from 'react';
import { useTranslation } from 'react-i18next';

import { render, screen, waitFor } from './test-utils';

describe('test-utils', () => {
  describe('customRender', () => {
    it('should render components with i18n provider', () => {
      const TestComponent = () => {
        const { t, i18n } = useTranslation();
        return (
          <div>
            <div data-testid="language">{i18n.language}</div>
            <div data-testid="translation">{t('test.key', 'Default text')}</div>
          </div>
        );
      };

      render(<TestComponent />);

      expect(screen.getByTestId('language')).toHaveTextContent('en');
      expect(screen.getByTestId('translation')).toBeInTheDocument();
    });

    it('should render components with QueryClientProvider', async () => {
      const TestComponent = () => {
        const { data, isLoading } = useQuery({
          queryKey: ['test-query'],
          queryFn: async () => {
            return { message: 'test data' };
          },
        });

        if (isLoading) return <div>Loading...</div>;

        return <div data-testid="query-result">{data?.message}</div>;
      };

      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('query-result')).toHaveTextContent('test data');
      });
    });

    it('should provide isolated QueryClient instances per render', async () => {
      const TestComponent = ({ value }: { value: string }) => {
        const { data } = useQuery({
          queryKey: ['isolated-query'],
          queryFn: async () => ({ value }),
        });

        return <div data-testid="result">{data?.value || 'loading'}</div>;
      };

      const { unmount: unmount1 } = render(<TestComponent value="first" />);

      await waitFor(() => {
        expect(screen.getByTestId('result')).toHaveTextContent('first');
      });

      unmount1();

      // Second render should not have cached data from first render
      render(<TestComponent value="second" />);

      await waitFor(() => {
        expect(screen.getByTestId('result')).toHaveTextContent('second');
      });
    });

    it('should support re-rendering with different props', () => {
      const TestComponent = ({ text }: { text: string }) => {
        return <div data-testid="text">{text}</div>;
      };

      const { rerender } = render(<TestComponent text="initial" />);

      expect(screen.getByTestId('text')).toHaveTextContent('initial');

      rerender(<TestComponent text="updated" />);

      expect(screen.getByTestId('text')).toHaveTextContent('updated');
    });

    it('should render multiple children within providers', () => {
      const Parent = () => {
        return (
          <div>
            <Child1 />
            <Child2 />
          </div>
        );
      };

      const Child1 = () => {
        const { i18n } = useTranslation();
        return <div data-testid="child1">{i18n.language}</div>;
      };

      const Child2 = () => {
        const { data } = useQuery({
          queryKey: ['child2'],
          queryFn: async () => 'child2-data',
          initialData: 'child2-data',
        });
        return <div data-testid="child2">{data}</div>;
      };

      render(<Parent />);

      expect(screen.getByTestId('child1')).toHaveTextContent('en');
      expect(screen.getByTestId('child2')).toHaveTextContent('child2-data');
    });

    it('should handle components that throw errors', () => {
      const ErrorComponent = () => {
        throw new Error('Test error');
      };

      // Suppress console.error for this test
      const originalError = console.error;
      console.error = jest.fn();

      expect(() => render(<ErrorComponent />)).toThrow('Test error');

      console.error = originalError;
    });

    it('should support unmounting components', () => {
      const TestComponent = () => <div data-testid="test">Test</div>;

      const { unmount } = render(<TestComponent />);

      expect(screen.getByTestId('test')).toBeInTheDocument();

      unmount();

      expect(screen.queryByTestId('test')).not.toBeInTheDocument();
    });
  });

  describe('i18n configuration', () => {
    it('should initialize with correct default language', () => {
      const TestComponent = () => {
        const { i18n } = useTranslation();
        return <div data-testid="lang">{i18n.language}</div>;
      };

      render(<TestComponent />);

      expect(screen.getByTestId('lang')).toHaveTextContent('en');
    });

    it('should have fallback language configured', () => {
      const TestComponent = () => {
        const { i18n } = useTranslation();
        return <div data-testid="fallback">{i18n.options.fallbackLng}</div>;
      };

      render(<TestComponent />);

      expect(screen.getByTestId('fallback')).toHaveTextContent('en');
    });
  });

  describe('exports', () => {
    it('should export screen utility', () => {
      const TestComponent = () => <div data-testid="export-test">Export Test</div>;

      render(<TestComponent />);

      expect(screen.getByTestId('export-test')).toBeInTheDocument();
    });

    it('should export waitFor utility', async () => {
      const TestComponent = () => {
        const [show, setShow] = React.useState(false);

        React.useEffect(() => {
          setTimeout(() => setShow(true), 10);
        }, []);

        return <div>{show && <div data-testid="delayed">Delayed</div>}</div>;
      };

      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('delayed')).toBeInTheDocument();
      });
    });
  });
});
