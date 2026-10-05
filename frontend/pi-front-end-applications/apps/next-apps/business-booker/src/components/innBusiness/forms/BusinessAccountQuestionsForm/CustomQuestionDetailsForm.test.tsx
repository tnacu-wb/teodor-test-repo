import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { CustomQuestionDetailsForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/CustomQuestionDetailsForm';

const mockProps = {
  icons: {},
  typeOfQuestion: 'COQU_44ff2d0e-828e-4843-978d-2d40bb50cf05',
  onSubmit: jest.fn(),
  formRef: { current: null },
  questionContent: {
    questionId: '1',
    label: 'BussQuestion1',
    mandatory: true,
    managementHeader: 'Customer Ref',
    active: false,
    location: 'R',
    managementInformationAnswer: { answerType: null, answers: null },
    type: null,
    positionId: 0,
  },
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
    findError: serverUtils.findError,
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('CustomQuestionDetailsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CustomQuestionDetailsForm component', async () => {
    const { getByTestId } = render(<CustomQuestionDetailsForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(
        getByTestId(`CustomQuestionDetailsForm-${mockProps.typeOfQuestion}-container`)
      ).toBeInTheDocument();
    });
  });

  it('should trim title and label before submit for new question', async () => {
    const user = userEvent.setup();
    const onSubmit = jest.fn();
    const { getByPlaceholderText, container } = render(
      <CustomQuestionDetailsForm
        {...(mockProps as any)}
        onSubmit={onSubmit}
        isNewQuestionAdded={true}
        questionContent={{}}
      />
    );

    const titleInput = getByPlaceholderText('coMngt.questions.details.input.questionTitle.label');
    const labelInput = getByPlaceholderText('coMngt.questions.details.input.questionLabel.label');

    await user.type(titleInput, '  My Title  ');
    await user.type(labelInput, '  My Label  ');

    const form = container.querySelector('form');
    fireEvent.submit(form as HTMLFormElement);

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          customQuestionTitle: 'My Title',
          customQuestionLabel: 'My Label',
        })
      );
    });
  });
});
