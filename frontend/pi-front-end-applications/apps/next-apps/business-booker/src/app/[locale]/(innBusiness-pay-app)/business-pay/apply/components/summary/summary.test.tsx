import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { PayApplicationStep } from '../types';
import { Summary } from './summary';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    updateAppContactDetails: () => ({ status: 'success' }),
    getLocaleByPathname: () => LOCALES.EN,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getPathForLocale: () => {
      return '/';
    },
    findError: serverUtils.findError,
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => jest.fn(),
}));

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: PayApplicationStep.SUMMARY,
  steps: [
    {
      id: PayApplicationStep.SUMMARY,
      component: <Summary locale={LOCALES.EN} />,
    },
  ],
};

describe('Summary component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render Summary component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });
});
