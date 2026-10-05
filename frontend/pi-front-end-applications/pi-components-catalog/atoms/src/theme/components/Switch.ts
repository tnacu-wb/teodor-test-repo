import { ComponentStyleConfig } from '@chakra-ui/react';

const Switch: ComponentStyleConfig = {
  baseStyle: {
    track: {
      _checked: {
        bg: 'var(--chakra-colors-primary)',
        boxShadow: '0px 0px 8px var(--chakra-colors-btnPrimaryFocusBoxShadow)',
      },
    },
  },
  variants: {
    focusless: {
      track: {
        _focus: {
          boxShadow: 'none',
        },
      },
    },
  },
};

export default Switch;
