import { Text } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import SecondaryHDPLayout from './SecondaryHDPLayout';

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Container: ({ children }: any) => <div data-testid="Container">{children}</div>,
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

describe('SecondaryHDPLayout', () => {
  it('should render a <SecondaryHDPLayout> with children', async function () {
    const { getByText, findByTestId } = render(
      <SecondaryHDPLayout>
        <Text>test</Text>
      </SecondaryHDPLayout>,
      {}
    );
    expect(await findByTestId('Header')).toBeInTheDocument();
    expect(getByText('test')).toBeInTheDocument();
  });
});
