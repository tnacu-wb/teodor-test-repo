'use client';

import { createContext, useContext } from 'react';

import { CreatePromoCodeFormContextType } from './types';

export const CreatePromoCodeFormContext = createContext<CreatePromoCodeFormContextType | null>(
  null
);

export function useCreatePromoCodeFormContext() {
  const context = useContext(CreatePromoCodeFormContext);

  if (!context) {
    throw new Error(
      'useCreatePromoCodeFormContext must be used inside CreatePromoCodeFormProvider'
    );
  }

  return context;
}
