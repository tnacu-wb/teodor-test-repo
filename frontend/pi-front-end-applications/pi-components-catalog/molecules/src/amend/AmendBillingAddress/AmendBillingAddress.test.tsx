import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import AmendBillingAddress from './AmendBillingAddress.component';

const mockedProps = {
  billingAddress: 'Main Street',
  selectedPaymentDetail: {
    type: 'PAY_NOW',
    order: 1,
    enabled: true,
  },
};

describe('AmendBillingAddress component', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<AmendBillingAddress {...mockedProps} />);
    expect(getByTestId('AmendBillingAddress-section')).toBeInTheDocument();
  });
  it('should disable the component if PAY_ON_ARRIVAL option is selected', () => {
    const props = {
      ...mockedProps,
      selectedPaymentDetail: {
        type: 'PAY_ON_ARRIVAL',
        order: 1,
        enabled: true,
      },
    };
    const { getAllByRole } = render(<AmendBillingAddress {...props} />);
    const options = getAllByRole('radio');
    options.forEach((option) => {
      expect(option).toBeDisabled();
    });
  });
});
