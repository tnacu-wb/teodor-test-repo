import '@testing-library/jest-dom';

import { render, userEvent } from '../../utils/test-utils';
import MarketingEmail from './MarketingEmail.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  analytics: {
    update: jest.fn(),
  },
}));

const mockProps = {
  handleMarketingOptin: jest.fn(),
  testIdPrefix: 'GuestDetailsPageBB',
};
describe('Email Updates', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<MarketingEmail {...mockProps} />);
    expect(getByTestId('GuestDetailsPageBB-MarketingEmail-Container')).toBeInTheDocument();
  });

  it('should contain a checkbox for updating the email preference', async () => {
    const { getByRole } = render(<MarketingEmail {...mockProps} />);
    const checkbox = getByRole('checkbox', {
      name: 'booking.pib.marketingEmail.checkbox',
    });
    expect(checkbox).not.toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
    expect(mockProps.handleMarketingOptin).toHaveBeenCalledWith(true);
  });
});
