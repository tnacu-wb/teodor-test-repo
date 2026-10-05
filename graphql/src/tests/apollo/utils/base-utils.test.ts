import {
  addField,
  addFieldIfNotUndefined,
  addFieldIfNotUndefinedAndRequired,
  joinCriteria,
  objectIsNullEmptyOrUndefined,
  maskSensitiveFields,
  toIsoDate
} from '../../../apollo/utils/base-utils';

describe('base-utils', () => {
  describe('addField', () => {
    it('should add a field to the finalMap when called', () => {
      const finalMap: { [key: string]: any } = {};
      addField('value', 'keyStr', finalMap);
      addField(true, 'keyBool', finalMap);
      addField(1, 'keyInt', finalMap);
      expect(finalMap).toEqual({ keyBool: true, keyInt: 1, keyStr: 'value' });
    });
  });

  describe('addFieldIfNotUndefined', () => {
    it('should add a field to the finalMap when it is not undefined or empty', () => {
      const finalMap: { [key: string]: any } = {};
      addFieldIfNotUndefined('value', 'keyStr1', finalMap);
      addFieldIfNotUndefined('0', 'keyStr2', finalMap);
      addFieldIfNotUndefined(true, 'keyBool1', finalMap);
      addFieldIfNotUndefined(false, 'keyBool2', finalMap);
      addFieldIfNotUndefined(0, 'keyInt', finalMap);
      addFieldIfNotUndefined(null, 'keyNull', finalMap);
      addFieldIfNotUndefined(undefined, 'keyUndefined', finalMap);
      expect(finalMap).toEqual({
        keyStr1: 'value',
        keyStr2: '0',
        keyBool1: true,
        keyBool2: false,
        keyInt: 0
      });
    });

    it('should not add a field to the finalMap when it is undefined or empty', () => {
      const finalMap: { [key: string]: any } = {};
      addFieldIfNotUndefined('', 'key', finalMap);
      expect(finalMap).toEqual({});
      addFieldIfNotUndefined('', 'key', finalMap);
      expect(finalMap).toEqual({});
    });
  });

  describe('addFieldIfNotUndefinedAndRequired', () => {
    it('should add a field to the finalMap when it is not undefined or empty', () => {
      const finalMap: { [key: string]: any } = {};
      addFieldIfNotUndefinedAndRequired('value', 'keyStr1', finalMap);
      addFieldIfNotUndefinedAndRequired('0', 'keyStr2', finalMap);
      addFieldIfNotUndefinedAndRequired(true, 'keyBool1', finalMap);
      addFieldIfNotUndefinedAndRequired(false, 'keyBool2', finalMap);
      addFieldIfNotUndefinedAndRequired(1, 'keyInt', finalMap);
      expect(finalMap).toEqual({
        keyStr1: 'value',
        keyStr2: '0',
        keyBool1: true,
        keyBool2: false,
        keyInt: 1
      });
    });

    it('should throw an error when the field is undefined or empty', () => {
      const finalMap: { [key: string]: any } = {};
      expect(() => addFieldIfNotUndefinedAndRequired(null, 'key', finalMap)).toThrow(
        'Field key is required'
      );
      expect(() => addFieldIfNotUndefinedAndRequired(undefined, 'key', finalMap)).toThrow(
        'Field key is required'
      );
    });
  });

  describe('joinCriteria', () => {
    it('should join criteria with the specified delimiter when called', () => {
      const criteria = [
        { field: 'value1' },
        { field: 'value2' },
        { field: '0' },
        { field: '' },
        { field: false },
        { field: null },
        { field: undefined }
      ];
      const result = joinCriteria(criteria, 'field', ',');
      expect(result).toBe('value1,value2,0,false');
    });

    it('should join criteria without a field when called', () => {
      const criteria = ['value1', 'value2'];
      const result = joinCriteria(criteria, '', ',');
      expect(result).toBe('value1,value2');
    });
  });

  describe('objectIsNullEmptyOrUndefined', () => {
    it('should return true when the object is null, empty, or undefined', () => {
      const obj = {};
      expect(objectIsNullEmptyOrUndefined(obj)).toBeTruthy();
    });

    it('should return false when the object is not null, empty, or undefined', () => {
      const obj = { key: 'value' };
      expect(objectIsNullEmptyOrUndefined(obj)).toBeFalsy();
    });

    it('should return false when the object is boolean', () => {
      const obj = false;
      expect(objectIsNullEmptyOrUndefined(obj)).toBeFalsy();
    });

    it('should return false when the object is number', () => {
      const obj = 12;
      expect(objectIsNullEmptyOrUndefined(obj)).toBeFalsy();
    });
  });
});

describe('maskSensitiveFields', () => {
  it('should redact only specified fields when called', () => {
    const input = {
      companyName: 'Sensitive Company',
      arNumber: '12345',
      limit: 10
    };

    const fieldsToMask = ['companyName'];

    const result = maskSensitiveFields(input, fieldsToMask);

    expect(result.companyName).toBe('[REDACTED]');

    expect(result.arNumber).toBe('12345');
    expect(result.limit).toBe(10);
  });

  it('should not redact fields that are not in the list when called', () => {
    const input = {
      companyName: 'Sensitive Company',
      arNumber: '12345',
      limit: 10
    };

    const fieldsToMask = ['companyName'];

    const result = maskSensitiveFields(input, fieldsToMask);

    expect(result.companyName).toBe('[REDACTED]');

    expect(result.arNumber).toBe('12345');
    expect(result.limit).toBe(10);
  });

  it('should handle an empty object gracefully when called', () => {
    const input = {};
    const fieldsToMask = ['companyName'];

    const result = maskSensitiveFields(input, fieldsToMask);

    expect(result).toEqual({});
  });

  it('should handle cases when the field to mask does not exist', () => {
    const input = {
      companyName: 'Sensitive Company',
      arNumber: '12345',
      limit: 10
    };

    const fieldsToMask = ['nonExistentField'];

    const result = maskSensitiveFields(input, fieldsToMask);

    expect(result.companyName).toBe('Sensitive Company');
    expect(result.arNumber).toBe('12345');
    expect(result.limit).toBe(10);
  });
});

describe('toIsoDate', () => {
  it('should convert a date string to ISO format', () => {
    const date = '31/03/2026';
    const isoDate = '2026/03/31';

    const result = toIsoDate(date);

    expect(result).toBe(isoDate);
  });

  it('should handle null, empty or undefined date strings gracefully', () => {
    const invalidDate = '';
    expect(() => toIsoDate(invalidDate)).toThrow('Date cannot be null, empty or undefined');
  });
});
