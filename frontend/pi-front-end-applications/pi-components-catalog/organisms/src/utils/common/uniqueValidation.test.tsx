import * as yup from 'yup';

import checkIsUnique from './uniqueValidation';

describe('checkIsUnique', () => {
  it("should add a 'unique' method to the 'array' schema", () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    expect(yup.array().unique).toBeUndefined();
    checkIsUnique();
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    expect(yup.array().unique).toBeDefined();
  });

  it('should validate unique fields in an array', () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const schema = yup.array().unique('This field must be unique');

    const validValues = [
      [{ firstName: 'John', lastName: 'Doe' }],
      [{ firstName: 'Jane', lastName: 'Doe' }],
      [{ firstName: 'John', lastName: 'Smith' }],
    ];

    validValues.forEach((value) => {
      expect(schema.isValidSync(value)).toBe(true);
    });

    validValues.forEach((value) => {
      expect(schema.isValidSync(value)).toBe(true);
    });
  });
});
