import '@testing-library/jest-dom';
import { render, fireEvent, act } from '@testing-library/react';

import { CompanyMainContact } from './CompanyMainContact';

const mockProps = {
  icons: {},
  companyId: '123',
  mainContactInformation: {
    id: '456',
    position: 'Manager',
    emailAddress: 'test@example.com',
    phoneNumber: '1234567890',
    mobileNumber: '0987654321',
    firstName: 'test',
    lastName: 'testerson',
  },
  handleEmployeeChange: jest.fn(),
  selectedEmployee: null,
};
const mockEmployeeDetails = {
  id: '123',
  ghNumber: null,
  emailAddress: 'john@mailnator.com',
  position: null,
  phoneNumber: '+11111111111',
  mobileNumber: '',
  textConfirmation: false,
  title: 'Mr',
  firstName: 'John',
  lastName: 'Doe',
  centralCardId: '1',
};
const mockEmployees = {
  employees: [
    {
      id: 'EMPL_14996d0d-8e9b-4145-9b88-22e5be4d9060',
      title: 'Mr',
      firstName: 'test stefan',
      lastName: 'stefan',
      emailAddress: 'stefan@yupmail.com',
      accessLevel: 'BOOKER',
      employeeStatus: 'INACTIVE',
    },
    {
      id: 'EMPL_f9acb3fe-0840-4cdb-b1b9-7cbeeee42cc1',
      title: 'Mr',
      firstName: 'test stefan',
      lastName: 'stefan',
      emailAddress: 'stefan@yupmail.com',
      accessLevel: 'BOOKER',
      employeeStatus: 'INACTIVE',
    },
    {
      id: 'EMPL_913aa521-d9f4-4dee-8648-ec6dfef4068d',
      title: 'Mr',
      firstName: 'Stefan',
      lastName: 'Ciora',
      emailAddress: 'stefan.ciora@yopmail.com',
      accessLevel: 'BOOKER',
      employeeStatus: 'ACTIVE',
    },
  ],
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getEmployeeDetails: () => {
      return mockEmployeeDetails;
    },
    getEmployees: () => {
      return mockEmployees;
    },
    findError: serverUtils.findError,
  };
});

jest.mock('react-hook-form', () => ({
  useForm: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
    handleSubmit: jest.fn(),
    watch: jest.fn(),
  }),
  useFormContext: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
    handleSubmit: jest.fn(),
    watch: jest.fn(),
  }),
  Controller: jest.fn(({ render }) => render({ field: {} })),
  FormProvider: jest.fn(({ children }) => <div>{children}</div>),
}));

describe('CompanyMainContact Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyMainContact component and click on input', async () => {
    const { getByTestId } = render(<CompanyMainContact {...mockProps} />);

    expect(getByTestId('CompanyMainContact-company-details-container')).toBeInTheDocument();

    const peoplePickerInput = getByTestId('PeoplePicker-Form-Input');
    const jobTitleFormInput = getByTestId('CompanyMainContact-job-title-Form-Input');

    await act(async () => {
      peoplePickerInput.focus();
      fireEvent.change(peoplePickerInput, { target: { value: 'Stefan' } });
    });

    await act(async () => {
      jobTitleFormInput.focus();
      fireEvent.change(jobTitleFormInput, { target: { value: 'Owner' } });
      jobTitleFormInput.blur();
    });
  });
});
