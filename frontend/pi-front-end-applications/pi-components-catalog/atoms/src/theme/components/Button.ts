import type { ComponentStyleConfig } from '@chakra-ui/theme';

const primary = {
  color: 'baseWhite',
  bgColor: 'primary',
  borderColor: 'primary',
  bgPosition: 'center',
  transition: 'background 0.8s',
  fontSize: 'var(--chakra-fontSizes-lg)',
  _disabled: {
    bgColor: 'lightGrey3',
    borderColor: 'lightGrey3',
    color: 'darkGrey1',
    opacity: '1',
  },
  _hover: {
    bg: 'var(--chakra-colors-btnPrimaryHoverBg) radial-gradient(circle, transparent 1%, var(--chakra-colors-btnPrimaryHoverBg) 1%) center/15000%',
    boxShadow: '0 4px 8px var(--chakra-colors-lightGrey1)',
    _disabled: {
      bg: 'lightGrey3',
    },
  },
  _active: {
    bgColor: 'primary',
    bgSize: '100%',
    transition: 'background 0s',
  },
  _focus: {
    boxShadow: 'none',
    bgColor: 'primary',
  },
  _focusVisible: {
    outline: '3px solid var(--chakra-colors-accessibleFocus)',
    outlineOffset: '2px',
    bgColor: 'primary',
  },
};
const Button: ComponentStyleConfig = {
  baseStyle: {
    borderRadius: 4,
    boxSizing: 'border-box',
    lineHeight: '3',
    fontWeight: '600',
    _hover: {
      _disabled: {
        boxShadow: 'none',
      },
    },
  },
  sizes: {
    xxs: {
      width: '24px',
      height: '24px',
    },
    xs: {
      width: '38px',
      height: '38px',
    },
    xsm: {
      height: '40px',
      width: '160px',
    },
    sm: {
      height: '40px',
      width: '181px',
    },
    md: {
      height: '56px',
      width: '288px',
    },
    full: {
      height: '56px',
      width: 'full',
    },
  },
  variants: {
    primary,
    secondary: {
      color: 'baseWhite',
      bgColor: 'btnSecondaryEnabled',
      borderColor: 'btnSecondaryEnabled',
      bgPosition: 'center',
      transition: 'background 0.8s',
      fontSize: 'var(--chakra-fontSizes-lg)',
      _disabled: {
        bgColor: 'lightGrey3',
        borderColor: 'lightGrey3',
        color: 'darkGrey1',
        opacity: '1',
      },
      _hover: {
        boxShadow: '0 4px 8px var(--chakra-colors-lightGrey1)',
        bg: 'var(--chakra-colors-btnSecondaryHoverBg) radial-gradient(circle, transparent 1%, var(--chakra-colors-btnSecondaryHoverBg) 1%) center/15000%',
        _disabled: {
          bg: 'lightGrey3',
        },
      },
      _active: {
        bgColor: 'btnSecondaryEnabled',
        bgSize: '100%',
        transition: 'background 0s',
      },
      _focus: {
        boxShadow: 'none',
      },
      _focusVisible: {
        outline: '3px solid var(--chakra-colors-accessibleFocus)',
        outlineOffset: '2px',
      },
    },
    tertiary: {
      bgColor: 'baseWhite',
      borderWidth: '1px',
      borderStyle: 'solid',
      borderColor: 'btnSecondaryEnabled',
      color: 'btnSecondaryEnabled',
      bgPosition: 'center',
      transition: 'all 0.8s',
      fontSize: 'var(--chakra-fontSizes-lg)',
      _disabled: {
        borderColor: 'lightGrey2',
        color: 'darkGrey1',
        opacity: '1',
      },
      _active: {
        bgSize: '100%',
        transition: 'all 0s',
        bgColor: 'baseWhite',
      },
      _hover: {
        boxShadow: '0 4px 8px var(--chakra-colors-lightGrey2)',
        bg: 'var(--chakra-colors-baseWhite) radial-gradient(circle, rgba(81, 30, 98, .15) 1%, var(--chakra-colors-baseWhite) 1%) center/15000%',
      },
      _focus: {
        boxShadow: 'none',
      },
      _focusVisible: {
        outline: '3px solid var(--chakra-colors-accessibleFocus)',
        outlineOffset: '2px',
      },
    },
    circle: {
      color: 'baseWhite',
      bgColor: 'transparent',
      borderRadius: 'full',
      cursor: 'pointer',
      border: '1px solid var(--chakra-colors-primary)',
      _disabled: {
        border: '1px solid var(--chakra-colors-lightGrey2)',
        color: 'grey',
        _active: {
          bgColor: 'transparent',
        },
      },

      _hover: {
        bgColor: 'transparent',
        border: '2px solid var(--chakra-colors-primary)',
        _disabled: {
          border: '2px solid var(--chakra-colors-lightGrey2)',
        },
      },

      _focus: {
        boxShadow: 'none',
      },
      _focusVisible: {
        outline: '3px solid var(--chakra-colors-accessibleFocus)',
        outlineOffset: '2px',
      },
      _active: {
        bgColor: 'var(--chakra-colors-primary)',
      },
    },
    default: {
      color: 'baseWhite',
      bgColor: 'lightGrey2',
      borderColor: 'lightGrey2',
    },
    generic: {
      bgColor: 'baseWhite',
      border: '1px solid',
      borderColor: 'darkGrey2',
      color: 'darkGrey1',
    },
    genericSecondary: {
      bgColor: 'baseWhite',
      border: '1px solid var(--chakra-colors-lightGrey2)',
      color: 'darkGrey1',
      fontWeight: 'medium',
      fontSize: 'md',
      _hover: {
        border: '1px solid var(--chakra-colors-primary)',
      },
      _focus: {
        boxShadow: 'none',
      },
      _focusVisible: {
        outline: '3px solid var(--chakra-colors-accessibleFocus)',
        outlineOffset: '2px',
      },
    },
    marketingDefault: {
      bgColor: 'baseWhite',
      color: 'darkGrey1',
      border: '2px solid var(--chakra-colors-darkGrey2)',
      _hover: {
        boxShadow: '0 2px 4px var(--chakra-colors-lightGrey2)',
        textDecoration: 'underline',
      },
    },
    marketingSolid: {
      bgColor: 'darkGrey2',
      color: 'baseWhite',
      border: '2px solid var(--chakra-colors-baseBlack)',
      _hover: {
        boxShadow: '0 2px 4px var(--chakra-colors-lightGrey2)',
        textDecoration: 'underline',
      },
    },
    unbrandedRestaurant: {
      borderRadius: '0px',
      borderColor: '#511E62',
      bg: '#511E62',
      color: '#ffffff',
      border: '2px solid',
      fontSize: 'var(--chakra-fontSizes-lg)',
      fontWeight: '600',
      _hover: {
        bg: '#3d1649',
        borderColor: '#3d1649',
        _disabled: {
          bg: 'lightGrey3',
        },
      },
      _focus: {
        boxShadow: '0 0 8px rgba(81, 30, 98, 0.5)',
        bgColor: '#511E62',
        color: '#fff',
      },
      _active: {
        bgColor: '#2d0f35',
        borderColor: '#2d0f35',
      },
    },
    login: {
      ...primary,
      _focusVisible: {
        ...primary._focusVisible,
        outline: '3px solid var(--chakra-colors-accessibleFocus)',
      },
    },
  },
  defaultProps: {
    variant: 'primary',
    size: 'md',
  },
};

export default Button;
