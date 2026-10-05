import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { act } from 'react-dom/test-utils';

import { PageSubtitle } from './page-subtitle';

const mockProps = {
  icons: {},
  title: '',
  message: '',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCommonIcons: () => null,
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));

describe('Page subtitle Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render page subtitle component', async () => {
    const { getByTestId } = render(<PageSubtitle {...mockProps} />);

    expect(getByTestId('InnBusinessTab-title-section')).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(getByTestId('InnBusinessTab-title-icon'));
    });

    await waitFor(() => {
      expect(getByTestId('InnBusinessTab-InfoTooltip')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.mouseUp(getByTestId('InnBusinessTab-title-icon'));
    });
  });
});
