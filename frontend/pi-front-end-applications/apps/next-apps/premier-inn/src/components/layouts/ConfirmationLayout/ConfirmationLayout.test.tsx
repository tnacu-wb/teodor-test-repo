import ConfirmationLayout from '.';
import { Text } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

describe('ConfirmationLayout', () => {
  it('should render a <ConfirmationLayout> with children', async function () {
    const { getByText, findByTestId } = render(
      <ConfirmationLayout>
        <Text>test</Text>
      </ConfirmationLayout>,
      {}
    );
    expect(await findByTestId('Header')).toBeInTheDocument();
    expect(getByText('test')).toBeInTheDocument();
  });
});
