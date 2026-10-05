import { ChakraProvider } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render } from '@testing-library/react';
import React from 'react';

import Header from './Header';
import { mockUseQueryRequest, mockUseRestQueryRequest, mockResponse } from './mockResponse';

const mockUseRouter = jest.fn();

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(),
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
  useAuthToken: () => ({ token: null, isAuth0Enabled: false, isLoading: false }),
  useAuth0User: () => ({ user: null, loading: false, error: null, refetch: jest.fn() }),
}));

const headerProps = {
  variant: 'step',
  isIcon: false,
  queryClient: new QueryClient(),
  user: {
    test: '123',
  },
  roles: ['12'],
};

const queryClient = new QueryClient();

describe('Header', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
    });
  });

  it('should render a <Header> with variant props step', function () {
    const { getByTestId } = render(
      <ChakraProvider>
        <Header bb={false} {...headerProps} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
  });

  it('should render a <Header> with variant props logo', function () {
    const { getByTestId } = render(
      <ChakraProvider>
        <Header {...{ ...headerProps, variant: 'logo' }} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
  });

  it('should render a <Header> with variant props default', function () {
    const { getByTestId } = render(
      <ChakraProvider>
        <Header {...{ ...headerProps, variant: 'default' }} />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
  });

  it('should render a <Header> with variant props business-default', function () {
    const { getByTestId } = render(
      <ChakraProvider>
        <Header {...{ ...headerProps, variant: 'business-default' }} />
      </ChakraProvider>
    );

    expect(getByTestId('logo-container')).toBeInTheDocument();
  });

  it('should render a <Header> with variant props business-step', function () {
    const { getByTestId } = render(
      <ChakraProvider>
        <Header {...{ ...headerProps, variant: 'business-step', bb: true }} />
      </ChakraProvider>
    );

    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
  });

  it('should render a <Header> with variant props agent', function () {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <ChakraProvider>
          <Header {...{ ...headerProps, variant: 'agent' }} />
        </ChakraProvider>
      </QueryClientProvider>
    );

    expect(getByTestId('common-header-wrapper')).toBeInTheDocument();
  });
});
