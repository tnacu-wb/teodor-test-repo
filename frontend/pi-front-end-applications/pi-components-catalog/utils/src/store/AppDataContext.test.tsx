import '@testing-library/jest-dom';
import { renderHook, act } from '@testing-library/react';

import { AppDataProvider, useAppData, useAppDataDispatch } from './AppDataContext';

describe('AppDataContext', () => {
  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <AppDataProvider>{children}</AppDataProvider>
  );

  const wrapperWithInitialData = (initialAppData: any) => {
    const Wrapper = ({ children }: { children: React.ReactNode }) => (
      <AppDataProvider initialAppData={initialAppData}>{children}</AppDataProvider>
    );
    Wrapper.displayName = 'WrapperWithInitialData';
    return Wrapper;
  };

  describe('changeRestaurantName action', () => {
    it('should update restaurantBrandName when changeRestaurantName is dispatched', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      expect(result.current.appData?.restaurantBrandName).toBeUndefined();

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantName',
          payload: 'beefeater',
        });
      });

      expect(result.current.appData?.restaurantBrandName).toBe('beefeater');
    });

    it('should update restaurantBrandName to different brand names', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantName',
          payload: 'table-table',
        });
      });

      expect(result.current.appData?.restaurantBrandName).toBe('table-table');

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantName',
          payload: 'brewers-fayre',
        });
      });

      expect(result.current.appData?.restaurantBrandName).toBe('brewers-fayre');
    });

    it('should handle undefined restaurantBrandName', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        {
          wrapper: wrapperWithInitialData({
            screenSize: 'md',
            orientation: 'landscape',
            restaurantBrandName: 'beefeater',
          }),
        }
      );

      expect(result.current.appData?.restaurantBrandName).toBe('beefeater');

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantName',
          payload: undefined,
        });
      });

      expect(result.current.appData?.restaurantBrandName).toBeUndefined();
    });
  });

  describe('changeRestaurantNameForMarketing action', () => {
    it('should update restaurantBrandNameForMarketing when changeRestaurantNameForMarketing is dispatched', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      expect(result.current.appData?.restaurantBrandNameForMarketing).toBeUndefined();

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantNameForMarketing',
          payload: 'Beefeater',
        });
      });

      expect(result.current.appData?.restaurantBrandNameForMarketing).toBe('Beefeater');
    });

    it('should update restaurantBrandNameForMarketing to different brand names', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantNameForMarketing',
          payload: 'Table Table',
        });
      });

      expect(result.current.appData?.restaurantBrandNameForMarketing).toBe('Table Table');

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantNameForMarketing',
          payload: 'Brewers Fayre',
        });
      });

      expect(result.current.appData?.restaurantBrandNameForMarketing).toBe('Brewers Fayre');
    });

    it('should handle undefined restaurantBrandNameForMarketing', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        {
          wrapper: wrapperWithInitialData({
            screenSize: 'md',
            orientation: 'landscape',
            restaurantBrandNameForMarketing: 'Beefeater',
          }),
        }
      );

      expect(result.current.appData?.restaurantBrandNameForMarketing).toBe('Beefeater');

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantNameForMarketing',
          payload: undefined,
        });
      });

      expect(result.current.appData?.restaurantBrandNameForMarketing).toBeUndefined();
    });
  });

  describe('changeOccasionId action', () => {
    it('should update occasionId when changeOccasionId is dispatched', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      expect(result.current.appData?.occasionId).toBe('');

      act(() => {
        result.current.dispatch({
          type: 'changeOccasionId',
          payload: 'occasion-123',
        });
      });

      expect(result.current.appData?.occasionId).toBe('occasion-123');
    });

    it('should update occasionId to different values', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      act(() => {
        result.current.dispatch({
          type: 'changeOccasionId',
          payload: 'birthday-occasion',
        });
      });

      expect(result.current.appData?.occasionId).toBe('birthday-occasion');

      act(() => {
        result.current.dispatch({
          type: 'changeOccasionId',
          payload: 'anniversary-occasion',
        });
      });

      expect(result.current.appData?.occasionId).toBe('anniversary-occasion');
    });

    it('should handle empty string occasionId', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        {
          wrapper: wrapperWithInitialData({
            screenSize: 'md',
            orientation: 'landscape',
            occasionId: 'occasion-456',
          }),
        }
      );

      expect(result.current.appData?.occasionId).toBe('occasion-456');

      act(() => {
        result.current.dispatch({
          type: 'changeOccasionId',
          payload: '',
        });
      });

      expect(result.current.appData?.occasionId).toBe('');
    });
  });

  describe('changeSiteId action', () => {
    it('should update siteId when changeSiteId is dispatched', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      expect(result.current.appData?.siteId).toBe('');

      act(() => {
        result.current.dispatch({
          type: 'changeSiteId',
          payload: 'site-123',
        });
      });

      expect(result.current.appData?.siteId).toBe('site-123');
    });

    it('should update siteId to different values', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      act(() => {
        result.current.dispatch({
          type: 'changeSiteId',
          payload: 'london-site',
        });
      });

      expect(result.current.appData?.siteId).toBe('london-site');

      act(() => {
        result.current.dispatch({
          type: 'changeSiteId',
          payload: 'manchester-site',
        });
      });

      expect(result.current.appData?.siteId).toBe('manchester-site');
    });

    it('should handle empty string siteId', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        {
          wrapper: wrapperWithInitialData({
            screenSize: 'md',
            orientation: 'landscape',
            siteId: 'site-789',
          }),
        }
      );

      expect(result.current.appData?.siteId).toBe('site-789');

      act(() => {
        result.current.dispatch({
          type: 'changeSiteId',
          payload: '',
        });
      });

      expect(result.current.appData?.siteId).toBe('');
    });
  });

  describe('additional switch cases', () => {
    it('should update screenSize when ui/screenSize is dispatched', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      act(() => {
        result.current.dispatch({
          type: 'ui/screenSize',
          payload: 'md',
        });
      });

      expect(result.current.appData?.screenSize).toBe('md');
    });

    it('should update orientation when ui/orientation is dispatched', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      act(() => {
        result.current.dispatch({
          type: 'ui/orientation',
          payload: 'landscape',
        });
      });

      expect(result.current.appData?.orientation).toBe('landscape');
    });
  });

  describe('provider initialization', () => {
    it('should initialize with default values when no initialAppData is provided', () => {
      const { result } = renderHook(() => useAppData(), { wrapper });

      expect(result.current).toEqual({
        screenSize: undefined,
        orientation: undefined,
        selectedLocation: undefined,
        selectedSubLocation: undefined,
        restaurantBrandName: undefined,
        occasionId: '',
        siteId: '',
      });
    });

    it('should initialize with custom initialAppData when provided', () => {
      const customInitialData = {
        screenSize: 'lg' as const,
        orientation: 'portrait' as const,
        selectedLocation: 'london',
        selectedSubLocation: 'central',
        restaurantBrandName: 'beefeater',
        occasionId: 'custom-occasion',
        siteId: 'custom-site',
        restaurantBrandNameForMarketing: 'Beefeater',
      };

      const { result } = renderHook(() => useAppData(), {
        wrapper: wrapperWithInitialData(customInitialData),
      });

      expect(result.current).toEqual(customInitialData);
    });
  });

  describe('multiple actions in sequence', () => {
    it('should handle multiple restaurant-related actions correctly', () => {
      const { result } = renderHook(
        () => ({
          appData: useAppData(),
          dispatch: useAppDataDispatch(),
        }),
        { wrapper }
      );

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantName',
          payload: 'beefeater',
        });
      });

      act(() => {
        result.current.dispatch({
          type: 'changeRestaurantNameForMarketing',
          payload: 'Beefeater',
        });
      });

      act(() => {
        result.current.dispatch({
          type: 'changeOccasionId',
          payload: 'occasion-123',
        });
      });

      act(() => {
        result.current.dispatch({
          type: 'changeSiteId',
          payload: 'site-456',
        });
      });

      expect(result.current.appData).toMatchObject({
        restaurantBrandName: 'beefeater',
        restaurantBrandNameForMarketing: 'Beefeater',
        occasionId: 'occasion-123',
        siteId: 'site-456',
      });
    });
  });
});
