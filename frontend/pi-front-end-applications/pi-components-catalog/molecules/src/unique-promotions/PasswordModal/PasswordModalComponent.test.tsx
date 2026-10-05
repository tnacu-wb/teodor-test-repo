import '@testing-library/jest-dom';
import { render, screen, fireEvent, act, within } from '@testing-library/react';

import PasswordModal from './PasswordModal.component';
import { PASSWORD_DURATION } from './common';

jest.mock('@whitbread-eos/utils', () => ({
  usePromoTranslation: () => ({
    oneTimePasswordOpenFileText: 'One time password',
    downloadFileText: 'Download file',
    accessFileInstruction: 'Use password to access file',
  }),
}));

const mockSetShowModal = jest.fn();

const mockContext = {
  showModal: true,
  setShowModal: mockSetShowModal,
  batchIdData: { password: 'SECRET123' },
};
jest.mock('lucide-react', () => ({
  Copy: ({ onClick }: any) => (
    <button data-testid="copy-icon" onClick={onClick}>
      copy
    </button>
  ),
  Check: () => <div data-testid="check-icon">check</div>,
}));

jest.mock('../List/usePromoBatchesTableContext', () => ({
  usePromoTableContext: () => mockContext,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ModalVariants: ({ children, onClose }: any) => (
    <div>
      <button data-testid="modal-close" onClick={onClose}>
        close
      </button>
      {children}
    </div>
  ),
}));

jest.mock('./common', () => ({
  PASSWORD_DURATION: 5,
  TimerStatus: ({ timeLeft }: any) => <div data-testid="timer-status">{timeLeft}</div>,
}));

describe('PasswordModal', () => {
  test('renders password modal with password and texts', () => {
    render(<PasswordModal />);

    expect(screen.getByText('Download file')).toBeInTheDocument();
    expect(screen.getByText('One time password')).toBeInTheDocument();
    expect(screen.getByText('Use password to access file')).toBeInTheDocument();
    expect(screen.getByText('SECRET123')).toBeInTheDocument();
  });

  test('does not start timer when modal is closed and batchIdData is missing', () => {
    jest.useFakeTimers();

    mockContext.showModal = false;
    mockContext.batchIdData = { password: '' };

    render(<PasswordModal />);

    act(() => {
      jest.advanceTimersByTime(3000);
    });

    // No crash, no timer text change
    expect(screen.queryByTestId('timer-status')).toBeInTheDocument();

    jest.useRealTimers();
  });

  test('timer counts down and hides password when expired', () => {
    jest.useFakeTimers();

    mockContext.showModal = true;
    mockContext.batchIdData = { password: 'SECRET123' };

    render(<PasswordModal />);

    act(() => {
      jest.advanceTimersByTime(PASSWORD_DURATION * 1000);
    });

    expect(screen.getByTestId('timer-status')).toHaveTextContent('0');
    expect(screen.queryByText('SECRET123')).not.toBeInTheDocument();

    jest.useRealTimers();
  });

  test('copies password to clipboard when copy icon is clicked', async () => {
    jest.useFakeTimers();

    const writeTextMock = jest.fn().mockResolvedValue(undefined);

    Object.assign(navigator, {
      clipboard: {
        writeText: writeTextMock,
      },
    });

    render(<PasswordModal />);

    const copyIcon = screen.getByTestId('copy-icon');

    await act(async () => {
      fireEvent.click(copyIcon);
    });

    expect(writeTextMock).toHaveBeenCalledWith('SECRET123');

    jest.useRealTimers();
  });

  test('calls setShowModal(false) when modal onClose is triggered', () => {
    render(<PasswordModal />);

    fireEvent.click(screen.getByTestId('modal-close'));

    expect(mockSetShowModal).toHaveBeenCalledWith(false);
  });

  test('shows check icon after copying password', async () => {
    Object.assign(navigator, {
      clipboard: {
        writeText: jest.fn().mockResolvedValue(undefined),
      },
    });

    render(<PasswordModal />);

    fireEvent.click(screen.getByTestId('copy-icon'));

    expect(screen.getByTestId('check-icon')).toBeInTheDocument();
  });

  test('resets copied state after timeout', async () => {
    jest.useFakeTimers();

    Object.assign(navigator, {
      clipboard: {
        writeText: jest.fn().mockResolvedValue(undefined),
      },
    });

    render(<PasswordModal />);

    fireEvent.click(screen.getByTestId('copy-icon'));

    expect(screen.getByTestId('check-icon')).toBeInTheDocument();

    act(() => {
      jest.advanceTimersByTime(2000);
    });

    expect(screen.getByTestId('copy-icon')).toBeInTheDocument();

    jest.useRealTimers();
  });

  test('executes setTimeout callback and resets copied state (Sonar fix)', async () => {
    jest.useFakeTimers();

    const writeTextMock = jest.fn().mockResolvedValue(undefined);

    Object.assign(navigator, {
      clipboard: {
        writeText: writeTextMock,
      },
    });

    render(<PasswordModal />);

    await act(async () => {
      fireEvent.click(screen.getByTestId('copy-icon'));
    });

    const passwordText = screen.getByText('SECRET123');
    const passwordRow = passwordText.closest('div')!;

    expect(within(passwordRow).getByTestId('check-icon')).toBeInTheDocument();

    act(() => {
      jest.runOnlyPendingTimers();
    });

    expect(within(passwordRow).getByTestId('copy-icon')).toBeInTheDocument();

    jest.useRealTimers();
  });
});
