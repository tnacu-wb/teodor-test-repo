import '@testing-library/jest-dom';
import { render, screen, act, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import * as utilsServer from '@whitbread-eos/utils/server';

import { PaymentDetailsDirectDebit } from './direct-debit';

const mockUseTranslation = jest.fn().mockReturnValue({ t: (key: string) => key });
const mockUseWizardContext = jest.fn().mockReturnValue({
  wizardState: { applicationId: 'id', applicationGuid: 'guid', scheme: 'scheme' },
  goToStep: jest.fn(),
  setWizardState: jest.fn(),
});
const mockGetAuthCookie = jest.fn().mockReturnValue('token');
const mockHandleWorldlineError = jest.fn();
const mockUseWorldlineErrorHandler = jest
  .fn()
  .mockReturnValue({ handleWorldlineError: mockHandleWorldlineError });
const mockUseToast = jest.fn().mockReturnValue({ toast: jest.fn() });
const mockUseRouter = jest.fn().mockReturnValue({ push: jest.fn() });

jest.mock('@whitbread-eos/utils', () => ({
  analytics: { update: jest.fn() },
  getAuthCookie: () => mockGetAuthCookie(),
  useTranslation: () => mockUseTranslation(),
  getPathForLocale: jest.fn(() => '/mock-path'),
}));
jest.mock('@whitbread-eos/utils/server', () => ({
  directDebit: jest.fn(),
  updateResumeUrl: jest.fn(),
  getDdSepaFormStatus: jest.fn(),
}));
jest.mock('@whitbread-eos/layout', () => ({
  useWizardContext: () => mockUseWizardContext(),
  WizardFooter: ({
    linkLabel,
    buttonLabel,
    linkDisabled,
    buttonDisabled,
    onLinkClick,
    onButtonClick,
  }: any) => (
    <div>
      <button data-testid="link" disabled={linkDisabled} onClick={onLinkClick}>
        {linkLabel}
      </button>
      <button data-testid="button" disabled={buttonDisabled} onClick={onButtonClick}>
        {buttonLabel}
      </button>
    </div>
  ),
  WizardPage: ({ children, footer }: any) => (
    <div>
      {children}
      {footer}
    </div>
  ),
}));
jest.mock('@whitbread-eos/atoms/ui', () => ({
  Skeleton: ({ className }: any) => <div data-testid="skeleton" className={className} />,
  useToast: () => mockUseToast(),
}));
jest.mock('next/navigation', () => ({
  useRouter: () => mockUseRouter(),
}));
jest.mock('../error-handling', () => ({
  useWorldlineErrorHandler: () => mockUseWorldlineErrorHandler(),
}));
jest.mock('../analytics/analytics', () => ({
  Analytics: ({ pageName }: any) => <div data-testid="analytics">{pageName}</div>,
}));
jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: () => <div data-testid="review-changes" />,
}));

describe('PaymentDetailsDirectDebit', () => {
  const defaultProps = {
    locale: LOCALES.EN,
    iframeUrl: 'https://iframe.url/',
    isStatusPollingEnabled: false,
    statusCheckInterval: 1000,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (utilsServer.directDebit as jest.Mock).mockResolvedValue({
      status: 'success',
      data: { hostedPageGuid: 'hosted-guid' },
    });
    (utilsServer.getDdSepaFormStatus as jest.Mock).mockResolvedValue({
      status: 'Pending',
    });
    (utilsServer.updateResumeUrl as jest.Mock).mockResolvedValue(true);
  });

  it('renders skeletons while loading', async () => {
    (utilsServer.directDebit as jest.Mock).mockResolvedValueOnce({
      status: 'success',
      data: { hostedPageGuid: '' },
    });
    await act(async () => {
      render(<PaymentDetailsDirectDebit {...defaultProps} />);
    });
    expect(screen.getAllByTestId('skeleton').length).toBeGreaterThan(0);
    expect(screen.queryByTestId('DirectDebit-Iframe')).not.toBeInTheDocument();
  });

  it('renders iframe when hostedPageGuid is available', async () => {
    await act(async () => {
      render(<PaymentDetailsDirectDebit {...defaultProps} />);
    });
    expect(screen.getByTestId('DirectDebit-Iframe')).toBeInTheDocument();
    expect(screen.getByTestId('DirectDebit-Iframe')).toHaveAttribute(
      'src',
      'https://iframe.url/hosted-guid'
    );
  });

  it('shows ReviewChanges when not pending', async () => {
    await act(async () => {
      render(<PaymentDetailsDirectDebit {...defaultProps} />);
    });
    expect(screen.getByTestId('review-changes')).toBeInTheDocument();
  });

  it('calls save(false) when continue button is clicked', async () => {
    await act(async () => {
      render(<PaymentDetailsDirectDebit {...defaultProps} />);
    });
    const button = screen.getByTestId('button');
    await act(async () => {
      fireEvent.click(button);
    });
    expect(mockUseWizardContext().goToStep).toHaveBeenCalled();
  });

  it('calls save(true) or continueLater when link is clicked', async () => {
    await act(async () => {
      render(<PaymentDetailsDirectDebit {...defaultProps} />);
    });
    const link = screen.getByTestId('link');
    await act(async () => {
      fireEvent.click(link);
    });
    expect(mockUseRouter().push).toHaveBeenCalledWith('/mock-path');
  });

  it('shows error toast if updateResumeUrl fails', async () => {
    (utilsServer.updateResumeUrl as jest.Mock).mockResolvedValueOnce(null);
    await act(async () => {
      render(<PaymentDetailsDirectDebit {...defaultProps} />);
    });
    const button = screen.getByTestId('button');
    await act(async () => {
      fireEvent.click(button);
    });
    expect(mockUseToast().toast).toHaveBeenCalled();
  });
});
