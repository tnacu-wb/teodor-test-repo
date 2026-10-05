import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BusinessQuestionType } from '@whitbread-eos/api';
import { BusinessQuestionDefaultHeader } from '@whitbread-eos/api/src';

import { BusinessEmployeeQuestions } from './BusinessEmployeeQuestions';

const mockProps = {
  icons: {},
  typeOfQuestion: BusinessQuestionType.PurchaseOrder,
  companyId: 'COMP_adasdad13131',
  questionContent: {
    questionId: BusinessQuestionType.PurchaseOrder,
    label: BusinessQuestionDefaultHeader.PurchaseOrder,
    mandatory: true,
    managementHeader: BusinessQuestionDefaultHeader.PurchaseOrder,
    active: false,
    location: 'R',
    managementInformationAnswer: { answerType: null, answers: null },
    type: null,
    positionId: 0,
  },
  onCollapse: jest.fn(),
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
    updateBusinessQuestions: () => mockUpdateResponse,
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

describe('BusinessEmployeeQuestions Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render BusinessEmployeeQuestions component', async () => {
    const { getByTestId } = render(<BusinessEmployeeQuestions {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(
        getByTestId(`BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-container`)
      ).toBeInTheDocument();
    });
  });

  it('should render BusinessEmployeeQuestions component and no discard changes', async () => {
    const { getByTestId, queryByTestId } = render(
      <BusinessEmployeeQuestions {...(mockProps as any)} />
    );

    const discardChangesButton = getByTestId(
      `BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-discard-changes-button`
    );

    await waitFor(async () => {
      expect(discardChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(discardChangesButton);
    });

    expect(queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('should render BusinessEmployeeQuestions component and click on discard changes after making changes', async () => {
    const user = userEvent.setup();
    const { getByTestId } = render(<BusinessEmployeeQuestions {...(mockProps as any)} />);

    const discardChangesButton = getByTestId(
      `BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-discard-changes-button`
    );
    const inputQuestion = getByTestId(
      `BusinessAccountQuestionsForm-typeOfQuestion-${mockProps.typeOfQuestion}-Form-Input`
    );

    await waitFor(async () => {
      expect(discardChangesButton).toBeInTheDocument();
      expect(inputQuestion).toBeInTheDocument();
    });

    await user.type(inputQuestion, 'Test');

    await waitFor(async () => {
      fireEvent.click(discardChangesButton);
    });

    expect(getByTestId('ReviewChanges')).toBeInTheDocument();
  });

  it('should render BusinessEmployeeQuestions component and click on save changes', async () => {
    const user = userEvent.setup();
    const { onCollapse } = mockProps;
    const { getByTestId } = render(<BusinessEmployeeQuestions {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(
      `BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-save-changes-button`
    );
    const inputQuestion = getByTestId(
      `BusinessAccountQuestionsForm-typeOfQuestion-${mockProps.typeOfQuestion}-Form-Input`
    );

    await waitFor(async () => {
      expect(inputQuestion).toBeInTheDocument();
      expect(saveChangesButton).toBeInTheDocument();
    });

    await user.type(inputQuestion, 'Test');

    await waitFor(async () => {
      expect(inputQuestion).toHaveValue(`${BusinessQuestionDefaultHeader.PurchaseOrder}Test`);
    });

    await waitFor(async () => {
      fireEvent.click(saveChangesButton);
    });

    await waitFor(() => {
      expect(onCollapse).toBeCalled();
    });
  });
  it('should call updateBusinessQuestion with questionId 1', async () => {
    mockProps.questionContent.questionId = '1' as any;
    const { onCollapse } = mockProps;

    const { getByTestId } = render(<BusinessEmployeeQuestions {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(
      `BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-save-changes-button`
    );

    await waitFor(async () => {
      expect(saveChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveChangesButton);
    });
    await waitFor(() => {
      expect(onCollapse).toBeCalled();
    });
  });
  it('should call updateBusinessQuestion with questionId 2 and managementHeader null', async () => {
    mockProps.questionContent.questionId = '2' as any;
    mockProps.questionContent.managementHeader = null as any;

    const { onCollapse } = mockProps;

    const { getByTestId } = render(<BusinessEmployeeQuestions {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(
      `BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-save-changes-button`
    );

    await waitFor(async () => {
      expect(saveChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveChangesButton);
    });
    await waitFor(() => {
      expect(onCollapse).toBeCalled();
    });
  });
  it('should call updateBusinessQuestion with questionId customer', async () => {
    mockProps.questionContent.questionId = BusinessQuestionType.CustomerReference;
    mockProps.typeOfQuestion = BusinessQuestionType.CustomerReference;
    const { onCollapse } = mockProps;

    const { getByTestId } = render(<BusinessEmployeeQuestions {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(
      `BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-save-changes-button`
    );

    await waitFor(async () => {
      expect(saveChangesButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(saveChangesButton);
    });
    await waitFor(() => {
      expect(onCollapse).toBeCalled();
    });
  });
  it('should call updateBusinessQuestion with no question ID and status fail', async () => {
    mockUpdateResponse = { status: 'fail' };
    mockProps.questionContent.questionId = null as any;

    const { getByTestId } = render(<BusinessEmployeeQuestions {...(mockProps as any)} />);

    const saveChangesButton = getByTestId(
      `BusinessEmployeeQuestions-${mockProps.typeOfQuestion}-save-changes-button`
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
