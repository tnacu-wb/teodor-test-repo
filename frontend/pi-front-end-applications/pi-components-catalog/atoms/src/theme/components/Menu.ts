import type { ComponentStyleConfig } from '@chakra-ui/theme';

const Menu: ComponentStyleConfig = {
  parts: ['button', 'list', 'item'],
  baseStyle: {
    button: {
      width: 'full',
      height: 'var(--chakra-space-4xl)',
      borderRadius: '4px',
      boxSizing: 'border-box',
      color: 'var(--chakra-colors-darkGrey2)',
      bgColor: 'var(--chakra-colors-baseWhite)',
      textAlign: 'left',
      padding: 'md',
      fontSize: 'var(--chakra-fontSizes-md)',
      lineHeight: 'var(--chakra-lineHeights-3)',
    },
    list: {
      overflowY: 'scroll',
      position: 'relative',
      top: '0',
      px: '0',
      minW: 'var(--chakra-space-full)',
      width: 'var(--chakra-space-full)',
      borderRadius: 'var(--chakra-space-xs)',
      borderWidth: '2px',
      borderColor: 'var(--chakra-colors-lightGrey3)',
      boxShadow: '0 2px 12px var(--chakra-colors-lightGrey2)',
    },
    item: {
      height: 'var(--chakra-space-2xl)',
      padding: '12px var(--chakra-space-md)',
      color: 'var(--chakra-colors-darkGrey1)',
      fontSize: 'var(--chakra-fontSizes-sm)',
      lineHeight: 'var(--chakra-lineHeights-1)',
      _active: {
        bgColor: 'var(--chakra-colors-lightGrey5)',
      },
      _hover: {
        bgColor: 'var(--chakra-colors-lightGrey5)',
      },
      _focus: {
        bgColor: 'var(--chakra-colors-lightGrey5)',
      },
    },
  },
  sizes: {},
  variants: {
    default: {
      button: {
        border: '1px solid var(--chakra-colors-darkGrey1)',
        color: 'var(--chakra-colors-darkGrey1)',
        _active: {
          borderColor: 'var(--chakra-colors-primary)',
          borderWidth: '2px',
        },
        _focus: {
          borderColor: 'var(--chakra-colors-primary)',
          borderWidth: '2px',
        },
        _disabled: {
          border: '1px solid var(--chakra-colors-lightGrey2)',
          color: 'var(--chakra-colors-lightGrey2)',
        },
      },
    },
    error: {
      button: {
        border: '2px solid var(--chakra-colors-error)',
      },
    },
  },
  defaultProps: {
    variant: 'default',
  },
};
export default Menu;
