import '@testing-library/jest-dom';

import { swapKeysAndValues } from './object-utils';

describe('object-utils', () => {
  it('should return an object that swapped a key with its value', function () {
    const object = {
      key: 'value',
    };
    expect(swapKeysAndValues(object)).toEqual({ value: 'key' });
  });
});
