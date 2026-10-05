import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BBAnswerType, QuestionLocationType } from '@whitbread-eos/api';

import { CustomEmployeeQuestions } from './CustomEmployeeQuestions';

const mockProps = {
  icons: {},
  companyId: 'COMP_adasdad13131',
  questionContent: {
    questionId: 'COQU_44ff2d0e-828e-4843-978d-2d40bb50cf05',
    label: 'BussQuestion1',
    mandatory: true,
    managementHeader: 'Customer Ref',
    active: false,
    location: QuestionLocationType.R,
    managementInformationAnswer: { answerType: BBAnswerType.U, answers: ['Cognizant'] },
    type: null,
    positionId: 0,
  },
  onCollapse: jest.fn(),
  isNewQuestionAdded: false,
};

let mockUpdateResponse = {
  status: 'success',
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
    updateCompanyUserQuestion: () => mockUpdateResponse,
    findError: serverUtils.findError,
  };
});

window.scrollTo = jest.fn();

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    refresh: jest.fn(),
  })),
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('CustomEmployeeQuestions Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CustomEmployeeQuestions component', async () => {
    const { getByTestId } = render(<CustomEmployeeQuestions {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(
        getByTestId(`CustomEmployeeQuestions-${mockProps.questionContent.questionId}-container`)
      ).toBeInTheDocument();
    });
  });

  it('should render CustomEmployeeQuestions component and no discard changes', async () => {
    const { getByTestId, queryByTestId } = render(
      <CustomEmployeeQuestions {...(mockProps as any)} />
    );

    const discardChangesButton = getByTestId(
      `CustomEmployeeQuestions-${mockProps.questionContent.questionId}-discard-changes-button`
    );

    await waitFor(async () => {
      expect(discardChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(discardChangesButton);
    });
    await waitFor(() => {
      expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
    });
  });

  it('should render CustomEmployeeQuestions component and click on discard changes after making changes', async () => {
    const user = userEvent.setup();
    const { getByTestId } = render(<CustomEmployeeQuestions {...(mockProps as any)} />);

    const discardChangesButton = getByTestId(
      `CustomEmployeeQuestions-${mockProps.questionContent.questionId}-discard-changes-button`
    );

    const inputQuestion = getByTestId(
      `CustomQuestionDetailsForm-typeOfQuestion-${mockProps.questionContent.questionId}-Form-Input`
    );

    await waitFor(async () => {
      expect(inputQuestion).toBeInTheDocument();
      expect(discardChangesButton).toBeInTheDocument();
    });

    await user.type(inputQuestion, 'Test');

    await waitFor(async () => {
      expect(inputQuestion).toHaveValue(`BussQuestion1Test`);
    });

    await waitFor(async () => {
      fireEvent.click(discardChangesButton);
    });

    await waitFor(() => {
      expect(getByTestId('ReviewChanges')).toBeInTheDocument();
    });
  });

  it('should render CustomEmployeeQuestions component and click on save changes', async () => {
    const user = userEvent.setup();
    const { onCollapse } = mockProps;
    const { getByTestId } = render(<CustomEmployeeQuestions {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(
      `CustomEmployeeQuestions-${mockProps.questionContent.questionId}-save-changes-button`
    );
    const inputQuestion = getByTestId(
      `CustomQuestionDetailsForm-typeOfQuestion-${mockProps.questionContent.questionId}-Form-Input`
    );

    await waitFor(async () => {
      expect(inputQuestion).toBeInTheDocument();
      expect(saveChangesButton).toBeInTheDocument();
    });

    await user.type(inputQuestion, 'Test');

    await waitFor(async () => {
      expect(inputQuestion).toHaveValue(`BussQuestion1Test`);
    });

    await waitFor(async () => {
      fireEvent.click(saveChangesButton);
    });

    await waitFor(() => {
      expect(onCollapse).toBeCalled();
    });
  });
  it('should call updateCompanyUserQuestion with status fail', async () => {
    mockUpdateResponse = { status: 'fail' };

    const { getByTestId } = render(<CustomEmployeeQuestions {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(
      `CustomEmployeeQuestions-${mockProps.questionContent.questionId}-save-changes-button`
    );

    await waitFor(async () => {
      expect(saveChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveChangesButton);
    });

    await waitFor(() => {
      expect(mockProps.onCollapse).not.toBeCalled();
      expect(window.scrollTo).toBeCalledWith({ top: 0, behavior: 'smooth' });
    });
  });
});
