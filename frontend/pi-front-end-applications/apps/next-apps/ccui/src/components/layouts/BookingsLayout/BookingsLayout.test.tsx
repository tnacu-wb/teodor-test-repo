import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import BookingsLayout from './BookingsLayout';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
  FooterWrapper: ({ children }: any) => <div data-testid="Footer">{children}</div>,
}));

describe('BookingsLayout', () => {
  it('should render a <BookingsLayout> with children', function () {
    const { getByText, findByTestId } = render(
      <BookingsLayout>
        <p>test</p>
      </BookingsLayout>,
      {}
    );

    findByTestId('Header');
    findByTestId('Footer');
    getByText('test');
  });
});
