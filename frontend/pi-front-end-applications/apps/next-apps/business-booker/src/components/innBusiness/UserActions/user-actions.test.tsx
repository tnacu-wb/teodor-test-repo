import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { UserActions, UserActionsProps } from './user-actions';

const mockProps: UserActionsProps = {
  row: {
    id: 'EMPL_1c3240fa-c893-4ad4-9166-29fb16cd816d',
    title: 'Mrs',
    firstName: 'Automation',
    lastName: 'Resendactivation',
    emailAddress: 'automation.resendactivation2@mailinator.com',
    accessLevel: 'BOOKER',
    employeeStatus: 'INACTIVE',
  },
  locale: LOCALES.EN,
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getPathForLocale: () => {
      return '/';
    },
  };
});

describe('UserActions Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render UserActions component', async () => {
    const { getByTestId } = render(await UserActions(mockProps));

    expect(getByTestId('UserActions')).toBeInTheDocument();
  });
});
