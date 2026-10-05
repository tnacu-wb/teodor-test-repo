import { render } from '@testing-library/react';

import NoPaymentMethodsNotification from './NoPaymentMethodsNotification.component';

describe('NoPaymentMethodsNotification', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders the corect text', () => {
    const { getByTestId } = render(<NoPaymentMethodsNotification />);
    const headerText = getByTestId('NoPaymentOptions-HeaderText');
    const titleText = getByTestId('NoPaymentOptions-TitleText');
    const descriptionText = getByTestId('NoPaymentOptions-DescriptionText');

    expect(headerText.textContent).toEqual('booking.header.payment');
    expect(titleText.textContent).toEqual('cc.noMethods');
    expect(descriptionText.textContent).toEqual('cc.noMethodsAuthorised');
  });
});
