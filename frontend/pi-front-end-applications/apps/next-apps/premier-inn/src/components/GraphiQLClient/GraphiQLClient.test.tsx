import '@testing-library/jest-dom';
import { act, fireEvent, render, screen, waitFor } from '@testing-library/react';
import GraphiQL from 'graphiql';
import { buildClientSchema, DocumentNode, getIntrospectionQuery, parse } from 'graphql';

import { fetcher, getOperationKind, getOperationName } from '~utils/graphql';

import GraphiQLClient from './GraphiQLClient';

// Mock components with display names
const MockToolbar = ({ children }: { children: React.ReactNode }) => (
  <div data-testid="graphiql-toolbar">{children}</div>
);
MockToolbar.displayName = 'MockToolbar';

const MockButton = ({
  onClick,
  label,
  title,
}: {
  onClick: () => void;
  label: string;
  title: string;
}) => (
  <button data-testid={`button-${label.toLowerCase()}`} onClick={onClick} title={title}>
    {label}
  </button>
);
MockButton.displayName = 'MockButton';

jest.mock('graphiql', () => ({
  __esModule: true,
  default: jest.fn(({ children, query, ref }) => {
    if (ref) {
      ref({
        handleRunQuery: jest.fn(),
        handlePrettifyQuery: jest.fn(),
        handleToggleHistory: jest.fn(),
        getQueryEditor: jest.fn(() => ({
          setOption: jest.fn(),
          options: {},
          getTokenAt: jest.fn(),
          indexFromPos: jest.fn(),
        })),
      });
    }
    return (
      <div data-testid="graphiql-component" data-query={query}>
        {children}
      </div>
    );
  }),
}));

// Add Toolbar and Button to the mocked GraphiQL
const MockedGraphiQL = jest.mocked(GraphiQL);
(MockedGraphiQL as any).Toolbar = MockToolbar;
(MockedGraphiQL as any).Button = MockButton;

jest.mock('graphiql-explorer', () => ({
  __esModule: true,
  default: jest.fn(({ query, onEdit, onRunOperation, explorerIsOpen, onToggleExplorer }) => (
    <div data-testid="graphiql-explorer" data-query={query} data-explorer-open={explorerIsOpen}>
      <button data-testid="explorer-edit" onClick={() => onEdit('new query')}>
        Edit
      </button>
      <button data-testid="explorer-run" onClick={() => onRunOperation('testOp')}>
        Run
      </button>
      <button data-testid="explorer-toggle" onClick={onToggleExplorer}>
        Toggle
      </button>
    </div>
  )),
}));

jest.mock('graphiql/graphiql.css', () => ({}));

jest.mock('~utils/graphql', () => ({
  fetcher: jest.fn(),
  getDefaultScalarArgValue: jest.fn(),
  getOperationKind: jest.fn(),
  getOperationName: jest.fn(),
  makeDefaultArg: jest.fn(),
}));

jest.mock('graphql', () => ({
  buildClientSchema: jest.fn(),
  getIntrospectionQuery: jest.fn(),
  parse: jest.fn(),
}));

