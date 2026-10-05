import { renderHook } from '@testing-library/react';
import { ReactNode } from 'react';

import { AppDataProvider, useAppData, useAppDataDispatch, AppData } from './AppDataContext';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  analytics: {
    update: jest.fn(),
  },
}));

describe('AppDataContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('useAppData', () => {
    it('should return the current app data', () => {
      const initialData: AppData = {
        screenSize: 'lg',
        orientation: 'portrait',
      };

      const wrapper = ({ children }: { children: ReactNode }) => (
        <AppDataProvider initialAppData={initialData}>{children}</AppDataProvider>
      );

      const { result } = renderHook(() => useAppData(), { wrapper });

      expect(result.current).toEqual({
        screenSize: 'lg',
        orientation: 'portrait',
      });
    });

    it('should return null when used outside provider', () => {
      const { result } = renderHook(() => useAppData());

      expect(result.current).toBeNull();
    });
  });

  describe('useAppDataDispatch', () => {
    it('should return the dispatch function', () => {
      const wrapper = ({ children }: { children: ReactNode }) => (
        <AppDataProvider>{children}</AppDataProvider>
      );

      const { result } = renderHook(() => useAppDataDispatch(), { wrapper });

      expect(result.current).toBeInstanceOf(Function);
    });
  });
});
