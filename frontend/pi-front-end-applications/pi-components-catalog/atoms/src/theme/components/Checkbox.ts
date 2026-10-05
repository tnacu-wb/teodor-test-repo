import { ComponentMultiStyleConfig } from '@chakra-ui/react';

const Checkbox: ComponentMultiStyleConfig = {
  parts: ['control', 'icon'],
  baseStyle: {
    icon: {
      color: 'var(--chakra-colors-baseWhite)',
      _active: {
        color: 'var(--chakra-colors-baseWhite)',
      },
      _checked: {
        color: 'var(--chakra-colors-baseWhite)',
      },
    },

    control: {
      width: '1.25rem',
      minHeight: '1.25rem',
      _focus: {
        boxShadow: 'none',
      },
      _focusVisible: {
        outline: '3px solid var(--chakra-colors-primary)',
        outlineOffset: '2px',
      },
      border: '1px',
      borderColor: 'var(--chakra-colors-lightGrey1)',
      borderRadius: 'none',
      _disabled: {
        borderColor: 'var(--chakra-colors-lightGrey2)',
        bg: 'var(--chakra-colors-lightGrey4)',
      },
      _checked: {
        bg: 'primary',
        color: 'baseWhite',
      },
      _active: {
        bgColor: 'var(--chakra-colors-primary)',
        borderColor: 'var(--chakra-colors-primary)',
        borderRadius: '1px',
        _disabled: {
          borderColor: 'var(--chakra-colors-lightGrey2)',
          bg: 'var(--chakra-colors-lightGrey4)',
          borderRadius: '0',
        },
      },
    },
  },
};

export default Checkbox;
