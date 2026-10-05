import '@testing-library/jest-dom';

import { render, waitFor } from '~utils/test-utils';

import PaymentPage from '~pages/payment';

const mockReplace = jest.fn();
const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
}));

jest.mock('~page-helper/payment', () => ({
  createPaymentPiDataLoaderFn: jest.fn(),
  PaymentPagePi: () => <div data-testid="payment-page-content" />,
}));

const props = {
  hiQueryInput: {} as never,
  pcksQueryInput: {} as never,
  basketReference: 'RES-456',
  featureToggles: {},
};

describe('PaymentPage Datatrans return', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseRouter.mockReturnValue({ query: {}, replace: mockReplace });
    window.history.replaceState({}, '', '/gb/en/test-flow/payment?reservationId=RES-456');
  });

  it.skip('redirects a Datatrans return to confirmation and removes the source query', async () => {
    mockUseRouter.mockReturnValue({
      query: { source: 'datatrans' },
      replace: mockReplace,
    });
    window.history.replaceState(
      {},
      '',
      '/gb/en/test-flow/payment?reservationId=RES-456&source=datatrans'
    );

    const { container } = render(<PaymentPage {...props} isDatatransReturn={true} />);

    await waitFor(() => {
      expect(mockReplace).toHaveBeenCalledWith(
        '/gb/en/test-flow/confirmation?reservationId=RES-456'
      );
    });
    expect(container).toBeEmptyDOMElement();
  });

  it('renders the payment page during a normal request', () => {
    const { getByTestId } = render(<PaymentPage {...props} />);

    expect(getByTestId('payment-page-content')).toBeInTheDocument();
    expect(mockReplace).not.toHaveBeenCalled();
  });
});
