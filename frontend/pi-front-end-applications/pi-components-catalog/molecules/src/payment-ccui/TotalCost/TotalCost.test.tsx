import '@testing-library/jest-dom';
import { EmailConfirmation } from '@whitbread-eos/api';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import TotalCost from './TotalCost.component';

const mockOnConfirmBooking = jest.fn();
const mockSetSendEmail = jest.fn();
const mockSetEmailAddress = jest.fn();
const mockSetEmailError = jest.fn();

const data = {
  totalCost: 162,
  hotelName: 'London Holborn',
  hotelId: 'LONKIN',
  hotelBrand: 'PI',
  onConfirmClick: mockOnConfirmBooking,
  country: 'en',
  isSectionVisible: true,
  hasAllChecks: true,
  setSendEmail: mockSetSendEmail,
  emailSection: {
    emailAddress: undefined,
    setEmailAddress: mockSetEmailAddress,
    emailError: undefined,
    setEmailError: mockSetEmailError,
  },
};

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
      ],
    },
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockResponse,
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      query: { key: '' },
    };
  },
}));

describe('<TotalCost />', () => {
  it('should render the component with default props', () => {
    const { getByTestId, queryByTestId } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} />
    );

    expect(getByTestId('totalCostSection')).toBeInTheDocument();
    expect(queryByTestId('totalCostSection_discount')).toBeFalsy();
  });

  it('Should render the discount section', () => {
    const { getByTestId, queryByText, queryByTestId } = render(
      <TotalCost {...data} discount={10} previousTotalCost={15} isDiscountApplied={true} />
    );

    expect(getByTestId('totalCostSection_discount')).toBeInTheDocument();
    expect(queryByTestId('totalCostSection_discount')).toBeTruthy();
    expect(queryByText('ccui.payment.confirmBooking.button')).toBeDisabled();
  });

  it('Should not render the discount section', () => {
    const { queryByText, queryByTestId } = render(
      <TotalCost {...data} discount={10} previousTotalCost={15} isDiscountApplied={false} />
    );

    expect(queryByTestId('totalCostSection_discount')).toBeFalsy();
    expect(queryByText('ccui.payment.confirmBooking.button')).toBeDisabled();
  });

  it('should change the option for mail', () => {
    const { getAllByRole } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} />
    );

    const radioSendMail = getAllByRole('radio')[0];
    const radioNoMail = getAllByRole('radio')[1];

    expect(radioSendMail).toBeInTheDocument();
    expect(radioNoMail).toBeInTheDocument();

    fireEvent.click(radioNoMail);

    expect(radioNoMail).toBeChecked();

    fireEvent.click(radioSendMail);

    expect(radioSendMail).toBeChecked();
  });

  it('should render the email input', () => {
    const { getByTestId } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} />
    );

    const emailInput = getByTestId('input-emailAddress');

    expect(emailInput).toHaveAttribute('type', 'email');
    expect(emailInput).toBeInTheDocument();
  });

  it('should be able to add email', () => {
    const { getByTestId } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} />
    );

    const emailInput = getByTestId(`input-emailAddress`);
    fireEvent.change(emailInput, { target: { value: 'test@testing.com' } });

    expect(emailInput).toHaveValue('test@testing.com');
  });

  it('should change the option for email', () => {
    const { getAllByRole } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} />
    );

    const radioSendMail = getAllByRole('radio')[0];
    const radioNoMail = getAllByRole('radio')[1];

    expect(radioSendMail).toBeInTheDocument();
    expect(radioNoMail).toBeInTheDocument();

    fireEvent.click(radioNoMail);

    expect(radioNoMail).toBeChecked();

    fireEvent.click(radioSendMail);

    expect(radioSendMail).toBeChecked();
  });

  it('should trigger a change when click on radio', () => {
    const { getByRole } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} />
    );
    const radiogroup = getByRole('radiogroup');
    const radioNoMail = getByRole('radio', { name: 'ccui.payment.confirmBooking.textNoMail' });

    expect(radiogroup).toBeInTheDocument();

    fireEvent.change(radioNoMail, { target: { value: 'NO_SEND_MAIL' } });
  });

  it('should open the overlay for terms', async () => {
    const { getByTestId } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} />
    );
    const checkBox = getByTestId('roomRatePolicies_checkbox');

    fireEvent.click(checkBox);

    await waitFor(() => {
      expect(getByTestId('roomRatePolicies-ModalContent')).toBeVisible();
    });
  });

  it('should have the confirm booking button enabled', async () => {
    const { getByTestId } = render(
      <TotalCost {...data} isDiscountApplied={false} previousTotalCost={5} isCompWithoutEckoh />
    );
    const checkBox = getByTestId('roomRatePolicies_checkbox');
    const confirmButton = getByTestId('totalCostSection_confirm-booking-total-cost');

    fireEvent.click(checkBox);

    await waitFor(() => {
      expect(getByTestId('roomRatePolicies-ModalContent')).toBeVisible();
      expect(confirmButton).toBeEnabled();
    });

    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(mockOnConfirmBooking).toHaveBeenCalled();
    });
  });

  it('should disable SEND_EMAIL when prop is passed', () => {
    const { getAllByRole } = render(
      <TotalCost
        {...data}
        disabledOption={EmailConfirmation.SEND_EMAIL}
        isDiscountApplied={false}
        previousTotalCost={5}
      />
    );
    const radioSendMail = getAllByRole('radio')[0];

    expect(radioSendMail).toBeDisabled();
  });

  it('should disable NO_SEND_EMAIL when prop is passed', () => {
    const { getAllByRole } = render(
      <TotalCost
        {...data}
        disabledOption={EmailConfirmation.NO_SEND_EMAIL}
        isDiscountApplied={false}
        previousTotalCost={5}
      />
    );
    const radioNoMail = getAllByRole('radio')[1];

    expect(radioNoMail).toBeDisabled();
  });
});
