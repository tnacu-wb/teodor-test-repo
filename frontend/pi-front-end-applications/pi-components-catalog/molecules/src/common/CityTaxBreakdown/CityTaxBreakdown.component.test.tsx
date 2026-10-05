import { fireEvent, render, screen } from '@testing-library/react';
import { formatCurrency, formatPrice, renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React from 'react';

import CityTaxBreakdown from '../CityTaxBreakdown';

jest.mock('next-i18next', () => ({
  useTranslation: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ChevronDown24: () => <span data-testid="chevron-down" />,
  ChevronUp24: () => <span data-testid="chevron-up" />,
}));

jest.mock('@whitbread-eos/utils', () => ({
  formatPrice: jest.fn(),
  formatCurrency: jest.fn(),
  renderSanitizedHtml: jest.fn(),
}));

const mockUseTranslation = useTranslation as jest.Mock;
const mockFormatPrice = formatPrice as jest.Mock;
const mockFormatCurrency = formatCurrency as jest.Mock;
const mockRenderSanitizedHtml = renderSanitizedHtml as jest.Mock;

describe('CityTaxBreakdown', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseTranslation.mockReturnValue({
      t: jest.fn((key: string) => {
        const translations: Record<string, string> = {
          'booking.cityTax.label': 'City tax',
          'booking.cityTax.tooltip.title': 'City tax information',
          'booking.cityTax.tooltip.description': '<strong>City tax</strong> is charged per person.',
        };

        return translations[key] ?? key;
      }),
    });

    mockFormatCurrency.mockReturnValue('GBP');
    mockFormatPrice.mockReturnValue('£12.34');
    mockRenderSanitizedHtml.mockReturnValue(
      <span data-testid="sanitized-html">Sanitized description</span>
    );
  });

  it('renders the city tax label and formatted amount', () => {
    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={12.34} language="en-GB" />);

    expect(screen.getByText('City tax')).toBeInTheDocument();
    expect(screen.getByText('£12.34')).toBeInTheDocument();

    expect(mockFormatCurrency).toHaveBeenCalledWith('GBP');
    expect(mockFormatPrice).toHaveBeenCalledWith('GBP', '12.34', 'en-GB');

    expect(screen.getByTestId('chevron-down')).toBeInTheDocument();
    expect(screen.queryByTestId('chevron-up')).not.toBeInTheDocument();
  });

  it('uses the provided label and price styles', () => {
    render(
      <CityTaxBreakdown
        currencyCode="EUR"
        totalCityTaxAmount={10}
        language="de"
        cityTaxStyles={{
          labelStyle: {
            color: 'red',
            fontSize: '20px',
          },
          priceStyle: {
            color: 'blue',
            fontWeight: 'bold',
          },
        }}
      />
    );

    const label = screen.getByText('City tax');
    const price = screen.getByText('£12.34');

    expect(label).toHaveStyle({
      color: 'red',
      fontSize: '20px',
    });

    expect(price).toHaveStyle({
      color: 'blue',
      fontWeight: 'bold',
    });
  });

  it('opens the city tax accordion when the label is clicked', () => {
    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={12.34} language="en-GB" />);

    expect(screen.queryByText('City tax information')).not.toBeInTheDocument();

    expect(screen.queryByTestId('sanitized-html')).not.toBeInTheDocument();

    fireEvent.click(screen.getByText('City tax'));

    expect(screen.getByText('City tax information')).toBeInTheDocument();

    expect(screen.getByTestId('sanitized-html')).toBeInTheDocument();

    expect(screen.getByTestId('chevron-up')).toBeInTheDocument();
    expect(screen.queryByTestId('chevron-down')).not.toBeInTheDocument();
  });

  it('closes the city tax accordion when the label is clicked again', () => {
    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={12.34} language="en-GB" />);

    const label = screen.getByText('City tax');

    fireEvent.click(label);

    expect(screen.getByText('City tax information')).toBeInTheDocument();

    fireEvent.click(label);

    expect(screen.queryByText('City tax information')).not.toBeInTheDocument();

    expect(screen.queryByTestId('sanitized-html')).not.toBeInTheDocument();

    expect(screen.getByTestId('chevron-down')).toBeInTheDocument();
    expect(screen.queryByTestId('chevron-up')).not.toBeInTheDocument();
  });

  it('renders the sanitized tooltip description when opened', () => {
    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={20} language="en-GB" />);

    fireEvent.click(screen.getByText('City tax'));

    expect(mockRenderSanitizedHtml).toHaveBeenCalledWith(
      '<strong>City tax</strong> is charged per person.'
    );

    expect(screen.getByTestId('sanitized-html')).toBeInTheDocument();
  });

  it('passes the tooltip title translation key to the translation function', () => {
    const mockT = jest.fn((key: string) => key);

    mockUseTranslation.mockReturnValue({
      t: mockT,
    });

    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={10} language="en-GB" />);

    expect(mockT).toHaveBeenCalledWith('booking.cityTax.label');

    fireEvent.click(screen.getByText('booking.cityTax.label'));

    expect(mockT).toHaveBeenCalledWith('booking.cityTax.tooltip.title');

    expect(mockT).toHaveBeenCalledWith('booking.cityTax.tooltip.description');
  });

  it('formats the city tax amount with two decimal places', () => {
    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={10} language="en-GB" />);

    expect(mockFormatPrice).toHaveBeenCalledWith('GBP', '10.00', 'en-GB');
  });

  it('passes an undefined amount to formatPrice when totalCityTaxAmount is undefined', () => {
    render(<CityTaxBreakdown currencyCode="GBP" language="en-GB" />);

    expect(mockFormatCurrency).toHaveBeenCalledWith('GBP');

    expect(mockFormatPrice).toHaveBeenCalledWith('GBP', undefined, 'en-GB');
  });

  it('passes an undefined amount to formatPrice when totalCityTaxAmount is zero', () => {
    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={0} language="en-GB" />);
    expect(mockFormatPrice).toHaveBeenCalledWith('GBP', 0, 'en-GB');
  });

  it('handles an undefined currency code', () => {
    render(<CityTaxBreakdown totalCityTaxAmount={15} language="en-GB" />);

    expect(mockFormatCurrency).toHaveBeenCalledWith(undefined);

    expect(mockFormatPrice).toHaveBeenCalledWith('GBP', '15.00', 'en-GB');
  });

  it('handles an undefined language', () => {
    render(<CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={15} />);

    expect(mockFormatPrice).toHaveBeenCalledWith('GBP', '15.00', undefined);
  });

  it('renders correctly when cityTaxStyles is omitted', () => {
    const { container } = render(
      <CityTaxBreakdown currencyCode="GBP" totalCityTaxAmount={15} language="en-GB" />
    );

    expect(container.firstChild).toBeInTheDocument();
    expect(screen.getByText('City tax')).toBeInTheDocument();
    expect(screen.getByText('£12.34')).toBeInTheDocument();
  });

  it('renders correctly when only labelStyle is provided', () => {
    render(
      <CityTaxBreakdown
        currencyCode="GBP"
        totalCityTaxAmount={15}
        language="en-GB"
        cityTaxStyles={{
          labelStyle: {
            color: 'green',
          },
        }}
      />
    );

    expect(screen.getByText('City tax')).toHaveStyle({
      color: 'green',
    });
  });

  it('renders correctly when only priceStyle is provided', () => {
    render(
      <CityTaxBreakdown
        currencyCode="GBP"
        totalCityTaxAmount={15}
        language="en-GB"
        cityTaxStyles={{
          priceStyle: {
            color: 'purple',
          },
        }}
      />
    );

    expect(screen.getByText('£12.34')).toHaveStyle({
      color: 'purple',
    });
  });
});
