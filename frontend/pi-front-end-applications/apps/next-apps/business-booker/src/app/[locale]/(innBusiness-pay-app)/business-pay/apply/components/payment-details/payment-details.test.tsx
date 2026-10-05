import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { PayApplicationStep } from '../types';
import { PaymentDetails } from './payment-details';

const mockGoToStep = jest.fn();
const mockGoToPreviousStep = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  getAuthCookie: () => 'mock-token',
  renderSanitizedHtml: (html: string) => (
    <div data-testid="sanitized-content" dangerouslySetInnerHTML={{ __html: html }} />
  ),
  analytics: {
    update: jest.fn(),
  },
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
  findError: () => undefined,
  formatIBAssetsUrl: () => '/test-icon',
  useTranslation: () => ({
    t: (key: string) => {
      const translations: { [key: string]: string } = {
        'companyDetails.closeOut': 'Close application',
        'your.details.continue': 'Continue',
        'payapp.directDebit.selectOption': 'Please select an option.',
        'payapp.paymentDetails.title': 'Payment Details',
        'payapp.update.directDebit': 'Update Direct Debit',
        'payapp.directDebit.auth': 'Authorization note',
        'payapp.directDebit.setUp': 'Set up Direct Debit',
        'payapp.directDebit.setUpOption1': 'Set up Direct Debit online',
        'payapp.directDebit.setUpPost.Option2': 'Set up Direct Debit by post',
        'payapp.directDebit.enquiry': 'Enquiry text',
        'payapp.directDebit.alternateOption':
          'If you are unable to setup your Direct Debit instruction online (multiple signatories may be required) then you can download a printable Direct Debit mandate.',
        'payapp.mandate.send': 'Please send the completed mandate to:',
        'payapp.directDirect.post.address':
          'Premier Inn Business Account,Worldline IT Services UK Limited,1 Trinity Court Broadlands,Wolverhampton,WV10 6UH',
        'payapp.mandate.download.reminder': 'Please remember to download mandate to continue.',
        'payapp.mandate.download': 'Download mandate',
        'payapp.directDebitMandate': '/mandate-url',
        'notification.message.error': 'Something went wrong. Please try again.',
      };
      return translations[key] || key;
    },
  }),
  getPathForLocale: () => '/test-save-url',
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getLocaleByPathname: () => 'en',
  cn: (...classes: string[]) => classes.filter(Boolean).join(' '),
  directDebit: jest.fn(),
  getDdSepaFormStatus: () => {
    return { status: 'success' };
  },
}));

const mockPush = jest.fn();
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: mockPush,
  }),
  usePathname: () => '/en/business-pay/apply',
}));

jest.mock('@whitbread-eos/layout', () => ({
  Wizard: ({
    steps,
  }: {
    children: React.ReactNode;
    steps: Array<{ component: React.ReactNode }>;
  }) => <div data-testid="wizard-page">{steps[0].component}</div>,
  WizardPage: ({ children, footer }: { children: React.ReactNode; footer: React.ReactNode }) => (
    <div data-testid="wizard-page">
      {children}
      {footer}
    </div>
  ),
  WizardFooter: ({
    linkLabel,
    buttonLabel,
    onButtonClick,
    onLinkClick,
    buttonDisabled,
  }: {
    linkLabel: string;
    buttonLabel: string;
    onButtonClick: () => void;
    onLinkClick: () => void;
    buttonDisabled: boolean;
  }) => (
    <div>
      <button data-testid="footer-link" onClick={onLinkClick}>
        {linkLabel}
      </button>
      <button data-testid="footer-button" onClick={onButtonClick} disabled={buttonDisabled}>
        {buttonLabel}
      </button>
    </div>
  ),
  useWizardContext: () => ({
    goToStep: mockGoToStep,
    goToPreviousStep: mockGoToPreviousStep,
    icons: {
      'icon.notification.error': '/error-icon',
      'icon.notification.alert': '/alert-icon',
      'icon.file.icon.purple': '/file-icon',
    },
    wizardState: {
      applicationGuid: 'test-guid',
      applicationId: 'test-id',
      scheme: 'GB',
    },
    setWizardState: jest.fn(),
  }),
}));

const mockHandleWorldlineError = jest.fn();
jest.mock('../error-handling', () => ({
  useWorldlineErrorHandler: () => ({
    handleWorldlineError: mockHandleWorldlineError,
  }),
}));

