import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { AccessRestrictedStep } from '../types';
import AccessRestrictedView from './access-restricted-view';

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: AccessRestrictedStep.ACCESS_RESTRICTED,
  steps: [
    {
      id: AccessRestrictedStep.ACCESS_RESTRICTED,
      component: <AccessRestrictedView locale={LOCALES.EN} />,
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
    getPathForLocale: (locale: string, path: string) => path,
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});
describe('AccessRestricted', () => {
  it('should render', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('AccessRestricted-wrapper')).toBeInTheDocument();

    expect(getByTestId('AccessRestricted-icon')).toBeInTheDocument();
    expect(getByTestId('AccessRestricted-title')).toBeInTheDocument();
    expect(getByTestId('AccessRestricted-description')).toBeInTheDocument();
    expect(getByTestId('AccessRestricted-contact')).toBeInTheDocument();
    expect(getByTestId('AccessRestricted-login')).toBeInTheDocument();
  });
});
