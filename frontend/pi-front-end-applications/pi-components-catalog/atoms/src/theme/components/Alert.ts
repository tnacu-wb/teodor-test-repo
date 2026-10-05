import { ComponentMultiStyleConfig } from '@chakra-ui/react';

const Alert: ComponentMultiStyleConfig = {
  parts: ['title', 'description', 'container'],
  baseStyle: {
    title: {
      color: 'darkGrey1',
      lineHeight: '2',
      fontWeight: 'semibold',
      marginLeft: 'sm',
    },
    description: {
      color: 'darkGrey1',
      lineHeight: '2',
      fontWeight: 'normal',
      marginLeft: 'sm',
    },
    container: {
      borderRadius: '3px',
      fontSize: 'sm',
    },
  },
  variants: {
    alert: {
      container: {
        bgColor: 'tooltipAlert',
        border: '1px solid var(--chakra-colors-alertBorderTint)',
      },
    },
    error: {
      container: {
        bgColor: 'tooltipError',
        border: '1px solid var(--chakra-colors-errorBorderTint)',
      },
    },
    info: {
      container: {
        bgColor: 'tooltipInfo',
        border: '1px solid var(--chakra-colors-infoBorderTint)',
      },
    },
    infoGrey: {
      container: {
        bgColor: 'baseWhite',
        border: '1px solid var(--chakra-colors-lightGrey3)',
      },
    },
    success: {
      container: {
        bgColor: 'tooltipSuccess',
        border: '1px solid var(--chakra-colors-successBorderTint)',
      },
    },
    accessible: {
      container: {
        bgColor: 'baseWhite',
        border: '1px solid var(--chakra-colors-infoBorder)',
      },
    },
  },
};

export default Alert;
