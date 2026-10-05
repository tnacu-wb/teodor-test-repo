import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import BookingSummaryUpgradeToFlex, { Props } from './BookingSummaryUpgradeToFlex';

const mockProps: Props = {
  initialRate: 0,
  showUpgradeToFlex: false,
  upgradeToFlexCallBack: jest.fn(),
  prefixDataTestId: null,
  amount: 600,
  currency: 'GBP',
  t: (key: string) => {
    switch (key) {
      default:
        return 'default';
    }
  },
};

describe('BookingSummaryUpgradeToFlex', () => {
  it('should render a <BookingSummaryUpgradeToFlex> without data-testid', function () {
    const { getByTestId } = render(<BookingSummaryUpgradeToFlex {...mockProps} />);

    expect(getByTestId('UpgradeToFlex-Wrapper')).toBeInTheDocument();
    expect(getByTestId('UpgradeToFlex-Button')).toBeInTheDocument();
    expect(getByTestId('UpgradeToFlex-CostForCancel-Message')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryUpgradeToFlex> with data-testid', function () {
    mockProps.prefixDataTestId = 'BookingSummary';
    const { getByTestId } = render(<BookingSummaryUpgradeToFlex {...mockProps} />);

    expect(getByTestId('BookingSummary-UpgradeToFlex-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-UpgradeToFlex-Button')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-UpgradeToFlex-CostForCancel-Message')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryUpgradeToFlex> correctly with given props', function () {
    const { getByTestId } = render(<BookingSummaryUpgradeToFlex {...mockProps} />);

    const expectCost = '£600.00';
    expect(getByTestId('BookingSummary-UpgradeToFlex-CostForCancel-Message').textContent).toEqual(
      expectCost
    );
  });
});
