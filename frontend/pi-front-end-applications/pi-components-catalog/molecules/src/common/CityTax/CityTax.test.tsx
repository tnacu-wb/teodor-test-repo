import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { formatPriceWithDecimal } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import CityTax from './CityTax.component';

jest.mock('next-i18next', () => ({
  useTranslation: jest.fn(),
  withTranslation: () => (component: any) => component,
}));

jest.mock('@whitbread-eos/utils', () => ({
  formatCurrency: jest.fn((currency) => currency),
  formatDataTestId: jest.fn((base, suffix) => (base ? `${base}-${suffix}` : suffix)),
  formatPriceWithDecimal: jest.fn((lang, currency, price) => `${currency} ${price.toFixed(2)}`),
  renderSanitizedHtml: jest.fn((html) => html),
  cn: jest.fn((...classes) => classes.filter(Boolean).join(' ')),
}));

describe('CityTax Component', () => {
  const mockTranslations = {
    'booking.cityTax.label': 'City Tax',
    'amend.cityTax.title': 'Mandatory city tax at this location',
    'amend.cityTax.description':
      'The Manchester City Visitor Charge is £1 per room or unit per night at a hotel room or other accommodation.',
  };

  beforeEach(() => {
    (useTranslation as jest.Mock).mockReturnValue({
      t: (key: string) => mockTranslations[key as keyof typeof mockTranslations] || key,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render the component with all required elements', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-header')).toBeInTheDocument();
      expect(screen.getByTestId('booking-city-tax-city-tax-label')).toBeInTheDocument();
      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toBeInTheDocument();
    });

    it('should display correct city tax label text', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      const label = screen.getByTestId('booking-city-tax-city-tax-label');
      expect(label).toHaveTextContent('City Tax');
    });

    it('should display the city tax price with formatting', () => {
      render(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={75.5}
          language="en"
          currency="EUR"
        />
      );

      const price = screen.getByTestId('booking-city-tax-city-tax-price');
      expect(price).toBeInTheDocument();
      expect(price).toHaveTextContent('EUR 75.50');
    });

    it('should handle zero city tax total correctly', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={0} language="en" currency="GBP" />
      );

      const price = screen.getByTestId('booking-city-tax-city-tax-price');
      expect(price).toHaveTextContent('GBP 0.00');
    });

    it('should render without baseDataTestId', () => {
      render(<CityTax cityTaxTotal={50} language="en" currency="GBP" />);

      expect(screen.getByTestId('city-tax-header')).toBeInTheDocument();
    });

    it('should display price with different currency codes', () => {
      const { rerender } = render(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={100}
          language="en"
          currency="USD"
        />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('USD 100.00');

      rerender(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={100}
          language="en"
          currency="JPY"
        />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('JPY 100.00');
    });
  });

  describe('Collapse Functionality', () => {
    it('should render ChevronDown icon in initial state', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-chevron-down')).toBeInTheDocument();
      expect(screen.queryByTestId('booking-city-tax-city-tax-chevron-up')).not.toBeInTheDocument();
    });

    it('should toggle chevron icon when header is clicked', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      const header = screen.getByTestId('booking-city-tax-city-tax-header');

      fireEvent.click(header);
      expect(
        screen.queryByTestId('booking-city-tax-city-tax-chevron-down')
      ).not.toBeInTheDocument();
      expect(screen.getByTestId('booking-city-tax-city-tax-chevron-up')).toBeInTheDocument();

      fireEvent.click(header);
      expect(screen.getByTestId('booking-city-tax-city-tax-chevron-down')).toBeInTheDocument();
      expect(screen.queryByTestId('booking-city-tax-city-tax-chevron-up')).not.toBeInTheDocument();
    });

    it('should show collapse content when expanded', async () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      const header = screen.getByTestId('booking-city-tax-city-tax-header');

      fireEvent.click(header);
      await waitFor(() => {
        expect(screen.getByText('Mandatory city tax at this location')).toBeVisible();
        expect(
          screen.getByText(
            'The Manchester City Visitor Charge is £1 per room or unit per night at a hotel room or other accommodation.'
          )
        ).toBeVisible();
      });
    });

    it('should hide collapse content when collapsed', async () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      const header = screen.getByTestId('booking-city-tax-city-tax-header');

      fireEvent.click(header);
      await waitFor(() => {
        expect(screen.getByText('Mandatory city tax at this location')).toBeVisible();
      });

      fireEvent.click(header);
      await waitFor(() => {
        expect(screen.getByText('Mandatory city tax at this location')).not.toBeVisible();
      });
    });

    it('should display correct collapse content title and description', async () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      const header = screen.getByTestId('booking-city-tax-city-tax-header');
      fireEvent.click(header);

      await waitFor(() => {
        const title = screen.getByText('Mandatory city tax at this location');
        const description = screen.getByText(
          'The Manchester City Visitor Charge is £1 per room or unit per night at a hotel room or other accommodation.'
        );

        expect(title).toBeInTheDocument();
        expect(description).toBeInTheDocument();
      });
    });

    it('should maintain collapse state through multiple toggles', async () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      const header = screen.getByTestId('booking-city-tax-city-tax-header');

      for (let i = 0; i < 3; i++) {
        fireEvent.click(header);
        await waitFor(() => {
          expect(screen.getByTestId('booking-city-tax-city-tax-chevron-up')).toBeInTheDocument();
        });
        fireEvent.click(header);
        await waitFor(() => {
          expect(screen.getByTestId('booking-city-tax-city-tax-chevron-down')).toBeInTheDocument();
        });
      }
    });
  });

  describe('Languages and Currencies', () => {
    it('should render correctly with English language and GBP currency', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="en" currency="GBP" />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('GBP 50.00');
    });

    it('should render correctly with French language and EUR currency', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={75} language="fr" currency="EUR" />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('EUR 75.00');
    });

    it('should render correctly with German language and EUR currency', () => {
      render(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={100}
          language="de"
          currency="EUR"
        />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('EUR 100.00');
    });

    it('should render correctly with USD currency', () => {
      render(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={150}
          language="en"
          currency="USD"
        />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('USD 150.00');
    });

    it('should handle decimal values for different currencies', () => {
      render(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={99.99}
          language="en"
          currency="GBP"
        />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('GBP 99.99');
    });

    it('should pass correct language and currency to formatPriceWithDecimal', () => {
      render(
        <CityTax baseDataTestId="booking-city-tax" cityTaxTotal={50} language="fr" currency="EUR" />
      );

      expect(jest.mocked(formatPriceWithDecimal)).toHaveBeenCalledWith('fr', 'EUR', 50, true);
    });

    it('should work with Spanish language and EUR currency', () => {
      render(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={120}
          language="es"
          currency="EUR"
        />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('EUR 120.00');
    });

    it('should work with Italian language and EUR currency', () => {
      render(
        <CityTax
          baseDataTestId="booking-city-tax"
          cityTaxTotal={85.5}
          language="it"
          currency="EUR"
        />
      );

      expect(screen.getByTestId('booking-city-tax-city-tax-price')).toHaveTextContent('EUR 85.50');
    });
  });
});
