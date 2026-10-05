import '@testing-library/jest-dom';

import validateForm from './formValidation';

const t = (key: string) => {
  return key;
};

const mockedEmptyFormData = {
  baseDataTestId: 'PreCheckInPage',
  currentLang: 'en',
  defaultValues: {
    firstName: '',
    lastName: '',
    address: '',
    postalCode: '',
    city: '',
    country: '',
    dateOfBirth: '',
    dependent: '',
  },
};

const mockFedormData = {
  firstName: 'whitbread',
  lastName: 'pi',
  address: 'palaceroad',
  city: 'London',
  country: 'UK',
  postalCode: 'B24N H11',
  dateOfBirth: 'Mon Feb 20 1900 13:28:21 GMT+0530 (India Standard Time)',
  nationality: { value: 'GB', label: 'UK' },
  passport: '123221G',
  dependent: '1',
  dependents: [
    {
      firstname: 'Tom',
      lastname: 'Garry',
      dateofbirth: 'Mon Feb 20 1900 13:28:21 GMT+0530 (India Standard Time)',
      nationality: { value: 'GB', label: 'UK' },
      passport: '123221G',
    },
    {
      firstname: 'John',
      lastname: 'Doe',
      dateofbirth: 'Mon Feb 20 1900 13:28:21 GMT+0530 (India Standard Time)',
      nationality: { value: 'GB', label: 'UK' },
      passport: '2123221G',
    },
  ],
};

describe('Validate Form', () => {
  it('should validate the form with valid input validators', () => {
    const params = {
      t,
      currentLang: 'en',
    };
    const result = validateForm(params);
    expect(result.formValidationObject).toBeDefined();
    expect(result.formValidationSchema).toBeDefined();
  });
});

describe('Validate Form', () => {
  const currentLang = 'en';
  const { defaultValues } = mockedEmptyFormData;
  const params = {
    t,
    currentLang,
    defaultValues,
  };
  const result = validateForm(params);
  it('should validate the form with valid input validators', () => {
    const params = {
      t,
      currentLang: 'en',
    };
    const result = validateForm(params);
    expect(result.formValidationObject).toBeDefined();
    expect(result.formValidationSchema).toBeDefined();
  });

  it('should validate all the fields', () => {
    expect(result.formValidationSchema.isValidSync(mockFedormData)).toBe(true);
    expect(result.saveFormValidationSchema.isValidSync(mockFedormData)).toBe(true);

    expect(
      result.formValidationSchema.isValidSync({ ...mockFedormData, firstName: 'John$#' })
    ).toBe(false);
    expect(
      result.saveFormValidationSchema.isValidSync({ ...mockFedormData, firstName: 'John$#' })
    ).toBe(false);

    expect(result.formValidationSchema.isValidSync({ ...mockFedormData, country: 'GB' })).toBe(
      false
    );
    expect(result.formValidationSchema.isValidSync({ ...mockFedormData, country: 'DE' })).toBe(
      false
    );
    expect(result.saveFormValidationSchema.isValidSync({ ...mockFedormData, country: 'GB' })).toBe(
      false
    );
    expect(result.saveFormValidationSchema.isValidSync({ ...mockFedormData, country: 'DE' })).toBe(
      false
    );
  });

  it('should validate form with empty values', () => {
    expect(
      result.formValidationSchema.isValidSync({
        firstName: '',
        lastName: '',
        address: '',
        city: '',
        country: '',
        postalCode: '',
        dateOfBirth: '',
        nationality: null,
        passport: '',
        dependents: [],
      })
    ).toBe(false);
    expect(
      result.saveFormValidationSchema.isValidSync({
        firstName: '',
        lastName: '',
        address: '',
        city: '',
        country: '',
        postalCode: '',
        dateOfBirth: '',
        nationality: null,
        passport: '',
        dependents: [],
      })
    ).toBe(false);
  });

  it('should validate form with empty postal code for Non-German', () => {
    expect(
      result.formValidationSchema.isValidSync({
        ...mockFedormData,
        nationality: { value: 'US', label: 'American' },
        postalCode: '',
      })
    ).toBe(false);
  });

  it('should validate form with invalid nationality for passport', () => {
    expect(result.formValidationSchema.isValidSync({ ...mockFedormData, passport: '' })).toBe(
      false
    );
  });
});
