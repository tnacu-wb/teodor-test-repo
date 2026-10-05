import '@testing-library/jest-dom';

import validateSearchAccountForm from './searchForm/validateSearchAccountForm';

const t = jest.fn();

describe('validateSearchAccountForm', () => {
  it('should validate the form with valid inputs', () => {
    const result = validateSearchAccountForm(t);

    expect(result.formValidationObject).toBeDefined();
    expect(result.formValidationSchema).toBeDefined();
  });

  it('should validate the firstname', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.firstName;

    const invalidFirstName = 'Thisnameismorethantwentyletterslong';
    expect(schema.isValidSync(invalidFirstName)).toBe(false);
    const validFirstName = 'John';
    expect(schema.isValidSync(validFirstName)).toBe(true);
    const emptyFirstName = '';
    expect(schema.isValidSync(emptyFirstName)).toBe(true);
  });

  it('should validate the lastname', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.lastName;

    const invalidLastName = 'Thisnameismorethanthirtyletterslong';
    expect(schema.isValidSync(invalidLastName)).toBe(false);
    const validLastName = 'Jonathan';
    expect(schema.isValidSync(validLastName)).toBe(true);
    const emptyLastName = '';
    expect(schema.isValidSync(emptyLastName)).toBe(true);
  });

  it('should validate the company name', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.companyName;

    const invalidCompanyName = 'Thiscompanynameismoremoremoremorethanfiftyletterslong';
    expect(schema.isValidSync(invalidCompanyName)).toBe(false);
    const validCompanyName = 'Company';
    expect(schema.isValidSync(validCompanyName)).toBe(true);
    const emptyCompanyName = '';
    expect(schema.isValidSync(emptyCompanyName)).toBe(true);
  });

  it('should validate the email', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.email;

    const invalidEmail = 'user.com';
    expect(schema.isValidSync(invalidEmail)).toBe(false);
    const validEmail = 'user@microsoft.com';
    expect(schema.isValidSync(validEmail)).toBe(true);
    const emptyEmail = '';
    expect(schema.isValidSync(emptyEmail)).toBe(true);
  });

  it('should validate the address', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.address;

    const validAddress = '31 Poplar Grove, Ramsbottom';
    expect(schema.isValidSync(validAddress)).toBe(true);
    const emptyAddress = '';
    expect(schema.isValidSync(emptyAddress)).toBe(true);
  });

  it('should validate the postcode', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.postalCode;

    const invalidPostcode = '1234567890123456789012345678901234567890';
    expect(schema.isValidSync(invalidPostcode)).toBe(false);
    const validPostcode = 'BL0 0BE';
    expect(schema.isValidSync(validPostcode)).toBe(true);
    const emptyPostcode = '';
    expect(schema.isValidSync(emptyPostcode)).toBe(true);
  });

  it('should validate the mobile number', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.mobileNumber;

    const invalidMobileNumber = '000';
    expect(schema.isValidSync(invalidMobileNumber)).toBe(false);
    const validMobileNumber = '+440000000';
    expect(schema.isValidSync(validMobileNumber)).toBe(true);
    const emptyMobileNumber = '';
    expect(schema.isValidSync(emptyMobileNumber)).toBe(true);
  });

  it('should validate the landline number', () => {
    const t = jest.fn();

    const result = validateSearchAccountForm(t);
    const schema = result.formValidationObject.landlineNumber;

    const invalidLandlineNumber = '000';
    expect(schema.isValidSync(invalidLandlineNumber)).toBe(false);
    const validLandlineNumber = '+440000000';
    expect(schema.isValidSync(validLandlineNumber)).toBe(true);
    const emptyLandlineNumber = '';
    expect(schema.isValidSync(emptyLandlineNumber)).toBe(true);
  });
});
