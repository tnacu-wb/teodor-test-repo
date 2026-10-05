import { useMediaQuery } from '@chakra-ui/react';
import { render, screen } from '@testing-library/react';

import SubPrice from './SubPrice.component';

// Mock dependencies
jest.mock('@whitbread-eos/utils', () => ({
  formatPrice: jest.fn((currency, price) => `${currency} ${price}`),
  formatCurrency: jest.fn((code) => code),
}));

jest.mock('./../Tooltip/Tooltip.component', () => {
  return function MockTooltip({ children, onClose }: any) {
    return (
      <div data-testid="tooltip" onClick={onClose}>
        {children}
      </div>
    );
  };
});

jest.mock('../../assets/icons', () => ({
  Info: () => <span data-testid="info-icon"></span>,
}));

jest.mock('@chakra-ui/react', () => {
  const actual = jest.requireActual('@chakra-ui/react');

  return {
    ...actual,
    useMediaQuery: jest.fn().mockReturnValue([true]),
    Text: ({ children, ...props }: any) => (
      <div data-testid="text" {...props}>
        {children}
      </div>
    ),
    Box: ({ children, ...props }: any) => (
      <div data-testid="box" {...props}>
        {children}
      </div>
    ),
  };
});

const mockedUseMediaQuery = useMediaQuery as jest.Mock;

describe('SubPrice Component', () => {
  it('should render with all props provided', () => {
    render(
      <SubPrice
        label="Total Price"
        price={250}
        currencyCode="GBP"
        language="en"
        infoIcon={true}
        tooltipContent={{
          title: 'We now charge mandatory city tax at this location',
          description:
            'The Manchester City Visitor Charge is £1 per room or unit per nigh at a hotel room or other accommodation',
        }}
      />
    );
    expect(screen.getByText('Total Price')).toBeInTheDocument();
    expect(screen.getByText(/GBP\s*250/)).toBeInTheDocument();
    expect(screen.getByTestId('info-icon')).toBeInTheDocument();
    expect(screen.getByTestId('tooltip')).toBeInTheDocument();
  });

  it('should render with all props provided with an alternative layout', () => {
    render(
      <SubPrice
        label="Total Price"
        price={250}
        currencyCode="GBP"
        language="en"
        infoIcon={true}
        defaultLayout={false}
        tooltipContent={{
          title: 'We now charge mandatory city tax at this location',
          description:
            'The Manchester City Visitor Charge is £1 per room or unit per nigh at a hotel room or other accommodation',
        }}
      />
    );
    expect(screen.getByText('Total Price')).toBeInTheDocument();
    expect(screen.getByText(/GBP\s*250/)).toBeInTheDocument();
    expect(screen.getByTestId('info-icon')).toBeInTheDocument();
    expect(screen.getByTestId('tooltip')).toBeInTheDocument();
  });
});

it('should not toggle tooltip on desktop click', () => {
  mockedUseMediaQuery.mockReturnValue([false]); // desktop

  render(
    <SubPrice
      label="Total Price"
      price={250}
      currencyCode="GBP"
      language="en"
      infoIcon={true}
      tooltipContent={{
        title: 'Title',
        description: 'Description',
      }}
    />
  );

  const icon = screen.getByTestId('info-icon');
  icon.click();

  expect(screen.getByTestId('tooltip')).toBeInTheDocument();
});

it('toggles tooltip state on mobile click', () => {
  mockedUseMediaQuery.mockReturnValue([true]); // mobile

  render(
    <SubPrice
      label="Total Price"
      price={250}
      currencyCode="GBP"
      language="en"
      infoIcon
      tooltipContent={{
        title: 'Title',
        description: 'Description',
      }}
    />
  );

  const icon = screen.getByTestId('info-icon');
  icon.click();
});

it('does nothing on desktop icon click', () => {
  mockedUseMediaQuery.mockReturnValue([false]); // desktop

  render(
    <SubPrice
      label="Total Price"
      price={250}
      currencyCode="GBP"
      language="en"
      infoIcon
      tooltipContent={{
        title: 'Title',
        description: 'Description',
      }}
    />
  );

  screen.getByTestId('info-icon').click();
});

it('handles tooltip onClose', () => {
  render(
    <SubPrice
      label="Total Price"
      price={250}
      currencyCode="GBP"
      language="en"
      infoIcon
      tooltipContent={{
        title: 'Title',
        description: 'Description',
      }}
    />
  );

  screen.getByTestId('tooltip').click();
});
