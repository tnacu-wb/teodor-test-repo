import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import PencePrice from './PencePrice.component';

describe('PencePrice', () => {
  it('should render the price with correct formatting', () => {
    const { getByTestId, queryByTestId } = render(
      <PencePrice price={1234.55} currency="GBP" language="en" size="s" />
    );
    expect(getByTestId('pence-price-leading-symbol')).toHaveTextContent('£');
    expect(getByTestId('pence-price-integer')).toHaveTextContent('1234');
    expect(getByTestId('pence-price-decimal')).toHaveTextContent('55');
    expect(queryByTestId('pence-price-trailing-symbol')).not.toBeInTheDocument();

    const priceElement = getByTestId('pence-price');
    expect(priceElement).toHaveAttribute('aria-label', '£1234.55');
  });
  it('should render the price without decimal when it is a whole number', () => {
    const { getByTestId, queryByTestId } = render(
      <PencePrice price={1234} currency="GBP" language="en" size="xs" />
    );
    expect(queryByTestId('pence-price-trailing-symbol')).not.toBeInTheDocument();
    expect(getByTestId('pence-price-integer')).toHaveTextContent('1234');
    expect(queryByTestId('pence-price-decimal')).not.toBeInTheDocument();
    expect(getByTestId('pence-price-leading-symbol')).toHaveTextContent('£');

    const priceElement = getByTestId('pence-price');
    expect(priceElement).toHaveAttribute('aria-label', '£1234');
  });
  it('should handle different currencies and languages', () => {
    const { getByTestId, queryByTestId } = render(
      <PencePrice price={1234.55} currency="EUR" language="de" size="xxs" />
    );
    expect(queryByTestId('pence-price-leading-symbol')).not.toBeInTheDocument();
    expect(getByTestId('pence-price-integer')).toHaveTextContent('1234');
    expect(getByTestId('pence-price-decimal')).toHaveTextContent('55');
    expect(getByTestId('pence-price-trailing-symbol')).toHaveTextContent('€');

    const priceElement = getByTestId('pence-price');
    expect(priceElement).toHaveAttribute('aria-label', '1234,55€');
  });

  it('should handle EUR en', () => {
    const { getByTestId, queryByTestId } = render(
      <PencePrice price={1234.55} currency="EUR" language="en" size="m" />
    );
    expect(queryByTestId('pence-price-trailing-symbol')).not.toBeInTheDocument();
    expect(getByTestId('pence-price-integer')).toHaveTextContent('1234');
    expect(getByTestId('pence-price-decimal')).toHaveTextContent('55');
    expect(getByTestId('pence-price-leading-symbol')).toHaveTextContent('€');

    const priceElement = getByTestId('pence-price');
    expect(priceElement).toHaveAttribute('aria-label', '€1234.55');
  });

  it('should apply line-through style when lineThrough prop is true', () => {
    const { getByTestId } = render(
      <PencePrice price={1234.55} currency="GBP" language="en" size="s" lineThrough={true} />
    );
    const priceElement = getByTestId('pence-price');
    expect(priceElement).toHaveStyle('text-decoration: line-through');
  });
});
