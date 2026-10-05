import '@testing-library/jest-dom';
import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import { LOCALES, Scheme } from '@whitbread-eos/api';
import { appPreCheck } from '@whitbread-eos/utils/server';

import { NoAccountBanner } from './no-account-banner';

const pushMock = jest.fn();
const mockGetDetailsFromToken = jest.fn();

// Mock dependencies
jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: (props: any) => <button {...props}>{props.children}</button>,
  SanitizedContent: ({ children }: any) => children,
}));
jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  formatIBAssetsUrl: (url: string) => url,
  getPathForLocale: (locale: string, path: string) => `/${locale}/${path}`,
}));
jest.mock('@whitbread-eos/utils/server', () => ({
  appPreCheck: jest.fn(() => Promise.resolve({ isTetheredUser: false })),
  getDetailsFromToken: () => mockGetDetailsFromToken(),
}));
jest.mock('next/image', () => {
  const MockImage = (props: any) => <img {...props} />;
  MockImage.displayName = 'Image';
  return MockImage;
});
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: pushMock,
  })),
}));

jest.mock('../ExistingAccountModal', () => ({
  ExistingAccountModal: ({ isModalOpen, onClose }: any) =>
    isModalOpen ? (
      <div data-testid="ExistingAccountModal" onClick={onClose}>
        Modal
      </div>
    ) : null,
}));

describe('NoAccountBanner', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetDetailsFromToken.mockReturnValue({ isTravelManager: true });
    process.env.NEXT_PUBLIC_WORLDLINE_HOST = 'https://host.com';
  });

  it('renders banner with all elements for travel manager', () => {
    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    expect(screen.getByTestId('NoAccountBanner-Title')).toHaveTextContent(
      'cardMgmt.applyBanner.title'
    );
    expect(screen.getByTestId('NoAccountBanner-SubTitle')).toHaveTextContent(
      'cardMgmt.applyBanner.subtitle'
    );
    expect(screen.getByTestId('NoAccountBanner-ApplyNowButton')).toBeInTheDocument();
    expect(screen.getByTestId('NoAccountBanner-LinkAccountButton')).toBeInTheDocument();
    expect(screen.getByTestId('NoAccountBanner-Image')).toBeInTheDocument();
  });

  it('renders banner with ApplyNowButton for business pay manager', () => {
    mockGetDetailsFromToken.mockReturnValue({ isTravelManager: false, isBusinessPayManager: true });

    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    expect(screen.getByTestId('NoAccountBanner-ApplyNowButton')).toBeInTheDocument();
    expect(screen.getByTestId('NoAccountBanner-LinkAccountButton')).toBeInTheDocument();
  });

  it('renders banner without ApplyNowButton if not travel manager or business pay manager', () => {
    mockGetDetailsFromToken.mockReturnValue({
      isTravelManager: false,
      isBusinessPayManager: false,
    });

    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    expect(screen.queryByTestId('NoAccountBanner-ApplyNowButton')).not.toBeInTheDocument();
    expect(screen.getByTestId('NoAccountBanner-LinkAccountButton')).toBeInTheDocument();
  });

  it('calls appPreCheck when ApplyNowButton is clicked', () => {
    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    fireEvent.click(screen.getByTestId('NoAccountBanner-ApplyNowButton'));
    expect(appPreCheck).toHaveBeenCalled();
  });

  it('calls appPreCheck when LinkAccountButton is clicked', async () => {
    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    fireEvent.click(screen.getByTestId('NoAccountBanner-LinkAccountButton'));
    expect(appPreCheck).toHaveBeenCalled();
  });

  it('navigates to apply page when ApplyNowButton clicked and isTetheredUser is null', async () => {
    (appPreCheck as jest.Mock).mockResolvedValueOnce({ isTetheredUser: true });
    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    await waitFor(() => fireEvent.click(screen.getByTestId('NoAccountBanner-ApplyNowButton')));
    //fireEvent.click(screen.getByTestId('NoAccountBanner-ApplyNowButton'));
    expect(pushMock).toHaveBeenCalledWith('/en-gb/business-pay/apply');
  });

  it('navigates to link account page when LinkAccountButton clicked and isTetheredUser is null', async () => {
    (appPreCheck as jest.Mock).mockResolvedValueOnce({ isTetheredUser: true });
    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    // fireEvent.click(screen.getByTestId('NoAccountBanner-LinkAccountButton'));
    await waitFor(() => fireEvent.click(screen.getByTestId('NoAccountBanner-LinkAccountButton')));
    expect(pushMock).toHaveBeenCalledWith('https://host.com/BBLinkCode.aspx');
  });

  it('formats image src using formatIBAssetsUrl', () => {
    render(<NoAccountBanner locale={LOCALES.EN} token="mockToken" scheme={'en' as Scheme} />);
    const img = screen.getByTestId('NoAccountBanner-Image');
    expect(img).toHaveAttribute('src', 'cardMgmt.applyBanner.image');
  });
});
