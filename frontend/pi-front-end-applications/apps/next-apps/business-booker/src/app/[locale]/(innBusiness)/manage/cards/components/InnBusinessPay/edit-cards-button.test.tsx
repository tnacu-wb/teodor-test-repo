import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES, Scheme } from '@whitbread-eos/api';

import { EditCardsButton } from './edit-cards-button';

const mockProps = {
  tetheredGuid: 'abc',
  returnUrl: '/',
  icons: {},
  scheme: 'GB' as Scheme,
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => LOCALES.EN,
    formatIBAssetsUrl: () => '/',
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('EditCardsButton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render EditCardsButton component', async () => {
    const { getByTestId } = render(<EditCardsButton {...mockProps} />);

    expect(getByTestId('InnBusinessPayTab-edit-cards-button')).toBeInTheDocument();
  });
});
