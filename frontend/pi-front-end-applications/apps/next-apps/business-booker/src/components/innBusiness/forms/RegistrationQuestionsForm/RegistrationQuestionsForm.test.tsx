import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { EmployeeQuestionType, LOCALES } from '@whitbread-eos/api';

import { RegistrationQuestionsForm } from './RegistrationQuestionsForm';

const mockProps = {
  formRef: { current: document.createElement('form') },
  onSubmit: (data: any) => {
    return data;
  },
  questions: [
    {
      id: '1',
      mandatory: true,
      type: 'text' as EmployeeQuestionType,
      options: null,
      answer: 'test',
      label: 'Customer reference?',
    },
    {
      id: 'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95',
      mandatory: false,
      type: 'select' as EmployeeQuestionType,
      options: ['Option1', 'Option2'],
      answer: '2',
      label: 'Dropdown Question?',
    },
  ],
  icons: { icon: 'test' },
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: (locale: LOCALES, path: string): string => `/${locale}/${path}`,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCountriesList: () => {
      return;
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    findError: serverUtils.findError,
    defaultQuestionsAndSchema: () => {
      return {
        questionsSchemaObj: {
          '1': {
            _def: {
              checks: [
                {
                  kind: 'min',
                  value: 1,
                  message: 'This field is required.',
                },
              ],
              typeName: 'ZodString',
              coerce: false,
            },
          },
          'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95': {
            _def: {
              unknownKeys: 'strip',
              catchall: {
                _def: {
                  typeName: 'ZodNever',
                },
              },
              typeName: 'ZodObject',
            },
            _cached: null,
          },
        },
        defaultQuestionsObj: {
          '1': 'test',
          'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95': {
            displayValue: 'Option2',
            value: '2',
          },
        },
      };
    },
    parseAnswersObj: () => {
      return {
        userDefinedAnswers: [
          {
            miID: '1',
            miAnswer: 'test',
          },
          {
            miID: 'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95',
            miAnswer: '2',
          },
        ],
      };
    },
  };
});

describe('RegistrationQuestionsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render RegistrationQuestionsForm component', async () => {
    const { getByTestId } = render(<RegistrationQuestionsForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Registration-Question-1')).toBeInTheDocument();
    });
  });
});
