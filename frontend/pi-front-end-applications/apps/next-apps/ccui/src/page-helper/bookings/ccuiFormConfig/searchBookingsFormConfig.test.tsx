import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import {
  ArrivalDate,
  BookingsSubmitButton,
  Cancellation,
  HotelDropdown,
  LocationDropdown,
  PhoneSelector,
  ResetSearchCriteriaButton,
} from '@whitbread-eos/molecules';

import { searchBookingsFormConfig } from './searchBookingsFormConfig';
import validateBookingsForm from './validateBookingsForm';

jest.mock('next-i18next', () => ({
  useTranslation: jest.fn(() => ({
    t: (key: string) => key,
  })),
}));

jest.mock('./validateBookingsForm', () => ({
  __esModule: true,
  default: jest.fn(() => ({
    formValidationSchema: { test: 'schema' },
  })),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatDataTestId: jest.fn((base: string, suffix: string) => `${base}-${suffix}`),
}));

describe('searchBookingsFormConfig', () => {
  const mockGetFormState = jest.fn();
  const mockOnSubmit = jest.fn();
  const mockOnReset = jest.fn();
  const mockSetClearPhoneField = jest.fn();
  const mockT = jest.fn((key: string) => key);
  const mockSingleDatePickerLabels = {
    selectDate: 'Select Date',
    clearDate: 'Clear Date',
    todayLabel: 'Today',
    tomorrowLabel: 'Tomorrow',
  };

  const defaultArgs = {
    getFormState: mockGetFormState,
    defaultValues: { bookingReference: '' },
    onSubmit: mockOnSubmit,
    onReset: mockOnReset,
    baseDataTestId: 'test',
    t: mockT,
    singleDatePickerLabels: mockSingleDatePickerLabels,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('searchBookingsFormConfig function', () => {
    describe('form structure', () => {
      it('should return correct form id', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        expect(config.id).toBe('searchBookingsForm');
      });

      it('should pass defaultValues through', () => {
        const defaultValues = { bookingReference: 'TEST123', bookerLastName: 'Smith' };
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          defaultValues,
        });

        expect(config.defaultValues).toEqual(defaultValues);
      });

      it('should pass getFormState through', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        expect(config.getFormState).toBe(mockGetFormState);
      });

      it('should pass resetForm through', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          resetForm: 5,
        });

        expect(config.resetForm).toBe(5);
      });

      it('should set validation schema from validateBookingsForm', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        expect(validateBookingsForm).toHaveBeenCalledWith({
          t: mockT,
          enhancedSearch: false,
        });
        expect(config.validationSchema).toEqual({ test: 'schema' });
      });

      it('should include fieldsContainerStyles', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        expect(config.elements.fieldsContainerStyles).toEqual({
          display: 'grid',
          gridTemplateColumns: '1fr 1fr 1fr',
          gridTemplateRows: 'auto',
          justifyItems: 'stretch',
          columnGap: 'lg',
          rowGap: 'sm',
          height: 'auto',
          mb: 'md',
        });
      });

      it('should include buttonsContainerStyles', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        expect(config.elements.buttonsContainerStyles).toEqual({});
      });
    });

    describe('bookingReference field', () => {
      it('should have bookingReference label when enhancedSearch is false', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: false,
        });

        const bookingReferenceField = config.elements.fields.find(
          (field) => field.name === 'bookingReference'
        );

        expect(bookingReferenceField?.label).toBe('ccui.manageBooking.bookingReference');
      });

      it('should have reference label when enhancedSearch is true', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const bookingReferenceField = config.elements.fields.find(
          (field) => field.name === 'bookingReference'
        );

        expect(bookingReferenceField?.label).toBe('ccui.manageBooking.reference');
      });

      it('should have correct field properties', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        const bookingReferenceField = config.elements.fields.find(
          (field) => field.name === 'bookingReference'
        );

        expect(bookingReferenceField).toMatchObject({
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'bookingReference',
          name: 'bookingReference',
          testid: 'test-BookingReference',
          styles: {
            w: {
              lg: '24.5rem',
              xl: '26.25rem',
            },
            p: 0,
          },
          props: {
            useTooltip: true,
          },
        });
      });
    });

    describe('bookerLastName field', () => {
      it('should be enabled when disableBookerSurname is false', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          disableBookerSurname: false,
        });

        const bookerLastNameField = config.elements.fields.find(
          (field) => field.name === 'bookerLastName'
        );

        expect(bookerLastNameField?.props?.isDisabled).toBe(false);
      });

      it('should be disabled when disableBookerSurname is true', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          disableBookerSurname: true,
        });

        const bookerLastNameField = config.elements.fields.find(
          (field) => field.name === 'bookerLastName'
        );

        expect(bookerLastNameField?.props?.isDisabled).toBe(true);
      });

      it('should be enabled by default', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        const bookerLastNameField = config.elements.fields.find(
          (field) => field.name === 'bookerLastName'
        );

        expect(bookerLastNameField?.props?.isDisabled).toBe(false);
      });

      it('should have correct field properties', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        const bookerLastNameField = config.elements.fields.find(
          (field) => field.name === 'bookerLastName'
        );

        expect(bookerLastNameField).toMatchObject({
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'bookerLastName',
          name: 'bookerLastName',
          label: 'ccui.manageBooking.bookingSurname',
          testid: 'test-BookingSurname',
          styles: {
            w: {
              lg: '24.5rem',
              xl: '26.25rem',
            },
            p: 0,
          },
          props: {
            useTooltip: true,
            isDisabled: false,
          },
        });
      });
    });

    describe('arrivalDate field', () => {
      it('should have correct field properties', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        const arrivalDateField = config.elements.fields.find(
          (field) => field.name === 'arrivalDate'
        );

        expect(arrivalDateField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'arrivalDate',
          name: 'arrivalDate',
          label: 'ccui.manageBooking.arrivalDate',
          testid: 'test-ArrivalDate',
          Component: ArrivalDate,
          styles: {
            w: {
              lg: '24.5rem',
              xl: '26.25rem',
            },
            p: 0,
          },
          props: {
            singleDatePickerLabels: mockSingleDatePickerLabels,
            useTooltip: true,
          },
        });
      });
    });

    describe('extendedSearchCriteria field', () => {
      it('should be hidden when enhancedSearch is false', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: false,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        expect(extendedSearchField?.hidden).toBe(true);
      });

      it('should be visible when enhancedSearch is true', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        expect(extendedSearchField?.hidden).toBe(false);
      });

      it('should have correct field properties', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        expect(extendedSearchField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'extendedSearchCriteria',
          name: 'extendedSearchCriteria',
          label: '',
          testid: 'test-ExtendedSearchCriteria',
          styles: {
            gridColumn: '3/3',
            placeSelf: 'end',
            marginRight: 'lg',
          },
          hidden: false,
        });
      });

      it('should include relatedFields with all extended criteria', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const relatedFieldNames = extendedSearchField?.relatedFields?.extendedSearchCriteria.map(
          (field: any) => field.name
        );

        expect(relatedFieldNames).toEqual([
          'guestLastName',
          'bookerPostcode',
          'hotelDetails',
          'hotelLocation',
          'bookerEmail',
          'bookerPhone',
          'cancellationDate',
          'companyName',
          'thirdPartyBookingReferenceNumber',
        ]);
      });
    });

    describe('extended search criteria fields', () => {
      it('should configure guestLastName field correctly when enabled', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
          disableGuestSurname: false,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const guestLastNameField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'guestLastName'
        );

        expect(guestLastNameField).toMatchObject({
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'guestLastName',
          name: 'guestLastName',
          label: 'ccui.manageBooking.guestSurname',
          testid: 'test-GuestSurname',
          props: {
            useTooltip: true,
            isDisabled: false,
          },
        });
      });

      it('should disable guestLastName field when disableGuestSurname is true', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
          disableGuestSurname: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const guestLastNameField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'guestLastName'
        );

        expect(guestLastNameField?.props?.isDisabled).toBe(true);
      });

      it('should configure bookerPostcode field correctly', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const bookerPostcodeField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'bookerPostcode'
        );

        expect(bookerPostcodeField).toMatchObject({
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'bookerPostcode',
          name: 'bookerPostcode',
          label: 'ccui.manageBooking.bookerPostcode',
          testid: 'test-BookerPostcode',
          props: {
            useTooltip: true,
          },
        });
      });

      it('should configure hotelDetails field correctly', () => {
        const mockClearHotelFields = { clear: true };
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
          clearHotelFields: mockClearHotelFields as any,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const hotelDetailsField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'hotelDetails'
        );

        expect(hotelDetailsField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'hotelDetails',
          name: 'hotelDetails',
          label: 'ccui.manageBooking.hotelName',
          testid: 'test-HotelName',
          Component: HotelDropdown,
          props: {
            clearHotelFields: mockClearHotelFields,
          },
        });
      });

      it('should configure hotelLocation field correctly', () => {
        const mockClearHotelFields = { clear: true };
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
          clearHotelFields: mockClearHotelFields as any,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const hotelLocationField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'hotelLocation'
        );

        expect(hotelLocationField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'hotelLocation',
          name: 'hotelLocation',
          label: 'ccui.manageBooking.hotelLocation',
          testid: 'test-HotelLocation',
          Component: LocationDropdown,
          props: {
            clearHotelFields: mockClearHotelFields,
          },
        });
      });

      it('should configure bookerEmail field correctly', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const bookerEmailField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'bookerEmail'
        );

        expect(bookerEmailField).toMatchObject({
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'bookerEmail',
          name: 'bookerEmail',
          label: 'ccui.manageBooking.emailAddress',
          testid: 'test-EmailAddress',
          props: {
            useTooltip: true,
          },
        });
      });

      it('should configure bookerPhone field correctly', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
          clearPhoneField: true,
          language: 'de',
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const bookerPhoneField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'bookerPhone'
        );

        expect(bookerPhoneField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'bookerPhone',
          name: 'bookerPhone',
          label: 'ccui.manageBooking.telephoneNumber',
          testid: 'test-TelephoneNumber',
          Component: PhoneSelector,
          props: {
            useTooltip: true,
            clearField: true,
            showIcon: false,
            currentLang: 'de',
          },
        });
      });

      it('should configure cancellationDate field correctly', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const cancellationDateField =
          extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
            (field: any) => field.name === 'cancellationDate'
          );

        expect(cancellationDateField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'cancellationDate',
          name: 'cancellationDate',
          label: 'ccui.manageBooking.cancellationDate',
          testid: 'test-CancellationDate',
          Component: Cancellation,
          props: {
            singleDatePickerLabels: mockSingleDatePickerLabels,
            useTooltip: true,
          },
        });
      });

      it('should configure companyName field correctly', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const companyNameField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'companyName'
        );

        expect(companyNameField).toMatchObject({
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'companyName',
          name: 'companyName',
          label: 'ccui.manageBooking.companyName',
          testid: 'test-CompanyName',
          props: {
            useTooltip: true,
          },
        });
      });

      it('should configure thirdPartyBookingReferenceNumber field as disabled', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const extendedSearchField = config.elements.fields.find(
          (field) => field.name === 'extendedSearchCriteria'
        );

        const thirdPartyField = extendedSearchField?.relatedFields?.extendedSearchCriteria.find(
          (field: any) => field.name === 'thirdPartyBookingReferenceNumber'
        );

        expect(thirdPartyField).toMatchObject({
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'thirdPartyBookingReferenceNumber',
          name: 'thirdPartyBookingReferenceNumber',
          label: 'ccui.manageBooking.3rdPartyBookingReference',
          testid: 'test-3rdPartyBookingReference',
          props: {
            useTooltip: true,
            isDisabled: true,
          },
        });
      });
    });

    describe('bookingsSubmitButton field', () => {
      it('should have correct field properties', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        const submitButtonField = config.elements.fields.find(
          (field) => field.name === 'bookingsSubmitButton'
        );

        expect(submitButtonField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          label: 'ccui.manageBooking.searchBooking',
          name: 'bookingsSubmitButton',
          action: mockOnSubmit,
          testid: 'test-Submit',
          Component: BookingsSubmitButton,
          props: {
            type: 'submit',
            enhancedSearch: true,
          },
        });
      });

      it('should pass enhancedSearch false to button', () => {
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: false,
        });

        const submitButtonField = config.elements.fields.find(
          (field) => field.name === 'bookingsSubmitButton'
        );

        expect(submitButtonField?.props?.enhancedSearch).toBe(false);
      });
    });

    describe('resetSearchCriteriaButton field', () => {
      it('should have correct field properties', () => {
        const mockClearHotelFields = { clear: true };
        const config = searchBookingsFormConfig({
          ...defaultArgs,
          clearHotelFields: mockClearHotelFields as any,
          setClearPhoneField: mockSetClearPhoneField,
        });

        const resetButtonField = config.elements.bottomFields?.find(
          (field: any) => field.name === 'resetSearchCriteriaButton'
        );

        expect(resetButtonField).toMatchObject({
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          label: 'ccui.manageBooking.clearSearch',
          name: 'resetSearchCriteriaButton',
          testid: 'test-Reset',
          Component: ResetSearchCriteriaButton,
          props: {
            action: mockOnReset,
            clearHotelFields: mockClearHotelFields,
            setClearPhoneField: mockSetClearPhoneField,
          },
        });
      });
    });

    describe('validation schema', () => {
      it('should call validateBookingsForm with enhancedSearch false by default', () => {
        searchBookingsFormConfig(defaultArgs);

        expect(validateBookingsForm).toHaveBeenCalledWith({
          t: mockT,
          enhancedSearch: false,
        });
      });

      it('should call validateBookingsForm with enhancedSearch true', () => {
        searchBookingsFormConfig({
          ...defaultArgs,
          enhancedSearch: true,
        });

        expect(validateBookingsForm).toHaveBeenCalledWith({
          t: mockT,
          enhancedSearch: true,
        });
      });
    });

    describe('complete configuration', () => {
      it('should return complete FormProps configuration', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        expect(config).toHaveProperty('id');
        expect(config).toHaveProperty('elements');
        expect(config).toHaveProperty('defaultValues');
        expect(config).toHaveProperty('validationSchema');
        expect(config).toHaveProperty('getFormState');
      });

      it('should have all main fields', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        const fieldNames = config.elements.fields.map((field) => field.name);

        expect(fieldNames).toContain('bookingReference');
        expect(fieldNames).toContain('bookerLastName');
        expect(fieldNames).toContain('arrivalDate');
        expect(fieldNames).toContain('extendedSearchCriteria');
        expect(fieldNames).toContain('bookingsSubmitButton');
      });

      it('should have bottom fields', () => {
        const config = searchBookingsFormConfig(defaultArgs);

        expect(config.elements.bottomFields).toHaveLength(1);
        expect(config.elements.bottomFields?.[0].name).toBe('resetSearchCriteriaButton');
      });
    });
  });
});
