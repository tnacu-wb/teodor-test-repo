import PaymentLayout from '.';
import { Text } from '@chakra-ui/react';
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
    const { getByText } = render(
      <PaymentLayout>
        <Text>test</Text>
      </PaymentLayout>,
      {}
    );
    expect(getByText('test')).toBeInTheDocument();
  });
});
