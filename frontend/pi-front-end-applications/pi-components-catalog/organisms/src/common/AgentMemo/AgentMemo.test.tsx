import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { renderHook } from '@testing-library/react';
import { MemoModalVariants } from '@whitbread-eos/api';
import { AgentMemoProvider, useAgentMemo } from '@whitbread-eos/utils';

import { act, fireEvent, screen, waitFor } from '../../utils/test-utils';
import AgentMemo from './AgentMemo.component';

const mockResponse = {
  data: {
    memos: [
      {
        ids: [
          {
            reservationId: '2941618',
            memoIds: ['148587'],
          },
        ],
        description: 'asdad adsasdas asdasdas',
        createdOn: '2023-08-04 08:54:00',
        modifiedOn: '2023-08-04 08:54:00',
      },
    ],
  },
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockResponse,
  graphQLRequest: () => jest.fn(),
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

interface Props {
  children: any;
}

const Component = ({ children }: Props) => {
  const queryClient = new QueryClient();
  return (
    <QueryClientProvider client={queryClient}>
      <AgentMemoProvider user={undefined}>
        {children}
        <AgentMemo />
      </AgentMemoProvider>
    </QueryClientProvider>
  );
};

describe('AgentMemo', () => {
  it('should render AgentMemo from Page (header link)', () => {
    const wrapper = ({ children }: Props) => <Component>{children}</Component>;
    const { result } = renderHook(() => useAgentMemo(), { wrapper });
    act(() => {
      result.current.openAgentMemo(MemoModalVariants.PAGE);
      result.current.setAgentMemoReservationId('test');
    });
    expect(screen.getByTestId('AgentMemo-PageVariant')).toBeInTheDocument();
  });
  it('should render AgentMemo from Card (booking card link)', () => {
    const wrapper = ({ children }: Props) => <Component>{children}</Component>;
    const { result } = renderHook(() => useAgentMemo(), { wrapper });
    act(() => {
      result.current.openAgentMemo(MemoModalVariants.CARD);
      result.current.setAgentMemoReservationId('test');
    });
    expect(screen.getByTestId('AgentMemo-CardVariant')).toBeInTheDocument();
  });
  it('should render AgentMemo and switch to AddMemo card', async () => {
    const wrapper = ({ children }: Props) => <Component>{children}</Component>;
    const { result } = renderHook(() => useAgentMemo(), { wrapper });
    act(() => {
      result.current.openAgentMemo(MemoModalVariants.PAGE);
      result.current.setAgentMemoReservationId('test');
    });
    const addMemoButton = screen.getByTestId('AgentMemo-AddMemoButton');

    fireEvent.click(addMemoButton);

    await waitFor(() => {
      expect(screen.getByTestId('AddMemo-Container')).toBeInTheDocument();
    });
  });
  it('should close AgentMemo', async () => {
    const wrapper = ({ children }: Props) => <Component>{children}</Component>;
    const { result } = renderHook(() => useAgentMemo(), { wrapper });
    await act(async () => {
      result.current.openAgentMemo(MemoModalVariants.PAGE);
      result.current.setAgentMemoReservationId('test');
    });

    const agentMemo = screen.getByTestId('AgentMemo-PageVariant');
    await waitFor(() => {
      expect(agentMemo).toBeInTheDocument();
    });

    const buttons = screen.getAllByRole('button');

    await act(async () => {
      fireEvent.click(buttons[0]);
    });

    await waitFor(() => {
      expect(screen.queryByTestId('AgentMemo-PageVariant')).not.toBeInTheDocument();
    });
  });
});
