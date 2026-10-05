import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import type { HotelBrandType } from '@whitbread-eos/api';
import React from 'react';

import { render } from '../../../../utils/test-utils';
import HeaderVariantLogo from './HeaderVariantLogo.component';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const props = {
  propsStepProgress: {
    steps: [{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }],
    activeStep: 2,
  },
  currentLang: 'en',
  currentCountry: 'gb',
  headerInfoData: {
    content: {
      countries: [
        {
          flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
          language: 'English',
        },
        {
          flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
          language: 'German',
        },
      ],
      menu: {
        guestAccount: 'Guest account',
        changeLogs: 'Change logs',
        agentMemo: 'Agent memo',
      },
      header: {
        image: '/etc/clientlibs/pi-header/resources/images/pi-refresh-logo.svg',
      },
    },
  },
  hotelBrand: 'pi' as HotelBrandType,
  isIcon: true,
  bb: false,
};

const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
}));

describe('HeaderVariantLogo', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render a <HeaderVariantLogo> with default props ', function () {
    const { getByTestId } = render(
      <ChakraProvider>
        {' '}
        <HeaderVariantLogo {...props} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
    expect(getByTestId('logo-container')).toBeInTheDocument();
  });

  it('should render a <HeaderVariantLogo> with brand pi ', function () {
    props.hotelBrand = 'pi';
    const { getByTestId } = render(
      <ChakraProvider>
        <HeaderVariantLogo {...props} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
    expect(getByTestId('logo-container')).toBeInTheDocument();
  });

  it('should render a <HeaderVariantLogo> with logo URL for Leisure ', function () {
    props.bb = false;
    const { getByTestId, getByRole } = render(
      <ChakraProvider>
        <HeaderVariantLogo {...props} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
    expect(getByTestId('logo-container')).toBeInTheDocument();

    const anchorElement = getByRole('link');
    expect(anchorElement).toHaveAttribute('href', expect.stringContaining('gb/en/home.html'));
    expect(anchorElement).toHaveAttribute(
      'href',
      expect.not.stringContaining('gb/en/business-booker/home.html')
    );
  });

  it('should render a <HeaderVariantLogo> with adjusted logo URL for BB prop true ', function () {
    props.bb = true;
    const { getByTestId, getByRole } = render(
      <ChakraProvider>
        <HeaderVariantLogo {...props} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
    expect(getByTestId('logo-container')).toBeInTheDocument();

    const anchorElement = getByRole('link');
    expect(anchorElement).toHaveAttribute(
      'href',
      expect.stringContaining('gb/en/business-booker/home.html')
    );
    expect(anchorElement).toHaveAttribute('href', expect.not.stringContaining('gb/en/home.html'));
  });
});
