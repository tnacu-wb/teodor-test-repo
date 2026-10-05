import '@testing-library/jest-dom';
import { act, renderHook } from '@testing-library/react';
import { useEffect } from 'react';

import { AppData, AppDataProvider } from '../store/AppDataContext';
import { render } from '../utils/test-utils';
import { useLocalStorage } from './use-local-storage';

interface Props {
  isValueChanged?: boolean;
  isValueFunction?: boolean;
}

function Value({ isValueChanged, isValueFunction }: Props) {
  const [value, setValue] = useLocalStorage('key', 'initial value');

  useEffect(
    function () {
      if (isValueChanged) {
        setValue(isValueFunction ? () => 'value changed' : 'value changed');
      }
    },
    [isValueChanged, isValueFunction, setValue]
  );

  return <>Value: {value}</>;
}

const initialAppData: AppData = {
  screenSize: undefined,
  orientation: undefined,
};

describe('use-local-storage hooks', () => {
  it('should display initial local storage value', () => {
    const { getByText } = render(
      <AppDataProvider initialAppData={initialAppData}>
        <Value />
      </AppDataProvider>
    );
    expect(getByText('Value: initial value')).toBeInTheDocument();
  });

  it('should display changed local storage value', () => {
    const { getByText } = render(
      <AppDataProvider initialAppData={initialAppData}>
        <Value isValueChanged />
      </AppDataProvider>
    );
    expect(getByText('Value: value changed')).toBeInTheDocument();
  });

  it('should display changed local storage value in case value is passed as function', () => {
    const { getByText } = render(
      <AppDataProvider initialAppData={initialAppData}>
        <Value isValueChanged isValueFunction />
      </AppDataProvider>
    );
    expect(getByText('Value: value changed')).toBeInTheDocument();
  });

  describe('useLocalStorage', () => {
    const localStorageMock = (() => {
      let store: Record<string, string> = {};
      return {
        getItem: (key: string) => {
          return store[key] || null;
        },
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        setItem: (key: string, value: any) => {
          store[key] = value.toString();
        },
        clear: () => {
          store = {};
        },
        removeItem: (key: string) => {
          delete store[key];
        },
      };
    })();

    Object.defineProperty(window, 'localStorage', { value: localStorageMock });
    const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();

    afterEach(() => {
      localStorage.clear();
      consoleLogSpy.mockClear();
    });

    afterAll(() => {
      consoleLogSpy.mockRestore();
    });

    it('should read the initial value from localStorage', () => {
      localStorage.setItem('key', JSON.stringify('fromLocalStorage'));
      const { result } = renderHook(() => useLocalStorage('key', 'initialValue'));
      const [storedValue] = result.current;

      expect(storedValue).toBe('fromLocalStorage');
    });

    it('should use the initial value when localStorage is empty', () => {
      localStorage.clear();
      const { result } = renderHook(() => useLocalStorage('key', 'initialValue'));
      const [storedValue] = result.current;

      expect(storedValue).toBe('initialValue');
    });

    it('should update the value in localStorage when the state updates', () => {
      const { result } = renderHook(() => useLocalStorage('key', 'initialValue'));
      const [, setValue] = result.current;

      act(() => {
        setValue('newValue');
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      expect(JSON.parse(localStorage.getItem('key')!)).toBe('newValue');
    });

    it('should handle JSON parse errors gracefully and return initial value', () => {
      localStorage.setItem('key', 'invalid json {]');
      const { result } = renderHook(() => useLocalStorage('key', 'initialValue'));
      const [storedValue] = result.current;

      expect(storedValue).toBe('initialValue');
      expect(consoleLogSpy).toHaveBeenCalled();
    });

    it('should handle setItem errors gracefully', () => {
      const { result } = renderHook(() => useLocalStorage('key', 'initialValue'));
      const [, setValue] = result.current;

      const setItemSpy = jest.spyOn(window.localStorage, 'setItem').mockImplementation(() => {
        throw new Error('Storage quota exceeded');
      });

      act(() => {
        setValue('newValue');
      });

      expect(consoleLogSpy).toHaveBeenCalled();
      expect(result.current[0]).toBe('newValue'); // state is still updated
      setItemSpy.mockRestore();
    });

    it('should work with complex objects', () => {
      const { result } = renderHook(() => useLocalStorage('key', { name: 'John', age: 0 }));
      const [, setValue] = result.current;

      act(() => {
        setValue({ name: 'Jane', age: 30 });
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const stored = JSON.parse(localStorage.getItem('key')!);
      expect(stored).toEqual({ name: 'Jane', age: 30 });
    });

    it('should work with arrays', () => {
      const { result } = renderHook(() => useLocalStorage('key', [1, 2, 3]));
      const [, setValue] = result.current;

      act(() => {
        setValue([4, 5, 6]);
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const stored = JSON.parse(localStorage.getItem('key')!);
      expect(stored).toEqual([4, 5, 6]);
    });

    it('should handle function-based state updates correctly', () => {
      const { result } = renderHook(() => useLocalStorage('counter', 0));
      const [, setValue] = result.current;

      act(() => {
        setValue((prev) => prev + 1);
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      expect(JSON.parse(localStorage.getItem('counter')!)).toBe(1);
    });

    it('should return a const tuple for readonly access pattern', () => {
      const { result } = renderHook(() => useLocalStorage('key', 'value'));
      const tuple = result.current;

      expect(Array.isArray(tuple)).toBe(true);
      expect(tuple.length).toBe(2);
      expect(typeof tuple[1]).toBe('function');
    });

    it('should use initial value when window is undefined during initialization', () => {
      const originalWindow = global.window;

      Object.defineProperty(global, 'window', {
        value: undefined,
        writable: true,
        configurable: true,
      });

      const useStateMock = jest.fn((initializer) => {
        const value = typeof initializer === 'function' ? initializer() : initializer;
        return [value, jest.fn()];
      });

      try {
        jest.isolateModules(() => {
          jest.doMock('react', () => ({
            ...jest.requireActual('react'),
            useState: useStateMock,
          }));
          const { useLocalStorage: isolatedUseLocalStorage } =
            jest.requireActual('./use-local-storage');
          const [storedValue] = isolatedUseLocalStorage('key', 'initialValue');
          expect(storedValue).toBe('initialValue');
        });
      } finally {
        jest.dontMock('react');
        jest.resetModules();
        Object.defineProperty(global, 'window', {
          value: originalWindow,
          writable: true,
          configurable: true,
        });
      }
    });

    it('should not throw when window is undefined during setter call', () => {
      const originalWindow = global.window;

      Object.defineProperty(global, 'window', {
        value: undefined,
        writable: true,
        configurable: true,
      });

      const setStoredValue = jest.fn();
      const useStateMock = jest.fn((initializer) => {
        const value = typeof initializer === 'function' ? initializer() : initializer;
        return [value, setStoredValue];
      });

      try {
        jest.isolateModules(() => {
          jest.doMock('react', () => ({
            ...jest.requireActual('react'),
            useState: useStateMock,
          }));
          const { useLocalStorage: isolatedUseLocalStorage } =
            jest.requireActual('./use-local-storage');
          const [, setValue] = isolatedUseLocalStorage('key', 'initialValue');

          expect(() => setValue('newValue')).not.toThrow();
          expect(setStoredValue).toHaveBeenCalledWith('newValue');
        });
      } finally {
        jest.dontMock('react');
        jest.resetModules();
        Object.defineProperty(global, 'window', {
          value: originalWindow,
          writable: true,
          configurable: true,
        });
      }
    });
  });
});
