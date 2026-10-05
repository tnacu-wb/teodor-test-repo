import PaymentAuthorization from '.';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';

const mockedProps = {
  togglePaymentAuth: jest.fn(),
  paymentAuth: false,
  password: '',
  onChangePassword: jest.fn(),
  isPaymentAuthError: false,
};

describe('PaymentAuthorization', () => {
  it('renders PaymentAuthorization empty component', () => {
    const { getByRole, container } = render(<PaymentAuthorization {...mockedProps} />);
    expect(getByRole('checkbox')).not.toBeChecked();
    expect(container.querySelector(`input[name="purchaseOrderNumber"]`)).not.toBeInTheDocument();
  });

  it('renders PaymentAuthorization with input visible', () => {
    mockedProps.paymentAuth = true;
    const { getByRole, getByLabelText } = render(<PaymentAuthorization {...mockedProps} />);
    expect(getByRole('checkbox')).toBeChecked();
    expect(getByLabelText('cardNotPresent.memorableWord')).toBeInTheDocument();
  });

  it('renders PaymentAuthorization with required error on input', async () => {
    mockedProps.paymentAuth = true;
    mockedProps.password = '';
    mockedProps.isPaymentAuthError = true;
    const { getByTestId, findByText } = render(<PaymentAuthorization {...mockedProps} />);

    getByTestId('input-password').focus();
    getByTestId('input-password').blur();

    expect(await findByText('cardNotPresent.memorableWordRequired')).toBeInTheDocument();
  });

  it('renders PaymentAuthorization with validated password', () => {
    mockedProps.paymentAuth = true;
    mockedProps.password = 'testPass';
    mockedProps.isPaymentAuthError = false;
    const { getByTestId, queryByText } = render(<PaymentAuthorization {...mockedProps} />);

    getByTestId('input-password').focus();
    getByTestId('input-password').blur();

    expect(queryByText('cardNotPresent.memorableWordRequired')).not.toBeInTheDocument();
  });
});
