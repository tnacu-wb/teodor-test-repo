import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { BusinessType } from '@whitbread-eos/api';

import { BusinessTypeForm } from '~components/innBusiness/forms/BusinessAccountApply/BusinessTypeForm';

const mockProps = {
  icons: {},
  businessType: BusinessType.Government,
  onSubmit: jest.fn(),
  companyType: '["test","test","test"]',
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

describe('BusinessTypeForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render BusinessTypeForm component', async () => {
    const { getByTestId } = render(<BusinessTypeForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId(`BusinessTypeForm-container`)).toBeInTheDocument();
    });
  });
});
