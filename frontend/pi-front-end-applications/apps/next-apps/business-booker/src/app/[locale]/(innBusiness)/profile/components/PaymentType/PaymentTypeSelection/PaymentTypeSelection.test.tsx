import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import * as React from 'react';

import PaymentTypeSelection from './PaymentTypeSelection';

const mockUseFeatureToggle = jest.fn().mockReturnValue({});

const MockAddressType = {
  Business: 'business',
  Personal: 'personal',
  Unknown: 'unknown',
};

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  formatIBAssetsUrl: (url: string) => url,
}));

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  formatIBAssetsUrl: (url: string) => url,
  useFeatureToggle: () => mockUseFeatureToggle(),
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  AddressType: {
    Business: 'business',
    Personal: 'personal',
    Unknown: 'unknown',
  },
  ROOM_TYPE: {
    PREMIER_PLUS: 'PREMIER_PLUS',
    STANDARD: 'STANDARD',
  },
  Language: {
    en: 'en',
    de: 'de',
  },
  FT_IB_PAY_PIBA_EURO: 'release_ib_pay_piba_euro',
  FT_IB_PIBA_MEMORABLE_WORD_IFRAME: 'release_piba_cnp_iframe_split',
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: function Button(props: any) {
    return (
      <button
        onClick={props.onClick}
        disabled={props.disabled}
        type={props.type}
        data-testid={props['data-testid'] || 'button'}
      >
        {props.children}
      </button>
    );
  },
  Checkbox: function Checkbox(props: any) {
    return (
      <input
        type="checkbox"
        id={props.id}
        checked={props.checked}
        onChange={(e) => props.onCheckedChange(e.target.checked)}
        data-testid={props['data-testid']}
        role="checkbox"
        aria-checked={props.checked}
      />
    );
  },
  CardIcon: function CardIcon({ type, className }: { type: string; className: string }) {
    return (
      <div data-testid="CardIcon" className={className}>
        {type}
      </div>
    );
  },
  FormInput: function FormInput(props: any) {
    return (
      <input
        id={props.id}
        value={props.value || ''}
        onChange={(e) => props.onChange(e.target.value)}
        placeholder={props.placeholder}
        data-testid={props['data-testid'] || `${props.id}-input`}
      />
    );
  },
  ButtonVariantDescriptor: { truncateWithEllipsisButton: 'truncateWithEllipsisButton' },
}));

jest.mock(
  '~components/innBusiness/ReviewChanges',
  () => ({
    ReviewChanges: ({ isOpen, onDiscard, onContinue }: any) => {
      if (!isOpen) return null;
      return (
        <div data-testid="mock-review-changes">
          <button onClick={onDiscard} data-testid="discard-changes-button">
            Discard Changes
          </button>
          <button onClick={onContinue} data-testid="continue-editing-button">
            Continue Editing
          </button>
        </div>
      );
    },
  }),
  { virtual: true }
);

jest.mock('../DifferentAddressForm/DifferentAddressForm', () => {
  return function MockDifferentAddressForm(props: any) {
    const isBusinessAddress = props.addressType === MockAddressType.Business;
    const [showCompanyNameError, setShowCompanyNameError] = React.useState(false);

    React.useEffect(() => {
      if (props.companyNameFormRef?.current) {
        props.companyNameFormRef.current.requestSubmit = jest.fn(() => {
          const companyNameValue = props.companyName || '';

          if (!companyNameValue.trim()) {
            setShowCompanyNameError(true);
            return;
          }

          props.onCompanyNameSubmit({ companyName: companyNameValue });
        });
      }
      if (props.addressFormRef?.current) {
        props.addressFormRef.current.requestSubmit = jest.fn(() => {
          props.onAddressSubmit({
            addressLine1: '',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            addressLine5: '',
            postCode: '',
            country: '',
          });
        });
      }
      if (props.addressTypeFormRef?.current) {
        props.addressTypeFormRef.current.requestSubmit = jest.fn(() => {
          props.onAddressTypeSubmit({ addressType: props.addressType });
        });
      }
    }, [props]);

    return (
      <div data-testid="different-address-form">
        <button
          onClick={() => {
            props.onAddressTypeChange(MockAddressType.Business);
          }}
          data-testid="change-to-business-address"
        >
          Change to Business Address
        </button>
        <button
          onClick={() => {
            props.onAddressTypeChange(MockAddressType.Personal);
          }}
          data-testid="change-to-personal-address"
        >
          Change to Personal Address
        </button>
        {isBusinessAddress && (
          <div>
            <input
              type="text"
              value={props.companyName || ''}
              onChange={(e) => {
                props.onCompanyNameChange(e.target.value);
                setShowCompanyNameError(false);
              }}
              placeholder="Company name"
              data-testid="company-name-input"
            />
            {showCompanyNameError && (
              <div data-testid="company-name-error">
                company.coMngt.companyInfo.company.name.required
              </div>
            )}
          </div>
        )}
        <input type="text" placeholder="Address Line 1" data-testid="address-line-1-input" />
        <form ref={props.companyNameFormRef} data-testid="company-name-form" />
        <form ref={props.addressFormRef} data-testid="address-form" />
        <form ref={props.addressTypeFormRef} data-testid="address-type-form" />
      </div>
    );
  };
});

