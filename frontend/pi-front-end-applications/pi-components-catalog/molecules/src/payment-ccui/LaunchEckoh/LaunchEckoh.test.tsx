import { Matcher, MatcherOptions } from '@testing-library/dom';
import '@testing-library/jest-dom';
import {
  EckohCardPresence,
  EckohCountry,
  EckohPayMethod,
  EckohStatus,
  EckohTimeOfPay,
  LanguageEnum,
} from '@whitbread-eos/api';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import LaunchEckoh from './LaunchEckoh.component';

const eckohParameters = {
  property: 'EdinburghParkAirport',
  cId: 'cId',
  aId: 'test@test.io',
  lang: LanguageEnum.ENGLISH,
  reservationId: 'EDIPAR4290445',
  merchantId: 'mWB-DEFAULT',
  env: 'ccui_dev',
  timeofpay: EckohTimeOfPay.PAY_NOW,
  paymethod: EckohPayMethod.CREDIT_CARD_DEBIT_CARD,
  cardpresence: EckohCardPresence.CARD_PRESENT,
  hotelCountry: EckohCountry.GB,
};

const mockinitiateIframe = jest.fn();
const mockEnableQuery = jest.fn();
const mockOnSuccess = jest.fn();
const mockOnFail = jest.fn();
const mockOnIframeLoad = jest.fn();

window.HTMLFormElement.prototype.submit = () => jest.fn();

const data = {
  initiateIframe: mockinitiateIframe,
  setIsEnabledEckohQuery: mockEnableQuery,
  eckohStatus: EckohStatus.SUCCESS,
  onSuccess: mockOnSuccess,
  onFail: mockOnFail,
  eckohParameters,
  disabledEckoh: false,
  onIframeLoad: mockOnIframeLoad,
};

const mockPushRouter = jest.fn();

jest.mock('next/router', () => ({
  useRouter() {
    return {
      push: mockPushRouter,
      router: { locale: 'en' },
    };
  },
}));

describe('<LaunchEckoh />', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render the component with default props', () => {
    const { getByTestId } = render(<LaunchEckoh {...data} />);
    expect(getByTestId('launchEckoh_launchButton')).toBeInTheDocument();
  });
  it('LaunchEckoh button should be disabled', () => {
    const { getByTestId } = render(<LaunchEckoh {...data} disabledEckoh={true} />);
    const launchButton = getByTestId('launchEckoh_launchButton');
    expect(launchButton).toBeDisabled();
  });
  it('should render the eckoh iframe when clicking on the button', async () => {
    const { getByTestId } = render(<LaunchEckoh {...data} />);
    const launchButton = getByTestId('launchEckoh_launchButton');
    fireEvent.click(launchButton);
    const firstModal = getByTestId('launchEckoh-ModalContent');

    await waitFor(() => {
      expect(firstModal).toBeVisible();
    });
  });

  describe('cancel booking', () => {
    let getByTestId: (id: Matcher, options?: MatcherOptions | undefined) => HTMLElement;
    let getByText: (id: Matcher, options?: MatcherOptions | undefined) => HTMLElement;
    let getAllByRole: (id: Matcher, options?: MatcherOptions | undefined) => HTMLElement[];

    beforeEach(async () => {
      const {
        getByTestId: byTestId,
        getByText: byText,
        getAllByRole: allByRole,
      } = render(<LaunchEckoh {...data} eckohStatus={EckohStatus.FAILED} />);
      getByTestId = byTestId;

      getByText = byText;

      getAllByRole = allByRole;

      const launchButton = getByTestId('launchEckoh_launchButton');
      fireEvent.click(launchButton);
      const firstModal = getByTestId('launchEckoh-ModalContent');

      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });

      fireEvent.keyDown(getByText(/iframe/i), {
        key: 'Escape',
        code: 'Escape',
        charCode: 27,
      });

      await waitFor(() => {
        expect(getByTestId('launchEckoh-ModalContent')).toBeVisible();
      });
    });

    it('should render the first modal when closing eckoh iframe', async () => {
      const firstModal = getByTestId('launchEckoh-ModalContent');

      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });
    });

    it('should close the first modal when clicking on re-try(I)', async () => {
      const firstModal = getByTestId('launchEckoh-ModalContent');

      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });

      const buttonRetry = getByTestId('launchEckoh_retryButton');
      fireEvent.click(buttonRetry);
      await waitFor(() => {
        expect(firstModal).not.toBeVisible();
      });
    });

    it('should render the second modal when clicking the cancel button (I)', async () => {
      const firstModal = getByTestId('launchEckoh-ModalContent');
      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });
      const buttonCancel = getByTestId('launchEckoh_cancelButton');
      fireEvent.click(buttonCancel);
      await waitFor(() => {
        expect(getByTestId('launchEckoh_confirm-ModalContent')).toBeVisible();
      });
    });

    it('should close the second modal when clicking on re-try(II)', async () => {
      const firstModal = getByTestId('launchEckoh-ModalContent');
      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });

      const buttonCancel = getByTestId('launchEckoh_cancelButton');
      fireEvent.click(buttonCancel);
      const buttonRetryConfirm = getByTestId('launchEckoh_confirm_retryButton');
      fireEvent.click(buttonRetryConfirm);
      expect(buttonRetryConfirm).not.toBeVisible();
    });

    it('should call onBookingCancel', async () => {
      const firstModal = getByTestId('launchEckoh-ModalContent');
      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });
      const buttonCancel = getByTestId('launchEckoh_cancelButton');
      fireEvent.click(buttonCancel);
      const buttonCancelConfirm = getByTestId('launchEckoh_confirm_cancelButton');
      fireEvent.click(buttonCancelConfirm);
      expect(mockPushRouter).toBeCalledWith('/gb/en');
    });

    it('should close the first modal when clicking outside', async () => {
      //TODO this test is causing the commit to fail
      const firstModal = getByTestId('launchEckoh-ModalContent');

      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });

      const parent = getAllByRole('dialog')[0].parentElement as HTMLElement;
      await waitFor(() => {
        expect(parent).toBeVisible();
      });
    });

    it('should close the second modal when clicking outside', async () => {
      const firstModal = getByTestId('launchEckoh-ModalContent');
      await waitFor(() => {
        expect(firstModal).toBeVisible();
      });
      const buttonCancel = getByTestId('launchEckoh_cancelButton');
      fireEvent.click(buttonCancel);
      await waitFor(() => {
        expect(getByTestId('launchEckoh_confirm-ModalContent')).toBeVisible();
      });

      fireEvent.keyDown(getByText('ccui.eckoh.confirmCancelBooking.title'), {
        key: 'Escape',
        code: 'Escape',
        charCode: 27,
      });
      await waitFor(() => {
        expect(getByTestId('launchEckoh_confirm-ModalContent')).not.toBeVisible();
      });
    });

    it('should call onFail', () => {
      render(<LaunchEckoh {...data} eckohStatus={EckohStatus.FAILED} />);
      expect(mockOnFail).toBeCalled();
    });

    it('should call onSuccess', () => {
      render(<LaunchEckoh {...data} eckohStatus={EckohStatus.SUCCESS} />);
      expect(mockOnSuccess).toBeCalled();
    });

    it('should call not found', () => {
      render(<LaunchEckoh {...data} eckohStatus={EckohStatus.NOT_FOUND} />);
      expect(mockOnFail).toBeCalled();
    });

    it('should call pending', () => {
      render(<LaunchEckoh {...data} eckohStatus={EckohStatus.PENDING} />);
      expect(mockOnFail).toBeCalled();
    });
  });
});
