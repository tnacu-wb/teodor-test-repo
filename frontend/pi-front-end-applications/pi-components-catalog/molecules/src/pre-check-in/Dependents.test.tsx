import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useQueryRequest } from '@whitbread-eos/utils';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, userEvent, waitFor, screen } from '../utils/test-utils';
import Dependents from './Dependents';

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockGetCountriesData = {
  data: {
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
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error country selection',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQuery: () => jest.fn(),
  useQueryRequest: jest.fn(() => ({
    data: mockGetCountriesData,
    isSuccess: true,
  })),
  useFeatureSwitch: () => true,
}));

export const dependents = [
  { id: '0', label: 0 },
  { id: '1', label: 1 },
  { id: '2', label: 2 },
  { id: '3', label: 3 },
];

const ComponentWithDynamicField = () => {
  const { control } = useForm({
    defaultValues: {
      consent: true,
    },
  });

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'fieldName',
    label: 'Field Name',
    testid: 'PreCheckInPage',
    props: { dependents, dependent: '0' },
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

  const defaultValues = {
    bookingReference: 12345678,
    hotelName: 'Frankfurt Messe',
    firstName: 'John',
    lastName: 'Smith',
    arrivalDate: '2024-05-10T18:30:00.000Z',
    scheduledDate: '2024-05-10T18:30:00.000Z',
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
          value: 'AR',
          label: 'Argentine',
          image: '/content/dam/global/flags/Argentina.png',
        },
        passport: 'OURFGE3456RE',
        firstname: 'Smith',
        surname: 'Evans',
        dateofbirth: '2005-05-01T18:30:00.000Z',
      },
    ],
    scheduleddate: '2024-05-04T18:30:00.000Z',
  };

  const props = {
    control,
    formField,
    errors: formErrors,
    getValues() {
      return defaultValues;
    },
  };
  return <Dependents {...props} />;
};

describe('Dependents component', () => {
  it('should render the component', () => {
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
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const dependent = getByTestId('DropdownComp-PreCheckInPage-Dependents-0');
    userEvent.click(dependent);
    expect(getByTestId('PreCheckInPage-Dependents')).toBeTruthy();
  });

  it('should fetch and set nationalities on mount', async () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    expect(getByTestId('DropdownComp-PreCheckInPage-Dependents-entireList')).toBeTruthy();
  });

  it('should add a dependent when addDependent is called', async () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const dropdown = getByTestId('DropdownComp-PreCheckInPage-Dependents-menuButton');
    userEvent.click(dropdown);
  });

  it('should display drop down options when clicked on add dropdown', async () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const addButton = getByTestId('DropdownComp-PreCheckInPage-Dependents-menuButton');
    userEvent.click(addButton);
    expect(getByTestId('DropdownComp-PreCheckInPage-Dependents-0')).toBeTruthy();
  });

  it('should handle handleDependentChange', async () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);
    const addButton = getByTestId('DropdownComp-PreCheckInPage-Dependents-menuButton');
    userEvent.click(addButton);
    const dropDownOption = getByTestId('DropdownComp-PreCheckInPage-Dependents-1');
    userEvent.click(dropDownOption);
  });

  it('should open the confirmation modal when removing a dependent', async () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);

    const addButton = getByTestId('DropdownComp-PreCheckInPage-Dependents-menuButton');
    userEvent.click(addButton);
    const dropDownOption = getByTestId('DropdownComp-PreCheckInPage-Dependents-1');
    userEvent.click(dropDownOption);

    await waitFor(() => {
      const dependent = getByTestId('PreCheckInPage-dependents-0-guest').querySelector('button');
      expect(dependent).toBeTruthy();
    });

    const removeButton = getByTestId('PreCheckInPage-dependents-0-guest').querySelector(
      'button'
    ) as HTMLElement;
    userEvent.click(removeButton);
  });

  it('should handle deleting a dependent', async () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);

    const addButton = getByTestId('DropdownComp-PreCheckInPage-Dependents-menuButton');
    await userEvent.click(addButton);
    const dropDownOption = getByTestId('DropdownComp-PreCheckInPage-Dependents-1');
    await userEvent.click(dropDownOption);

    await waitFor(() => {
      const dependent = getByTestId('PreCheckInPage-dependents-0-guest').querySelector('button');
      expect(dependent).toBeTruthy();
    });

    const removeButton = getByTestId('PreCheckInPage-dependents-0-guest').querySelector(
      'button'
    ) as HTMLElement;
    await userEvent.click(removeButton);

    const confirmButton = getByTestId('PreCheckIn-ModalConfirmationButton');
    await userEvent.click(confirmButton);
  });

  it('should handle deleting a dependent', async () => {
    jest.clearAllMocks();
    const { getByTestId } = render(<ComponentWithDynamicField />);

    const addButton = getByTestId('DropdownComp-PreCheckInPage-Dependents-menuButton');
    await userEvent.click(addButton);
    const dropDownOption = getByTestId('DropdownComp-PreCheckInPage-Dependents-2');
    await userEvent.click(dropDownOption);

    await waitFor(() => {
      const dependent = getByTestId('PreCheckInPage-dependents-0-guest').querySelector('button');
      expect(dependent).toBeTruthy();
    });

    const removeOption = getByTestId('DropdownComp-PreCheckInPage-Dependents-1');
    await userEvent.click(removeOption);

    const confirmButton = getByTestId('PreCheckIn-ModalConfirmationButton');
    await userEvent.click(confirmButton);

    const confirmModalClodeButton = getByTestId('PreCheckIn-ModalDenyButton');
    await userEvent.click(confirmModalClodeButton);

    expect(getByTestId('DropdownComp-PreCheckInPage-Dependents-entireList')).toBeTruthy();
  });

  it('should trigger onToggleSection and call handleAccordionToggle with correct arguments', () => {
    render(<ComponentWithDynamicField />);
    const accordionToggleButton = screen.getByTestId('Button-precheckin.additionalguests.title');
    userEvent.click(accordionToggleButton);
  });
});
