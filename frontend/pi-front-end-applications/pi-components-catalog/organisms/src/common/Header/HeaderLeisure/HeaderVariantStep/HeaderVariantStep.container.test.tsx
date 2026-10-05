import { ChakraProvider } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';

import { render } from '../../../../utils/test-utils';
import HeaderVariantStepContainer from './HeaderVariantStep.container';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  analytics: {
    update: jest.fn(),
  },
  useCustomLocale: () => ({ language: 'en', country: 'gb' }),
  useHotelBrands: () => ({ brand: null, stepProgress: { activeStep: 0, steps: [] } }),
}));

jest.mock('@tanstack/react-query', () => {
  const original = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    fetchQuery: () => Promise.resolve({}),
    mount: jest.fn(),
    unmount: jest.fn(),
  };
});

const props = {
  headerInfoData: {
    config: { authentication: { business: {}, accountLinks: [] } },
    content: {
      menu: { business: 'test' },
      subnav: [],
      global: { brand: [] },
      countries: [[], []],
      header: { image: '' },
    },
  },
  queryClient: new QueryClient(),
  bb: false,
};

describe('HeaderVariantStep container', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      query: { reservationId: 'AWM323321321' },
      locale: 'en',
    });
  });

  it('should render a <HeaderVariantStep> with default props ', async function () {
    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderVariantStepContainer {...props} />
      </ChakraProvider>
    );
    await waitFor(() => {
      expect(queryByTestId('common-header-wrapper')).toBeTruthy();
    });
  });
});
