import { isStringValid } from './validators';

export function formatCurrency(currencyCode: string): string {
  switch (currencyCode) {
    case 'GBP':
      return '£';
    case 'EUR':
      return '€';
    default:
      return '';
  }
}

export function formatPrice(
  currency: string | undefined,
  price: number | string | undefined,
  language = 'en'
) {
  if (language === 'de' && currency === '€') {
    return `${price}${currency}`;
  }
  return `${currency}${price}`;
}

export function formatDataTestId(prefix: string | null = '', dataTestId: string | null = '') {
  const previousString = isStringValid(prefix) ? `${prefix}-` : '';
  const testId = isStringValid(dataTestId) ? dataTestId : '';
  return `${previousString}${testId}`;
}
