import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import RoomRatePolicies, { Props } from './RoomRatePolicies.component';

const mockResponse = {
  data: {
    ratesInformation: {
      rateClassifications: [
        {
          rateClassification: 'FLEXRATE',
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'Flex',
        },
        {
          rateClassification: 'SEMIFLEX',
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'semi-flex',
        },
        {
          rateClassification: 'ADVANCE',
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'advance',
        },
        {
          rateClassification: 'STANDARD',
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'standard',
        },
        {
          rateClassification: 'NONFLEX',
          rateDescription: 'Pay now. No changes',
          rateName: 'nonflex',
        },
      ],
    },
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockResponse,
}));

const props: Props = {
  isRoomRatePoliciesChecked: false,
  onChange: jest.fn(),
  language: 'gb',
  country: 'gb',
  hotelId: 'LONKIN',
};

describe('<RoomRatePolicies />', () => {
  it('should render the component with default props', () => {
    const { getByTestId } = render(<RoomRatePolicies {...props} />);
    expect(getByTestId('roomRatePolicies_Component')).toBeInTheDocument();
  });

  it('should render the modal when clicking on the button', async () => {
    const { getByTestId } = render(<RoomRatePolicies {...props} />);
    const launchButton = getByTestId('roomRatePolicies_launchButton');
    fireEvent.click(launchButton);
    const modal = getByTestId('roomRatePolicies-ModalContent');

    await waitFor(() => {
      expect(modal).toBeVisible();
    });
  });

  it('should close the modal when clicking outside', async () => {
    const { getByTestId, getByRole, getByText } = render(<RoomRatePolicies {...props} />);
    const launchButton = getByTestId('roomRatePolicies_launchButton');
    fireEvent.click(launchButton);

    const modal = getByTestId('roomRatePolicies-ModalContent');

    await waitFor(() => {
      expect(modal).toBeVisible();
    });

    const parent = getByRole('dialog').parentElement as HTMLElement;
    await waitFor(() => {
      expect(parent).toBeVisible();
    });
    fireEvent.keyDown(getByText(/Semi-Flex/i), {
      key: 'Escape',
      code: 'Escape',
      charCode: 27,
    });

    await waitFor(() => {
      expect(modal).not.toBeVisible();
    });
  });

  it('should not render the modal when clicking on the close button', async () => {
    const { getByTestId } = render(<RoomRatePolicies {...props} />);
    const launchButton = getByTestId('roomRatePolicies_launchButton');
    fireEvent.click(launchButton);
    const modal = getByTestId('roomRatePolicies-ModalContent');
    const buttonCancel = getByTestId('roomRatePolicies-ModalCloseButton');
    fireEvent.click(buttonCancel);

    await waitFor(() => {
      expect(modal).not.toBeVisible();
    });
  });

  it('should render the modal only one time', async () => {
    const { getByTestId } = render(<RoomRatePolicies {...props} />);
    const checkbox = getByTestId('roomRatePolicies_checkbox');
    fireEvent.click(checkbox);
    const modal = getByTestId('roomRatePolicies-ModalContent');
    await waitFor(() => {
      expect(modal).toBeVisible();
    });
    fireEvent.click(checkbox);
    await waitFor(() => {
      expect(modal).toBeVisible();
    });
  });
});
