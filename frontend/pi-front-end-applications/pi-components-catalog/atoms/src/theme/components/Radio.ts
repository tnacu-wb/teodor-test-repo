import { ComponentMultiStyleConfig } from '@chakra-ui/react';

const Radio: ComponentMultiStyleConfig = {
  parts: ['label', 'control'],
  baseStyle: {
    control: {
      marginTop: 'xs',
      borderColor: 'var(--chakra-colors-lightGrey1)',
      borderRadius: 'full',
      _hover: {
        bg: 'unset',
      },
      _focus: {
        outline: '0 none',
        boxShadow: 'none',
      },
      _checked: {
        bg: 'baseWhite',
        borderColor: 'var(--chakra-colors-primary)',
        _focus: {
          outline: 'none',
        },
        _hover: {
          bg: 'unset',
        },
        _before: {
          bg: 'primary',
          width: '65%',
          height: '65%',
        },
      },
    },
  },
  variants: {
    payment: {
      control: {
        height: 'var(--chakra-sizes-5)',
        width: 'var(--chakra-sizes-5)',
      },
    },
    white: {
      control: {
        marginTop: '0',
        _checked: {
          bg: 'transparent',
          borderColor: 'var(--chakra-colors-baseWhite)',
          _focus: {
            outline: 'none',
          },
          _hover: {
            bg: 'unset',
          },
          _before: {
            bg: 'baseWhite',
            width: '65%',
            height: '65%',
          },
        },
      },
    },
    softBundle: {
      control: {
        marginTop: '0',
        _checked: {
          bg: 'transparent',
          borderColor: 'var(--chakra-colors-baseWhite)',
          _focus: {
            outline: 'none',
          },
          _hover: {
            bg: 'unset',
          },
          _before: {
            bg: 'baseWhite',
            width: '10px',
            height: '10px',
          },
        },
      },
    },
  },
};

export default Radio;
