import '@testing-library/jest-dom';

import validateForm from './formValidation';

const mockFormContentData = {
  'reservationform.adult.error': 'Please select number of adults',
  'reservationform.menu.error': 'Please select a menu',
  'reservationform.enquiry.adult.invalidlength': 'Adults field is required',
  'reservationform.enquiry.adult.invalidcharacters': 'Invalid characters in adults field',
  'reservationform.enquiry.adult.label': 'Adults cannot be 0',
  'reservationform.enquiry.children.invalidcharacters': 'Invalid characters in children field',
  'reservationform.yourdetails.firstname.required': 'First name is required',
  'reservationform.yourdetails.firstname.invalidlength':
    'First name must be between 2 and 100 characters',
  'reservationform.yourdetails.firstname.invalidcharacters': 'Invalid characters in first name',
  'reservationform.yourdetails.lastname.required': 'Last name is required',
  'reservationform.yourdetails.lastname.invalidlength':
    'Last name must be between 2 and 100 characters',
  'reservationform.yourdetails.lastname.invalidcharacters': 'Invalid characters in last name',
  'reservationform.yourdetails.email.incompleteerror': 'Email is required',
  'reservationform.yourdetails.email.invalidcharacters': 'Invalid email address',
  'reservationform.yourdetails.contact.invalidcontact': 'Phone number is required',
  'reservationform.yourdetails.contact.invalidspacescharacters':
    'Phone number cannot contain spaces',
  'reservationform.yourdetails.contact.invalidcharacters': 'Invalid input',
  'reservationform.yourdetails.contact.invalidlength': 'Phone number must be 11 digits or less',
  'reservationform.policystatement.checkbox.error': 'You must accept the privacy statement',
};

