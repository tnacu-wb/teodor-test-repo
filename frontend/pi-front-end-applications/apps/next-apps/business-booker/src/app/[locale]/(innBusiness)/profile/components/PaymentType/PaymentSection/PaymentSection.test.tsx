import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import * as React from 'react';

import PaymentSection from './PaymentSection';

const mockRouter = {
  refresh: jest.fn(),
  replace: jest.fn(),
  push: jest.fn(),
};

jest.mock('next/navigation', () => ({
  useRouter: () => mockRouter,
}));

const mockToast = jest.fn();
jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: function Button(props: any) {
    return <button onClick={props.onClick}>{props.children}</button>;
  },
  Notification: function Notification(props: any) {
    return <div>{props.message}</div>;
  },
  useToast: () => ({
    toast: mockToast,
  }),
  ButtonVariantDescriptor: { truncateWithEllipsisButton: 'truncateWithEllipsisButton' },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getPathForLocale: jest.fn((locale: string, path: string) => `/${locale}/${path}`),
  useTranslation: () => ({ t: (key: string) => key, language: 'en' }),
  getCountriesList: jest.fn(() =>
    Promise.resolve([
      { countryCode: 'DE', countryName: 'Germany' },
      { countryCode: 'GB', countryName: 'United Kingdom' },
    ])
  ),
  formatIBAssetsUrl: (url: string) => url,
  updateProfileDetails: jest.fn(() => Promise.resolve({ status: 'success' })),
}));

jest.mock('@whitbread-eos/utils', () => ({
  getAuthCookie: jest.fn(() => 'mock-token'),
}));

jest.mock('../PaymentCard/PaymentCard', () => {
  return function MockPaymentCard(props: any) {
    return (
      <div data-testid="payment-card">
        <button onClick={props.onAddNew} data-testid="add-new-button">
          Add New
        </button>
        <button onClick={props.onDelete} data-testid="delete-button">
          Delete Card
        </button>
      </div>
    );
  };
});

jest.mock('../PaymentTypeCta/PaymentTypeCta', () => {
  return function MockPaymentTypeCta(props: any) {
    return (
      <button onClick={props.onClick} data-testid="payment-type-cta">
        Add Payment Method
      </button>
    );
  };
});

jest.mock('../PaymentTypeSelection/PaymentTypeSelection', () => {
  return function MockPaymentTypeSelection(props: any) {
    return (
      <div data-testid="payment-type-selection">
        <div data-testid="user-address">{props.userAddress}</div>
        <button onClick={() => props.onSubmit({ cardDetails: true })} data-testid="submit-button">
          Submit
        </button>
        <button onClick={props.onCancel} data-testid="cancel-button">
          Cancel
        </button>
      </div>
    );
  };
});

jest.mock('../../../../manage/cards/components/revalidate-link', () => ({
  revalidateCacheOnLink: () => jest.fn(() => Promise.resolve()),
}));

