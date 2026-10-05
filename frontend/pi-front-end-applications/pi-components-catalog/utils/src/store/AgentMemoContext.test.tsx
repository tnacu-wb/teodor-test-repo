import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { act, renderHook } from '@testing-library/react';
import { MemoModalVariants } from '@whitbread-eos/api';

import { render, screen } from '../utils/test-utils';
import { AgentMemoProvider, useAgentMemo } from './AgentMemoContext';

interface MockUser {
  name: string;
}

const mockUser: MockUser = {
  name: 'CCUI Manager 2',
};

function MockApp() {
  return <>App Content</>;
}
interface Props {
  children: any;
  user: MockUser;
}

const mockUseQueryRequest = {
  isLoading: true,
  isError: false,
  error: { message: '' },
  data: {
    bookingHistory: {
      bookings: [
        {
          hotelName: '',
          leadGuest: '',
          arrivalDate: '',
          noOfNights: '',
          hotelCode: '',
          bookedBy: '',
        },
      ],
    },
  } as any,
};

jest.mock('../hooks/use-request.ts', () => ({
  ...jest.requireActual('../hooks/use-request.ts'),
  useQueryRequest: () => mockUseQueryRequest,
}));

const Component = ({ children }: Props) => {
  const queryClient = new QueryClient();
  return (
    <QueryClientProvider client={queryClient}>
      <AgentMemoProvider user={mockUser}>{children}</AgentMemoProvider>
    </QueryClientProvider>
  );
};

describe('AgentMemoContext', () => {
  it('should render children', () => {
    render(
      <Component user={mockUser}>
        <MockApp />
      </Component>
    );
    expect(screen.getByText('App Content')).toBeInTheDocument();
  });
  it('should set agentMemoState and open Agent Memo drawer', () => {
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <Component user={mockUser}>{children}</Component>
    );
    const { result } = renderHook(() => useAgentMemo(), { wrapper });
    expect(result.current.isAgentMemoOpen).toBe(false);
    act(() => {
      result.current.openAgentMemo(MemoModalVariants.PAGE);
      result.current.setAgentMemoReservationId('test');
    });
    expect(result.current.isAgentMemoOpen).toBe(true);
    expect(result.current.agentMemoState).toEqual({
      reservationId: 'test',
      variant: MemoModalVariants.PAGE,
    });
  });
  it('should close Agent Memo drawer', () => {
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <Component user={mockUser}>{children}</Component>
    );
    const { result } = renderHook(() => useAgentMemo(), { wrapper });
    expect(result.current.isAgentMemoOpen).toBe(false);
    act(() => {
      result.current.openAgentMemo(MemoModalVariants.PAGE);
      result.current.setAgentMemoReservationId('test');
    });
    expect(result.current.isAgentMemoOpen).toBe(true);
    expect(result.current.agentMemoState).toEqual({
      reservationId: 'test',
      variant: MemoModalVariants.PAGE,
    });
    act(() => {
      result.current.closeAgentMemo();
    });
    expect(result.current.isAgentMemoOpen).toBe(false);
    expect(result.current.agentMemoState).toEqual({
      reservationId: 'test',
      variant: MemoModalVariants.PAGE,
    });
  });
  it('should close Agent Memo modal', () => {
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <Component user={mockUser}>{children}</Component>
    );
    const { result } = renderHook(() => useAgentMemo(), { wrapper });
    expect(result.current.isAgentMemoOpen).toBe(false);
    act(() => {
      result.current.openAgentMemo(MemoModalVariants.CARD);
      result.current.setAgentMemoReservationId('test');
    });
    expect(result.current.isAgentMemoOpen).toBe(true);
    expect(result.current.agentMemoState).toEqual({
      reservationId: 'test',
      variant: MemoModalVariants.CARD,
    });
    act(() => {
      result.current.closeAgentMemo();
    });
    expect(result.current.isAgentMemoOpen).toBe(false);
    expect(result.current.agentMemoState).toEqual({
      reservationId: '',
      variant: MemoModalVariants.PAGE,
    });
  });
});
