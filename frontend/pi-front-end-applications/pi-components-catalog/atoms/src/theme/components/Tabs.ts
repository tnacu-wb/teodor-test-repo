import { ComponentMultiStyleConfig } from '@chakra-ui/react';

const Tabs: ComponentMultiStyleConfig = {
  parts: ['tablist', 'tab', 'greyTabsGroup'],
  baseStyle: {
    tab: {
      _selected: {
        borderColor: 'primary',
        borderBottom: '2px solid var(--chakra-colors-primary)',
        fontWeight: 'semibold',
      },
      _focus: {
        boxShadow: 'none',
      },
      _blur: {
        background: 'none',
      },
    },
  },
  variants: {
    sm: {
      tablist: {
        w: 'full',
      },
      tab: {
        w: 'auto',
      },
    },
    tabsGroup: {
      tablist: {
        borderBottom: 'none',
        borderWidth: '0',
        h: '3.5rem',
        borderColor: 'baseWhite',
        outline: 'none',
      },
      tab: {
        p: 'md',
        minW: '7rem',
        borderBottom: 'none',
        borderColor: 'var(--chakra-colors-lightGrey2)',
        border: '1px solid var(--chakra-colors-lightGrey2)',
        _selected: {
          fontWeight: 'semibold',
          borderColor: 'var(--chakra-colors-primary)',
          backgroundColor: 'tabSelection',
          border: '1px solid var(--chakra-colors-primary)',
        },
        _focus: {
          boxShadow: 'none',
        },
      },
    },

    greyTabsGroup: {
      tablist: {
        bgColor: 'lightGrey2',
        h: '3.5rem',
        outline: 'none',
        alignItems: 'center',
      },

      tab: {
        bgColor: 'lightGrey4',
        boxSize: 'border-box',
        border: '1px solid transparent',
        borderLeft: 'none',
        borderRight: 'none',
        w: 'full',
        h: '3.375rem',
        pb: {
          mobile: 'var(--chakra-space-xs)',
          sm: 'var(--chakra-space-sm)',
          lg: 'var(--chakra-space-xs)',
        },
        px: 'var(--chakra-space-sm)',
        pt: 'var(--chakra-space-sm)',
        h4: {
          fontSize: { mobile: 'md', sm: 'xl' },
          fontWeight: 'medium',
          lineHeight: '3',
          color: 'darkGrey2',
        },
        h5: {
          fontWeight: 'normal',
          fontSize: { mobile: 'xs', sm: 'sm' },
          lineHeight: { mobile: '1', sm: '2' },
          color: 'darkGrey2',
        },
        _selected: {
          h: 'var(--chakra-space-4xl)',
          fontWeight: 'semibold',
          backgroundColor: 'baseWhite',
          border: '1px solid var(--chakra-colors-lightGrey2)',
          borderBottomWidth: '0',
          _first: {
            borderLeftWidth: '0',
          },
          _last: {
            borderRightWidth: '0',
          },
          h4: { color: 'darkGrey1' },
          h5: { color: 'darkGrey1' },
        },
        _focus: {
          boxShadow: 'none',
        },
      },
    },
  },
};

export default Tabs;