describe('PaymentSection', () => {
  const mockIcons = {
    'icon.card': 'card-icon.svg',
    'icon.notification.success': 'success-icon.svg',
    'icon.notification.error': 'error-icon.svg',
  };

  const mockProfileDetails = {
    contactDetail: {
      address: {
        line1: '123 Test St',
        line2: 'Area 51',
        line4: 'London',
        postCode: 'SW1A 1AA',
        countryCode: 'GB',
      },
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders PaymentCard when hasPaymentCard is true', () => {
    render(
      <PaymentSection
        hasPaymentCard={true}
        icons={mockIcons}
        profileDetails={mockProfileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={jest.fn()}
        onIsEditableToggle={jest.fn()}
        onIsUpdatingToggle={jest.fn()}
        onIsDirtyToggle={jest.fn()}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );

    expect(screen.getByTestId('payment-card')).toBeInTheDocument();
    expect(screen.queryByTestId('payment-type-cta')).not.toBeInTheDocument();
  });

  it('shows PaymentTypeSelection when Add New is clicked', async () => {
    render(
      <PaymentSection
        hasPaymentCard={true}
        icons={mockIcons}
        profileDetails={mockProfileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={jest.fn()}
        onIsEditableToggle={jest.fn()}
        onIsUpdatingToggle={jest.fn()}
        onIsDirtyToggle={jest.fn()}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );

    fireEvent.click(screen.getByTestId('add-new-button'));

    await waitFor(() => {
      expect(screen.getByTestId('payment-type-selection')).toBeInTheDocument();
    });
  });

  it('formats address correctly with country name', async () => {
    render(
      <PaymentSection
        hasPaymentCard={true}
        icons={mockIcons}
        profileDetails={mockProfileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={jest.fn()}
        onIsEditableToggle={jest.fn()}
        onIsUpdatingToggle={jest.fn()}
        onIsDirtyToggle={jest.fn()}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );

    fireEvent.click(screen.getByTestId('add-new-button'));

    await waitFor(() => {
      const addressText = screen.getByTestId('user-address').textContent;
      expect(addressText).toBe('123 Test St, Area 51, London, SW1A 1AA, United Kingdom');
    });
  });

  it('handles German country code transformation', async () => {
    const germanProfile = {
      contactDetail: {
        address: {
          line1: 'Teststraße 1',
          line4: 'Berlin',
          postCode: '10115',
          countryCode: 'D',
        },
      },
    };

    render(
      <PaymentSection
        hasPaymentCard={true}
        icons={mockIcons}
        profileDetails={germanProfile}
        locale={LOCALES.DE}
        onReviewChangesToggle={jest.fn()}
        onIsEditableToggle={jest.fn()}
        onIsUpdatingToggle={jest.fn()}
        onIsDirtyToggle={jest.fn()}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );

    fireEvent.click(screen.getByTestId('add-new-button'));

    await waitFor(() => {
      const addressText = screen.getByTestId('user-address').textContent;
      expect(addressText).toBe('Teststraße 1, Berlin, 10115, Germany');
    });
  });

  it('hides PaymentTypeSelection when cancel is clicked', async () => {
    render(
      <PaymentSection
        hasPaymentCard={true}
        icons={mockIcons}
        profileDetails={mockProfileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={jest.fn()}
        onIsEditableToggle={jest.fn()}
        onIsUpdatingToggle={jest.fn()}
        onIsDirtyToggle={jest.fn()}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );

    fireEvent.click(screen.getByTestId('add-new-button'));
    await waitFor(() => {
      expect(screen.getByTestId('payment-type-selection')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('cancel-button'));
    expect(screen.queryByTestId('payment-type-selection')).not.toBeInTheDocument();
    expect(screen.getByTestId('payment-card')).toBeInTheDocument();
  });

  it('calls router.refresh when card is deleted', async () => {
    render(
      <PaymentSection
        hasPaymentCard={true}
        icons={mockIcons}
        profileDetails={mockProfileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={jest.fn()}
        onIsEditableToggle={jest.fn()}
        onIsUpdatingToggle={jest.fn()}
        onIsDirtyToggle={jest.fn()}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );

    fireEvent.click(screen.getByTestId('delete-button'));

    await waitFor(() => {
      expect(mockRouter.refresh).toHaveBeenCalled();
    });
  });

  it('calls router.refresh when new card is submitted', async () => {
    render(
      <PaymentSection
        hasPaymentCard={true}
        icons={mockIcons}
        profileDetails={mockProfileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={jest.fn()}
        onIsEditableToggle={jest.fn()}
        onIsUpdatingToggle={jest.fn()}
        onIsDirtyToggle={jest.fn()}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );

    fireEvent.click(screen.getByTestId('add-new-button'));
    await waitFor(() => {
      expect(screen.getByTestId('payment-type-selection')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('submit-button'));
    await waitFor(() => {
      expect(mockRouter.refresh).toHaveBeenCalled();
    });
  });
});
