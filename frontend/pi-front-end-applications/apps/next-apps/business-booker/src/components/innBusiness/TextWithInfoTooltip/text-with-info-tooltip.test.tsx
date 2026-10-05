import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { TextWithInfoTooltip } from './text-with-info-tooltip';

const mockProps = {
  locale: LOCALES.EN,
  baseDataTestId: 'BaseId',
  mainText: 'Main',
  infoText: 'Info',
  icons: { 'icon.arrow.left': '/' },
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
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => new URLSearchParams(),
    getCardManagementLabels: () => null,
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

describe('TextWithInfoTooltip Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render TextWithInfoTooltip component', async () => {
    const { getByTestId } = render(<TextWithInfoTooltip {...mockProps} />);

    expect(getByTestId(`${mockProps.baseDataTestId}-title-icon`)).toBeInTheDocument();
  });

  it('should click on icon and outside of it', async () => {
    const { getByTestId } = render(<TextWithInfoTooltip {...mockProps} />);

    const icon = getByTestId(`${mockProps.baseDataTestId}-title-icon`);

    await act(async () => {
      fireEvent.click(icon);
    });

    await waitFor(() => {
      expect(getByTestId('BaseId-Mobile-InfoTooltip')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.mouseUp(icon);
    });
  });
});
