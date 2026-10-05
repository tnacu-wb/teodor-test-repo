import { analytics } from '@whitbread-eos/utils';
import { createContext, ReactNode, useContext } from 'react';
import { useImmerReducer } from 'use-immer';

/* README:
  - Reducer + Context Pattern: https://beta.reactjs.org/learn/scaling-up-with-reducer-and-context
  - Immer: https://immerjs.github.io/immer/example-setstate#useimmerreducer
*/

export interface AppData {
  screenSize: ScreenSize;
  orientation: Orientation;
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

const defaultInitialAppData = {
  screenSize: undefined,
  orientation: undefined,
};

type AppDataAction =
  | { type: 'exampleDomain/exampleAction'; payload?: any }
  | { type: 'ui/screenSize'; payload: ScreenSize }
  | { type: 'ui/orientation'; payload: Orientation };

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

function setAnalyticsBreakpoint(value: ScreenSize) {
  switch (value) {
    case 'xs':
      return 'Extra small';
    case 'sm':
      return 'Small';
    case 'md':
      return 'Medium';
    case 'lg':
      return 'Large';
    case 'xl':
      return 'Extra Large';
    default:
      return '';
  }
}

function appDataReducer(appData: AppData, action: AppDataAction) {
  switch (action.type) {
    case 'ui/screenSize': {
      appData.screenSize = action.payload;
      analytics.update({
        breakpoint: setAnalyticsBreakpoint(action.payload),
      });
      break;
    }
    case 'ui/orientation': {
      appData.orientation = action.payload;
      break;
    }
    default: {
      throw Error('Unknown action: ' + action.type);
    }
  }
}
