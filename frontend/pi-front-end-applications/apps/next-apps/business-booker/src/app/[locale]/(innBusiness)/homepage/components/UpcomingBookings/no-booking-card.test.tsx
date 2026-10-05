import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { NoBookingCard } from './no-booking-card';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => [],
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

const mockT = (key: string) => key;

describe('NoBookingCard', () => {
  it('should render no bookings card', () => {
    const { getByTestId } = render(<NoBookingCard t={mockT} baseDataTestId="test" searchIcon="" />);

    expect(getByTestId('test-NoBookingCard')).toBeInTheDocument();
  });
});
