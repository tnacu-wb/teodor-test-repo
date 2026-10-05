import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import type { HotelBrandType } from '@whitbread-eos/api';
import React from 'react';

import { render } from '../../../../utils/test-utils';
import HeaderVariantDefault from './HeaderVariantDefault.component';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockCustomLocale = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
}));

const headerInfoData = {
  content: {
    countries: [
      { language: 'English', flagUrl: '/images/british-round.svg' },
      { language: 'German', flagUrl: '/images/germany-round.svg' },
    ],
    menu: {
      discoverPI: 'Discover Premier Inn',
      business: 'Business',
      findBooking: 'Manage booking',
      logIn: 'Log in',
      mobileMenuButton: 'Menu',
      language: 'Language',
      tick: '/images/tick.svg',
    },
    header: {
      image: '/images/pi-logo.svg',
    },
    global: {
      brand: {
        pi: 'Premier Inn',
        hub: 'hub by Premier Inn',
        zip: 'ZIP by Premier Inn',
      },
    },
    subNav: [],
    authentication: {
      logoutButton: 'Log out',
      signUpButton: 'Sign up',
    },
  },
  config: {
    authentication: {
      accountLinks: [],
    },
  },
};

describe('HeaderVariantDefault', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({ locale: 'en', asPath: '/gb/en/home.html' });
    mockCustomLocale.mockReturnValue({ language: 'en', country: 'gb' });
  });

  it('should render the header wrapper', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <HeaderVariantDefault headerInfoData={headerInfoData} hotelBrand={'pi' as HotelBrandType} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
  });

  it('should render the logo with alt text from content.global.brand when hotelBrand is provided', async () => {
    const { findByAltText } = render(
      <ChakraProvider>
        <HeaderVariantDefault headerInfoData={headerInfoData} hotelBrand={'pi' as HotelBrandType} />
      </ChakraProvider>
    );
    expect(await findByAltText('Premier Inn')).toBeInTheDocument();
  });

  it('should not render a logo alt text when hotelBrand is not provided', async () => {
    const { findByRole } = render(
      <ChakraProvider>
        <HeaderVariantDefault headerInfoData={headerInfoData} />
      </ChakraProvider>
    );
    const img = await findByRole('img', { name: '' });
    expect(img).not.toHaveAttribute('alt');
  });

  it('should render the language selector', async () => {
    const { findByTestId } = render(
      <ChakraProvider>
        <HeaderVariantDefault headerInfoData={headerInfoData} hotelBrand={'pi' as HotelBrandType} />
      </ChakraProvider>
    );
    expect(await findByTestId('pi-languageSelectorContainer')).toBeInTheDocument();
  });

  it('should render flag icons with correct alt text for each language', async () => {
    const { findAllByAltText } = render(
      <ChakraProvider>
        <HeaderVariantDefault headerInfoData={headerInfoData} hotelBrand={'pi' as HotelBrandType} />
      </ChakraProvider>
    );
    const englishFlags = await findAllByAltText('English');
    expect(englishFlags.length).toBeGreaterThan(0);
  });
});
