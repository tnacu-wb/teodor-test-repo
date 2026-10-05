import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import BookingSummaryTotalCost, { Props } from './BookingSummaryTotalCost';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useSemanticTypography: jest.fn(() => (legacyTypography: object) => legacyTypography),
}));

const mockProps: Props = {
  prefixDataTestId: undefined,
  currency: 'GBP',
  showVATMessage: true,
  totalCostAmount: 10,
  t: (key: string) => {
    switch (key) {
      default:
        return 'default';
    }
  },
  language: 'en',
};

describe('BookingSummaryTotalCost', () => {
  it('should render a <BookingSummaryTotalCost> without data-testid', function () {
    const { getByTestId, queryByTestId } = render(<BookingSummaryTotalCost {...mockProps} />);

    expect(getByTestId('TotalCost-Wrapper')).toBeInTheDocument();
    expect(getByTestId('TotalCost-CostAmount')).toBeInTheDocument();
    expect(getByTestId('TotalCost-VATMessage')).toBeInTheDocument();
    expect(queryByTestId('TotalCost-CityTaxMessage')).not.toBeInTheDocument();
  });

  it('should render a <BookingSummaryTotalCost> without showVATMessage undefined', function () {
    const { getByTestId } = render(
      <BookingSummaryTotalCost {...mockProps} showVATMessage={undefined} />
    );

    expect(getByTestId('TotalCost-VATMessage')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryTotalCost> with data-testid', function () {
    mockProps.prefixDataTestId = 'BookingSummary';
    const { getByTestId, queryByTestId } = render(<BookingSummaryTotalCost {...mockProps} />);

    expect(getByTestId('BookingSummary-TotalCost-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-TotalCost-CostAmount')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-TotalCost-VATMessage')).toBeInTheDocument();
    expect(queryByTestId('BookingSummary-TotalCost-CityTaxMessage')).not.toBeInTheDocument();
  });

  it('should render a <BookingSummaryTotalCost> correctly with given props', function () {
    const { getByTestId } = render(<BookingSummaryTotalCost {...mockProps} />);

    const expectTotalCost = '£10';
    expect(getByTestId('BookingSummary-TotalCost-CostAmount').textContent).toEqual(expectTotalCost);
  });

  it('should render a <BookingSummaryTotalCost> without VAT label ', function () {
    const { queryByTestId } = render(
      <BookingSummaryTotalCost {...mockProps} showVATMessage={false} />
    );

    expect(queryByTestId('BookingSummary-TotalCost-VATMessage')).not.toBeInTheDocument();
  });

  it('should render a <BookingSummaryTotalCost> with City Tax label ', function () {
    const taxesMessage = 'Price includes taxes and fees';
    const { getByTestId } = render(
      <BookingSummaryTotalCost {...mockProps} taxesMessage={taxesMessage} />
    );

    expect(getByTestId('BookingSummary-TotalCost-TaxesMessage').textContent).toEqual(taxesMessage);
  });

  it('should render a <BookingSummaryTotalCost> with discount applied ', function () {
    const { queryByTestId } = render(
      <BookingSummaryTotalCost {...mockProps} isDiscountApplied={true} />
    );

    expect(queryByTestId('BookingSummary-TotalCost-DiscountName')).toBeInTheDocument();
    expect(queryByTestId('BookingSummary-TotalCost-DiscountPrice')).toBeInTheDocument();
    expect(queryByTestId('BookingSummary-TotalCost-PreviousTotalCostName')).toBeInTheDocument();
    expect(queryByTestId('BookingSummary-TotalCost-PreviousTotalCostPrice')).toBeInTheDocument();
    expect(queryByTestId('BookingSummary-TotalCost-NewTotalCostName')).toBeInTheDocument();
  });
});
