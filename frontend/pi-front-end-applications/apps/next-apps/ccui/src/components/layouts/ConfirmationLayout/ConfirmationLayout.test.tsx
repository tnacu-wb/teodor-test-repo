import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import ConfirmationLayout from './ConfirmationLayout';

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  Header: ({ children }: any) => <div data-testid="Header">{children}</div>,
}));

describe('ConfirmationLayout', () => {
  it('should render a <ConfirmationLayout> with children', function () {
    const { getByText, findByTestId } = render(
      <ConfirmationLayout>
        <p>test</p>
      </ConfirmationLayout>
    );

    findByTestId('Header');
    getByText('test');
  });
});
