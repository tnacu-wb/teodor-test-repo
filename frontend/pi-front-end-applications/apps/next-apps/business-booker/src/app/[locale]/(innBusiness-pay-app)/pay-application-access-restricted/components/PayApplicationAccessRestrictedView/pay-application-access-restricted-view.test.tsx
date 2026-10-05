import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { PayApplicationAccessRestrictedStep } from '../types';
import PayApplicationAccessRestrictedView from './pay-application-access-restricted-view';

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: PayApplicationAccessRestrictedStep.ACCESS_RESTRICTED,
  steps: [
    {
      id: PayApplicationAccessRestrictedStep.ACCESS_RESTRICTED,
      component: <PayApplicationAccessRestrictedView locale={LOCALES.EN} />,
    },
  ],
};

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
  redirect: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => ({
      t: (key: string) => `/${key}`,
    }),
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPathForLocale: (locale: string, path: string) => path,
  };
});

describe('PayApplicationAccessRestricted', () => {
  it('should render', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('PayApplicationAccessRestricted-wrapper')).toBeInTheDocument();

    expect(getByTestId('PayApplicationAccessRestricted-icon')).toBeInTheDocument();
    expect(getByTestId('PayApplicationAccessRestricted-title')).toBeInTheDocument();
    expect(getByTestId('PayApplicationAccessRestricted-description')).toBeInTheDocument();
    expect(getByTestId('PayApplicationAccessRestricted-contact')).toBeInTheDocument();
    expect(getByTestId('PayApplicationAccessRestricted-homepage')).toBeInTheDocument();
  });
});
