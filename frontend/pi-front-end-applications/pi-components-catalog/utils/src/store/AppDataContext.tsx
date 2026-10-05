'use client';

import { createContext, ReactNode, useContext } from 'react';
import { useImmerReducer } from 'use-immer';

/* README:
  - Reducer + Context Pattern: https://beta.reactjs.org/learn/scaling-up-with-reducer-and-context
  - Immer: https://immerjs.github.io/immer/example-setstate#useimmerreducer
*/

export interface AppData {
  screenSize: ScreenSize;
  orientation: Orientation;
  selectedLocation?: LocationName;
  selectedSubLocation?: LocationName;
  restaurantBrandName?: BrandName;
  occasionId?: string;
  siteId?: string;
  restaurantBrandNameForMarketing?: BrandName;
}

type ScreenSize =
  | 'base' // < 320ppx
  | 'mobile' // < 375px
  | 'xs' // < 576px
  | 'sm' // < 768px
  | 'md' // < 1280px
  | 'lg' // < 1440px
  | 'xl' // >= 1440px
  | undefined;

type Orientation = 'portrait' | 'landscape' | undefined;
type LocationName = string | undefined;

const defaultInitialAppData = {
  screenSize: undefined,
  orientation: undefined,
  selectedLocation: undefined,
  selectedSubLocation: undefined,
  restaurantBrandName: undefined,
  occasionId: '',
  siteId: '',
};

type BrandName = undefined | string;

type AppDataAction =
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  | { type: 'exampleDomain/exampleAction'; payload?: any }
  | { type: 'ui/screenSize'; payload: ScreenSize }
  | { type: 'ui/orientation'; payload: Orientation }
  | { type: 'changeRestaurantNameForMarketing'; payload: BrandName }
  | { type: 'changeRestaurantName'; payload: BrandName }
  | { type: 'changeOccasionId'; payload: string }
  | { type: 'changeSiteId'; payload: string };

const AppDataContext = createContext<AppData | null>(null);
const AppDataDispatchContext = createContext<React.Dispatch<AppDataAction>>(() => null);

interface AppDataProviderProps {
  children: ReactNode;
  initialAppData?: AppData;
}

export function AppDataProvider({ children, initialAppData }: Readonly<AppDataProviderProps>) {
  const [appData, dispatch] = useImmerReducer(
    appDataReducer,
    initialAppData ?? defaultInitialAppData
  );

  return (
    <AppDataContext.Provider value={appData}>
      <AppDataDispatchContext.Provider value={dispatch}>{children}</AppDataDispatchContext.Provider>
    </AppDataContext.Provider>
  );
}

export function useAppData() {
  return useContext(AppDataContext);
}

export function useAppDataDispatch() {
  return useContext(AppDataDispatchContext);
}

function appDataReducer(appData: AppData, action: AppDataAction) {
  switch (action.type) {
    case 'ui/screenSize': {
      appData.screenSize = action.payload;
      break;
    }
    case 'ui/orientation': {
      appData.orientation = action.payload;
      break;
    }
    case 'changeRestaurantName': {
      appData.restaurantBrandName = action.payload;
      break;
    }
    case 'changeRestaurantNameForMarketing': {
      appData.restaurantBrandNameForMarketing = action.payload;
      break;
    }
    case 'changeOccasionId': {
      appData.occasionId = action.payload;
      break;
    }
    case 'changeSiteId': {
      appData.siteId = action.payload;
      break;
    }
    default: {
      throw Error('Unknown action: ' + action.type);
    }
  }
}
