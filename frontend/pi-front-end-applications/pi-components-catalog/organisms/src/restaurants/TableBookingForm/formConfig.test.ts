import '@testing-library/jest-dom';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES } from '@whitbread-eos/atoms/restaurants';
import { formatDataTestId } from '@whitbread-eos/utils';
import { dropdrownManipulatorFn } from '@whitbread-eos/utils/restaurants';

import { tabletBookingDetailsFormConfig } from './formConfig';
import validateForm from './formValidation';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatDataTestId: jest.fn((base, suffix) => `${base}-${suffix}`),
}));

jest.mock('@whitbread-eos/utils/restaurants', () => ({
  dropdrownManipulatorFn: jest.fn(),
}));

jest.mock('./formValidation', () => ({
  __esModule: true,
  default: jest.fn(),
}));

describe('tabletBookingDetailsFormConfig', () => {
  const mockGetFormState = jest.fn();
  const mockOnSubmit = jest.fn();
  const mockValidateForm = jest.mocked(validateForm);
  const mockDropdownManipulator = jest.mocked(dropdrownManipulatorFn);
  const mockFormatDataTestId = jest.mocked(formatDataTestId);

  const defaultFormContentData = {
    'reservationform.adult.dropdown.values': '1,2,3,4',
    'reservationform.children.dropdown.values': '0,1,2,3',
    'reservationform.highchair.dropdown.values': '0,1,2',
    'reservationform.adult.label': 'Adults',
    'reservationform.children.label': 'Children',
    'reservationform.highchair.label': 'Highchairs',
    'reservationform.wheelchair.label': 'Wheelchair Access',
    'reservationform.other.requirement.label': 'Special Requests',
    'reservationform.optional.label': '(Optional)',
    'reservationform.yourdetails.firstname.label': 'First Name',
    'reservationform.yourdetails.lastname.label': 'Last Name',
    'reservationform.yourdetails.email.label': 'Email Address',
    'reservationform.yourdetails.contact.label': 'Phone Number',
    'reservationform.policystatement.checkbox.label': 'I agree to the privacy policy',
    'reservationform.policystatement.checkbox.label.linkname': 'Privacy Policy',
    'reservationform.adult.placeholder': 'Select adults',
    'reservationform.children.placeholder': 'Select children',
    'reservationform.highchair.placeholder': 'Select highchairs',
    'reservationform.date.label': 'Date',
    'reservationform.session.label': 'Session',
    'reservationform.sessionstarttime.label': 'Choose your session start time',
    'reservationform.sessionTime.label': 'Time',
    'reservationform.menu.label': 'Menu',
    'reservationform.menu.placeholder': 'Select menu',
    'reservationform.additional.requirement.label': 'Additional Requirements',
    'reservationform.yourdetails.heading': 'Your Details',
    'reservationform.continue.button.label': 'Continue',
    'reservationform.enquiry.button.label': 'Send Enquiry',
    'reservationform.book.button.label': 'Book Table',
    'reservationform.enquiry.adult.label': 'Number of Adults',
    'reservationform.enquiry.children.label': 'Number of Children',
  };

  const defaultValidationSchemas = {
    formStepOneValidationSchema: { type: 'step1' },
    completeFormValidationSchema: { type: 'complete' },
    enquiryFormValidationSchema: { type: 'enquiry' },
  };

  const defaultArgs = {
    getFormState: mockGetFormState,
    defaultValues: {},
    onSubmit: mockOnSubmit,
    baseDataTestId: 'table-booking',
    formContentData: defaultFormContentData,
    isMenuOptionAvailable: true,
    currentLang: 'en',
    isAltStyle: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockValidateForm.mockReturnValue(defaultValidationSchemas as any);
    mockDropdownManipulator.mockImplementation((value) => (value?.split(',') as any) || []);
    mockFormatDataTestId.mockImplementation((base, suffix) => `${base}-${suffix}`);
  });

  describe('Basic configuration', () => {
    it('should return form configuration object', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config).toBeDefined();
      expect(config.id).toBe('TableBookingForm');
      expect(config.elements).toBeDefined();
      expect(config.elements.fields).toBeDefined();
      expect(config.elements.buttons).toBeDefined();
    });

    it('should set correct form id', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config.id).toBe('TableBookingForm');
    });

    it('should include getFormState from args', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config.getFormState).toBe(mockGetFormState);
    });

    it('should include defaultValues from args', () => {
      const customDefaults = { adults: '2', children: '1' };
      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        defaultValues: customDefaults,
      });

      expect(config.defaultValues).toEqual(customDefaults);
    });

    it('should include validation schemas', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config.validationSchema).toEqual([
        defaultValidationSchemas.formStepOneValidationSchema,
        defaultValidationSchemas.completeFormValidationSchema,
        defaultValidationSchemas.enquiryFormValidationSchema,
      ]);
    });

    it('should define errors order', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config.errorsOrder).toEqual([
        'adults',
        'adultsByEnquiry',
        'childrenByEnquiry',
        'selectedSession',
        'firstname',
        'lastname',
        'email',
        'telephoneNumber',
      ]);
    });
  });

  describe('Dropdown options generation', () => {
    it('should call dropdrownManipulatorFn for adults', () => {
      tabletBookingDetailsFormConfig(defaultArgs);

      expect(mockDropdownManipulator).toHaveBeenCalledWith('1,2,3,4');
    });

    it('should call dropdrownManipulatorFn for children', () => {
      tabletBookingDetailsFormConfig(defaultArgs);

      expect(mockDropdownManipulator).toHaveBeenCalledWith('0,1,2,3');
    });

    it('should call dropdrownManipulatorFn for highchairs', () => {
      tabletBookingDetailsFormConfig(defaultArgs);

      expect(mockDropdownManipulator).toHaveBeenCalledWith('0,1,2');
    });

    it('should use dropdown values in adult field options', () => {
      mockDropdownManipulator.mockReturnValueOnce(['1', '2', '3', '4'] as any);

      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const adultsField = config.elements.fields.find((f) => f.name === 'adults');

      expect(adultsField?.dropdownOptions).toEqual(['1', '2', '3', '4']);
    });

    it('should use dropdown values in children field options', () => {
      mockDropdownManipulator
        .mockReturnValueOnce([])
        .mockReturnValueOnce(['0', '1', '2', '3'] as any);

      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const childrenField = config.elements.fields.find((f) => f.name === 'children');

      expect(childrenField?.dropdownOptions).toEqual(['0', '1', '2', '3']);
    });
  });

  describe('Validation schema integration', () => {
    it('should call validateForm with formContentData', () => {
      tabletBookingDetailsFormConfig(defaultArgs);

      expect(mockValidateForm).toHaveBeenCalledWith(defaultFormContentData, true);
    });

    it('should call validateForm with isMenuOptionAvailable', () => {
      tabletBookingDetailsFormConfig({
        ...defaultArgs,
        isMenuOptionAvailable: false,
      });

      expect(mockValidateForm).toHaveBeenCalledWith(defaultFormContentData, false);
    });

    it('should handle undefined isMenuOptionAvailable', () => {
      tabletBookingDetailsFormConfig({
        ...defaultArgs,
        isMenuOptionAvailable: undefined,
      });

      expect(mockValidateForm).toHaveBeenCalledWith(defaultFormContentData, undefined);
    });

    it('should use all three validation schemas', () => {
      const customSchemas = {
        formStepOneValidationSchema: { step: 1 },
        completeFormValidationSchema: { complete: true },
        enquiryFormValidationSchema: { enquiry: true },
      };
      mockValidateForm.mockReturnValue(customSchemas as any);

      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config.validationSchema).toEqual([
        customSchemas.formStepOneValidationSchema,
        customSchemas.completeFormValidationSchema,
        customSchemas.enquiryFormValidationSchema,
      ]);
    });
  });

  describe('Form fields configuration', () => {
    it('should include adults dropdown field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const adultsField = config.elements.fields.find((f) => f.name === 'adults');

      expect(adultsField).toBeDefined();
      expect(adultsField?.type).toBe(FORM_FIELD_TYPES.DROPDOWN);
      expect(adultsField?.label).toBe('Adults');
    });

    it('should include children dropdown field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const childrenField = config.elements.fields.find((f) => f.name === 'children');

      expect(childrenField).toBeDefined();
      expect(childrenField?.type).toBe(FORM_FIELD_TYPES.DROPDOWN);
      expect(childrenField?.optional).toBe(true);
    });

    it('should include enquiry form fields', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const enquiryField = config.elements.fields.find((f) => f.name === 'enquryFormFields');

      expect(enquiryField).toBeDefined();
      expect(enquiryField?.type).toBe(FORM_FIELD_TYPES.ENQUIRY_FORM_FIELDS);
      expect(enquiryField?.relatedFields).toHaveLength(2);
    });

    it('should include date picker field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const dateField = config.elements.fields.find((f) => f.name === 'date');

      expect(dateField).toBeDefined();
      expect(dateField?.type).toBe(FORM_FIELD_TYPES.SINGLE_DATE_PICKER);
    });

    it('should include session tabs field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const timeField = config.elements.fields.find((f) => f.name === 'time');

      expect(timeField).toBeDefined();
      expect(timeField?.type).toBe(FORM_FIELD_TYPES.SESSION_TABS);
    });

    it('should include menu dropdown field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const menuField = config.elements.fields.find((f) => f.name === 'menuId');

      expect(menuField).toBeDefined();
      expect(menuField?.type).toBe(FORM_FIELD_TYPES.MENUDROPDOWN);
    });

    it('should include additional requirements switch', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const additionalField = config.elements.fields.find(
        (f) => f.name === 'additionalRequirement'
      );

      expect(additionalField).toBeDefined();
      expect(additionalField?.type).toBe(FORM_FIELD_TYPES.SWITCH);
    });

    it('should include user details field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');

      expect(userDetailsField).toBeDefined();
      expect(userDetailsField?.type).toBe(FORM_FIELD_TYPES.USER_DETAILS);
    });

    it('should have correct number of main fields', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config.elements.fields).toHaveLength(8);
    });
  });

  describe('Enquiry form fields', () => {
    it('should include adultsByEnquiry field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const enquiryField = config.elements.fields.find((f) => f.name === 'enquryFormFields');
      const adultsEnquiry = enquiryField?.relatedFields?.find((f) => f.name === 'adultsByEnquiry');

      expect(adultsEnquiry).toBeDefined();
      expect(adultsEnquiry?.type).toBe(FORM_FIELD_TYPES.INPUT_TEXT);
      expect(adultsEnquiry?.props?.charLimit).toBe(3);
    });

    it('should include childrenByEnquiry field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const enquiryField = config.elements.fields.find((f) => f.name === 'enquryFormFields');
      const childrenEnquiry = enquiryField?.relatedFields?.find(
        (f) => f.name === 'childrenByEnquiry'
      );

      expect(childrenEnquiry).toBeDefined();
      expect(childrenEnquiry?.optional).toBe(true);
      expect(childrenEnquiry?.props?.charLimit).toBe(3);
    });
  });

  describe('Special request fields', () => {
    it('should include highchair dropdown', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const additionalField = config.elements.fields.find(
        (f) => f.name === 'additionalRequirement'
      );
      const highchairField = additionalField?.relatedFields?.find((f) => f.name === 'highchair');

      expect(highchairField).toBeDefined();
      expect(highchairField?.type).toBe(FORM_FIELD_TYPES.DROPDOWN);
      expect(highchairField?.optional).toBe(true);
    });

    it('should include wheelchair checkbox', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const additionalField = config.elements.fields.find(
        (f) => f.name === 'additionalRequirement'
      );
      const wheelchairField = additionalField?.relatedFields?.find((f) => f.name === 'wheelchair');

      expect(wheelchairField).toBeDefined();
      expect(wheelchairField?.type).toBe(FORM_FIELD_TYPES.CHECKBOX);
    });

    it('should include special request textarea', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const additionalField = config.elements.fields.find(
        (f) => f.name === 'additionalRequirement'
      );
      const specialRequestField = additionalField?.relatedFields?.find(
        (f) => f.name === 'specialRequest'
      );

      expect(specialRequestField).toBeDefined();
      expect(specialRequestField?.type).toBe(FORM_FIELD_TYPES.TEXT_AREA);
      expect(specialRequestField?.charLimit).toBe(200);
    });

    it('should have three special request fields', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const additionalField = config.elements.fields.find(
        (f) => f.name === 'additionalRequirement'
      );

      expect(additionalField?.relatedFields).toHaveLength(3);
    });
  });

  describe('User details fields', () => {
    it('should include firstname field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const firstnameField = userDetailsField?.relatedFields?.find((f) => f.name === 'firstname');

      expect(firstnameField).toBeDefined();
      expect(firstnameField?.type).toBe(FORM_FIELD_TYPES.INPUT_TEXT);
      expect(firstnameField?.props?.isAutoFocused).toBe(true);
      expect(firstnameField?.props?.charLimit).toBe(100);
    });

    it('should include lastname field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const lastnameField = userDetailsField?.relatedFields?.find((f) => f.name === 'lastname');

      expect(lastnameField).toBeDefined();
      expect(lastnameField?.type).toBe(FORM_FIELD_TYPES.INPUT_TEXT);
      expect(lastnameField?.className).toBe('sessioncamhidetext');
    });

    it('should include email field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const emailField = userDetailsField?.relatedFields?.find((f) => f.name === 'emailAddress');

      expect(emailField).toBeDefined();
      expect(emailField?.type).toBe(FORM_FIELD_TYPES.INPUT_TEXT);
    });

    it('should include telephone field with PhoneSelector', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const telephoneField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'telephoneNumber'
      );

      expect(telephoneField).toBeDefined();
      expect(telephoneField?.type).toBe(FORM_FIELD_TYPES.DYNAMIC_FIELD);
    });

    it('should include privacy statement checkbox', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const privacyField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'privacyStatement'
      );

      expect(privacyField).toBeDefined();
      expect(privacyField?.type).toBe(FORM_FIELD_TYPES.CHECKBOX);
      expect(privacyField?.isPrivacStatement).toBe(true);
    });

    it('should have five user details fields', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');

      expect(userDetailsField?.relatedFields).toHaveLength(5);
    });

    it('should pass currentLang to PhoneSelector', () => {
      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        currentLang: 'es',
      });
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const telephoneField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'telephoneNumber'
      );

      expect(telephoneField?.props?.currentLang).toBe('es');
    });

    it('should pass isAltStyle to PhoneSelector', () => {
      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        isAltStyle: true,
      });
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const telephoneField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'telephoneNumber'
      );

      expect(telephoneField?.props?.isAltStyle).toBe(true);
    });
  });

  describe('Submit button configuration', () => {
    it('should include submit button', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      expect(config.elements.buttons).toHaveLength(1);
      expect(config?.elements?.buttons?.[0]?.type).toBe(FORM_BUTTON_TYPES.SUBMIT);
    });

    it('should set button labels from formContentData', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const submitButton = config?.elements?.buttons?.[0];

      expect(submitButton?.label).toEqual(['Continue', 'Send Enquiry', 'Book Table']);
    });

    it('should set onSubmit action', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const submitButton = config?.elements?.buttons?.[0];

      expect(submitButton?.action).toBe(mockOnSubmit);
    });

    it('should set button variant to primary', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const submitButton = config?.elements?.buttons?.[0];

      expect(submitButton?.props?.variant).toBe('primary');
    });

    it('should set button size to md', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const submitButton = config?.elements?.buttons?.[0];

      expect(submitButton?.props?.size).toBe('md');
    });
  });

  describe('Data test id generation', () => {
    it('should call formatDataTestId for adults field', () => {
      tabletBookingDetailsFormConfig(defaultArgs);

      expect(mockFormatDataTestId).toHaveBeenCalledWith('table-booking', 'AdultForBooking');
    });

    it('should call formatDataTestId for children field', () => {
      tabletBookingDetailsFormConfig(defaultArgs);

      expect(mockFormatDataTestId).toHaveBeenCalledWith('table-booking', 'Child');
    });

    it('should call formatDataTestId for submit button', () => {
      tabletBookingDetailsFormConfig(defaultArgs);

      expect(mockFormatDataTestId).toHaveBeenCalledWith('table-booking', 'Submit');
    });

    it('should use baseDataTestId from args', () => {
      tabletBookingDetailsFormConfig({
        ...defaultArgs,
        baseDataTestId: 'custom-test-id',
      });

      expect(mockFormatDataTestId).toHaveBeenCalledWith('custom-test-id', expect.any(String));
    });

    it('should set testid on fields', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const adultsField = config.elements.fields.find((f) => f.name === 'adults');

      expect(adultsField?.testid).toBe('table-booking-AdultForBooking');
    });
  });

  describe('Field properties and styles', () => {
    it('should set placeholder for adults dropdown', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const adultsField = config.elements.fields.find((f) => f.name === 'adults');

      expect(adultsField?.props?.placeholder).toBe('Select adults');
      expect(adultsField?.props?.showStatusIcon).toBe(false);
    });

    it('should set optional text for children field', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const childrenField = config.elements.fields.find((f) => f.name === 'children');

      expect(childrenField?.optionalText).toBe('(Optional)');
    });

    it('should set styles for date picker', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const dateField = config.elements.fields.find((f) => f.name === 'date');

      expect(dateField?.styles).toEqual({ w: '100%', mb: '2xl' });
    });

    it('should set isRightIcon for date picker', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const dateField = config.elements.fields.find((f) => f.name === 'date');

      expect(dateField?.props?.isRightIcon).toBe(true);
      expect(dateField?.props?.isDisabled).toBe(false);
    });

    it('should set sessioncamhidetext class on sensitive fields', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const firstnameField = userDetailsField?.relatedFields?.find((f) => f.name === 'firstname');

      expect(firstnameField?.className).toBe('sessioncamhidetext');
    });
  });

  describe('Content data handling', () => {
    it('should handle missing formContentData gracefully', () => {
      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        formContentData: undefined as any,
      });

      expect(config).toBeDefined();
      expect(config.elements.fields).toBeDefined();
    });

    it('should use formContentData values for labels', () => {
      const customContentData = {
        ...defaultFormContentData,
        'reservationform.adult.label': 'Custom Adults Label',
      };

      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        formContentData: customContentData,
      });

      const adultsField = config.elements.fields.find((f) => f.name === 'adults');
      expect(adultsField?.label).toBe('Custom Adults Label');
    });

    it('should handle partial formContentData', () => {
      const partialContentData = {
        'reservationform.adult.label': 'Adults',
      };

      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        formContentData: partialContentData,
      });

      expect(config.elements.fields).toBeDefined();
    });
  });

  describe('Type conformance', () => {
    it('should return FormProps type', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);

      // Type check - should have FormProps properties
      expect(config.id).toBeDefined();
      expect(config.elements).toBeDefined();
      expect(config.validationSchema).toBeDefined();
      expect(config.getFormState).toBeDefined();
      expect(config.defaultValues).toBeDefined();
      expect(config.errorsOrder).toBeDefined();
    });

    it('should accept TableBookingDetails in onSubmit', () => {
      const mockSubmit = jest.fn(() => {
        // Type check function
      });

      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        onSubmit: mockSubmit,
      });

      expect(config?.elements?.buttons?.[0]?.action).toBe(mockSubmit);
    });
  });

  describe('Optional parameters', () => {
    it('should handle undefined currentLang', () => {
      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        currentLang: undefined,
      });

      const userDetailsField = config?.elements?.fields?.find((f) => f.name === 'userDetails');
      const telephoneField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'telephoneNumber'
      );

      expect(telephoneField?.props?.currentLang).toBeUndefined();
    });

    it('should handle undefined isAltStyle', () => {
      const config = tabletBookingDetailsFormConfig({
        ...defaultArgs,
        isAltStyle: undefined,
      });

      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const telephoneField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'telephoneNumber'
      );

      expect(telephoneField?.props?.isAltStyle).toBeUndefined();
    });
  });

  describe('Session tabs configuration', () => {
    it('should set tabLabel for session tabs', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const timeField = config.elements.fields.find((f) => f.name === 'time');

      expect(timeField?.tabLabel).toBe('Choose your session start time');
    });

    it('should set tapLabel for session tabs', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const timeField = config.elements.fields.find((f) => f.name === 'time');

      expect(timeField?.tapLabel).toBe('Time');
    });
  });

  describe('Privacy statement field', () => {
    it('should set privacy statement link text', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const privacyField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'privacyStatement'
      );

      expect(privacyField?.privactStatementLinkText).toBe('Privacy Policy');
    });

    it('should mark privacy statement with isPrivacStatement flag', () => {
      const config = tabletBookingDetailsFormConfig(defaultArgs);
      const userDetailsField = config.elements.fields.find((f) => f.name === 'userDetails');
      const privacyField = userDetailsField?.relatedFields?.find(
        (f) => f.name === 'privacyStatement'
      );

      expect(privacyField?.isPrivacStatement).toBe(true);
    });
  });
});
