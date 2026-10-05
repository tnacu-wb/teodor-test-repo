import { extendTheme } from '@chakra-ui/react';
import { theme as CommonUITheme } from '@whitbread-eos/atoms';

const appTheme = {
  styles: {
    global: {
      '.formatLinks a': {
        color: 'btnSecondaryEnabled',
        textDecoration: 'underline',
        '&:hover': {
          cursor: 'pointer',
          textDecoration: 'none',
        },
      },
      '[data-testid="hdp_mobileBasketBookNowButton"] , [data-testid="hdp_mobileBasketChooseRoomTypeButton"]':
        {
          marginBottom: '50px',
        },
    },
  },
};

// use the theme exported by the Common UI library as a base
// and overwrite whatever is needed with the settings from appTheme
export const theme = extendTheme(CommonUITheme, appTheme);