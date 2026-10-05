import '@testing-library/jest-dom';
import { render, screen, act, fireEvent } from '@testing-library/react';

import { SomethingWentWrongContent } from './something-went-wrong-content';

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: jest.fn(() => ({
    t: jest.fn((key) => key),
  })),
}));

const mockProps = {
  testId: 'SomethingWentWrong-Content',
  className: 'classNameA',
  handleRetry: jest.fn(),
};

describe('SomethingWentWrongContent', () => {
  it('should render the component', async () => {
    render(<SomethingWentWrongContent {...mockProps} />);

    expect(screen.getByText('application.sent.failed.message')).toBeInTheDocument();
    expect(screen.getByText('application.sent.tryAgain')).toBeInTheDocument();
    const tryAgainButton = screen.getByTestId('SomethingWentWrong-Content-retry-button');
    expect(tryAgainButton).not.toBeDisabled();
    await act(async () => {
      fireEvent.click(tryAgainButton);
    });
  });
});
