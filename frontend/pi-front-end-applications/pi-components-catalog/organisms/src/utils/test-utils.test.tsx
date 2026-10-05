import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { UserDataContext } from '@whitbread-eos/utils';
import React from 'react';

import { render, renderUserContext } from './test-utils';

// Mock external dependencies
jest.mock('@tanstack/react-query', () => ({
  QueryClient: jest.fn(() => ({
    clear: jest.fn(),
    mount: jest.fn(),
  })),
  QueryClientProvider: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="query-client-provider">{children}</div>
  ),
}));

jest.mock('react-i18next', () => ({
  I18nextProvider: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="i18next-provider">{children}</div>
  ),
  initReactI18next: {
    type: '3rdParty',
    init: jest.fn(),
  },
}));

jest.mock('i18next', () => ({
  use: jest.fn().mockReturnThis(),
  init: jest.fn(),
  t: jest.fn((key) => key),
  changeLanguage: jest.fn(),
}));

describe('test-utils', () => {
  const mockQueryClient = jest.mocked(QueryClient);

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('customRender', () => {
    it('should render component', () => {
      const TestComponent = () => <div data-testid="test-component">Test</div>;

      const { getByTestId } = render(<TestComponent />);

      expect(getByTestId('test-component')).toBeInTheDocument();
    });

    it('should wrap component with I18nextProvider', () => {
      const TestComponent = () => <div>Test</div>;

      const { getByTestId } = render(<TestComponent />);

      expect(getByTestId('i18next-provider')).toBeInTheDocument();
    });

    it('should wrap component with QueryClientProvider', () => {
      const TestComponent = () => <div>Test</div>;

      const { getByTestId } = render(<TestComponent />);

      expect(getByTestId('query-client-provider')).toBeInTheDocument();
    });

    it('should create QueryClient instance', () => {
      const TestComponent = () => <div>Test</div>;

      render(<TestComponent />);

      expect(mockQueryClient).toHaveBeenCalled();
    });

    it('should render children correctly', () => {
      const TestComponent = () => <div data-testid="child">Child Content</div>;

      const { getByTestId } = render(<TestComponent />);

      expect(getByTestId('child')).toHaveTextContent('Child Content');
    });

    it('should handle components with props', () => {
      const TestComponent = ({ text }: { text: string }) => (
        <div data-testid="prop-test">{text}</div>
      );

      const { getByTestId } = render(<TestComponent text="Hello World" />);

      expect(getByTestId('prop-test')).toHaveTextContent('Hello World');
    });

    it('should return RTL render result', () => {
      const TestComponent = () => <div data-testid="test">Test</div>;

      const result = render(<TestComponent />);

      expect(result.getByTestId).toBeDefined();
      expect(result.queryByTestId).toBeDefined();
      expect(result.container).toBeDefined();
    });
  });

  describe('renderUserContext', () => {
    it('should render component with UserDataContext', () => {
      const TestComponent = () => <div data-testid="test">Test</div>;
      const mockUserData = {
        user: { id: '123', name: 'Test User' },
      } as any;

      const { getByTestId } = renderUserContext(<TestComponent />, mockUserData);

      expect(getByTestId('test')).toBeInTheDocument();
    });

    it('should provide UserDataContext value to component', () => {
      const TestComponent = () => {
        const context = React.useContext(UserDataContext) as any;
        return <div data-testid="user-id">{context?.user?.id}</div>;
      };
      const mockUserData = {
        user: { id: 'user-123', name: 'John Doe' },
      } as any;

      const { getByTestId } = renderUserContext(<TestComponent />, mockUserData);

      expect(getByTestId('user-id')).toHaveTextContent('user-123');
    });

    it('should wrap with all providers', () => {
      const TestComponent = () => <div>Test</div>;
      const mockUserData = {} as any;

      const { getByTestId } = renderUserContext(<TestComponent />, mockUserData);

      expect(getByTestId('i18next-provider')).toBeInTheDocument();
      expect(getByTestId('query-client-provider')).toBeInTheDocument();
    });

    it('should handle null user data', () => {
      const TestComponent = () => {
        const context = React.useContext(UserDataContext);
        return <div data-testid="context-value">{context ? 'has-context' : 'no-context'}</div>;
      };

      const { getByTestId } = renderUserContext(<TestComponent />, null as any);

      expect(getByTestId('context-value')).toBeInTheDocument();
    });

    it('should handle empty user data object', () => {
      const TestComponent = () => {
        const context = React.useContext(UserDataContext);
        return <div data-testid="has-context">{context ? 'yes' : 'no'}</div>;
      };
      const mockUserData = {} as any;

      const { getByTestId } = renderUserContext(<TestComponent />, mockUserData);

      expect(getByTestId('has-context')).toHaveTextContent('yes');
    });

    it('should handle complex user data structure', () => {
      const TestComponent = () => {
        const context = React.useContext(UserDataContext) as any;
        return (
          <div>
            <div data-testid="user-name">{context?.user?.name}</div>
            <div data-testid="user-email">{context?.user?.email}</div>
          </div>
        );
      };
      const mockUserData = {
        user: {
          id: '123',
          name: 'Jane Smith',
          email: 'jane@example.com',
        },
      } as any;

      const { getByTestId } = renderUserContext(<TestComponent />, mockUserData);

      expect(getByTestId('user-name')).toHaveTextContent('Jane Smith');
      expect(getByTestId('user-email')).toHaveTextContent('jane@example.com');
    });
  });

  describe('AllTheProviders component behavior', () => {
    it('should create new QueryClient on each render', () => {
      const TestComponent = () => <div>Test</div>;

      render(<TestComponent />);
      render(<TestComponent />);

      // QueryClient constructor should be called for each render
      expect(mockQueryClient).toHaveBeenCalledTimes(2);
    });

    it('should maintain same QueryClient instance across re-renders', () => {
      const TestComponent = ({ count }: { count: number }) => <div>{count}</div>;

      const { rerender } = render(<TestComponent count={1} />);

      const initialCallCount = mockQueryClient.mock.calls.length;

      rerender(<TestComponent count={2} />);

      expect(mockQueryClient.mock.calls.length).toBe(initialCallCount);
    });
  });

  describe('Exported utilities', () => {
    it('should export render function', () => {
      expect(render).toBeDefined();
      expect(typeof render).toBe('function');
    });

    it('should export renderUserContext function', () => {
      expect(renderUserContext).toBeDefined();
      expect(typeof renderUserContext).toBe('function');
    });
  });

  describe('Complex component rendering', () => {
    it('should handle components with hooks', () => {
      const TestComponent = () => {
        const [count, setCount] = React.useState(0);
        return (
          <div>
            <div data-testid="count">{count}</div>
            <button onClick={() => setCount(count + 1)}>Increment</button>
          </div>
        );
      };

      const { getByTestId } = render(<TestComponent />);

      expect(getByTestId('count')).toHaveTextContent('0');
    });

    it('should handle nested components', () => {
      const ChildComponent = ({ text }: { text: string }) => <div data-testid="child">{text}</div>;
      const ParentComponent = () => (
        <div data-testid="parent">
          <ChildComponent text="Nested" />
        </div>
      );

      const { getByTestId } = render(<ParentComponent />);

      expect(getByTestId('parent')).toBeInTheDocument();
      expect(getByTestId('child')).toHaveTextContent('Nested');
    });

    it('should handle components with multiple children', () => {
      const TestComponent = () => (
        <div>
          <div data-testid="child-1">First</div>
          <div data-testid="child-2">Second</div>
          <div data-testid="child-3">Third</div>
        </div>
      );

      const { getByTestId } = render(<TestComponent />);

      expect(getByTestId('child-1')).toBeInTheDocument();
      expect(getByTestId('child-2')).toBeInTheDocument();
      expect(getByTestId('child-3')).toBeInTheDocument();
    });
  });

  describe('Edge cases', () => {
    it('should handle empty component', () => {
      const EmptyComponent = () => null;

      const { container } = render(<EmptyComponent />);

      expect(container).toBeInTheDocument();
    });

    it('should handle fragment', () => {
      const FragmentComponent = () => (
        <>
          <div data-testid="first">First</div>
          <div data-testid="second">Second</div>
        </>
      );

      const { getByTestId } = render(<FragmentComponent />);

      expect(getByTestId('first')).toBeInTheDocument();
      expect(getByTestId('second')).toBeInTheDocument();
    });
  });

  describe('Re-render scenarios', () => {
    it('should handle component re-renders', () => {
      const TestComponent = ({ value }: { value: string }) => (
        <div data-testid="value">{value}</div>
      );

      const { getByTestId, rerender } = render(<TestComponent value="initial" />);

      expect(getByTestId('value')).toHaveTextContent('initial');

      rerender(<TestComponent value="updated" />);

      expect(getByTestId('value')).toHaveTextContent('updated');
    });
  });

  describe('UserDataContext integration', () => {
    it('should provide context to deeply nested components', () => {
      const DeepComponent = () => {
        const context = React.useContext(UserDataContext) as any;
        return <div data-testid="deep">{context?.user?.name}</div>;
      };

      const MiddleComponent = () => (
        <div>
          <DeepComponent />
        </div>
      );

      const TopComponent = () => (
        <div>
          <MiddleComponent />
        </div>
      );

      const mockUserData = {
        user: { id: '1', name: 'Deep User' },
      } as any;

      const { getByTestId } = renderUserContext(<TopComponent />, mockUserData);

      expect(getByTestId('deep')).toHaveTextContent('Deep User');
    });

    it('should allow multiple components to access same context', () => {
      const Component1 = () => {
        const context = React.useContext(UserDataContext) as any;
        return <div data-testid="comp1">{context?.user?.id}</div>;
      };

      const Component2 = () => {
        const context = React.useContext(UserDataContext) as any;
        return <div data-testid="comp2">{context?.user?.name}</div>;
      };

      const ParentComponent = () => (
        <div>
          <Component1 />
          <Component2 />
        </div>
      );

      const mockUserData = {
        user: { id: '123', name: 'Shared User' },
      } as any;

      const { getByTestId } = renderUserContext(<ParentComponent />, mockUserData);

      expect(getByTestId('comp1')).toHaveTextContent('123');
      expect(getByTestId('comp2')).toHaveTextContent('Shared User');
    });
  });
});
