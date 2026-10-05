import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { TypeOfDeleteModal } from '@whitbread-eos/api';

import DeleteModal from './DeleteModal';

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({
    children,
    onClick,
    'data-testid': testId,
  }: {
    children: React.ReactNode;
    onClick: () => void;
    'data-testid': string;
  }) => (
    <button onClick={onClick} data-testid={testId}>
      {children}
    </button>
  ),
  Dialog: ({ children, open }: { children: React.ReactNode; open: boolean }) =>
    open ? <div>{children}</div> : null,
  DialogContent: ({
    children,
    'data-testid': testId,
  }: {
    children: React.ReactNode;
    'data-testid': string;
  }) => <div data-testid={testId}>{children}</div>,
  DialogHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogTitle: ({ children }: { children: React.ReactNode }) => (
    <h2 data-testid="modal-title">{children}</h2>
  ),
  DialogFooter: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  SanitizedContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  cn: jest.fn(),
}));

describe('DeleteModal', () => {
  const mockProps = {
    isOpen: true,
    onClose: jest.fn(),
    onConfirm: jest.fn(),
    variant: TypeOfDeleteModal.Card,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the modal when isOpen is true', () => {
    render(<DeleteModal {...mockProps} />);

    expect(screen.getByTestId(`DeleteModal-${mockProps.variant}`)).toBeInTheDocument();
    expect(screen.getByTestId('modal-title')).toHaveTextContent(
      'profile.profile.deleteCard.confirm'
    );
    expect(screen.getByText('profile.profile.deleteCard.confirmation')).toBeInTheDocument();
  });

  it('does not render the modal when isOpen is false', () => {
    render(<DeleteModal {...mockProps} isOpen={false} />);

    expect(screen.queryByTestId(`DeleteModal-${mockProps.variant}`)).not.toBeInTheDocument();
  });

  it('calls onClose when cancel button is clicked', () => {
    render(<DeleteModal {...mockProps} />);

    const cancelButton = screen.getByTestId(`DeleteModal-${mockProps.variant}-CancelButton`);
    fireEvent.click(cancelButton);

    expect(mockProps.onClose).toHaveBeenCalledTimes(1);
  });

  it('calls onConfirm when delete button is clicked', () => {
    render(<DeleteModal {...mockProps} />);

    const deleteButton = screen.getByTestId(`DeleteModal-${mockProps.variant}-DeleteButton`);
    fireEvent.click(deleteButton);

    expect(mockProps.onConfirm).toHaveBeenCalledTimes(1);
  });

  it('renders buttons with correct text', () => {
    render(<DeleteModal {...mockProps} />);

    const cancelButton = screen.getByTestId(`DeleteModal-${mockProps.variant}-CancelButton`);
    const deleteButton = screen.getByTestId(`DeleteModal-${mockProps.variant}-DeleteButton`);

    expect(cancelButton).toHaveTextContent('profile.profile.deleteCard.cancel');
    expect(deleteButton).toHaveTextContent('profile.profile.deleteCard.confirm');
  });
  it('renders buttons with correct text but with different variant (question)', () => {
    mockProps.variant = TypeOfDeleteModal.Question;
    render(<DeleteModal {...mockProps} />);

    const cancelButton = screen.getByTestId(`DeleteModal-${mockProps.variant}-CancelButton`);
    const deleteButton = screen.getByTestId(`DeleteModal-${mockProps.variant}-DeleteButton`);

    expect(cancelButton).toHaveTextContent('company.coMngt.questions.delete.cancelButton');
    expect(deleteButton).toHaveTextContent('company.coMngt.questions.delete.DeleteButton');
  });
});
