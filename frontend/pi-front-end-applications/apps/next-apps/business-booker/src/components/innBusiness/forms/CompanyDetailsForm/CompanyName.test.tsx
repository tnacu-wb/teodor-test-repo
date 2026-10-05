import '@testing-library/jest-dom';
import { render, waitFor, act } from '@testing-library/react';

import { CompanyName } from '~components/innBusiness/forms/CompanyDetailsForm/CompanyName';
import { userEvent } from '~utils/test-utils';

const mockProps = {
  companyName: 'My company',
  onSubmit: jest.fn(),
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    findError: serverUtils.findError,
  };
});

describe('CompanyName Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyName component ', async () => {
    const { getByTestId } = render(<CompanyName {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyName-form')).toBeInTheDocument();
      expect(getByTestId('Company-name-Form-Input')).toBeInTheDocument();
    });

    await act(async () => {
      const input = getByTestId('Company-name-Form-Input');
      input.focus();
      await userEvent.tab();
    });
  });
});
