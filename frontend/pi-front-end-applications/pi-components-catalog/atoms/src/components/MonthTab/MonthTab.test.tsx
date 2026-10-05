import { render, fireEvent } from '@testing-library/react';

import MonthTab from './MonthTab';

describe('MonthTab', () => {
  it('renders label and price when active', () => {
    const { getByText } = render(
      <MonthTab
        label="January"
        price={100}
        isActive={true}
        currencySymbol="GBP"
        onClick={jest.fn()}
        isLowestPriceInMonthTabEnabled={true}
      />
    );
    expect(getByText('January')).toBeInTheDocument();
    expect(getByText('priceFinder.from')).toBeInTheDocument();
    expect(getByText('£100')).toBeInTheDocument();
  });

  it('should render skelaton box', () => {
    const { getByTestId } = render(
      <MonthTab
        label="January"
        price={100}
        isActive={true}
        currencySymbol="GBP"
        onClick={jest.fn()}
        isLoading={true}
        isLowestPriceInMonthTabEnabled={true}
      />
    );
    expect(getByTestId('skeleton-box')).toBeInTheDocument();
  });

  it('calls onClick when clicked', () => {
    const handleClick = jest.fn();
    const { getByRole } = render(
      <MonthTab
        label="February"
        isActive={false}
        onClick={handleClick}
        isLowestPriceInMonthTabEnabled={true}
      />
    );
    fireEvent.click(getByRole('button'));
    expect(handleClick).toHaveBeenCalled();
  });
});