jest.mock('~components/innBusiness/FormRadioGroup/FormRadioGroup', () => ({
  FormRadioGroup: function FormRadioGroup(props: any) {
    return (
      <div data-testid={`radio-group-${props.variant}`}>
        {props.items.map((item: any) => (
          <div key={item.value}>
            <input
              type="radio"
              name={props.name}
              value={item.value}
              checked={props.selectedValue === item.value}
              onChange={(e) => props.onChange(e.target.value)}
              data-testid={item.testid}
            />
            <label>{item.label}</label>
            {item.icons && (
              <div className="icons-container">
                {Array.isArray(item.icons) ? item.icons : [item.icons]}
              </div>
            )}
          </div>
        ))}
      </div>
    );
  },
}));

describe('PaymentForm', () => {
  const mockProps = {
    onSubmit: jest.fn(),
    onError: jest.fn(),
    icons: {
      'icon.notification.error': 'error-icon.svg',
    },
    userAddress: '123 Test St, London',
    language: 'en' as const,
    onReviewChangesToggle: jest.fn(),
    onIsEditableToggle: jest.fn(),
    onIsUpdatingToggle: jest.fn(),
    onIsDirtyToggle: jest.fn(),
    isEditable: true,
    isUpdating: false,
    isDirty: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockUseFeatureToggle.mockReturnValue({});
  });

  it('renders the payment form with default values', () => {
    render(<PaymentTypeSelection {...mockProps} />);

    expect(screen.getByText('payment.add.title')).toBeInTheDocument();
    expect(screen.getByTestId('PaymentForm-CreditDebit')).toBeInTheDocument();
    expect(screen.getByTestId('PaymentForm-InnBusinessPay')).toBeInTheDocument();
    expect(screen.getByTestId('PaymentForm-PersonalAddress')).toBeInTheDocument();
    expect(screen.getByTestId('PaymentForm-DifferentAddress')).toBeInTheDocument();
  });

  it('shows different address form when different billing address is selected', async () => {
    render(<PaymentTypeSelection {...mockProps} />);

    const differentAddressRadio = screen.getByTestId('PaymentForm-DifferentAddress');
    fireEvent.click(differentAddressRadio);

    await waitFor(() => {
      expect(screen.getByTestId('different-address-form')).toBeInTheDocument();
    });
  });

  it('disables submit button when loading', () => {
    render(<PaymentTypeSelection {...mockProps} isLoading={true} />);

    const submitButton = screen.getByTestId('submit-button');
    expect(submitButton).toBeDisabled();
  });

  it('calls onCancel directly when cancel button is clicked (if updating)', () => {
    const onCancel = jest.fn();
    render(
      <PaymentTypeSelection
        {...mockProps}
        onCancel={onCancel}
        isUpdating={true}
        isEditable={true}
      />
    );

    const cancelButton = screen.getByTestId('cancel-button');
    fireEvent.click(cancelButton);

    expect(onCancel).toHaveBeenCalled();
  });

  it('doesnt call onReviewChangesToggle when cancel button is clicked (not updating, not dirty and editable)', () => {
    render(<PaymentTypeSelection {...mockProps} isUpdating={false} isEditable={true} />);

    const cancelButton = screen.getByTestId('cancel-button');
    fireEvent.click(cancelButton);

    expect(mockProps.onReviewChangesToggle).not.toHaveBeenCalledWith('paymentType', true);
  });

  it('calls onReviewChangesToggle when cancel button is clicked (not updating, dirty and editable)', () => {
    render(
      <PaymentTypeSelection {...mockProps} isUpdating={false} isEditable={true} isDirty={true} />
    );

    const cancelButton = screen.getByTestId('cancel-button');
    fireEvent.click(cancelButton);

    expect(mockProps.onReviewChangesToggle).toHaveBeenCalledWith('paymentType', true);
  });

  it('renders company name and address type handlers in DifferentAddressForm', async () => {
    render(<PaymentTypeSelection {...mockProps} />);

    const differentAddressRadio = screen.getByTestId('PaymentForm-DifferentAddress');
    fireEvent.click(differentAddressRadio);

    await waitFor(() => {
      expect(screen.getByTestId('different-address-form')).toBeInTheDocument();
    });

    const changeToBusinessButton = screen.getByTestId('change-to-business-address');
    fireEvent.click(changeToBusinessButton);

    await waitFor(() => {
      expect(screen.getByTestId('company-name-input')).toBeInTheDocument();
    });

    // No error expected, just coverage for handlers
    expect(screen.getByTestId('different-address-form')).toBeInTheDocument();
  });

  it('renders CardIconWrapper for each card type', () => {
    render(<PaymentTypeSelection {...mockProps} />);
    expect(screen.getAllByTestId('CardIcon').length).toBeGreaterThan(0);
  });

  it('shows prepay checkbox only when paymentType is NEW_PIBA', () => {
    render(<PaymentTypeSelection {...mockProps} />);
    expect(screen.queryByTestId('prepay-checkbox')).not.toBeInTheDocument();
    const innBusinessPayRadio = screen.getByTestId('PaymentForm-InnBusinessPay');
    fireEvent.click(innBusinessPayRadio);
    expect(screen.getByTestId('prepay-checkbox')).toBeInTheDocument();
  });

  it('should hide memorable word input when feature flag is ON and PIBA + prepay selected', async () => {
    mockUseFeatureToggle.mockReturnValue({ release_piba_cnp_iframe_split: true });

    render(<PaymentTypeSelection {...mockProps} />);

    const innBusinessPayRadio = screen.getByTestId('PaymentForm-InnBusinessPay');
    fireEvent.click(innBusinessPayRadio);

    const prepayCheckbox = screen.getByTestId('prepay-checkbox');
    fireEvent.click(prepayCheckbox);

    await waitFor(() => {
      expect(screen.queryByTestId('memorable-word-container')).not.toBeInTheDocument();
    });
  });

  it('should show memorable word input when feature flag is OFF and PIBA + prepay selected', async () => {
    mockUseFeatureToggle.mockReturnValue({ release_piba_cnp_iframe_split: false });

    render(<PaymentTypeSelection {...mockProps} />);

    const innBusinessPayRadio = screen.getByTestId('PaymentForm-InnBusinessPay');
    fireEvent.click(innBusinessPayRadio);

    const prepayCheckbox = screen.getByTestId('prepay-checkbox');
    fireEvent.click(prepayCheckbox);

    await waitFor(() => {
      expect(screen.getByTestId('memorable-word-container')).toBeInTheDocument();
    });
  });

  it('triggers company name validation when business address is selected and company name is empty on form submission', async () => {
    render(<PaymentTypeSelection {...mockProps} />);

    const differentAddressRadio = screen.getByTestId('PaymentForm-DifferentAddress');
    fireEvent.click(differentAddressRadio);

    await waitFor(() => {
      expect(screen.getByTestId('different-address-form')).toBeInTheDocument();
    });

    const changeToBusinessButton = screen.getByTestId('change-to-business-address');
    fireEvent.click(changeToBusinessButton);

    await waitFor(() => {
      expect(screen.getByTestId('company-name-input')).toBeInTheDocument();
    });

    const companyNameInput = screen.getByTestId('company-name-input');
    expect(companyNameInput).toHaveValue('');

    expect(screen.queryByTestId('company-name-error')).not.toBeInTheDocument();

    const submitButton = screen.getByTestId('submit-button');

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByTestId('company-name-error')).toBeInTheDocument();
      expect(screen.getByTestId('company-name-error')).toHaveTextContent(
        'company.coMngt.companyInfo.company.name.required'
      );
    });

    fireEvent.change(companyNameInput, { target: { value: 'Test Company' } });

    await waitFor(() => {
      expect(screen.queryByTestId('company-name-error')).not.toBeInTheDocument();
    });
  });

  it('shows the NEW_PIBA option for GB when PIBA Euro flag is enabled', () => {
    mockUseFeatureToggle.mockReturnValue({ release_ib_pay_piba_euro: true });

    render(<PaymentTypeSelection {...mockProps} />);

    expect(screen.getByTestId('PaymentForm-InnBusinessPay')).toBeInTheDocument();
  });

  it('shows the NEW_PIBA option for GB when PIBA Euro flag is disabled', () => {
    render(<PaymentTypeSelection {...mockProps} />);

    expect(screen.getByTestId('PaymentForm-InnBusinessPay')).toBeInTheDocument();
  });

  it('hides the NEW_PIBA option for DE when PIBA Euro flag is disabled', () => {
    render(<PaymentTypeSelection {...mockProps} language="de" />);

    expect(screen.queryByTestId('PaymentForm-InnBusinessPay')).not.toBeInTheDocument();
  });

  it('shows the NEW_PIBA option for DE when PIBA Euro flag is enabled', () => {
    mockUseFeatureToggle.mockReturnValue({ release_ib_pay_piba_euro: true });

    render(<PaymentTypeSelection {...mockProps} language="de" />);

    expect(screen.getByTestId('PaymentForm-InnBusinessPay')).toBeInTheDocument();
  });
});
