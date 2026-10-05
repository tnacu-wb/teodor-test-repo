import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';

import validateBookingsForm from './validateBookingsForm';

describe('validateBookingsForm', () => {
  const mockT = jest.fn((key: string) => key);

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('function return value', () => {
    it('should return formValidationObject and formValidationSchema', () => {
      const result = validateBookingsForm({ t: mockT });

      expect(result).toHaveProperty('formValidationObject');
      expect(result).toHaveProperty('formValidationSchema');
    });

    it('should have all expected fields in formValidationObject', () => {
      const result = validateBookingsForm({ t: mockT });

      expect(result.formValidationObject).toHaveProperty('bookingReference');
      expect(result.formValidationObject).toHaveProperty('bookerLastName');
      expect(result.formValidationObject).toHaveProperty('arrivalDate');
      expect(result.formValidationObject).toHaveProperty('guestLastName');
      expect(result.formValidationObject).toHaveProperty('cancellationDate');
      expect(result.formValidationObject).toHaveProperty('companyName');
      expect(result.formValidationObject).toHaveProperty('bookerPostcode');
      expect(result.formValidationObject).toHaveProperty('bookerEmail');
      expect(result.formValidationObject).toHaveProperty('bookerPhone');
      expect(result.formValidationObject).toHaveProperty('hotelDetails');
      expect(result.formValidationObject).toHaveProperty('hotelLocation');
      expect(result.formValidationObject).toHaveProperty('thirdPartyBookingReferenceNumber');
    });
  });

  describe('default parameter', () => {
    it('should use enhancedSearch = false by default', () => {
      validateBookingsForm({ t: mockT });

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingReference.invalid');
      expect(mockT).not.toHaveBeenCalledWith('ccui.manageBooking.reference.invalid');
    });
  });

  describe('bookingReference validation', () => {
    it('should use bookingReference error messages when enhancedSearch is false', async () => {
      const result = validateBookingsForm({ t: mockT, enhancedSearch: false });

      await expect(
        result.formValidationSchema.validateAt('bookingReference', { bookingReference: '12' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingReference.invalid');
    });

    it('should use reference error messages when enhancedSearch is true', async () => {
      const result = validateBookingsForm({ t: mockT, enhancedSearch: true });

      await expect(
        result.formValidationSchema.validateAt('bookingReference', { bookingReference: '12' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.reference.min');
    });

    it('should not require validation when bookingReference is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookingReference', { bookingReference: '' })
      ).resolves.toBeDefined();
    });

    it('should validate with BOOKING_REFERENCE_CCUI.MATCHES pattern', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookingReference', {
          bookingReference: 'invalid@ref!',
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingReference.invalid');
    });

    it('should validate min length with BOOKING_REFERENCE_CCUI.MIN', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookingReference', { bookingReference: '12345' })
      ).rejects.toThrow();
    });

    it('should validate max length with BOOKING_REFERENCE_CCUI.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longRef = 'A'.repeat(FORM_VALIDATIONS.BOOKING_REFERENCE_CCUI.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('bookingReference', { bookingReference: longRef })
      ).rejects.toThrow();
    });

    it('should pass validation with valid bookingReference', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookingReference', {
          bookingReference: 'ABC1234567',
        })
      ).resolves.toBeDefined();
    });
  });

  describe('bookerLastName validation', () => {
    it('should not require validation when bookerLastName is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerLastName', { bookerLastName: '' })
      ).resolves.toBeDefined();
    });

    it('should validate with BOOKING_SURNAME.MATCHES pattern', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerLastName', {
          bookerLastName: 'Invalid#Name',
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingSurname.invalid');
    });

    it('should validate min length with BOOKING_SURNAME.MIN', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerLastName', { bookerLastName: 'A' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingSurname.min');
    });

    it('should validate max length with BOOKING_SURNAME.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longName = 'A'.repeat(FORM_VALIDATIONS.BOOKING_SURNAME.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('bookerLastName', { bookerLastName: longName })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingSurname.max');
    });

    it('should pass validation with valid bookerLastName', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerLastName', { bookerLastName: 'Smith' })
      ).resolves.toBeDefined();
    });
  });

  describe('arrivalDate validation', () => {
    it('should be nullable and not required', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('arrivalDate', { arrivalDate: null })
      ).resolves.toBeDefined();

      await expect(
        result.formValidationSchema.validateAt('arrivalDate', { arrivalDate: '' })
      ).resolves.toBeDefined();
    });

    it('should accept valid date string', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('arrivalDate', { arrivalDate: '2026-03-15' })
      ).resolves.toBeDefined();
    });
  });

  describe('guestLastName validation', () => {
    it('should not require validation when guestLastName is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('guestLastName', { guestLastName: '' })
      ).resolves.toBeDefined();
    });

    it('should validate with GUEST_SURNAME.MATCHES pattern', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('guestLastName', {
          guestLastName: 'Invalid#Guest',
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.guestSurname.invalid');
    });

    it('should validate min length with GUEST_SURNAME.MIN', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('guestLastName', { guestLastName: 'A' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.guestSurname.min');
    });

    it('should validate max length with GUEST_SURNAME.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longName = 'A'.repeat(FORM_VALIDATIONS.GUEST_SURNAME.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('guestLastName', { guestLastName: longName })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.guestSurname.max');
    });

    it('should pass validation with valid guestLastName', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('guestLastName', { guestLastName: 'Johnson' })
      ).resolves.toBeDefined();
    });
  });

  describe('cancellationDate validation', () => {
    it('should be nullable and not required', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('cancellationDate', { cancellationDate: null })
      ).resolves.toBeDefined();

      await expect(
        result.formValidationSchema.validateAt('cancellationDate', { cancellationDate: '' })
      ).resolves.toBeDefined();
    });

    it('should accept valid date string', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('cancellationDate', {
          cancellationDate: '2026-03-20',
        })
      ).resolves.toBeDefined();
    });
  });

  describe('companyName validation', () => {
    it('should not require validation when companyName is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: '' })
      ).resolves.toBeDefined();
    });

    it('should validate min length with CCUI_COMPANY_NAME.MIN', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: 'A' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.companyName.min');
    });

    it('should validate max length with CCUI_COMPANY_NAME.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longName = 'A'.repeat(FORM_VALIDATIONS.CCUI_COMPANY_NAME.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: longName })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.companyName.max');
    });

    it('should pass validation with valid companyName', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('companyName', { companyName: 'Acme Corp' })
      ).resolves.toBeDefined();
    });
  });

  describe('bookerPostcode validation', () => {
    it('should not require validation when bookerPostcode is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPostcode', { bookerPostcode: '' })
      ).resolves.toBeDefined();
    });

    it('should validate with CCUI_POSTAL_CODE.MATCHES pattern', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPostcode', {
          bookerPostcode: 'INVALID!CODE',
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPostcode.invalid');
    });

    it('should validate min length with CCUI_POSTAL_CODE.MIN', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPostcode', { bookerPostcode: 'A' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPostcode.min');
    });

    it('should validate max length with CCUI_POSTAL_CODE.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longPostcode = 'A'.repeat(FORM_VALIDATIONS.CCUI_POSTAL_CODE.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('bookerPostcode', { bookerPostcode: longPostcode })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPostcode.max');
    });

    it('should pass validation with valid bookerPostcode', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPostcode', { bookerPostcode: 'SW1A 1AA' })
      ).resolves.toBeDefined();
    });
  });

  describe('bookerEmail validation', () => {
    it('should not require validation when bookerEmail is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerEmail', { bookerEmail: '' })
      ).resolves.toBeDefined();
    });

    it('should validate with CCUI_EMAIL.MATCHES pattern', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerEmail', { bookerEmail: 'invalidemail' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerEmail.invalid');
    });

    it('should validate max length with CCUI_EMAIL.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longEmail = `${'a'.repeat(FORM_VALIDATIONS.CCUI_EMAIL.MAX)}@test.com`;
      await expect(
        result.formValidationSchema.validateAt('bookerEmail', { bookerEmail: longEmail })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerEmail.max');
    });

    it('should pass validation with valid bookerEmail', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerEmail', { bookerEmail: 'test@example.com' })
      ).resolves.toBeDefined();
    });
  });

  describe('bookerPhone validation', () => {
    it('should not require validation when bookerPhone is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPhone', { bookerPhone: '' })
      ).resolves.toBeDefined();
    });

    it('should validate with CCUI_PHONE.MATCHES pattern', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPhone', { bookerPhone: 'invalid-phone' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPhone.invalid');
    });

    it('should validate min length with CCUI_PHONE.MIN', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPhone', { bookerPhone: '12' })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPhone.min');
    });

    it('should validate max length with CCUI_PHONE.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longPhone = '1'.repeat(FORM_VALIDATIONS.CCUI_PHONE.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('bookerPhone', { bookerPhone: longPhone })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPhone.max');
    });

    it('should pass validation with valid bookerPhone', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('bookerPhone', { bookerPhone: '1234567890' })
      ).resolves.toBeDefined();
    });
  });

  describe('hotelDetails validation', () => {
    it('should accept valid object', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('hotelDetails', {
          hotelDetails: { id: '123', name: 'Test Hotel' },
        })
      ).resolves.toBeDefined();
    });
  });

  describe('hotelLocation validation', () => {
    it('should accept valid string', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('hotelLocation', { hotelLocation: 'London' })
      ).resolves.toBeDefined();
    });
  });

  describe('thirdPartyBookingReferenceNumber validation', () => {
    it('should not require validation when thirdPartyBookingReferenceNumber is empty', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('thirdPartyBookingReferenceNumber', {
          thirdPartyBookingReferenceNumber: '',
        })
      ).resolves.toBeDefined();
    });

    it('should validate with CCUI_THIRD_PARTY_REF.MATCHES pattern', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('thirdPartyBookingReferenceNumber', {
          thirdPartyBookingReferenceNumber: 'Invalid@Ref',
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith(
        'ccui.manageBooking.thirdPartyBookingReferenceNumber.invalid'
      );
    });

    it('should validate min length with CCUI_THIRD_PARTY_REF.MIN', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('thirdPartyBookingReferenceNumber', {
          thirdPartyBookingReferenceNumber: 'A',
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.thirdPartyBookingReferenceNumber.min');
    });

    it('should validate max length with CCUI_THIRD_PARTY_REF.MAX', async () => {
      const result = validateBookingsForm({ t: mockT });

      const longRef = 'A'.repeat(FORM_VALIDATIONS.CCUI_THIRD_PARTY_REF.MAX + 1);
      await expect(
        result.formValidationSchema.validateAt('thirdPartyBookingReferenceNumber', {
          thirdPartyBookingReferenceNumber: longRef,
        })
      ).rejects.toThrow();

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.thirdPartyBookingReferenceNumber.max');
    });

    it('should pass validation with valid thirdPartyBookingReferenceNumber', async () => {
      const result = validateBookingsForm({ t: mockT });

      await expect(
        result.formValidationSchema.validateAt('thirdPartyBookingReferenceNumber', {
          thirdPartyBookingReferenceNumber: 'ABC123456',
        })
      ).resolves.toBeDefined();
    });
  });

  describe('cyclic dependencies', () => {
    it('should define cyclic dependencies for yup.when fields', () => {
      const result = validateBookingsForm({ t: mockT });

      expect(result.formValidationSchema).toBeDefined();
    });

    it('should validate entire form with multiple fields', async () => {
      const result = validateBookingsForm({ t: mockT });

      const validData = {
        bookingReference: 'ABC1234567',
        bookerLastName: 'Smith',
        arrivalDate: '2026-03-15',
        guestLastName: 'Johnson',
        cancellationDate: null,
        companyName: 'Acme Corp',
        bookerPostcode: 'SW1A 1AA',
        bookerEmail: 'test@example.com',
        bookerPhone: '1234567890',
        hotelDetails: { id: '123' },
        hotelLocation: 'London',
        thirdPartyBookingReferenceNumber: '',
      };

      await expect(result.formValidationSchema.validate(validData)).resolves.toBeDefined();
    });

    it('should handle validation errors for multiple fields', async () => {
      const result = validateBookingsForm({ t: mockT });

      const invalidData = {
        bookingReference: '12',
        bookerLastName: 'A',
        guestLastName: 'B',
        companyName: 'C',
        bookerPostcode: 'D',
        bookerEmail: 'invalid',
        bookerPhone: '12',
        thirdPartyBookingReferenceNumber: 'E',
      };

      await expect(result.formValidationSchema.validate(invalidData)).rejects.toThrow();
    });
  });

  describe('branch coverage', () => {
    it('should handle enhancedSearch true for all branches', () => {
      const result = validateBookingsForm({ t: mockT, enhancedSearch: true });

      expect(result.formValidationObject).toBeDefined();
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.reference.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.reference.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.reference.max');
    });

    it('should handle enhancedSearch false for all branches', () => {
      const result = validateBookingsForm({ t: mockT, enhancedSearch: false });

      expect(result.formValidationObject).toBeDefined();
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingReference.invalid');
    });

    it('should handle enhancedSearch undefined (default)', () => {
      const result = validateBookingsForm({ t: mockT });

      expect(result.formValidationObject).toBeDefined();
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingReference.invalid');
    });
  });

  describe('translation keys', () => {
    it('should call t function with all expected translation keys', () => {
      validateBookingsForm({ t: mockT, enhancedSearch: false });

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingReference.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingSurname.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingSurname.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookingSurname.max');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.guestSurname.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.guestSurname.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.guestSurname.max');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.companyName.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.companyName.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.companyName.max');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPostcode.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPostcode.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPostcode.max');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerEmail.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerEmail.max');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPhone.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPhone.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.bookerPhone.max');
      expect(mockT).toHaveBeenCalledWith(
        'ccui.manageBooking.thirdPartyBookingReferenceNumber.invalid'
      );
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.thirdPartyBookingReferenceNumber.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.thirdPartyBookingReferenceNumber.max');
    });

    it('should call t function with enhanced search translation keys', () => {
      validateBookingsForm({ t: mockT, enhancedSearch: true });

      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.reference.invalid');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.reference.min');
      expect(mockT).toHaveBeenCalledWith('ccui.manageBooking.reference.max');
    });
  });
});
