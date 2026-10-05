import '@testing-library/jest-dom';

import { formatCurrency, formatDataTestId, formatPrice } from './formatters';

describe('formatPrice Method', () => {
  it('should display the price before currency if language is german and currency is euro', function () {
    const testLanguage = 'de';
    const testCurrency = '€';
    const testPrice = 10;
    expect(formatPrice(testCurrency, testPrice, testLanguage)).toEqual('10€');
  });

  it('should display the price after currency if language is not german and currency is not euro', function () {
    const testLanguage = 'en';
    const testCurrency = '£';
    const testPrice = 20;
    expect(formatPrice(testCurrency, testPrice, testLanguage)).toEqual('£20');
  });
});
describe('formatCurrency Method', () => {
  it('should display the € symbol when EUR is sent', function () {
    const testCurrency = 'EUR';
    const expectedCurrency = '€';
    expect(formatCurrency(testCurrency)).toEqual(expectedCurrency);
  });
  it('should display the £ symbol when GBP is sent', function () {
    const testCurrency = 'GBP';
    const expectedCurrency = '£';
    expect(formatCurrency(testCurrency)).toEqual(expectedCurrency);
  });
  it('should display empty string when other currency codes are sent', function () {
    const testCurrency = 'USD';
    const expectedCurrency = '';
    expect(formatCurrency(testCurrency)).toEqual(expectedCurrency);
  });
});
describe('formatDataTestId Method', () => {
  it('should display empty string when both params are null', function () {
    const prefix = null;
    const dataTestId = null;
    const expectedOutput = '';
    expect(formatDataTestId(prefix, dataTestId)).toEqual(expectedOutput);
  });
  it('should display only the dataTestId when prefix is null', function () {
    const prefix = null;
    const dataTestId = 'DataTestId';
    const expectedOutput = 'DataTestId';
    expect(formatDataTestId(prefix, dataTestId)).toEqual(expectedOutput);
  });
  it('should display both prefix and dataTestId when both are sent', function () {
    const prefix = 'Prefix';
    const dataTestId = 'DataTestId';
    const expectedOutput = 'Prefix-DataTestId';
    expect(formatDataTestId(prefix, dataTestId)).toEqual(expectedOutput);
  });
});
