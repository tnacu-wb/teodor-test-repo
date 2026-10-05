import { ComponentStyleConfig } from '@chakra-ui/react';

const Drawer: ComponentStyleConfig = {
  parts: ['overlay', 'dialogContainer', 'header', 'dialog', 'closeButton', 'body'],
  baseStyle: {
    overlay: {
      bottom: '0',
      h: 'auto',
      position: 'absolute',
      top: '6xl',
      zIndex: 1009,
    },
    header: {
      fontWeight: '700',
      color: 'var(--chakra-colors-darkGrey1)',
    },
    dialogContainer: {
      justifyContent: 'right',
      height: '100%',
      position: 'relative',
      zIndex: 1009,
    },
    dialog: {
      bottom: '0 !important',
      boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
      h: 'calc(100vh - 72px)',
      paddingBottom: '32px',
      paddingLeft: '4px',
      paddingRight: '4px',
      paddingTop: '32px',
      position: 'absolute !important',
      zIndex: 1009,
      width: '32rem !important',
    },
    closeButton: {
      color: 'var(--chakra-colors-darkGrey1)',
      right: 'xl',
      top: '3xl',
    },
    body: {
      marginTop: '16px',
    },
  },
};

export default Drawer;
