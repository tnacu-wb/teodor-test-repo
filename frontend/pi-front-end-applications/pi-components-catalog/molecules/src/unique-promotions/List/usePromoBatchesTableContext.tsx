'use client';

import { createContext, useContext } from 'react';

import { PromoBatchesTableContextType } from './types';

export const PromoBatchesTableContext = createContext<PromoBatchesTableContextType<any> | null>(
  null
);

export function usePromoTableContext() {
  const result = useContext<PromoBatchesTableContextType<any> | null>(PromoBatchesTableContext);

  if (!result) {
    throw new Error(
      'usePromoBatchesTableContext can only be used inside a PromoBatchesTableContext.Provider'
    );
  }

  return result;
}
