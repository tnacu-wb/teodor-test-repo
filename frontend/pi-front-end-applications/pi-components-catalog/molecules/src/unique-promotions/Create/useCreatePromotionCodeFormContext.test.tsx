import { renderHook } from '@testing-library/react';
import React from 'react';

import {
  CreatePromoCodeFormContext,
  useCreatePromoCodeFormContext,
} from './useCreatePromotionCodeFormContext';

describe('CreatePromoCodeFormContext', () => {
  const mockContextValue = {
    createPromoMutation: {
      mutate: jest.fn(),
      data: {
        createPromoBatch: {
          id: '1',
          batchId: 'batch-1',
          status: 'active',
          createdAt: '2024-01-01T00:00:00Z',
          operaPromoCode: 'PROMO001',
          prefix: 'PREFIX',
          batchCount: 100,
          codeLength: 8,
          notes: 'Test batch',
          requestedBy: 'user@example.com',
          expiryDate: '2024-12-31T00:00:00Z',
          campaignName: 'Test Campaign',
        },
      },
      error: null,
      variables: undefined,
      isError: false,
      isLoading: false,
      isSuccess: true,
      status: 'success' as const,
      reset: jest.fn(),
    } as any,
    createPromoData: {
      createPromoBatch: {
        id: '1',
        batchId: 'batch-1',
        status: 'active',
        createdAt: '2024-01-01T00:00:00Z',
        operaPromoCode: 'PROMO001',
        prefix: 'PREFIX',
        batchCount: 100,
        codeLength: 8,
        notes: 'Test batch',
        requestedBy: 'user@example.com',
        expiryDate: '2024-12-31T00:00:00Z',
        campaignName: 'Test Campaign',
      },
    },
    createPromoError: null,
    createPromoIsError: false,
    createPromoIsSuccess: true,
    createPromoIsLoading: false,
    loadingTransition: false,
    setLoadingTransition: jest.fn(),
  };

  it('returns context value when used inside provider', () => {
    const wrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
      <CreatePromoCodeFormContext.Provider value={mockContextValue}>
        {children}
      </CreatePromoCodeFormContext.Provider>
    );

    const { result } = renderHook(() => useCreatePromoCodeFormContext(), { wrapper });

    expect(result.current).toBe(mockContextValue);
    expect(result.current.createPromoIsSuccess).toBe(true);
  });

  it('throws an error when used outside provider', () => {
    const consoleError = jest.spyOn(console, 'error').mockImplementation(() => {
      return null;
    });

    expect(() => {
      renderHook(() => useCreatePromoCodeFormContext());
    }).toThrow('useCreatePromoCodeFormContext must be used inside CreatePromoCodeFormProvider');

    consoleError.mockRestore();
  });
});
