import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import PaymentLayout from './PaymentLayout';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
  Container: ({ children }: any) => <div data-testid="Container">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

describe('PaymentLayout', () => {
  it('should render a <PaymentLayout> with children', function () {
    const { getByText, findByTestId } = render(
      <PaymentLayout>
        <p>test</p>
      </PaymentLayout>
    );

    findByTestId('Header');
    getByText('test');
  });
});
