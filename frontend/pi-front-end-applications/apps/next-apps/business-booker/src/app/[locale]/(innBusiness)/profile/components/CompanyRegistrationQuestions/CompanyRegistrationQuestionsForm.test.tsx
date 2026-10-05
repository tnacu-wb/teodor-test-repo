import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { Language } from '@whitbread-eos/api';
import { act } from 'react-dom/test-utils';

import { CompanyRegistrationQuestionsForm } from './CompanyRegistrationQuestionsForm';
import { mockCompanyRegistrationQuestions, mockEmployeeDetails } from './mockData';

const mockProps = {
  companyRegistrationQuestions: mockCompanyRegistrationQuestions,
  baseDataTestId: 'testId',
  handleEditMode: jest.fn(),
  icons: { icon: 'test' },
  employeeDetails: mockEmployeeDetails,
  language: 'en' as Language,
  companyId: '123',
  isUpdating: false,
  isDirty: false,
  onIsUpdatingToggle: jest.fn(),
  onReviewChangesToggle: jest.fn(),
  onIsDirtyToggle: jest.fn(),
};

let mockUpdateResponse = {
  status: 'success',
};

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    refresh: jest.fn(),
  })),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    updateEmployeeDetails: () => mockUpdateResponse,
    findError: serverUtils.findError,
    defaultQuestionsAndSchema: () => {
      return {
        questionsSchemaObj: {},
        defaultQuestionsObj: {},
      };
    },
    parseAnswersObj: () => {
      return {
        userDefinedAnswers: [
          {
            miID: 'COQU_7bc823b1-40cc-4ec6-8102-f9fc818a72a2',
            miAnswer: '1',
          },
          {
            miID: 'COQU_8933007d-a8d3-4426-bb26-d4dba18a47a9',
            miAnswer: 'TEST',
          },
        ],
        customerReferenceAnswer: '123',
      };
    },
  };
});

window.scrollTo = jest.fn();

describe('CompanyRegistrationQuestionsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyRegistrationQuestionsForm component', async () => {
    const { getByTestId } = render(<CompanyRegistrationQuestionsForm {...mockProps} />);

    expect(getByTestId('testId-Company-Registration-Questions-Form')).toBeInTheDocument();
  });

  it('should click Save button and call handleEditMode', async () => {
    const { handleEditMode } = mockProps;
    const { getByTestId } = render(<CompanyRegistrationQuestionsForm {...mockProps} />);

    expect(getByTestId('testId-Company-Registration-Questions-Form')).toBeInTheDocument();

    const saveButton = getByTestId('testId-Company-Registration-Questions-Save-Button');

    expect(saveButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(saveButton);
    });

    await waitFor(() => {
      expect(handleEditMode).toBeCalled();
    });
  });

  it('should click Save button and call updateEmployeeDetails with no employee ID and status fail', async () => {
    mockUpdateResponse = { status: 'fail' };
    mockProps.employeeDetails.id = null;
    const { handleEditMode } = mockProps;

    const { getByTestId } = render(<CompanyRegistrationQuestionsForm {...mockProps} />);

    expect(getByTestId('testId-Company-Registration-Questions-Form')).toBeInTheDocument();

    const saveButton = getByTestId('testId-Company-Registration-Questions-Save-Button');

    expect(saveButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(saveButton);
    });

    await waitFor(() => {
      expect(handleEditMode).toBeCalledTimes(0);
    });
  });
});
