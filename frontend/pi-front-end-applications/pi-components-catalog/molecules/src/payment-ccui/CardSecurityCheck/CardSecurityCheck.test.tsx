import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import CardSecurityCheck from './CardSecurityCheck.component';

const mockResponse = {
  isError: false,
  error: { message: '' },
  mutation: {
    isLoading: false,
    data: {
      initiateSecurityCheck: {
        fraudCheckDecision: 'REJECT',
        fraudCheckResult: '202',
      },
    },
    mutate: jest.fn(),
  },
};

const props = {
  billingAddress: {
    addressLine1: 'address',
    countryCode: 'countryCode',
    country: 'country',
    postalCode: 'postalCode',
  },
  basketReference: 'basketReference',
  cardHolderNames: {
    firstName: 'test',
    lastName: 'test2',
  },
  setIsSecurityCheckPassed: jest.fn(),
  setHasErrors: jest.fn(),
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => mockResponse,
}));

describe('<CardSecurityCheck />', () => {
  it('should show a loading state', async () => {
    mockResponse.mutation.isLoading = true;
    mockResponse.isError = false;
    mockResponse.error.message = '';
    mockResponse.mutation.data.initiateSecurityCheck.fraudCheckDecision = 'ACCEPT';
    jest.useRealTimers();

    const { getByText } = render(<CardSecurityCheck {...props} {...mockResponse} />);
    waitFor(() => {
      expect(getByText('booking.loading')).toBeInTheDocument();
    });
  });

  it('should show an error state', async () => {
    mockResponse.isError = true;
    mockResponse.error.message = 'Error message';

    const { getByText } = render(<CardSecurityCheck {...props} {...mockResponse} />);
    expect(getByText('Error message')).toBeInTheDocument();
  });

  it('should render the modal when fraudCheckDecision is REJECT', async () => {
    mockResponse.mutation.isLoading = false;
    mockResponse.isError = false;
    mockResponse.error.message = '';
    mockResponse.mutation.data.initiateSecurityCheck.fraudCheckDecision = 'REJECT';

    const { getByTestId } = render(<CardSecurityCheck {...props} {...mockResponse} />);

    const button = getByTestId('cardSecurityCheck_launchButton');

    expect(button).toBeInTheDocument();

    fireEvent.click(button);

    const modal = getByTestId('cardSecurityCheck_Modal-ModalContent');
    waitFor(() => {
      expect(modal).toBeVisible();
    });
  });

  it('should close the modal when clicking outside', async () => {
    mockResponse.mutation.isLoading = false;
    mockResponse.isError = false;
    mockResponse.mutation.data.initiateSecurityCheck.fraudCheckDecision = 'REJECT';
    const { getByTestId, getByRole } = render(<CardSecurityCheck {...props} {...mockResponse} />);

    const button = getByTestId('cardSecurityCheck_launchButton');

    expect(button).toBeInTheDocument();

    const modal = getByTestId('cardSecurityCheck_Modal-ModalContent');
    waitFor(() => {
      expect(modal).toBeVisible();
    });

    const parent = getByRole('dialog').parentElement as HTMLElement;
    waitFor(() => {
      expect(parent).toBeVisible();
    });
    fireEvent.keyDown(modal, {
      key: 'Escape',
      code: 'Escape',
      charCode: 27,
    });

    waitFor(() => {
      expect(modal).not.toBeVisible();
    });
  });

  it('should hide the modal when clicking on the close button', async () => {
    mockResponse.isError = false;
    mockResponse.error.message = '';
    mockResponse.mutation.data.initiateSecurityCheck.fraudCheckDecision = 'REJECT';
    const { getByTestId } = render(<CardSecurityCheck {...props} {...mockResponse} />);

    const button = getByTestId('cardSecurityCheck_launchButton');

    expect(button).toBeInTheDocument();

    const modal = getByTestId('cardSecurityCheck_Modal-ModalContent');
    const buttonCancel = getByTestId('cardSecurityCheck_cancel');
    fireEvent.click(buttonCancel);

    waitFor(() => {
      expect(modal).not.toBeVisible();
    });
  });

  it('should load the success modal', async () => {
    mockResponse.isError = false;
    mockResponse.error.message = '';
    mockResponse.mutation.data.initiateSecurityCheck.fraudCheckDecision = 'ACCEPT';

    const { getByTestId } = render(<CardSecurityCheck {...props} {...mockResponse} />);

    const button = getByTestId('cardSecurityCheck_launchButton');

    expect(button).toBeInTheDocument();

    const modalSuccess = getByTestId('cardSecurityCheck_Modal-Success-ModalContent');

    waitFor(() => {
      expect(modalSuccess).toBeVisible();
    });
  });

  it('should close the success modal when clicking outside', async () => {
    mockResponse.mutation.isLoading = false;
    mockResponse.isError = false;
    mockResponse.mutation.data.initiateSecurityCheck.fraudCheckDecision = 'ACCEPT';
    const { getByTestId, getByRole } = render(<CardSecurityCheck {...props} {...mockResponse} />);

    const button = getByTestId('cardSecurityCheck_launchButton');

    expect(button).toBeInTheDocument();

    const modalSuccess = getByTestId('cardSecurityCheck_Modal-Success-ModalContent');

    waitFor(() => {
      expect(modalSuccess).toBeVisible();
    });

    const parent = getByRole('dialog').parentElement as HTMLElement;
    waitFor(() => {
      expect(parent).toBeVisible();
    });
    fireEvent.keyDown(modalSuccess, {
      key: 'Escape',
      code: 'Escape',
      charCode: 27,
    });

    waitFor(() => {
      expect(modalSuccess).not.toBeVisible();
    });
  });

  it('should hide the success modal when clicking on the continue button', async () => {
    mockResponse.isError = false;
    mockResponse.error.message = '';
    mockResponse.mutation.data.initiateSecurityCheck.fraudCheckDecision = 'ACCEPT';
    const { getByTestId } = render(<CardSecurityCheck {...props} {...mockResponse} />);

    const button = getByTestId('cardSecurityCheck_launchButton');

    expect(button).toBeInTheDocument();

    const modalSuccess = getByTestId('cardSecurityCheck_Modal-Success-ModalContent');

    const buttonContinue = getByTestId('cardSecurityCheck_continue');
    fireEvent.click(buttonContinue);

    waitFor(() => {
      expect(modalSuccess).not.toBeVisible();
    });
  });
});
