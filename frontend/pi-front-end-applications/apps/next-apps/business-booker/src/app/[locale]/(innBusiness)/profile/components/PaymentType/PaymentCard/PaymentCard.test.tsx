import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';

import PaymentCard from './PaymentCard';

jest.mock('@whitbread-eos/atoms/ui', () => ({
  __esModule: true,
  Button: function Button({
    children,
    onClick,
    className,
    'data-testid': testId,
  }: {
    children: React.ReactNode;
    onClick: () => void;
    className: string;
    'data-testid': string;
  }) {
    return (
      <button onClick={onClick} className={className} data-testid={testId}>
        {children}
      </button>
    );
  },
  Notification: function Notification({ message }: { message: string }) {
    return <div>{message}</div>;
  },
  useToast: () => ({
    toast: jest.fn(),
  }),

  CardIcon: function CardIcon({ type, className }: { type: string; className: string }) {
    return (
      <div data-testid="CardIcon" className={className}>
        {type}
      </div>
    );
  },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  __esModule: true,
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  formatIBAssetsUrl: (url: string) => url,
}));

jest.mock('~components/innBusiness/DeleteModal/DeleteModal', () => ({
  __esModule: true,
  default: function DeleteModal({
    isOpen,
    onClose,
    onConfirm,
  }: {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
  }) {
    return isOpen ? (
      <div role="dialog">
        <button onClick={onClose}>Close</button>
        <button onClick={onConfirm}>Confirm</button>
      </div>
    ) : null;
  },
}));

describe('PaymentCard', () => {
  const mockProfileDetails = {
    paymentPreference: {
      paymentCard: {
        cardNumber: '4111111111111111',
        cardHolderName: 'John Doe',
        expiryDate: '12/25',
        cardType: 'VI',
      },
    },
  };

  const mockIcons = {
    'icon.notification.success': 'success-icon-url',
    'icon.notification.error': 'error-icon-url',
    'icon.notification.info': 'info-icon-url',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the payment card with correct details', () => {
    render(<PaymentCard profileDetails={mockProfileDetails} icons={mockIcons} />);

    expect(screen.getByTestId('PaymentCard-Container')).toBeInTheDocument();
    expect(screen.getByText('payment.title')).toBeInTheDocument();
    expect(screen.getByText('Visa Debit')).toBeInTheDocument();
    expect(screen.getByText('•••• •••• •••• 1111')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('payment.card.expires 12/25')).toBeInTheDocument();
  });

  it('shows InnBusiness Pay notification when card type is PIBA EURO', () => {
    const innBusinessProfile = {
      paymentPreference: {
        paymentCard: {
          ...mockProfileDetails.paymentPreference.paymentCard,
          cardType: 'BD',
        },
      },
    };

    render(<PaymentCard profileDetails={innBusinessProfile} icons={mockIcons} />);
    expect(screen.getByText('payment.paymentType.notification.PIBA.EURO')).toBeInTheDocument();
  });

  it('shows InnBusiness Pay notification when card type is PIBA UK', () => {
    const innBusinessProfile = {
      paymentPreference: {
        paymentCard: {
          ...mockProfileDetails.paymentPreference.paymentCard,
          cardType: 'AT',
        },
      },
    };

    render(<PaymentCard profileDetails={innBusinessProfile} icons={mockIcons} />);
    expect(screen.getByText('payment.paymentType.notification.PIBA.UK')).toBeInTheDocument();
  });

  it('handles delete button click and shows modal', () => {
    render(<PaymentCard profileDetails={mockProfileDetails} icons={mockIcons} />);

    const deleteButton = screen.getByTestId('PaymentCard-Delete-Button');
    fireEvent.click(deleteButton);

    expect(screen.getByRole('dialog')).toBeInTheDocument();
  });

  it('calls onAddNew when add new button is clicked', () => {
    const onAddNew = jest.fn();
    render(
      <PaymentCard profileDetails={mockProfileDetails} icons={mockIcons} onAddNew={onAddNew} />
    );

    const addButton = screen.getByTestId('PaymentCard-Add-New-Button');
    fireEvent.click(addButton);

    expect(onAddNew).toHaveBeenCalledTimes(1);
  });

  it('calls onDelete when delete is confirmed', async () => {
    const onDelete = jest.fn().mockResolvedValue(undefined);
    render(
      <PaymentCard
        profileDetails={{
          paymentPreference: {
            paymentCard: {
              cardNumber: '4111111111111111',
              cardHolderName: 'John Doe',
              expiryDate: '12/25',
              cardType: 'VI',
            },
          },
        }}
        icons={{}}
        onDelete={onDelete}
      />
    );
    fireEvent.click(screen.getByTestId('PaymentCard-Delete-Button'));
    fireEvent.click(screen.getByText('Confirm'));
    await screen.findByTestId('PaymentCard-Delete-Button');
    expect(onDelete).toHaveBeenCalled();
  });

  it('closes delete modal when close button is clicked', () => {
    render(
      <PaymentCard
        profileDetails={{
          paymentPreference: {
            paymentCard: {
              cardNumber: '4111111111111111',
              cardHolderName: 'John Doe',
              expiryDate: '12/25',
              cardType: 'VI',
            },
          },
        }}
        icons={{}}
      />
    );
    fireEvent.click(screen.getByTestId('PaymentCard-Delete-Button'));
    fireEvent.click(screen.getByText('Close'));
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
  });

  it('renders unknown card type if cardType is not in CARD_TYPE_DISPLAY', () => {
    render(
      <PaymentCard
        profileDetails={{
          paymentPreference: {
            paymentCard: {
              cardNumber: '4111111111111111',
              cardHolderName: 'John Doe',
              expiryDate: '12/25',
              cardType: 'ZZ',
            },
          },
        }}
        icons={{}}
      />
    );
    const zzElements = screen.getAllByText('ZZ');
    expect(zzElements.length).toBeGreaterThanOrEqual(1);
  });

  it('renders CardIcon with correct type and icons', () => {
    render(
      <PaymentCard
        profileDetails={{
          paymentPreference: {
            paymentCard: {
              cardNumber: '4111111111111111',
              cardHolderName: 'John Doe',
              expiryDate: '12/25',
              cardType: 'VI',
            },
          },
        }}
        icons={{ test: 'icon-url' }}
      />
    );
    expect(screen.getByTestId('CardIcon')).toBeInTheDocument();
  });

  it('does not throw if onDelete is not provided', async () => {
    render(
      <PaymentCard
        profileDetails={{
          paymentPreference: {
            paymentCard: {
              cardNumber: '4111111111111111',
              cardHolderName: 'John Doe',
              expiryDate: '12/25',
              cardType: 'VI',
            },
          },
        }}
        icons={{}}
      />
    );
    fireEvent.click(screen.getByTestId('PaymentCard-Delete-Button'));
    fireEvent.click(screen.getByText('Confirm'));
    await screen.findByTestId('PaymentCard-Delete-Button');
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
  });
});
