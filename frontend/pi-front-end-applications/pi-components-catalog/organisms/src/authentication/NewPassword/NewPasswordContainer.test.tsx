import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import NewPasswordContainer from './NewPasswordContainer';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
}));

const Component = () => {
  const props = {
    queryClient: new QueryClient(),
    isBusinessBooker: true,
    token: 'asdadadas',
    toggleLoginModal: jest.fn(),
    defaultValues: { email: '', password: '' },
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <NewPasswordContainer {...props} />
    </QueryClientProvider>
  );
};

describe('NewPasswordContainer', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      query: { reservationId: 'AWM323321321' },
      locale: 'en',
    });
  });
  const baseDataTestId = 'NewPassword';

  it('should render a NewPasswordContainer with default props ', function () {
    const { getByTestId } = render(<Component />);

    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });
});
