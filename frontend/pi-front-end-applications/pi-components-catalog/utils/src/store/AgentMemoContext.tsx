'use client';

import { type Claims, GET_AGENT_MEMOS, MemoModalVariants } from '@whitbread-eos/api';
import React, { ReactNode, useContext, useState } from 'react';

import { useQueryRequest } from '../hooks';
import noop from '../utils/noop';

export type AgentMemoState = {
  reservationId: any;
  variant: string;
};

export type AgentMemoContextType = {
  isAgentMemoOpen: boolean;
  openAgentMemo: (variant: string) => void;
  closeAgentMemo: () => void;
  setAgentMemoReservationId: (reservationId: string) => void;
  agentMemoState: AgentMemoState;
  user: Claims | undefined;
};

export const AgentMemoContext = React.createContext<AgentMemoContextType>({
  isAgentMemoOpen: false,
  openAgentMemo: noop,
  closeAgentMemo: noop,
  setAgentMemoReservationId: noop,
  agentMemoState: { reservationId: '', variant: '' },
  user: {},
});

interface AgentMemoProviderProps {
  children: ReactNode;
  user: Claims | undefined;
}

export function useAgentMemo() {
  const { agentMemoState, isAgentMemoOpen, ...rest } = useContext(AgentMemoContext);

  const { data: agentMemoData } = useQueryRequest(
    ['GetAgentMemos', agentMemoState.reservationId],
    GET_AGENT_MEMOS,
    {
      basketReference: agentMemoState.reservationId,
    },
    {
      enabled: !!agentMemoState.reservationId,
      select: (data: any) => {
        return data.getMemos || data.createMemo;
      },
      onError: (error: any) => {
        console.log(error);
      },
    },
    undefined,
    !!'returnError'
  );

  return {
    agentMemoData,
    agentMemoCount: agentMemoData?.memos?.length,
    agentMemoState,
    isAgentMemoOpen,
    ...rest,
  };
}

export function AgentMemoProvider({ children, user }: Readonly<AgentMemoProviderProps>) {
  const defaultState = { reservationId: '', variant: MemoModalVariants.PAGE };

  const [isAgentMemoOpen, setIsAgentMemoOpen] = useState(false);
  const [agentMemoState, setAgentMemoState] = useState<AgentMemoState>(defaultState);

  const setAgentMemoReservationId = (reservationId: string) => {
    setAgentMemoState((prev: AgentMemoState) => ({ ...prev, reservationId }));
  };

  const openAgentMemo = (variant: string) => {
    setAgentMemoState((prev: AgentMemoState) => ({ ...prev, variant }));
    setIsAgentMemoOpen(true);
  };

  const closeAgentMemo = () => {
    if (agentMemoState.variant === MemoModalVariants.CARD) {
      setAgentMemoState(defaultState);
    }
    setIsAgentMemoOpen(false);
  };
  const value = {
    isAgentMemoOpen,
    openAgentMemo,
    closeAgentMemo,
    agentMemoState,
    setAgentMemoReservationId,
    user,
  };

  return <AgentMemoContext.Provider value={value}>{children}</AgentMemoContext.Provider>;
}
