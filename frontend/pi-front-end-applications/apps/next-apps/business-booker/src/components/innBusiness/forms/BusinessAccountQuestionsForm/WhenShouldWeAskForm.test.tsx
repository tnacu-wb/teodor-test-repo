import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { BusinessQuestionType } from '@whitbread-eos/api';

import { WhenShouldWeAskForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/WhenShouldWeAskForm';

const mockProps = {
  icons: {},
  onSubmit: jest.fn(),
  formRef: { current: null },
  typeOfQuestion: BusinessQuestionType.CustomerReference,
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
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('WhenShouldWeAskForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render WhenShouldWeAskForm component', async () => {
    const { getByTestId } = render(<WhenShouldWeAskForm {...mockProps} />);

    await waitFor(async () => {
      expect(
        getByTestId(`WhenShouldWeAskForm-${mockProps.typeOfQuestion}-container`)
      ).toBeInTheDocument();
    });
  });
});
