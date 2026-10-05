import '@testing-library/jest-dom';

import { isStringValid } from './validators';

describe('isStringValid Method', () => {
  it('should return false if given string is empty ', function () {
    const testString = '';
    expect(isStringValid(testString)).toEqual(false);
  });

  it('should return true if given string has at least one character ', function () {
    const testString = 'abc';
    expect(isStringValid(testString)).toEqual(true);
  });
});
