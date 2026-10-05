import { useMediaQuery } from '@chakra-ui/react';
import { useEffect } from 'react';

import { useAppData, useAppDataDispatch } from '~store/AppDataContext';

export default function useSetOrientation() {
  const dispatch = useAppDataDispatch();
  const [portrait, landscape] = useMediaQuery([
    '(orientation: portrait)',
    '(orientation: landscape)',
  ]);

  useEffect(() => {
    switch (true) {
      case portrait:
        dispatch({ type: 'ui/orientation', payload: 'portrait' });
        break;
      case landscape:
        dispatch({ type: 'ui/orientation', payload: 'landscape' });
    }
  }, [portrait, landscape, dispatch]);
}

export function useOrientation() {
  const orientations = {
    isPortrait: undefined,
    isLandscape: undefined,
  };

  const appData = useAppData();
  switch (appData?.orientation) {
    case 'portrait':
      return {
        isPortrait: true,
        isLandscape: false,
      };
    case 'landscape':
      return {
        isPortrait: false,
        isLandscape: true,
      };
    default:
      return orientations;
  }
}
