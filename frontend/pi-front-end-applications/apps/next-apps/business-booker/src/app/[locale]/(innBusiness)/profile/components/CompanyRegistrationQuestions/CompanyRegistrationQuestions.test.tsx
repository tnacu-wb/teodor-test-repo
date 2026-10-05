import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Language } from '@whitbread-eos/api';

import { CompanyRegistrationQuestions } from './CompanyRegistrationQuestions';
import { mockCompanyRegistrationQuestions, mockEmployeeDetails } from './mockData';

const mockProps = {
  companyRegistrationQuestions: mockCompanyRegistrationQuestions,
  baseDataTestId: 'testId',
  icons: { icon: 'test' },
  employeeDetails: mockEmployeeDetails,
  language: 'en' as Language,
  companyId: '123',
  onReviewChangesToggle: jest.fn(),
  onIsEditableToggle: jest.fn(),
  onIsUpdatingToggle: jest.fn(),
  onIsDirtyToggle: jest.fn(),
  isEditable: false,
  isUpdating: false,
  isDirty: false,
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('CompanyRegistrationQuestions Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyRegistrationQuestions component', async () => {
    const { getByTestId } = render(<CompanyRegistrationQuestions {...mockProps} />);

    expect(getByTestId('testId-Company-Registration-Questions')).toBeInTheDocument();
  });
});
