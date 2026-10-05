import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { AccessLevel } from '@whitbread-eos/api';

import { UserRoleLabel, UserRoleProps } from './user-role-label';

const mockProps: UserRoleProps = {
  type: AccessLevel.Super,
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
  };
});

describe('UserRoleLabel Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render UserRoleLabel component', async () => {
    const { getByTestId } = render(await UserRoleLabel(mockProps));

    expect(getByTestId('UserRoleLabel')).toBeInTheDocument();
  });
});
