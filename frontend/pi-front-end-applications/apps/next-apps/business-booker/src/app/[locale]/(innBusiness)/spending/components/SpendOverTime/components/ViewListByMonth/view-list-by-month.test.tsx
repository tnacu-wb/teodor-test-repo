import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { ViewListByMonth } from './view-list-by-month';

const spendingDataMock = [
  {
    month: '1',
    spending: 1000,
    bookingCurrency: 'GBP',
  },
  {
    month: '2',
    spending: 2000,
    bookingCurrency: 'GBP',
  },
  {
    month: '3',
    spending: 2000,
    bookingCurrency: 'GBP',
  },
  {
    month: '4',
    spending: -2000,
    bookingCurrency: 'GBP',
  },
  {
    month: '5',
    spending: 2000,
    bookingCurrency: 'GBP',
  },
  {
    month: '6',
    spending: 2000,
    bookingCurrency: 'GBP',
  },
  {
    month: '7',
    spending: 2000,
    bookingCurrency: 'GBP',
  },
];

const mockProps = {
  dataTestId: 'test123',
  spendingData: spendingDataMock,
  formatSpendingValue: jest.fn(),
  monthsSpendings: spendingDataMock as any,
  isAccountSuspended: false,
};

describe('ViewListByMonth', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('should render the ViewListByMonth component', async () => {
    const { getByTestId } = render(<ViewListByMonth {...mockProps} />);

    expect(getByTestId('test123-View-List-By-Month-Desktop')).toBeInTheDocument();
  });
  it('should render the ViewListByMonth component with more than 6 spending months', async () => {
    mockProps.monthsSpendings = [
      {
        month: '1',
        spending: 1000,
        bookingCurrency: 'GBP',
      },
      {
        month: '2',
        spending: 2000,
        bookingCurrency: 'GBP',
      },
      {
        month: '7',
        spending: 3000,
        bookingCurrency: 'GBP',
      },
    ];

    const { getByTestId } = render(<ViewListByMonth {...mockProps} />);

    expect(getByTestId('test123-View-List-By-Month-Desktop')).toBeInTheDocument();
  });
  it('should render correctly when account is suspended', async () => {
    const { queryByTestId } = render(
      <ViewListByMonth {...{ ...mockProps, isAccountSuspended: true }} />
    );

    expect(queryByTestId('test123-View-List-By-Month-Desktop')?.children.length).toBeLessThan(2);
    expect(queryByTestId('test123-Spend-Over-Time-Per-Months-Item')).not.toBeInTheDocument();
  });
});
