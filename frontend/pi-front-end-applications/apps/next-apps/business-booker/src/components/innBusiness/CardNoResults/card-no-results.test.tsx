import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { CardNoResults } from './card-no-results';

const mockProps = {
  locale: LOCALES.EN,
  addCardUrl: '/',
  hideAddCardButton: false,
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getInitials: serverUtils.getInitials,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('CardNoResults Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.hideAddCardButton = false;
  });

  it('should render CardNoResults component', async () => {
    const { getByTestId } = render(await CardNoResults(mockProps));

    expect(getByTestId('CardNoResults')).toBeInTheDocument();
  });

  it('should render CardNoResults component hiding the add card button', async () => {
    mockProps.hideAddCardButton = true;
    const { getByTestId } = render(await CardNoResults(mockProps));

    expect(getByTestId('CardNoResults')).toBeInTheDocument();
  });
});
