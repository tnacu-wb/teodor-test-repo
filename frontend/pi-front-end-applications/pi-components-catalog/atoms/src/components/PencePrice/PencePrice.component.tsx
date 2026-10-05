import { Text, TextProps } from '@chakra-ui/react';
import { Currency, LanguageEnum } from '@whitbread-eos/api';
import { formatCurrency, formatRatePrice } from '@whitbread-eos/utils';

function getFractionStyle(size: 'xxs' | 'xs' | 's' | 'm'): TextProps {
  switch (size) {
    case 'xxs':
      return {
        fontSize: '0.75rem',
        lineHeight: '1.25rem',
        fontWeight: 'normal',
        marginLeft: '0.125rem',
      };
    case 'xs':
      return {
        fontSize: '0.675rem',
        lineHeight: '1.375rem',
        fontWeight: 'normal',
        marginLeft: '0.125rem',
      };
    case 's':
      return {
        fontSize: '0.75rem',
        lineHeight: '0.938rem',
        fontWeight: 'normal',
        marginLeft: '0.125rem',
      };
    case 'm':
      return {
        fontSize: '0.75rem',
        lineHeight: '1.0rem',
        fontWeight: 'normal',
        marginLeft: '0.125rem',
      };
    default:
      return {
        fontSize: '0.75rem',
        lineHeight: '1.125rem',
        fontWeight: 'normal',
        marginLeft: '0.125rem',
      };
  }
}

function getIntegerStyle(size: 'xxs' | 'xs' | 's' | 'm'): TextProps {
  switch (size) {
    case 'xs':
      return {
        fontSize: '1.25rem',
        lineHeight: '1.813rem',
        fontWeight: 'bold',
      };
    case 'xxs':
      return {
        fontSize: '1.125rem',
        lineHeight: '1.5rem',
        fontWeight: 'bold',
      };
    case 's':
      return {
        fontSize: '1.25rem',
        lineHeight: '1.375rem',
        fontWeight: 'bold',
      };

    case 'm':
      return {
        fontSize: '1.625rem',
        lineHeight: '1.625rem',
        fontWeight: 'bold',
      };
    default:
      return {
        fontSize: '1.25rem',
        lineHeight: '1.5rem',
        fontWeight: 'bold',
      };
  }
}

export type PenceProps = {
  lineThrough?: boolean;
  price: number | string;
  currency: string | undefined;
  language: string | undefined;
  size: 'xxs' | 'xs' | 's' | 'm';
};

export default function PencePrice({
  price,
  currency,
  language,
  size,
  lineThrough,
}: Readonly<PenceProps>) {
  const isPriceFormatDE = language === LanguageEnum.GERMAN && currency === Currency.EUR_NAME;

  const normalizedPrice = typeof price === 'number' ? formatRatePrice(price) : price;
  const [integer, decimal] = normalizedPrice.toString().split(/[.,]/);
  const currencySymbol = formatCurrency(currency || '');
  const separator = isPriceFormatDE ? ',' : '.';

  const formattedPrice = isPriceFormatDE
    ? `${integer}${decimal ? separator + '' + decimal : ''}${currencySymbol}`
    : `${currencySymbol}${integer}${decimal ? separator + '' + decimal : ''}`;

  return (
    <Text
      data-testid="pence-price"
      as="span"
      sx={pencePriceStyle}
      aria-label={formattedPrice}
      data-price={formattedPrice}
      textDecoration={lineThrough ? 'line-through' : undefined}
    >
      {!isPriceFormatDE && (
        <Text
          as="span"
          data-testid="pence-price-leading-symbol"
          aria-hidden="true"
          sx={getIntegerStyle(size)}
        >
          {currencySymbol}
        </Text>
      )}
      <Text
        data-testid="pence-price-integer"
        as="span"
        aria-hidden="true"
        sx={getIntegerStyle(size)}
      >
        {integer}
      </Text>
      {decimal ? (
        <>
          <Text /* added hidden to keep automation tests valid */
            as="span"
            data-testid="pence-price-separator"
            sx={{ visibility: 'hidden', width: '0', display: 'inline-block' }}
            aria-hidden="true"
          >
            {separator}
          </Text>
          <Text
            data-testid="pence-price-decimal"
            as="span"
            sx={getFractionStyle(size)}
            aria-hidden="true"
          >
            {decimal}
          </Text>
        </>
      ) : null}
      {isPriceFormatDE && (
        <Text
          as="span"
          data-testid="pence-price-trailing-symbol"
          aria-hidden="true"
          sx={getIntegerStyle(size)}
        >
          {currencySymbol}
        </Text>
      )}
    </Text>
  );
}

const pencePriceStyle = {
  display: 'inline-flex',
  alignItems: 'flex-start',
};
