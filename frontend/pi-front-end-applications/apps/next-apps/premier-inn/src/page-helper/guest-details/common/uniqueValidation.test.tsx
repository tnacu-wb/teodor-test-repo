import * as yup from 'yup';

import checkIsUnique from './uniqueValidation';

describe('checkIsUnique', () => {
  it("should add a 'unique' method to the 'array' schema", () => {
    checkIsUnique();
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    expect(yup.array().unique).toBeDefined();
  });

  it('should validate unique fields in an array', () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const schema = yup.array().unique('firstName', 'This field must be unique');

    const validValues = [
      [{ title: 'Mr', firstName: 'John', lastName: 'Doe' }],
      [{ title: 'Ms', firstName: 'Jane', lastName: 'Doe' }],
      [{ title: 'Mr', firstName: 'John', lastName: 'Smith' }],
      [
        { title: 'Mr', firstName: 'John', lastName: 'Doe' },
        { title: 'Ms', firstName: 'Jane', lastName: 'Smith' },
      ],
    ];

    validValues.forEach((value) => {
      expect(schema.isValidSync(value)).toBe(true);
    });

    const invalidValues = [
      [
        { title: 'Mr', firstName: 'John', lastName: 'Doe' },
        { title: 'Mr', firstName: 'John', lastName: 'Doe' },
      ],
      [
        { title: 'Ms', firstName: 'Jane', lastName: 'Smith' },
        { title: 'Ms', firstName: 'Jane', lastName: 'Smith' },
        { title: 'Mr', firstName: 'Bob', lastName: 'Jones' },
      ],
    ];

    invalidValues.forEach((value) => {
      expect(schema.isValidSync(value)).toBe(false);
    });
  });
});