describe('PaymentDetails component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    Object.defineProperty(window, 'open', {
      value: jest.fn(),
      writable: true,
    });
    window.analyticsData = {};
  });

  it('should render PaymentDetails component', () => {
    const { getByTestId } = render(<PaymentDetails locale={LOCALES.EN} />);
    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should show validation error when no payment method is selected', async () => {
    const { getByTestId, getByText } = render(<PaymentDetails locale={LOCALES.EN} />);

    const continueButton = getByTestId('footer-button');

    await act(async () => {
      fireEvent.click(continueButton);
    });

    expect(getByText('Please select an option.')).toBeInTheDocument();
    expect(continueButton).toBeDisabled();
  });

  it('should navigate to direct debit step when direct debit is selected and conditions are met', async () => {
    const { getByTestId } = render(<PaymentDetails locale={LOCALES.EN} />);

    const directDebitRadio = getByTestId('radio-direct-debit');
    await act(async () => {
      fireEvent.click(directDebitRadio);
    });

    const authorizedCheckbox = getByTestId('checkbox-only-person-authorized');
    const accountHolderCheckbox = getByTestId('checkbox-account-holder-payer');

    await act(async () => {
      fireEvent.click(authorizedCheckbox);
      fireEvent.click(accountHolderCheckbox);
    });

    const continueButton = getByTestId('footer-button');
    await act(async () => {
      fireEvent.click(continueButton);
    });

    expect(mockGoToStep).toHaveBeenCalledWith(PayApplicationStep.PAYMENT_DETAILS_DIRECT_DEBIT);
  });

  it('should show mandate section when direct debit by post is selected', async () => {
    const { getByTestId, getByText } = render(<PaymentDetails locale={LOCALES.EN} />);

    const directDebitByPostRadio = getByTestId('radio-direct-debit-post');
    await act(async () => {
      fireEvent.click(directDebitByPostRadio);
    });

    expect(
      getByText(
        'If you are unable to setup your Direct Debit instruction online (multiple signatories may be required) then you can download a printable Direct Debit mandate.'
      )
    ).toBeInTheDocument();
    expect(getByText('Please send the completed mandate to:')).toBeInTheDocument();
    expect(getByTestId('download-mandate-button')).toBeInTheDocument();
  });

  it('should show alert notification on first Continue click for direct debit by post', async () => {
    const { getByTestId, getByText } = render(<PaymentDetails locale={LOCALES.EN} />);

    const directDebitByPostRadio = getByTestId('radio-direct-debit-post');
    await act(async () => {
      fireEvent.click(directDebitByPostRadio);
    });

    const continueButton = getByTestId('footer-button');
    await act(async () => {
      fireEvent.click(continueButton);
    });

    expect(getByText('Please remember to download mandate to continue.')).toBeInTheDocument();
  });

  it('should call directDebit mutation on second Continue click for direct debit by post', async () => {
    const { getByTestId } = render(<PaymentDetails locale={LOCALES.EN} />);

    const directDebitByPostRadio = getByTestId('radio-direct-debit-post');
    await act(async () => {
      fireEvent.click(directDebitByPostRadio);
    });

    const continueButton = getByTestId('footer-button');

    await act(async () => {
      fireEvent.click(continueButton);
    });

    await act(async () => {
      fireEvent.click(continueButton);
    });

    const directDebitMock = jest.mocked(await import('@whitbread-eos/utils/server')).directDebit;
    expect(directDebitMock).toHaveBeenCalledWith(
      'mock-token',
      'test-id',
      'test-guid',
      'BY_POST',
      'SUMMARY'
    );
  });

  it('should open mandate download when download button is clicked', async () => {
    const { getByTestId } = render(<PaymentDetails locale={LOCALES.EN} />);

    const directDebitByPostRadio = getByTestId('radio-direct-debit-post');
    await act(async () => {
      fireEvent.click(directDebitByPostRadio);
    });

    const downloadButton = getByTestId('download-mandate-button');
    await act(async () => {
      fireEvent.click(downloadButton);
    });

    expect(window.open).toHaveBeenCalledWith('/test-icon', '_blank', 'noopener,noreferrer');
  });

  it('should navigate to save page when close application is clicked', async () => {
    const { getByTestId } = render(<PaymentDetails locale={LOCALES.EN} />);

    const closeApplicationLink = getByTestId('footer-link');
    await act(async () => {
      fireEvent.click(closeApplicationLink);
    });

    expect(mockPush).toHaveBeenCalledWith('/test-save-url');
  });
});
