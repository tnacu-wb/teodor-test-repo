import GuestDetailsLayout from '.';

import { render } from '../../../utils/test-utils';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

describe('AncillariesLayout', () => {
  it('should render a <AncillariesLayout> with children', function () {
    const { findByTestId, getByText } = render(
      <GuestDetailsLayout>
        <p>test</p>
      </GuestDetailsLayout>,
      {}
    );
    findByTestId('Header');
    getByText('test');
  });
});
