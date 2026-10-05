import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import PriceFinderLayout from './PriceFinderLayout';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
  Container: ({ children }: any) => <div data-testid="Container">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: (props: any) => <div data-testid="Header">{props.children}</div>,
  FooterWrapper: (props: any) => <div data-testid="Footer-Wrapper">{props.children}</div>,
}));

describe('PriceFinderLayout', () => {
  it('should render a <PriceFinderLayout> with children', async function () {
    const { getByText, findByTestId } = render(
      <PriceFinderLayout>
        <p>Welcome to the amazing world of price finder!</p>
      </PriceFinderLayout>
    );

    await findByTestId('Header');
    await findByTestId('Footer-Wrapper');
    getByText('Welcome to the amazing world of price finder!');
  });
});
