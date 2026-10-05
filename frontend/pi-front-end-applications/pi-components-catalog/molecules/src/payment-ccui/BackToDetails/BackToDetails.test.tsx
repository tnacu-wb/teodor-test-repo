import BackToDetails from '.';
import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';

const mockedBackToDetails = {
  goBack: jest.fn(),
  isPaymentsErrorPage: true,
};

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));
describe('BackToDetails ', () => {
  it('should render the Back To Details component for Leisure', function () {
    const { getByTestId } = render(<BackToDetails {...mockedBackToDetails} />);
    expect(getByTestId('backToDetails_leisure')).toBeVisible();
  });
  it('should be able to click the back to details text ', function () {
    const { getByTestId } = render(<BackToDetails {...mockedBackToDetails} />);
    const backText = getByTestId('backToDetails_back-text');
    fireEvent.click(backText);
    expect(backText).toBeInTheDocument();
  });

  it('should render different label on payment error page', function () {
    const { getByTestId, getByText } = render(<BackToDetails {...mockedBackToDetails} />);
    const backText = getByTestId('backToDetails_back-text');
    expect(getByText('ccui.paymentErrorPage.hyperlink.backToPaymentsPage')).toBeInTheDocument();
    expect(backText).toBeInTheDocument();
  });

  it('should render different label if is not on payment error page', function () {
    const { getByTestId, getByText } = render(
      <BackToDetails {...mockedBackToDetails} isPaymentsErrorPage={false} />
    );
    const backText = getByTestId('backToDetails_back-text');
    expect(getByText('booking.submitBox.backToYourDetails')).toBeInTheDocument();
    expect(backText).toBeInTheDocument();
  });
});
