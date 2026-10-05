import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { CompanyAdditionalDetails } from './CompanyAdditionalDetails';

const mockProps = {
  isEditMode: false,
  additionalDetailsInformation: {
    companySector: 'Arts',
    averageMonthlyBooking: '51-200 rooms',
    numberOfEmployee: '',
  },
  icons: {
    'icon.chevron.down': '/icons/chevron-down.svg',
  },
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...utils,
    cn: jest.fn(),
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => ({
      t: (key: string) => {
        const translations: Record<string, string> = {
          'coMngt.additionalDetails.coSector.title': 'Company sector',
          'coMngt.additionalDetails.avgBookings.title': 'Average monthly bookings',
          'coMngt.additionalDetails.numEmployees.title': 'Number of employees',
          'coMngt.additionalDetails.coSector.options.arts': 'Arts, entertainment and recreation',
          'coMngt.additionalDetails.avgBookings.options.51rooms': '51-200 rooms',
          'coMngt.additionalDetails.select.placeholder': 'Select option',
          'coMngt.notSelected': 'Not selected',
        };
        return translations[key] || key;
      },
    }),
  };
});

jest.mock('react-hook-form', () => ({
  useFormContext: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    setValue: jest.fn(),
    getValues: (field?: string) => {
      switch (field) {
        case 'companySector':
          return 'Arts';
        case 'averageMonthlyBooking':
          return '';
        case 'numberOfEmployee':
          return '';
        default:
          return undefined;
      }
    },
    handleSubmit: jest.fn(),
    watch: jest.fn(),
  }),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('CompanyAdditionalDetails Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyAdditionalDetails component in view mode', () => {
    const { getByTestId, getByText } = render(<CompanyAdditionalDetails {...mockProps} />);

    expect(getByTestId('CompanyAdditionalDetails-container')).toBeInTheDocument();
    expect(getByText('Company sector')).toBeInTheDocument();
    expect(getByText('Arts, entertainment and recreation')).toBeInTheDocument();
    expect(getByText('Average monthly bookings')).toBeInTheDocument();
    expect(getByText('51-200 rooms')).toBeInTheDocument();
    expect(getByText('Number of employees')).toBeInTheDocument();
    expect(getByText('Not selected')).toBeInTheDocument();
  });

  it('should render CompanyAdditionalDetails component in edit mode', () => {
    const updatedProps = {
      ...mockProps,
      isEditMode: true,
      additionalDetailsInformation: {
        companySector: 'Arts',
        averageMonthlyBooking: '',
        numberOfEmployee: '',
      },
    };
    const { getByTestId, getByText, getAllByText } = render(
      <CompanyAdditionalDetails {...updatedProps} />
    );

    expect(getByTestId('CompanyAdditionalDetails-container')).toBeInTheDocument();
    expect(getByText('Company sector')).toBeInTheDocument();
    expect(getByText('Arts, entertainment and recreation')).toBeInTheDocument();
    expect(getByText('Average monthly bookings')).toBeInTheDocument();
    expect(getByText('Number of employees')).toBeInTheDocument();
    expect(getAllByText('Select option')).toHaveLength(2);
  });
});
