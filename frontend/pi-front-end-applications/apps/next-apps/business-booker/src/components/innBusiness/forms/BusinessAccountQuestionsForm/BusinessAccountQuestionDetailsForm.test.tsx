import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { BusinessQuestionType } from '@whitbread-eos/api';

import { BusinessAccountQuestionDetailsForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/BusinessAccountQuestionDetailsForm';

const mockProps = {
  icons: {},
  typeOfQuestion: BusinessQuestionType.PurchaseOrder,
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

describe('BusinessAccountQuestionDetailsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render BusinessAccountQuestionDetailsForm component', async () => {
    const { getByTestId } = render(<BusinessAccountQuestionDetailsForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(
        getByTestId(`BusinessAccountQuestionsForm-${mockProps.typeOfQuestion}-container`)
      ).toBeInTheDocument();
    });
  });
});
