import GuestDetailsLayout from '.';
import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));
jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

describe('GuestDetailsLayout', () => {
  it('should render a <GuestDetailsLayout> with children', function () {
    const { getByText, findByTestId } = render(
      <GuestDetailsLayout>
        <div>test guest details</div>
      </GuestDetailsLayout>
    );

    findByTestId('Header');
    getByText('test guest details');
  });
});
