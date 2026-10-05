import { ChakraProvider } from '@chakra-ui/react';

import { render } from '../../../utils/test-utils';
import { mockUseQueryRequest, mockUseRestQueryRequest, mockResponse } from '../mockResponse';
import HeaderLeisure from './HeaderLeisure.container';

const mockUseRouter = jest.fn();
const routerMock = {
  locale: 'gb',
  asPath: '/en/en/home.html',
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockCustomLocale = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  useQueryRequest: () => mockUseQueryRequest,
  useQuery: () => mockResponse,
  useRestQueryRequest: () => mockUseRestQueryRequest,
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
}));

describe('HeaderLeisure', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue(routerMock);
    mockUseQueryRequest.isLoading = false;
    mockCustomLocale.mockReturnValue({
      language: 'gb',
    });
  });

  it('should render a <HeaderLeisure> with default props ', function () {
    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderLeisure variant="default" />
      </ChakraProvider>
    );
    expect(queryByTestId('menuWrapperId')).toBeTruthy();
    expect(queryByTestId('logo-container-pi')).toBeTruthy();
    expect(queryByTestId('pi-languageSelectorContainer')).toBeTruthy();
  });

  it('should render a default <HeaderLeisure> with  currentLang in de', function () {
    mockUseRouter.mockReturnValue({
      ...routerMock,
      locale: 'de',
    });
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderLeisure variant="default" />
      </ChakraProvider>
    );
    expect(queryByTestId('logo-container-pi-simple')).toBeTruthy();
  });

  it('should render a step <HeaderLeisure> with  currentLang in de', function () {
    mockUseRouter.mockReturnValue({
      ...routerMock,
      locale: 'de',
    });
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderLeisure variant="step" />
      </ChakraProvider>
    );
    expect(queryByTestId('progress-indicator-wrapper')).toBeTruthy();
  });

  it('should render a <HeaderLeisure> without variant ', function () {
    const { queryByTestId } = render(
      <ChakraProvider>
        <HeaderLeisure />
      </ChakraProvider>
    );
    expect(queryByTestId('menuWrapperId')).toBeTruthy();
    expect(queryByTestId('logo-container-pi')).toBeTruthy();
    expect(queryByTestId('pi-languageSelectorContainer')).toBeTruthy();
  });

  it('should render a <HeaderLeisure> with variant ', function () {
    const { queryByTestId, queryAllByTestId } = render(
      <ChakraProvider>
        <HeaderLeisure variant="step" />
      </ChakraProvider>
    );

    expect(queryAllByTestId('logo-container').length).toBe(2);
    expect(queryByTestId('languageSelectorContainer')).toBeFalsy();
    expect(queryByTestId('menuWrapperId')).toBeFalsy();
    expect(queryByTestId('progress-indicator-wrapper')).toBeTruthy();
  });

  it('should render a <HeaderLeisure> with variant and hotel type ', function () {
    const { queryByTestId, queryAllByTestId } = render(
      <ChakraProvider>
        <HeaderLeisure variant="step" />
      </ChakraProvider>
    );

    expect(queryAllByTestId('logo-container').length).toBe(2);
    expect(queryByTestId('languageSelectorContainer')).toBeFalsy();
    expect(queryByTestId('menuWrapperId')).toBeFalsy();
  });

  it('should render loading text when static content is loading', function () {
    mockUseQueryRequest.isLoading = true;
    const { getByText, queryByTestId } = render(
      <ChakraProvider>
        <HeaderLeisure variant="default" />
      </ChakraProvider>
    );

    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
    expect(queryByTestId('menuWrapperId')).toBeFalsy();
  });
});
