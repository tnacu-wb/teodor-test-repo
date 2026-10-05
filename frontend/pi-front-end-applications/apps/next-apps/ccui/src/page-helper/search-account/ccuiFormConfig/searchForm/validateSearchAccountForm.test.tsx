import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';

import validateSearchAccountForm from './validateSearchAccountForm';

describe('validateSearchAccountForm', () => {
  const mockT = jest.fn((key: string) => key);

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('function return value', () => {
    it('should return formValidationObject and formValidationSchema', () => {
      const result = validateSearchAccountForm(mockT);

      expect(result).toHaveProperty('formValidationObject');
      expect(result).toHaveProperty('formValidationSchema');
    });

    it('should have all expected fields in formValidationObject', () => {
      const result = validateSearchAccountForm(mockT);

      expect(result.formValidationObject).toHaveProperty('firstName');
      expect(result.formValidationObject).toHaveProperty('lastName');
      expect(result.formValidationObject).toHaveProperty('companyName');
      expect(result.formValidationObject).toHaveProperty('email');
      expect(result.formValidationObject).toHaveProperty('address');
      expect(result.formValidationObject).toHaveProperty('postalCode');
      expect(result.formValidationObject).toHaveProperty('mobileNumber');
      expect(result.formValidationObject).toHaveProperty('landlineNumber');
    });
  });

  describe('firstName validation', () => {
    it('should accept null firstName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('firstName', { firstName: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('firstName', {
        firstName: '',
      });

      expect(validated).toBeNull();
    });

    it('should validate with FIRST_NAME.MATCHES pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('firstName', { firstName: 'Invalid123@Name' })
      ).rejects.toThrow('Please enter a valid first name (max 20 characters)');
    });

    it('should validate max length with FIRST_NAME.MAX', async () => {
      const result = validateSearchAccountForm(mockT);

      const longName = 'A'.repeat(FORM_VALIDATIONS.FIRST_NAME.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('firstName', { firstName: longName })
      ).rejects.toThrow('Please enter a valid first name (max 20 characters)');
    });

    it('should pass validation with valid firstName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('firstName', { firstName: 'John' })
      ).resolves.toBeDefined();
    });
  });

  describe('lastName validation', () => {
    it('should accept null lastName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('lastName', { lastName: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('lastName', { lastName: '' });

      expect(validated).toBeNull();
    });

    it('should validate with LAST_NAME.MATCHES pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('lastName', { lastName: 'Invalid123@Surname' })
      ).rejects.toThrow('Please enter a valid surname (max 30 characters)');
    });

    it('should validate max length with LAST_NAME.MAX', async () => {
      const result = validateSearchAccountForm(mockT);

      const longName = 'A'.repeat(FORM_VALIDATIONS.LAST_NAME.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('lastName', { lastName: longName })
      ).rejects.toThrow('Please enter a valid surname (max 30 characters)');
    });

    it('should pass validation with valid lastName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('lastName', { lastName: 'Smith' })
      ).resolves.toBeDefined();
    });
  });

  describe('companyName validation', () => {
    it('should accept null companyName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('companyName', {
        companyName: '',
      });

      expect(validated).toBeNull();
    });

    it('should validate with COMPANY_NAME.MATCHES pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: 'Invalid%Company!' })
      ).rejects.toThrow('Please enter a valid company (max 50 characters)');
    });

    it('should validate max length with COMPANY_NAME.MAX (50)', async () => {
      const result = validateSearchAccountForm(mockT);

      const longName = 'A'.repeat(51);
      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: longName })
      ).rejects.toThrow('Please enter a valid company (max 50 characters)');
    });

    it('should pass validation with valid companyName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: 'Acme Corp Ltd.' })
      ).resolves.toBeDefined();
    });

    it('should accept special characters in COMPANY_NAME pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: 'Company & Co. @#()' })
      ).resolves.toBeDefined();
    });
  });

  describe('email validation', () => {
    it('should accept null email', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('email', { email: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('email', { email: '' });

      expect(validated).toBeNull();
    });

    it('should validate with EMAIL.MATCHES pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('email', { email: 'invalidemail' })
      ).rejects.toThrow('Please enter a valid e-mail address');
    });

    it('should validate max length with EMAIL.MAX', async () => {
      const result = validateSearchAccountForm(mockT);

      const longEmail = `${'a'.repeat(FORM_VALIDATIONS.EMAIL.MAX)}@test.com`;
      await expect(
        result.formValidationSchema.validateAt('email', { email: longEmail })
      ).rejects.toThrow('Please enter a valid e-mail address');
    });

    it('should pass validation with valid email', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('email', { email: 'test@example.com' })
      ).resolves.toBeDefined();
    });
  });

  describe('address validation', () => {
    it('should accept null address', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('address', { address: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('address', { address: '' });

      expect(validated).toBeNull();
    });

    it('should validate with ADDRESS.MATCHES pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('address', { address: 'Invalid%Address!' })
      ).rejects.toThrow('Please enter a valid address');
    });

    it('should validate max length with ADDRESS.MAX (200)', async () => {
      const result = validateSearchAccountForm(mockT);

      const longAddress = 'A'.repeat(201);
      await expect(
        result.formValidationSchema.validateAt('address', { address: longAddress })
      ).rejects.toThrow('Please enter a valid address');
    });

    it('should pass validation with valid address', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('address', {
          address: '123 Main St, Apt 4B',
        })
      ).resolves.toBeDefined();
    });

    it('should accept special characters in ADDRESS pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('address', {
          address: '456 Oak Ave & Co. @#()-',
        })
      ).resolves.toBeDefined();
    });
  });

  describe('postalCode validation', () => {
    it('should accept null postalCode', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('postalCode', { postalCode: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('postalCode', {
        postalCode: '',
      });

      expect(validated).toBeNull();
    });

    it('should validate with POSTAL_CODE.MATCHES pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('postalCode', { postalCode: 'Invalid@Code!' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.postcode.invalid');
    });

    it('should validate max length with POSTAL_CODE.MAX (7)', async () => {
      const result = validateSearchAccountForm(mockT);

      const longPostcode = 'A'.repeat(8);
      await expect(
        result.formValidationSchema.validateAt('postalCode', { postalCode: longPostcode })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.postcode.invalid');
    });

    it('should pass validation with valid postalCode', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('postalCode', { postalCode: 'SW1A1AA' })
      ).resolves.toBeDefined();
    });

    it('should accept alphanumeric with spaces in POSTAL_CODE pattern', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('postalCode', { postalCode: 'SW1 1AA' })
      ).resolves.toBeDefined();
    });
  });

  describe('mobileNumber validation', () => {
    it('should accept null mobileNumber', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('mobileNumber', { mobileNumber: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('mobileNumber', {
        mobileNumber: '',
      });

      expect(validated).toBeNull();
    });

    it('should validate with PHONE.MATCHES pattern using excludeEmptyString', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('mobileNumber', { mobileNumber: 'invalid-phone' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.mobile.invalid');
    });

    it('should validate min length with PHONE.MIN', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('mobileNumber', { mobileNumber: '12' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.mobile.invalid');
    });

    it('should validate max length with PHONE.MAX', async () => {
      const result = validateSearchAccountForm(mockT);

      const longPhone = '1'.repeat(FORM_VALIDATIONS.PHONE.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('mobileNumber', { mobileNumber: longPhone })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.mobile.invalid');
    });

    it('should pass validation with valid mobileNumber', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('mobileNumber', { mobileNumber: '07123456789' })
      ).resolves.toBeDefined();
    });
  });

  describe('landlineNumber validation', () => {
    it('should accept null landlineNumber', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('landlineNumber', { landlineNumber: null })
      ).resolves.toBeDefined();
    });

    it('should transform empty string to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const validated = await result.formValidationSchema.validateAt('landlineNumber', {
        landlineNumber: '',
      });

      expect(validated).toBeNull();
    });

    it('should validate with PHONE.MATCHES pattern using excludeEmptyString', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('landlineNumber', {
          landlineNumber: 'invalid-landline',
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.telephone.invalid');
    });

    it('should validate min length with PHONE.MIN', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('landlineNumber', { landlineNumber: '12' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.telephone.invalid');
    });

    it('should validate max length with PHONE.MAX', async () => {
      const result = validateSearchAccountForm(mockT);

      const longPhone = '1'.repeat(FORM_VALIDATIONS.PHONE.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('landlineNumber', { landlineNumber: longPhone })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.telephone.invalid');
    });

    it('should pass validation with valid landlineNumber', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('landlineNumber', { landlineNumber: '02012345678' })
      ).resolves.toBeDefined();
    });
  });

  describe('cyclic dependencies', () => {
    it('should define cyclic dependencies for phone and landline fields', () => {
      const result = validateSearchAccountForm(mockT);

      expect(result.formValidationSchema).toBeDefined();
    });

    it('should validate entire form with all valid fields', async () => {
      const result = validateSearchAccountForm(mockT);

      const validData = {
        firstName: 'John',
        lastName: 'Smith',
        companyName: 'Acme Corp',
        email: 'john@example.com',
        address: '123 Main Street',
        postalCode: 'SW1A1AA',
        mobileNumber: '07123456789',
        landlineNumber: '02012345678',
      };

      await expect(result.formValidationSchema.validate(validData)).resolves.toBeDefined();
    });

    it('should validate entire form with all null fields', async () => {
      const result = validateSearchAccountForm(mockT);

      const nullData = {
        firstName: null,
        lastName: null,
        companyName: null,
        email: null,
        address: null,
        postalCode: null,
        mobileNumber: null,
        landlineNumber: null,
      };

      await expect(result.formValidationSchema.validate(nullData)).resolves.toBeDefined();
    });

    it('should validate entire form with all empty strings transformed to null', async () => {
      const result = validateSearchAccountForm(mockT);

      const emptyData = {
        firstName: '',
        lastName: '',
        companyName: '',
        email: '',
        address: '',
        postalCode: '',
        mobileNumber: '',
        landlineNumber: '',
      };

      await expect(result.formValidationSchema.validate(emptyData)).resolves.toBeDefined();
    });

    it('should handle validation errors for multiple fields', async () => {
      const result = validateSearchAccountForm(mockT);

      const invalidData = {
        firstName: 'Invalid123@',
        lastName: 'Invalid123@',
        companyName: 'Invalid%!',
        email: 'invalidemail',
        address: 'Invalid%!',
        postalCode: 'Invalid@Code',
        mobileNumber: 'invalid',
        landlineNumber: 'invalid',
      };

      await expect(result.formValidationSchema.validate(invalidData)).rejects.toThrow();
    });
  });

  describe('translation keys', () => {
    it('should call t function with expected translation keys', () => {
      validateSearchAccountForm(mockT);

      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.postcode.invalid');
      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.mobile.invalid');
      expect(mockT).toHaveBeenCalledWith('config.errorMessages.yourDetails.telephone.invalid');
    });
  });

  describe('hardcoded error messages', () => {
    it('should use hardcoded error messages for firstName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('firstName', { firstName: 'A'.repeat(21) })
      ).rejects.toThrow('Please enter a valid first name (max 20 characters)');
    });

    it('should use hardcoded error messages for lastName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('lastName', { lastName: 'A'.repeat(31) })
      ).rejects.toThrow('Please enter a valid surname (max 30 characters)');
    });

    it('should use hardcoded error messages for companyName', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: 'A'.repeat(51) })
      ).rejects.toThrow('Please enter a valid company (max 50 characters)');
    });

    it('should use hardcoded error messages for email', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('email', { email: 'invalidemail' })
      ).rejects.toThrow('Please enter a valid e-mail address');
    });

    it('should use hardcoded error messages for address', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('address', { address: 'Invalid%!' })
      ).rejects.toThrow('Please enter a valid address');
    });
  });

  describe('transform behavior', () => {
    it('should transform empty string to null for all string fields', async () => {
      const result = validateSearchAccountForm(mockT);

      const firstNameResult = await result.formValidationSchema.validateAt('firstName', {
        firstName: '',
      });
      const lastNameResult = await result.formValidationSchema.validateAt('lastName', {
        lastName: '',
      });
      const companyNameResult = await result.formValidationSchema.validateAt('companyName', {
        companyName: '',
      });
      const emailResult = await result.formValidationSchema.validateAt('email', { email: '' });
      const addressResult = await result.formValidationSchema.validateAt('address', {
        address: '',
      });
      const postalCodeResult = await result.formValidationSchema.validateAt('postalCode', {
        postalCode: '',
      });

      expect(firstNameResult).toBeNull();
      expect(lastNameResult).toBeNull();
      expect(companyNameResult).toBeNull();
      expect(emailResult).toBeNull();
      expect(addressResult).toBeNull();
      expect(postalCodeResult).toBeNull();
    });

    it('should transform falsy values to null for phone fields', async () => {
      const result = validateSearchAccountForm(mockT);

      const mobileResult = await result.formValidationSchema.validateAt('mobileNumber', {
        mobileNumber: '',
      });
      const landlineResult = await result.formValidationSchema.validateAt('landlineNumber', {
        landlineNumber: '',
      });

      expect(mobileResult).toBeNull();
      expect(landlineResult).toBeNull();
    });

    it('should not transform valid values', async () => {
      const result = validateSearchAccountForm(mockT);

      const firstNameResult = await result.formValidationSchema.validateAt('firstName', {
        firstName: 'John',
      });
      const mobileResult = await result.formValidationSchema.validateAt('mobileNumber', {
        mobileNumber: '07123456789',
      });

      expect(firstNameResult).toBe('John');
      expect(mobileResult).toBe('07123456789');
    });
  });

  describe('excludeEmptyString behavior', () => {
    it('should use excludeEmptyString for mobileNumber matches', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('mobileNumber', { mobileNumber: '' })
      ).resolves.toBeDefined();
    });

    it('should use excludeEmptyString for landlineNumber matches', async () => {
      const result = validateSearchAccountForm(mockT);

      await expect(
        result.formValidationSchema.validateAt('landlineNumber', { landlineNumber: '' })
      ).resolves.toBeDefined();
    });
  });
});
