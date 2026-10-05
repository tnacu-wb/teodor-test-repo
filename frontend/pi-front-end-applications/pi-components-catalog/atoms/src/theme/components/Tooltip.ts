import { ComponentStyleConfig } from '@chakra-ui/react';
import { StyleFunctionProps } from '@chakra-ui/theme-tools';

const Tooltip: ComponentStyleConfig = {
  baseStyle: {
    fontSize: 'sm',
    lineHeight: '2',
    borderRadius: '3',
    alignItems: 'center',
    color: 'baseWhite',
    _before: {
      content: '" "',
      position: 'absolute',
      top: '-0.25rem',
      left: '1.531rem',
      width: 'var(--chakra-space-xmd)',
      height: 'var(--chakra-space-xmd)',
      transform: 'rotate(-45deg)',
      bgColor: 'baseWhite',
    },
  },
  variants: {
    alert: {
      bgColor: 'alert',
      _before: {
        bgColor: 'alert',
      },
    },
    error: {
      bgColor: 'error',
      _before: {
        bgColor: 'error',
      },
    },
    info: {
      bgColor: 'info',
      _before: {
        bgColor: 'info',
      },
    },
    infoGrey: {
      bgColor: 'baseWhite',
      border: '1px solid var(--chakra-colors-lightGrey3)',
      color: 'darkGrey1',
      _before: {
        bgColor: 'baseWhite',
        borderRight: '1px solid var(--chakra-colors-lightGrey3)',
        borderTop: '1px solid var(--chakra-colors-lightGrey3)',
        top: '-6.6px',
      },
    },
    success: {
      bgColor: 'success',
      _before: {
        bgColor: 'success',
      },
    },
    inlineAlert: {
      bgColor: 'tooltipAlert',
      color: 'darkGrey1',
      _before: {
        bgColor: 'tooltipAlert',
      },
    },
    inlineError: {
      bgColor: 'tooltipError',
      color: 'darkGrey1',
      _before: {
        bgColor: 'tooltipError',
      },
    },
    inlineInfo: {
      bgColor: 'tooltipInfo',
      color: 'darkGrey1',
      _before: {
        bgColor: 'tooltipInfo',
      },
    },
    inlineSuccess: {
      bgColor: 'tooltipSuccess',
      color: 'darkGrey1',
      _before: {
        bgColor: 'tooltipSuccess',
      },
    },
    facilities: (props: StyleFunctionProps) => ({
      bgColor: 'baseWhite',
      border: `1px solid var(--chakra-colors-${props.colorScheme || 'infoBorder'})`,
      color: 'darkGrey1',
      _before: {
        bgColor: 'baseWhite',
        borderTop: `1px solid var(--chakra-colors-${props.colorScheme || 'infoBorder'})`,
        borderRight: `1px solid var(--chakra-colors-${props.colorScheme || 'infoBorder'})`,
        top: '-6.6px',
      },
    }),
    standard: {
      bgColor: 'info',
      _before: {
        bgColor: 'info',
      },
    },
    accessible: {
      zIndex: 1,
      bgColor: 'baseWhite',
      border: '1px solid var(--chakra-colors-lightGrey3)',
      color: 'darkGrey1',
      _before: {
        bgColor: 'baseWhite',
        borderRight: '1px solid var(--chakra-colors-lightGrey3)',
        borderTop: '1px solid var(--chakra-colors-lightGrey3)',
        top: '-6.6px',
      },
    },
  },
};

export default Tooltip;
