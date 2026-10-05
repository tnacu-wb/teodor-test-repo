import '@testing-library/jest-dom';
import { act, renderHook } from '@testing-library/react';
import { useEffect } from 'react';

import { AppData, AppDataProvider } from '../store/AppDataContext';
import { render } from '../utils/test-utils';
import { useSessionStorage } from './use-session-storage';

interface Props {
  isValueChanged?: boolean;
  isValueFunction?: boolean;
}

function Value({ isValueChanged, isValueFunction }: Props) {
  const [value, setValue] = useSessionStorage('key', 'initial value');

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

describe('use-session-storage hooks', () => {
  it('should display initial session storage value', () => {
    const { getByText } = render(
      <AppDataProvider initialAppData={initialAppData}>
        <Value />
      </AppDataProvider>
    );
    expect(getByText('Value: initial value')).toBeInTheDocument();
  });

  it('should display changed session storage value', () => {
    const { getByText } = render(
      <AppDataProvider initialAppData={initialAppData}>
        <Value isValueChanged />
      </AppDataProvider>
    );
    expect(getByText('Value: value changed')).toBeInTheDocument();
  });

  it('should display changed session storage value in case value is passed as function', () => {
    const { getByText } = render(
      <AppDataProvider initialAppData={initialAppData}>
        <Value isValueChanged isValueFunction />
      </AppDataProvider>
    );
    expect(getByText('Value: value changed')).toBeInTheDocument();
  });

  describe('useSessionStorage', () => {
    const sessionStorageMock = (() => {
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

    Object.defineProperty(window, 'sessionStorage', { value: sessionStorageMock });
    const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();

    afterEach(() => {
      sessionStorage.clear();
      consoleLogSpy.mockClear();
    });

    afterAll(() => {
      consoleLogSpy.mockRestore();
    });

    it('should read the initial value from sessionStorage', () => {
      sessionStorage.setItem('key', JSON.stringify('fromSessionStorage'));
      const { result } = renderHook(() => useSessionStorage('key', 'initialValue'));
      const [storedValue] = result.current;

      expect(storedValue).toBe('fromSessionStorage');
    });

    it('should use the initial value when sessionStorage is empty', () => {
      sessionStorage.clear();
      const { result } = renderHook(() => useSessionStorage('key', 'initialValue'));
      const [storedValue] = result.current;

      expect(storedValue).toBe('initialValue');
    });

    it('should update the value in sessionStorage when the state updates', () => {
      const { result } = renderHook(() => useSessionStorage('key', 'initialValue'));
      const [, setValue] = result.current;

      act(() => {
        setValue('newValue');
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      expect(JSON.parse(sessionStorage.getItem('key')!)).toBe('newValue');
    });

    it('should handle JSON parse errors gracefully and return initial value', () => {
      sessionStorage.setItem('key', 'invalid json {]');
      const { result } = renderHook(() => useSessionStorage('key', 'initialValue'));
      const [storedValue] = result.current;

      expect(storedValue).toBe('initialValue');
      expect(consoleLogSpy).toHaveBeenCalled();
    });

    it('should handle setItem errors gracefully', () => {
      const { result } = renderHook(() => useSessionStorage('key', 'initialValue'));
      const [, setValue] = result.current;

      const setItemSpy = jest.spyOn(window.sessionStorage, 'setItem').mockImplementation(() => {
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
      const { result } = renderHook(() => useSessionStorage('key', { name: 'John', age: 0 }));
      const [, setValue] = result.current;

      act(() => {
        setValue({ name: 'Jane', age: 30 });
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const stored = JSON.parse(sessionStorage.getItem('key')!);
      expect(stored).toEqual({ name: 'Jane', age: 30 });
    });

    it('should work with arrays', () => {
      const { result } = renderHook(() => useSessionStorage('key', [1, 2, 3]));
      const [, setValue] = result.current;

      act(() => {
        setValue([4, 5, 6]);
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const stored = JSON.parse(sessionStorage.getItem('key')!);
      expect(stored).toEqual([4, 5, 6]);
    });

    it('should handle function-based state updates correctly', () => {
      const { result } = renderHook(() => useSessionStorage('counter', 0));
      const [, setValue] = result.current;

      act(() => {
        setValue((prev) => prev + 1);
      });

      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      expect(JSON.parse(sessionStorage.getItem('counter')!)).toBe(1);
    });

    it('should return a const tuple for readonly access pattern', () => {
      const { result } = renderHook(() => useSessionStorage('key', 'value'));
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
          const { useSessionStorage: isolatedUseSessionStorage } =
            jest.requireActual('./use-session-storage');
          const [storedValue] = isolatedUseSessionStorage('key', 'initialValue');
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
          const { useSessionStorage: isolatedUseSessionStorage } =
            jest.requireActual('./use-session-storage');
          const [, setValue] = isolatedUseSessionStorage('key', 'initialValue');

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
