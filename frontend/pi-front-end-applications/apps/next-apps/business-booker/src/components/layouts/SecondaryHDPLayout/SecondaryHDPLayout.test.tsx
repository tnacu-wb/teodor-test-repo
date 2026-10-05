import SecondaryHDPLayout from '.';
import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/atoms', () => ({
  Container: ({ children }: any) => <div data-testid="Container">{children}</div>,
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

describe('SecondaryHDPLayout', () => {
  it('should render a <SecondaryHDPLayout> with children', async function () {
    const { getByText, findByTestId } = render(
      <SecondaryHDPLayout>
        <div>test</div>
      </SecondaryHDPLayout>,
      {}
    );

    findByTestId('Header');
    getByText('test');
  });
});
