import PaymentLayout from '.';
import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

describe('PaymentLayout', () => {
  it('should render a <PaymentLayout> with children', async function () {
    const { getByText, findByTestId } = render(
      <PaymentLayout>
        <div>test</div>
      </PaymentLayout>,
      {}
    );

    findByTestId('Header');
    getByText('test');
  });

  it('should render a <PaymentLayout business booker> with children and steps = 3', function () {
    const { getByText, findByTestId } = render(
      <PaymentLayout>
        <div>test</div>
      </PaymentLayout>,
      {}
    );

    findByTestId('Header');
    getByText('test');
  });
});
