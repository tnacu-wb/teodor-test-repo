import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import DefaultLayout from './DefaultLayout';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
  Container: ({ children }: any) => <div data-testid="Container">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
  FooterWrapper: ({ children }: any) => <div data-testid="Footer">{children}</div>,
}));

describe('DefaultLayout', () => {
  it('should render a <DefaultLayout> with children', function () {
    const { getByText, findByTestId, getAllByTestId } = render(
      <DefaultLayout>
        <div>test</div>
      </DefaultLayout>,
      {}
    );

    getByText('test');
    findByTestId('Footer');
    findByTestId('Header');
    getAllByTestId('Container');
    getAllByTestId('ErrorBoundary');
  });
});
