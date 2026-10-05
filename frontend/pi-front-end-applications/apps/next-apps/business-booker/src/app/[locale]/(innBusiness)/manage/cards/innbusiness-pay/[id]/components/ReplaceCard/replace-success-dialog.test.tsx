import '@testing-library/jest-dom';
import { render, act, fireEvent } from '@testing-library/react';

import { ReplaceSuccessDialog } from './replace-success-dialog';

const mockProps = {
  open: true,
  onOpenChange: jest.fn(),
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    cn: jest.fn(),
  };
});

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
}));

describe('ReplaceSuccessDialog Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = { track: jest.fn() };
  });

  it('should render ReplaceSuccessDialog component and close it', async () => {
    const { getByTestId } = render(<ReplaceSuccessDialog {...mockProps} />);

    expect(getByTestId('Replace-Card-Success-Dialog')).toBeInTheDocument();

    const closeButton = getByTestId('Replace-Success-Back-Button');
    expect(closeButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(closeButton);
    });
  });
});
