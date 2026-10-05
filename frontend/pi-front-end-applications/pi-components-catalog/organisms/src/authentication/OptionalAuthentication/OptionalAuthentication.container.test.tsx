import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import OptionalAuthenticationContainer from './OptionalAuthentication.container';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => Promise.resolve({}),
}));

const Component = () => {
  const props = {
    queryClient: new QueryClient(),
    currentLang: 'en',
    showIcon: true,
    isRegisterSelected: false,
    setRegisterSectionSelected: jest.fn(),
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <OptionalAuthenticationContainer {...props} />
    </QueryClientProvider>
  );
};

describe('OptionalAuthenticationContainer', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      query: { reservationId: 'AWM323321321' },
      locale: 'en',
    });
  });
  const baseDataTestId = 'GuestDetails-OptionalAuth';

  it('should render a OptionalAuthenticationContainer with default props ', function () {
    const { getByTestId } = render(<Component />);

    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });
});