describe('GraphiQLClient', () => {
  const mockSchema = { type: 'GraphQLSchema' };
  const mockIntrospectionResponse = {
    data: {
      __schema: {
        types: [],
      },
    },
  };

  const mockFetcher = jest.mocked(fetcher);
  const mockGetOperationKind = jest.mocked(getOperationKind);
  const mockGetOperationName = jest.mocked(getOperationName);
  const mockBuildClientSchema = jest.mocked(buildClientSchema);
  const mockGetIntrospectionQuery = jest.mocked(getIntrospectionQuery);
  const mockParse = jest.mocked(parse);
  const mockGraphiQL = jest.mocked(GraphiQL);

  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
    mockGetIntrospectionQuery.mockReturnValue('introspection query');
    mockFetcher.mockResolvedValue(mockIntrospectionResponse);
    mockBuildClientSchema.mockReturnValue(mockSchema as any);
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
  });

  describe('Component rendering', () => {
    it('should render GraphiQL component', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-component')).toBeInTheDocument();
      });
    });

    it('should render GraphiQLExplorer component', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-explorer')).toBeInTheDocument();
      });
    });

    it('should render with container classes', async () => {
      const { container } = render(<GraphiQLClient />);

      await waitFor(() => {
        expect(container.querySelector('.container.graphiql-container')).toBeInTheDocument();
      });
    });

    it('should render toolbar with all buttons', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-toolbar')).toBeInTheDocument();
        expect(screen.getByTestId('button-prettify')).toBeInTheDocument();
        expect(screen.getByTestId('button-history')).toBeInTheDocument();
        expect(screen.getByTestId('button-explorer')).toBeInTheDocument();
      });
    });

    it('should render with default query', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        const graphiqlComponent = screen.getByTestId('graphiql-component');
        expect(graphiqlComponent).toHaveAttribute('data-query');
        const query = graphiqlComponent.getAttribute('data-query');
        expect(query).toContain('Welcome to GraphiQL');
      });
    });
  });

  describe('Schema fetching', () => {
    it('should fetch schema on mount', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(mockGetIntrospectionQuery).toHaveBeenCalled();
        expect(mockFetcher).toHaveBeenCalledWith({
          query: 'introspection query',
        });
      });
    });

    it('should build client schema from fetched data', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(mockBuildClientSchema).toHaveBeenCalledWith(mockIntrospectionResponse.data);
      });
    });

    it('should pass schema to GraphiQL component', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(mockGraphiQL).toHaveBeenCalled();
      });
    });
  });

  describe('Query state management', () => {
    it('should initialize with default query', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        const explorer = screen.getByTestId('graphiql-explorer');
        const query = explorer.getAttribute('data-query');
        expect(query).toContain('Welcome to GraphiQL');
      });
    });

    it('should update query when explorer triggers onEdit', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('explorer-edit')).toBeInTheDocument();
      });

      const editButton = screen.getByTestId('explorer-edit');
      fireEvent.click(editButton);

      await waitFor(() => {
        const explorer = screen.getByTestId('graphiql-explorer');
        expect(explorer).toHaveAttribute('data-query', 'new query');
      });
    });

    it('should pass query to both GraphiQL and GraphiQLExplorer', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        const graphiqlCalls = mockGraphiQL.mock.calls;

        expect(graphiqlCalls[graphiqlCalls.length - 1][0].query).toBeDefined();
      });
    });
  });

  describe('Explorer state management', () => {
    it('should initialize with explorer open', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        const explorer = screen.getByTestId('graphiql-explorer');
        expect(explorer).toHaveAttribute('data-explorer-open', 'true');
      });
    });

    it('should toggle explorer state when explorer toggle button is clicked', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('explorer-toggle')).toBeInTheDocument();
      });

      const toggleButton = screen.getByTestId('explorer-toggle');

      // Initially open
      expect(screen.getByTestId('graphiql-explorer')).toHaveAttribute('data-explorer-open', 'true');

      // Click to close
      fireEvent.click(toggleButton);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-explorer')).toHaveAttribute(
          'data-explorer-open',
          'false'
        );
      });

      // Click to open again
      fireEvent.click(toggleButton);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-explorer')).toHaveAttribute(
          'data-explorer-open',
          'true'
        );
      });
    });

    it('should toggle explorer state when toolbar Explorer button is clicked', async () => {
      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: jest.fn(() => ({
              setOption: jest.fn(),
              options: {},
            })),
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('button-explorer')).toBeInTheDocument();
      });

      const explorerButton = screen.getByTestId('button-explorer');

      // Initially open
      expect(screen.getByTestId('graphiql-explorer')).toHaveAttribute('data-explorer-open', 'true');

      // Click to close
      fireEvent.click(explorerButton);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-explorer')).toHaveAttribute(
          'data-explorer-open',
          'false'
        );
      });
    });
  });

  describe('Toolbar button interactions', () => {
    it('should call handlePrettifyQuery when Prettify button is clicked', async () => {
      const mockHandlePrettifyQuery = jest.fn();

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: mockHandlePrettifyQuery,
            handleToggleHistory: jest.fn(),
            getQueryEditor: jest.fn(() => ({
              setOption: jest.fn(),
              options: {},
            })),
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('button-prettify')).toBeInTheDocument();
      });

      const prettifyButton = screen.getByTestId('button-prettify');
      fireEvent.click(prettifyButton);

      expect(mockHandlePrettifyQuery).toHaveBeenCalled();
    });

    it('should call handleToggleHistory when History button is clicked', async () => {
      const mockHandleToggleHistory = jest.fn();

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: mockHandleToggleHistory,
            getQueryEditor: jest.fn(() => ({
              setOption: jest.fn(),
              options: {},
            })),
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('button-history')).toBeInTheDocument();
      });

      const historyButton = screen.getByTestId('button-history');
      fireEvent.click(historyButton);

      expect(mockHandleToggleHistory).toHaveBeenCalled();
    });

    it('should have correct titles on toolbar buttons', async () => {
      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('button-prettify')).toHaveAttribute(
          'title',
          'Prettify Query (Shift-Ctrl-P)'
        );
        expect(screen.getByTestId('button-history')).toHaveAttribute('title', 'Show History');
        expect(screen.getByTestId('button-explorer')).toHaveAttribute('title', 'Toggle Explorer');
      });
    });
  });

  describe('GraphiQLExplorer integration', () => {
    it('should call handleRunQuery when explorer triggers onRunOperation', async () => {
      const mockHandleRunQuery = jest.fn();

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: mockHandleRunQuery,
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: jest.fn(() => ({
              setOption: jest.fn(),
              options: {},
            })),
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('explorer-run')).toBeInTheDocument();
      });

      const runButton = screen.getByTestId('explorer-run');
      fireEvent.click(runButton);

      expect(mockHandleRunQuery).toHaveBeenCalledWith('testOp');
    });
  });

  describe('Editor configuration', () => {
    it('should configure editor extra keys after timeout', async () => {
      const mockSetOption = jest.fn();
      const mockGetQueryEditor = jest.fn(() => ({
        setOption: mockSetOption,
        options: { extraKeys: { 'Ctrl-Space': 'autocomplete' } },
      }));

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: mockGetQueryEditor,
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      render(<GraphiQLClient />);

      await waitFor(() => {
        expect(mockGetQueryEditor).not.toHaveBeenCalled();
      });

      // Fast-forward through setTimeout
      act(() => {
        jest.runAllTimers();
      });

      await waitFor(() => {
        expect(mockGetQueryEditor).toHaveBeenCalled();
        expect(mockSetOption).toHaveBeenCalledWith(
          'extraKeys',
          expect.objectContaining({
            'Ctrl-Space': 'autocomplete',
            'Shift-Alt-LeftClick': expect.any(Function),
          })
        );
      });
    });

    it('should handle missing editor gracefully', async () => {
      const mockGetQueryEditor = jest.fn(() => null);

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: mockGetQueryEditor,
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      render(<GraphiQLClient />);

      act(() => {
        jest.runAllTimers();
      });

      await waitFor(() => {
        expect(mockGetQueryEditor).toHaveBeenCalled();
      });

      // Should not throw error
      expect(screen.getByTestId('graphiql-component')).toBeInTheDocument();
    });
  });

  describe('handleInspectOperation functionality', () => {
    it('should handle inspect operation when Shift-Alt-LeftClick is triggered', async () => {
      const mockGetTokenAt = jest.fn(() => ({
        start: 0,
        end: 5,
      }));
      const mockIndexFromPos = jest.fn((pos) => pos.ch);

      const mockEditor = {
        setOption: jest.fn(),
        options: {},
        getTokenAt: mockGetTokenAt,
        indexFromPos: mockIndexFromPos,
      };

      const mockGetQueryEditor = jest.fn(() => mockEditor);
      let inspectHandler: any;

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: mockGetQueryEditor,
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      const mockDefinition = {
        loc: { start: 0, end: 10 },
        operation: 'query',
        name: { value: 'TestQuery' },
      };

      mockParse.mockReturnValue({
        definitions: [mockDefinition],
      } as any);
      mockGetOperationKind.mockReturnValue('query');
      mockGetOperationName.mockReturnValue('TestQuery');

      // Mock DOM element
      const mockElement = document.createElement('div');
      mockElement.scrollIntoView = jest.fn();
      document.querySelector = jest.fn(() => mockElement);

      render(<GraphiQLClient />);

      act(() => {
        jest.runAllTimers();
      });

      await waitFor(() => {
        expect(mockEditor.setOption).toHaveBeenCalled();
        const setOptionCall = mockEditor.setOption.mock.calls[0];
        inspectHandler = setOptionCall[1]['Shift-Alt-LeftClick'];
      });

      // Trigger the inspect handler
      const mousePos = { line: 0, ch: 2 };
      inspectHandler(mockEditor, mousePos);

      expect(mockGetTokenAt).toHaveBeenCalledWith(mousePos);
      expect(mockParse).toHaveBeenCalled();
      expect(mockGetOperationKind).toHaveBeenCalledWith(mockDefinition);
      expect(mockGetOperationName).toHaveBeenCalledWith(mockDefinition);
      expect(document.querySelector).toHaveBeenCalledWith(
        '.graphiql-explorer-root #query-TestQuery'
      );
      expect(mockElement.scrollIntoView).toHaveBeenCalled();
    });

    it('should handle parse error in handleInspectOperation', async () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation(() => {
        return null;
      });

      const mockEditor = {
        setOption: jest.fn(),
        options: {},
        getTokenAt: jest.fn(() => ({ start: 0, end: 5 })),
        indexFromPos: jest.fn((pos) => pos.ch),
      };

      const mockGetQueryEditor = jest.fn(() => mockEditor);
      let inspectHandler: any;

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: mockGetQueryEditor,
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      mockParse.mockReturnValue(null as unknown as DocumentNode);

      render(<GraphiQLClient />);

      act(() => {
        jest.runAllTimers();
      });

      await waitFor(() => {
        const setOptionCall = mockEditor.setOption.mock.calls[0];
        inspectHandler = setOptionCall[1]['Shift-Alt-LeftClick'];
      });

      const result = inspectHandler(mockEditor, { line: 0, ch: 2 });

      expect(consoleError).toHaveBeenCalledWith("Couldn't parse query document");
      expect(result).toBeNull();

      consoleError.mockRestore();
    });

    it('should handle missing definition location in handleInspectOperation', async () => {
      const consoleLog = jest.spyOn(console, 'log').mockImplementation(() => {
        return null;
      });

      const mockEditor = {
        setOption: jest.fn(),
        options: {},
        getTokenAt: jest.fn(() => ({ start: 0, end: 5 })),
        indexFromPos: jest.fn((pos) => pos.ch),
      };

      const mockGetQueryEditor = jest.fn(() => mockEditor);
      let inspectHandler: any;

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: mockGetQueryEditor,
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      mockParse.mockReturnValue({
        definitions: [{ operation: 'query' }], // Missing loc
      } as any);

      render(<GraphiQLClient />);

      act(() => {
        jest.runAllTimers();
      });

      await waitFor(() => {
        const setOptionCall = mockEditor.setOption.mock.calls[0];
        inspectHandler = setOptionCall[1]['Shift-Alt-LeftClick'];
      });

      const result = inspectHandler(mockEditor, { line: 0, ch: 2 });

      expect(consoleLog).toHaveBeenCalledWith('Missing location information for definition');
      expect(result).toBeFalsy();

      consoleLog.mockRestore();
    });

    it('should handle no matching definition in handleInspectOperation', async () => {
      const consoleError = jest.spyOn(console, 'error').mockImplementation(() => {
        return null;
      });

      const mockEditor = {
        setOption: jest.fn(),
        options: {},
        getTokenAt: jest.fn(() => ({ start: 0, end: 5 })),
        indexFromPos: jest.fn(() => 2),
      };

      const mockGetQueryEditor = jest.fn(() => mockEditor);
      let inspectHandler: any;

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: mockGetQueryEditor,
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      mockParse.mockReturnValue({
        definitions: [
          {
            loc: { start: 100, end: 200 }, // Position doesn't match
          },
        ],
      } as any);

      render(<GraphiQLClient />);

      act(() => {
        jest.runAllTimers();
      });

      await waitFor(() => {
        const setOptionCall = mockEditor.setOption.mock.calls[0];
        inspectHandler = setOptionCall[1]['Shift-Alt-LeftClick'];
      });

      const result = inspectHandler(mockEditor, { line: 0, ch: 2 });

      expect(consoleError).toHaveBeenCalledWith(
        'Unable to find definition corresponding to mouse position'
      );
      expect(result).toBeNull();

      consoleError.mockRestore();
    });
  });

  describe('Effect dependencies', () => {
    it('should re-run effect when query changes', async () => {
      const mockEditor = {
        setOption: jest.fn(),
        options: {},
      };

      const mockGetQueryEditor = jest.fn(() => mockEditor);

      mockGraphiQL.mockImplementation(({ children, ref }: any) => {
        if (ref) {
          ref({
            handleRunQuery: jest.fn(),
            handlePrettifyQuery: jest.fn(),
            handleToggleHistory: jest.fn(),
            getQueryEditor: mockGetQueryEditor,
          });
        }
        return (<div data-testid="graphiql-component">{children}</div>) as any;
      });

      render(<GraphiQLClient />);

      act(() => {
        jest.runAllTimers();
      });

      const initialCallCount = mockEditor.setOption.mock.calls.length;

      // Trigger query update
      const editButton = screen.getByTestId('explorer-edit');
      fireEvent.click(editButton);

      act(() => {
        jest.runAllTimers();
      });

      await waitFor(() => {
        expect(mockEditor.setOption.mock.calls.length).toBeGreaterThan(initialCallCount);
      });
    });
  });

  describe('Multiple renders', () => {
    it('should handle re-renders correctly', async () => {
      const { rerender } = render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-component')).toBeInTheDocument();
      });

      rerender(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-component')).toBeInTheDocument();
      });
    });

    it('should maintain state across re-renders', async () => {
      const { rerender } = render(<GraphiQLClient />);

      await waitFor(() => {
        expect(screen.getByTestId('explorer-toggle')).toBeInTheDocument();
      });

      // Toggle explorer
      fireEvent.click(screen.getByTestId('explorer-toggle'));

      await waitFor(() => {
        expect(screen.getByTestId('graphiql-explorer')).toHaveAttribute(
          'data-explorer-open',
          'false'
        );
      });

      // Re-render
      rerender(<GraphiQLClient />);

      // State should be maintained
      await waitFor(() => {
        expect(screen.getByTestId('graphiql-explorer')).toHaveAttribute(
          'data-explorer-open',
          'false'
        );
      });
    });
  });
});
