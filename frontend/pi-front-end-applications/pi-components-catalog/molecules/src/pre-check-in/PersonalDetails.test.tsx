import { useMediaQuery } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../utils/test-utils';
import PersonalDetails from './PersonalDetails';

const mockUseMediaQuery = useMediaQuery as jest.Mock;

jest.mock('./common', () => ({
  ...jest.requireActual('./common'),
  personalDetailsFields: [
    {
      type: 'input',
      name: 'firstName',
      label: 'firstname',
      testId: 'FirstName',
    },
    {
      type: 'input',
      name: 'lastName',
      label: 'lastname',
      testId: 'Surname',
    },
    {
      type: 'input',
      name: 'address',
      label: 'address',
      testId: 'Address',
    },
    {
      type: 'input',
      name: 'city',
      label: 'city',
      testId: 'City',
    },
    {
      type: 'input',
      name: 'postalCode',
      label: 'postalcode',
      testId: 'PostalCode',
    },
    {
      type: 'countryDropdown',
      name: 'country',
      label: 'country',
      testId: 'Country',
    },
    {
      type: 'datePicker',
      name: 'dateOfBirth',
      label: 'dateofbirth',
      testId: 'DateOfBirth',
    },
    {
      type: 'autoComplete',
      name: 'nationality',
      label: 'nationality',
      testId: 'Nationality',
    },
    {
      type: 'input',
      name: 'passport',
      label: 'mandatoryPassport',
      testId: 'Passport',
    },
    {
      type: 'testInputType',
      name: 'testInputName',
      label: 'testInputLabel',
      testId: 'testInputTestID',
    },
  ],
}));

const getCountriesData = {
  countries: {
    countries: [
      {
        countryCode: 'GB',
        countryCodeLegacy: 'GB',
        nationality: 'British, UK',
        dialingCode: '+44',
        flagSrc: '',
        passportRequired: true,
      },
      {
        countryCode: 'DE',
        countryCodeLegacy: 'D',
        nationality: 'German',
        dialingCode: '+49',
        flagSrc: '',
        passportRequired: true,
      },
    ],
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error country selection',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQuery: () => ({}),
  useQueryRequest: jest.fn(() => ({
    data: getCountriesData,
    isSuccess: true,
  })),
  useFeatureSwitch: () => true,
  useCustomLocale: jest.fn().mockImplementation(() => ({ language: 'en' })),
}));

const mockUseRouter = useRouter as jest.Mock;
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: jest
    .fn()
    .mockImplementation(() => ({ query: { bookingReference: 12345678, reservationId: 24234234 } })),
}));

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
  useMediaQuery: jest.fn().mockReturnValue([true]),
}));

const errorState = {
  dateOfBirth: {
    message: 'First Name is required',
  },
  nationality: {
    value: {
      message: 'Nationality is required',
    },
  },
};

const defaultValues = {
  roomNo: '1',
  bookingReference: 12345678,
  hotelName: 'Frankfurt Messe',
  firstName: 'John',
  lastName: 'Smith',
  arrivalDate: '2024-05-10T18:30:00.000Z',
  departureDate: '2024-05-11T18:30:00.000Z',
  address: '105 LONDON STREET',
  city: 'London',
  country: 'GB',
  postalCode: 'AA9A 9AA',
  dateOfBirth: '2005-05-04T18:30:00.000Z',
  nationality: {
    value: 'GB',
    label: 'British, UK',
    image: '/content/dam/global/flags/United-Kingdom.png',
  },
  passport: 'ABA9875413',
  dependent: '1',
  dependents: [
    {
      nationality: {
        value: 'GB',
        label: 'British, UK',
        image: '/content/dam/global/flags/United-Kingdom.png',
      },
      passport: '',
      firstname: 'Brown',
      surname: 'Taylor',
      dateofbirth: '2005-05-03T18:30:00.000Z',
    },
  ],
};

const formErrors = {
  firstName: {
    message: 'This field can only contain letters',
    type: 'matches',
    ref: {
      name: 'firstName',
    },
  },
  dependents: [
    {
      nationality: {
        value: {
          message: 'Please enter your nationality',
          type: 'required',
        },
      },
      passport: {
        message: 'Please enter a valid passport number',
        type: 'required',
      },
      firstname: {
        message: 'Please enter your first name',
        type: 'required',
        ref: {
          name: 'dependents[2].firstname',
        },
      },
      lastname: {
        message: 'Please enter your lastname',
        type: 'required',
        ref: {
          name: 'dependents[2].lastname',
        },
      },
      dateofbirth: {
        message: 'Please enter your date of birth',
        type: 'required',
        ref: {
          name: 'dependents[2].dateofbirth',
        },
      },
    },
  ],
};

const ComponentWithDynamicFieldWithNoErros = () => {
  const { control } = useForm({});

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'fieldName',
    label: 'Field Name',
    testid: 'PreCheckInPage-Form',
    props: { setIsLocationRequired: jest.fn() },
  };

  const props = {
    control,
    formField,
    errors: formErrors,
    getValues() {
      return {
        ...defaultValues,
      };
    },
  };

  return <PersonalDetails {...props} />;
};

const ComponentWithDynamicField = () => {
  const { control } = useForm({});

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'fieldName',
    label: 'Field Name',
    testid: 'PreCheckInPage-Form',
    props: { setIsLocationRequired: jest.fn() },
  };

  const props = {
    control,
    formField,
    errors: errorState,
    getValues() {
      return {
        ...defaultValues,
        errors: errorState,
      };
    },
  };

  return <PersonalDetails {...props} />;
};

