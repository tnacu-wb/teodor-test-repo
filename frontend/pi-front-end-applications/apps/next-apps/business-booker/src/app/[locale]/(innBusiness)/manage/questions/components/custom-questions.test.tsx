import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';

import { CustomQuestions } from './custom-questions';

const mockProps = {
  questions: [],
  icons: {},
  companyId: 'COMP_123',
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('CustomQuestions Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CustomQuestions component', async () => {
    const { getByTestId } = render(<CustomQuestions {...mockProps} />);

    expect(getByTestId('CustomQuestions-container')).toBeInTheDocument();
  });
  it('should render CustomQuestions component and click on create new question', async () => {
    const { getByTestId } = render(<CustomQuestions {...mockProps} />);

    expect(getByTestId('CustomQuestions-container')).toBeInTheDocument();

    const createNewQuestionButton = getByTestId(`CustomQuestions-button-add`);
    await waitFor(async () => {
      expect(createNewQuestionButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(createNewQuestionButton);
    });
  });
});
