import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import AddToWalletLayout from './AddToWalletLayout';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
  Container: ({ children }: any) => <div data-testid="Container">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: (props: any) => <div data-testid="Header">{props.children}</div>,
}));

describe('PriceFinderLayout', () => {
  it('should render a <PriceFinderLayout> with children', async function () {
    const { getByText, findByTestId } = render(
      <AddToWalletLayout>
        <p>Add to Wallet</p>
      </AddToWalletLayout>
    );

    await findByTestId('Header');
    getByText('Add to Wallet');
  });
});