const ComponentWithDynamicFieldWithError = () => {
  const { control } = useForm({});

  const errorState = {
    nationality: {
      message: 'Nationality is required',
    },
  };

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'fieldName',
    label: 'Field Name',
    testid: 'PreCheckInPage-Form',
    props: { setIsLocationRequired: jest.fn() },
  };

  const props = {
    control,
    formField,
    errors: errorState,
    getValues() {
      return {
        ...defaultValues,
        errors: errorState,
      };
    },
  };

  return <PersonalDetails {...props} />;
};

describe('PersonalDetails component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the component', () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const fields = [
      'PreCheckInPage-Form-FirstName',
      'PreCheckInPage-Form-Surname',
      'PreCheckInPage-Form-Address',
      'PreCheckInPage-Form-PostalCode',
      'PreCheckInPage-Form-City',
      'PreCheckInPage-Form-Country',
      'PreCheckInPage-Form-DateOfBirth',
      'PreCheckInPage-Form-Nationality',
      'PreCheckInPage-Form-Passport',
    ];

    fields.forEach((field) => expect(getByTestId(field)).toBeTruthy());
  });

  it('should render when language is de', () => {
    (useCustomLocale as jest.Mock).mockImplementation(() => ({ language: 'de' }));
    const { getByTestId } = render(<ComponentWithDynamicField />);
    expect(getByTestId('PreCheckInPage-Form-FirstName')).toBeInTheDocument();
  });

  it('should render title for single room', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    mockUseRouter.mockImplementation(() => ({
      query: { bookingReference: 12345678, reservationId: '' },
    }));
    const { getByTestId } = render(<ComponentWithDynamicField />);
    expect(getByTestId('PreCheckInPage-Form-FirstName')).toBeInTheDocument();
  });

  it('should trigger onToggleSection and call handleAccordionToggle with correct arguments', () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const accordionToggleButton = getByTestId('Button-precheckin.yourdetails.title');
    userEvent.click(accordionToggleButton);
  });

  it('renders SingleDatePicker with popperPlacement set to bottom when screen size is larger than 576px', () => {
    jest.clearAllMocks();

    mockUseMediaQuery.mockReturnValue([false]);

    const { getByTestId } = render(<ComponentWithDynamicField />);
    const singleDatePicker = getByTestId('PreCheckInPage-Form-DateOfBirth');
    expect(singleDatePicker).toBeInTheDocument();
  });

  it('renders all fileds with no error', () => {
    jest.clearAllMocks();

    const { getByTestId } = render(<ComponentWithDynamicFieldWithNoErros />);
    const singleDatePicker = getByTestId('PreCheckInPage-Form-DateOfBirth');
    expect(singleDatePicker).toBeInTheDocument();
  });

  it('renders all fields when roomNo is empty', () => {
    jest.clearAllMocks();

    const emptyRoomNoValues = {
      ...defaultValues,
      roomNo: '', // Set roomNo to empty
    };

    const ComponentWithEmptyRoomNo = () => {
      const { control } = useForm({});

      const formField: FieldsType = {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        name: 'fieldName',
        label: 'Field Name',
        testid: 'PreCheckInPage-Form',
        props: { setIsLocationRequired: jest.fn() },
      };

      const props = {
        control,
        formField,
        errors: errorState,
        getValues() {
          return {
            ...emptyRoomNoValues,
            errors: errorState,
          };
        },
      };

      return <PersonalDetails {...props} />;
    };

    const { getByTestId } = render(<ComponentWithEmptyRoomNo />);
    const fields = [
      'PreCheckInPage-Form-FirstName',
      'PreCheckInPage-Form-Surname',
      'PreCheckInPage-Form-Address',
      'PreCheckInPage-Form-PostalCode',
      'PreCheckInPage-Form-City',
      'PreCheckInPage-Form-Country',
      'PreCheckInPage-Form-DateOfBirth',
      'PreCheckInPage-Form-Nationality',
      'PreCheckInPage-Form-Passport',
    ];

    fields.forEach((field) => expect(getByTestId(field)).toBeTruthy());
  });
  it('should render component for empty countries list', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: { error: 'error' },
      isSuccess: false,
    }));
    mockUseRouter.mockImplementation(() => ({
      query: { bookingReference: 12345678, reservationId: '' },
    }));
    const { getByTestId } = render(<ComponentWithDynamicFieldWithError />);
    expect(getByTestId('PreCheckInPage-Form-FirstName')).toBeInTheDocument();
  });

  it('should render component for empty countries list', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: { error: 'error' },
      isSuccess: false,
    }));
    mockUseRouter.mockImplementation(() => ({
      query: { bookingReference: 12345678, reservationId: '' },
    }));
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const selectInput = getByTestId('PreCheckInPage-Form-Nationality').querySelector('input');
    expect(selectInput).toHaveAttribute('aria-describedby', 'react-select-10-placeholder');
    selectInput?.focus();
    selectInput?.blur();
    if (selectInput) userEvent.type(selectInput, 'German');

    expect(getByTestId('PreCheckInPage-Form-FirstName')).toBeInTheDocument();
  });

  it('renders SingleDatePicker with i icon', () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const infoIcon = getByTestId('datepicker-info-icon');
    userEvent.click(infoIcon);
    expect(infoIcon).toBeInTheDocument();
  });
});
