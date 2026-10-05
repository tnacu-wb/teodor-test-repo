import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import AddMemo from './AddMemo.component';

const mockedAddMemoProps = {
  basketReference: 'AKU-ff06c768-1aea-42f6-827c-ae072e18dd0e',
  onSave: jest.fn(),
  user: {},
};
const mockedMutationRequest = {
  mutation: {
    mutate: jest.fn(),
  },
  isSuccess: false,
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useMutationRequest: () => mockedMutationRequest,
}));

const Component = () => {
  const queryClient = new QueryClient();
  return (
    <QueryClientProvider client={queryClient}>
      <AddMemo {...mockedAddMemoProps} />
    </QueryClientProvider>
  );
};
describe('AddMemo ', () => {
  it('should render the Add Memo component', function () {
    const { getByTestId } = render(<Component />);
    expect(getByTestId('AddMemo-Container')).toBeVisible();
  });

  it('should render the description text-area', function () {
    const { getByTestId } = render(<Component />);
    expect(getByTestId('AddMemo-Description')).toBeVisible();
  });

  it('should render the description text-area and user can type in it', async function () {
    const { getByTestId, getByText } = render(<Component />);
    const textarea = getByTestId('AddMemo-Description');

    await userEvent.type(textarea, 'add memo notes here');

    await waitFor(() => {
      expect(getByText('add memo notes here')).toBeInTheDocument();
    });
  });

  it('should render the save action', function () {
    const { getByTestId } = render(<Component />);
    expect(getByTestId('AddMemo-SaveAction')).toBeInTheDocument();
  });

  it('should save the memo after the link btn has been clicked', async function () {
    const { getByTestId, getByText } = render(<Component />);
    const textarea = getByTestId('AddMemo-Description');
    const saveLink = getByText('ccui.agentMemo.save');

    await userEvent.type(textarea, 'add memo notes here');
    fireEvent.click(saveLink);

    expect(mockedMutationRequest.mutation.mutate).toHaveBeenCalled();
  });
});
