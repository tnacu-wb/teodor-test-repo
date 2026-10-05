import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import ErrorBoundary from './ErrorBoundary';

jest.mock(
  './StaticFooter',
  () =>
    function StaticFooter() {
      return <div>Static Footer Mock</div>;
    }
);

jest.mock(
  './StaticHeader',
  () =>
    function StaticHeader() {
      return <div>Static Header Mock</div>;
    }
);

const NoErrorComponent = () => <span>No error component</span>;
const ErrorComponent = () => {
  const error = new Error('Intentional created error message inside test file');
  throw error;
};

describe('ErrorBoundary', function () {
  const consoleError = console.error;
  beforeAll(() => {
    console.error = () => {};
  });
  afterAll(() => {
    console.error = consoleError;
  });

  it('Should render component inside ErrorBoundary', () => {
    const { getByText } = render(
      <ErrorBoundary>
        <NoErrorComponent />
      </ErrorBoundary>
    );
    expect(getByText('No error component')).toBeInTheDocument();
  });

  it('Should render error when rendering a component that errors', () => {
    const { getByText } = render(
      <ErrorBoundary>
        <ErrorComponent />
      </ErrorBoundary>
    );
    expect(getByText('errors.sorry')).toBeInTheDocument();
  });

  it('Should render StaticFooter content when used for footer', () => {
    const { getByText } = render(
      <ErrorBoundary isFooterBoundary={true}>
        <ErrorComponent />
      </ErrorBoundary>
    );
    expect(getByText('Static Footer Mock')).toBeInTheDocument();
  });

  it('Should render StaticHeader content when used for header', () => {
    const { getByText } = render(
      <ErrorBoundary isHeaderBoundary={true}>
        <ErrorComponent />
      </ErrorBoundary>
    );
    expect(getByText('Static Header Mock')).toBeInTheDocument();
  });

  it('Should render null when error and content is suppressed', () => {
    const { queryByText } = render(
      <ErrorBoundary noContentBoundary={true}>
        <ErrorComponent />
      </ErrorBoundary>
    );
    expect(queryByText('errors.sorry')).not.toBeInTheDocument();
  });

  it('Should render custom error message passed in as prop when available', () => {
    const { queryByText } = render(
      <ErrorBoundary errorMessage="custom error message">
        <ErrorComponent />
      </ErrorBoundary>
    );
    expect(queryByText('custom error message')).toBeInTheDocument();
  });

  it('Should render default error message when no custom error message is available', () => {
    const { queryByText } = render(
      <ErrorBoundary>
        <ErrorComponent />
      </ErrorBoundary>
    );
    expect(queryByText('errors.sorry')).toBeInTheDocument();
  });
});