describe('formValidation', () => {
  describe('formStepOneValidationSchema', () => {
    it('should validate adults field - valid number greater than 0', () => {
      const { formStepOneValidationSchema } = validateForm(mockFormContentData, false);

      const result = formStepOneValidationSchema.safeParse({
        adults: 2,
        children: 0,
        time: '18:00',
      });

      expect(result.success).toBe(true);
    });

    it('should invalidate adults field when 0', () => {
      const { formStepOneValidationSchema } = validateForm(mockFormContentData, false);

      const result = formStepOneValidationSchema.safeParse({
        adults: 0,
        children: 0,
        time: '18:00',
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Please select number of adults');
      }
    });

    it('should validate optional children field', () => {
      const { formStepOneValidationSchema } = validateForm(mockFormContentData, false);

      const result = formStepOneValidationSchema.safeParse({
        adults: 2,
        time: '18:00',
      });

      expect(result.success).toBe(true);
    });

    it('should validate menuId when menu option is available', () => {
      const { formStepOneValidationSchema } = validateForm(mockFormContentData, true);

      const result = formStepOneValidationSchema.safeParse({
        adults: 2,
        children: 0,
        time: '18:00',
        menuId: 'menu-123',
      });
      expect(result.success).toBe(true);
    });
    it('should invalidate phone number with multiple invalid characters', () => {
      const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

      // This should trigger chars !== 1 branch
      const result = completeFormValidationSchema.safeParse({
        firstname: 'John',
        lastname: 'Doe',
        emailAddress: 'john.doe@example.com',
        telephoneNumber: '07abc56789',
        privacyStatement: true,
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Invalid input');
      }
    });

    it('should invalidate phone number with a single invalid character', () => {
      const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

      // This should trigger chars === 1 branch
      const result = completeFormValidationSchema.safeParse({
        firstname: 'John',
        lastname: 'Doe',
        emailAddress: 'john.doe@example.com',
        telephoneNumber: '0712345678a',
        privacyStatement: true,
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Invalid input');
      }
    });

    it('should invalidate empty menuId when menu option is available', () => {
      const { formStepOneValidationSchema } = validateForm(mockFormContentData, true);

      const result = formStepOneValidationSchema.safeParse({
        adults: 2,
        children: 0,
        time: '18:00',
        menuId: '',
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Please select a menu');
      }
    });

    it('should allow empty menuId when menu option is not available', () => {
      const { formStepOneValidationSchema } = validateForm(mockFormContentData, false);

      const result = formStepOneValidationSchema.safeParse({
        adults: 2,
        children: 0,
        time: '18:00',
        menuId: '',
      });

      expect(result.success).toBe(true);
    });

    it('should validate optional fields (highchair, wheelchair, specialRequest)', () => {
      const { formStepOneValidationSchema } = validateForm(mockFormContentData, false);

      const result = formStepOneValidationSchema.safeParse({
        adults: 2,
        children: 1,
        time: '18:00',
        highchair: 1,
        wheelchair: true,
        specialRequest: 'Window seat please',
      });

      expect(result.success).toBe(true);
    });
  });

  describe('enquiryFormValidationSchema', () => {
    it('should validate valid adultsByEnquiry', () => {
      const { enquiryFormValidationSchema } = validateForm(mockFormContentData, false);

      const result = enquiryFormValidationSchema.safeParse({
        adultsByEnquiry: '2',
        childrenByEnquiry: '',
      });

      expect(result.success).toBe(true);
    });

    it('should invalidate empty adultsByEnquiry', () => {
      const { enquiryFormValidationSchema } = validateForm(mockFormContentData, false);

      const result = enquiryFormValidationSchema.safeParse({
        adultsByEnquiry: '',
        childrenByEnquiry: '',
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Adults field is required');
      }
    });

    it('should invalidate adultsByEnquiry with value 0', () => {
      const { enquiryFormValidationSchema } = validateForm(mockFormContentData, false);

      const result = enquiryFormValidationSchema.safeParse({
        adultsByEnquiry: '0',
        childrenByEnquiry: '',
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Adults cannot be 0');
      }
    });

    it('should validate optional childrenByEnquiry', () => {
      const { enquiryFormValidationSchema } = validateForm(mockFormContentData, false);

      const result = enquiryFormValidationSchema.safeParse({
        adultsByEnquiry: '2',
        childrenByEnquiry: '1',
      });

      expect(result.success).toBe(true);
    });

    it('should invalidate adultsByEnquiry with invalid characters', () => {
      const { enquiryFormValidationSchema } = validateForm(mockFormContentData, false);

      const result = enquiryFormValidationSchema.safeParse({
        adultsByEnquiry: 'abc',
        childrenByEnquiry: '',
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Invalid characters in adults field');
      }
    });

    it('should invalidate childrenByEnquiry with invalid characters', () => {
      const { enquiryFormValidationSchema } = validateForm(mockFormContentData, false);

      const result = enquiryFormValidationSchema.safeParse({
        adultsByEnquiry: '2',
        childrenByEnquiry: 'xyz',
      });

      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].message).toBe('Invalid characters in children field');
      }
    });
  });

  describe('completeFormValidationSchema', () => {
    it('should validate complete valid form data', () => {
      const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

      const result = completeFormValidationSchema.safeParse({
        firstname: 'John',
        lastname: 'Doe',
        emailAddress: 'john.doe@example.com',
        telephoneNumber: '+447123456789',
        consent: true,
        privacyStatement: true,
      });

      expect(result.success).toBe(true);
    });

    describe('firstname validation', () => {
      it('should invalidate empty firstname', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: '',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '07123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('First name is required');
        }
      });

      it('should invalidate firstname shorter than 2 characters', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'J',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '07123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe(
            'First name must be between 2 and 100 characters'
          );
        }
      });

      it('should trim whitespace from firstname', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: '  John  ',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '+447123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(true);
      });
    });

    describe('lastname validation', () => {
      it('should invalidate empty lastname', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: '',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '07123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Last name is required');
        }
      });

      it('should invalidate lastname shorter than 2 characters', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'D',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '07123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe(
            'Last name must be between 2 and 100 characters'
          );
        }
      });
    });

    describe('emailAddress validation', () => {
      it('should invalidate empty email', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: '',
          telephoneNumber: '07123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Email is required');
        }
      });

      it('should invalidate invalid email format', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'invalid-email',
          telephoneNumber: '07123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Invalid email address');
        }
      });

      it('should validate correct email format', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '+447123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(true);
      });
    });

    describe('telephoneNumber validation', () => {
      it('should invalidate empty phone number', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '',
          privacyStatement: true,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Phone number is required');
        }
      });

      it('should invalidate phone number with multiple invalid characters', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);
        // This should trigger chars !== 1 branch
        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '07abc56789',
          privacyStatement: true,
        });
        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Invalid input');
        }
      });

      it('should invalidate phone number with a single invalid character', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);
        // This should trigger chars === 1 branch
        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '0712345678a',
          privacyStatement: true,
        });
        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Invalid input');
        }
      });

      it('should invalidate phone number with multiple invalid characters', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);
        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '07abc56789',
          privacyStatement: true,
        });
        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Invalid input');
        }
      });

      it('should invalidate phone number with a single invalid character', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);
        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '0712345678a',
          privacyStatement: true,
        });
        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('Invalid input');
        }
      });

      it('should validate correct phone number', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '+447123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(true);
      });
    });

    describe('privacyStatement validation', () => {
      it('should invalidate when privacyStatement is false', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '+447123456789',
          privacyStatement: false,
        });

        expect(result.success).toBe(false);
        if (!result.success) {
          expect(result.error.issues[0].message).toBe('You must accept the privacy statement');
        }
      });

      it('should validate when privacyStatement is true', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '+447123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(true);
      });
    });

    describe('consent validation', () => {
      it('should allow optional consent field', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '+447123456789',
          privacyStatement: true,
        });

        expect(result.success).toBe(true);
      });

      it('should validate consent as true', () => {
        const { completeFormValidationSchema } = validateForm(mockFormContentData, false);

        const result = completeFormValidationSchema.safeParse({
          firstname: 'John',
          lastname: 'Doe',
          emailAddress: 'john.doe@example.com',
          telephoneNumber: '+447123456789',
          consent: true,
          privacyStatement: true,
        });

        expect(result.success).toBe(true);
      });
    });
  });

  describe('validateForm return values', () => {
    it('should return all three validation schemas', () => {
      const result = validateForm(mockFormContentData, false);

      expect(result).toHaveProperty('formStepOneValidationSchema');
      expect(result).toHaveProperty('completeFormValidationSchema');
      expect(result).toHaveProperty('enquiryFormValidationSchema');
    });

    it('should create different schemas based on isMenuOptionAvailable parameter', () => {
      const withMenu = validateForm(mockFormContentData, true);
      const withoutMenu = validateForm(mockFormContentData, false);

      // Test that menuId validation differs
      const withMenuResult = withMenu.formStepOneValidationSchema.safeParse({
        adults: 2,
        time: '18:00',
        menuId: '',
      });

      const withoutMenuResult = withoutMenu.formStepOneValidationSchema.safeParse({
        adults: 2,
        time: '18:00',
        menuId: '',
      });

      expect(withMenuResult.success).toBe(false); // Should fail when menu is required
      expect(withoutMenuResult.success).toBe(true); // Should pass when menu is optional
    });
  });
});
