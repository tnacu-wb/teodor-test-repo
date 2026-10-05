import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BusinessQuestionType, BBAnswerType } from '@whitbread-eos/api';

import { HowToAnswerForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/HowToAnswerForm';

const mockProps = {
  icons: {},
  onSubmit: jest.fn(),
  formRef: { current: null },
  questionId: BusinessQuestionType.CustomerReference,
  answers: { answerType: BBAnswerType.U, answers: ['Whitbread', 'Whitbread Co'] },
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

describe('HowToAnswerForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render HowToAnswerForm component', async () => {
    const { getByTestId } = render(<HowToAnswerForm {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId(`HowToAnswerForm-${mockProps.questionId}-container`)).toBeInTheDocument();
    });
  });

  it('should trim preset answers before submit', async () => {
    const user = userEvent.setup();
    const onSubmit = jest.fn();
    const { getByPlaceholderText, getByTestId, container } = render(
      <HowToAnswerForm
        {...mockProps}
        answers={{ answerType: BBAnswerType.U, answers: ['  First  '] }}
        onSubmit={onSubmit}
        questionId="test-id"
      />
    );

    const presetInput = getByPlaceholderText(
      'coMngt.questions.details.input.presetAnswer.label'
    ) as HTMLInputElement;
    await user.type(presetInput, '  Second  ');
    const addButton = getByTestId('HowToAnswerForm-test-id-add-answer-button');
    await user.click(addButton);

    const form = container.querySelector('form');
    fireEvent.submit(form as HTMLFormElement);

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith({
        answerType: BBAnswerType.U,
        answers: ['First', 'Second'],
      });
    });
  });

  it('should add preset answer to the list', async () => {
    const user = userEvent.setup();
    const { getByPlaceholderText, getByTestId } = render(
      <HowToAnswerForm {...mockProps} answers={{ answerType: BBAnswerType.U, answers: [] }} />
    );

    const presetInput = getByPlaceholderText(
      'coMngt.questions.details.input.presetAnswer.label'
    ) as HTMLInputElement;
    await user.type(presetInput, 'New Answer');

    const addButton = getByTestId(`HowToAnswerForm-${mockProps.questionId}-add-answer-button`);
    await user.click(addButton);

    await waitFor(() => {
      expect(
        getByTestId(`HowToAnswerForm-${mockProps.questionId}-answer-text-New Answer-0`)
      ).toHaveTextContent('New Answer');
    });
  });

  it('should delete answer from the list', async () => {
    const user = userEvent.setup();
    const { getByTestId, queryByTestId } = render(<HowToAnswerForm {...mockProps} />);

    const deleteButton = getByTestId(
      `HowToAnswerForm-${mockProps.questionId}-answer-button-Whitbread-0`
    );
    await user.click(deleteButton);

    await waitFor(() => {
      expect(
        queryByTestId(`HowToAnswerForm-${mockProps.questionId}-answer-text-Whitbread-0`)
      ).not.toBeInTheDocument();
    });
  });

  it('should not add empty preset answer', async () => {
    const user = userEvent.setup();
    const { getByPlaceholderText, getByTestId, queryByTestId } = render(
      <HowToAnswerForm {...mockProps} answers={{ answerType: BBAnswerType.U, answers: [] }} />
    );

    const presetInput = getByPlaceholderText(
      'coMngt.questions.details.input.presetAnswer.label'
    ) as HTMLInputElement;
    await user.type(presetInput, '   ');

    const addButton = getByTestId(`HowToAnswerForm-${mockProps.questionId}-add-answer-button`);
    await user.click(addButton);

    await waitFor(() => {
      expect(
        queryByTestId(`HowToAnswerForm-${mockProps.questionId}-answer-text-   -0`)
      ).not.toBeInTheDocument();
    });
  });

  it('should call onDirtyChange when deleting an answer', async () => {
    const user = userEvent.setup();
    const onDirtyChange = jest.fn();
    const { getByTestId } = render(
      <HowToAnswerForm {...mockProps} onDirtyChange={onDirtyChange} />
    );

    const deleteButton = getByTestId(
      `HowToAnswerForm-${mockProps.questionId}-answer-button-Whitbread-0`
    );
    await user.click(deleteButton);

    await waitFor(() => {
      expect(onDirtyChange).toHaveBeenCalled();
    });
  });

  it('should render existing answers on mount', async () => {
    const { getByTestId } = render(<HowToAnswerForm {...mockProps} />);

    await waitFor(() => {
      expect(
        getByTestId(`HowToAnswerForm-${mockProps.questionId}-answer-text-Whitbread-0`)
      ).toHaveTextContent('Whitbread');
      expect(
        getByTestId(`HowToAnswerForm-${mockProps.questionId}-answer-text-Whitbread Co-1`)
      ).toHaveTextContent('Whitbread Co');
    });
  });

  it('should reset preset answer input after adding', async () => {
    const user = userEvent.setup();
    const { getByPlaceholderText, getByTestId } = render(
      <HowToAnswerForm {...mockProps} answers={{ answerType: BBAnswerType.U, answers: [] }} />
    );

    const presetInput = getByPlaceholderText(
      'coMngt.questions.details.input.presetAnswer.label'
    ) as HTMLInputElement;
    await user.type(presetInput, 'Test Answer');

    const addButton = getByTestId(`HowToAnswerForm-${mockProps.questionId}-add-answer-button`);
    await user.click(addButton);

    await waitFor(() => {
      expect(presetInput.value).toBe('');
    });
  });
});
