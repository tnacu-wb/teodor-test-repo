import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import type { HotelBrandType } from '@whitbread-eos/api';

import { render, RenderOptions } from '../../../../utils/test-utils';
import HeaderVariantStep from './HeaderVariantStep.component';

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
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

describe('HeaderVariantStep', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });
  });

  it('should render a <HeaderVariantStep> with default props ', function () {
    const { queryByTestId, queryAllByTestId } = render(
      <ChakraProvider>
        <HeaderVariantStep {...props} />
      </ChakraProvider>
    );
    expect(queryByTestId('common-header-wrapper')).toBeTruthy();
    expect(queryByTestId('logo-container-pi')).toBeTruthy();
    expect(queryAllByTestId('logo-container').length).toBe(2);
  });

  it('should render a <HeaderVariantStep> with current lang,country undefined ', function () {
    const { queryByTestId, queryAllByTestId } = render(
      <ChakraProvider>
        <HeaderVariantStep {...props} currentLang={undefined} currentCountry={undefined} />
      </ChakraProvider>
    );
    expect(queryByTestId('common-header-wrapper')).toBeTruthy();
    expect(queryByTestId('logo-container-pi')).toBeTruthy();
    expect(queryAllByTestId('logo-container').length).toBe(2);
  });

  it('should render a <HeaderVariantStep> with default props mobile', function () {
    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderVariantStep {...props} />
      </ChakraProvider>,
      {
        initialAppData: { screenSize: 'mobile' },
      } as Omit<RenderOptions, 'wrapper'>
    );
    expect(queryByTestId('logo-container-pi-icon')).toBeTruthy();
  });

  it('should render a <HeaderVariantStep> with hotel brand hub logo', function () {
    props.hotelBrand = 'hub';

    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderVariantStep {...props} />
      </ChakraProvider>
    );
    expect(queryByTestId('logo-container-hub')).toBeTruthy();
  });

  it('should render a <HeaderVariantStep> with hotel brand hub logo mobile', function () {
    props.hotelBrand = 'hub';

    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderVariantStep {...props} />
      </ChakraProvider>,
      {
        initialAppData: { screenSize: 'mobile' },
      } as Omit<RenderOptions, 'wrapper'>
    );
    expect(queryByTestId('logo-container-hub-simple')).toBeTruthy();
  });
});
