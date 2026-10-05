import { BoxProps, ButtonProps, FlexProps, ModalContentProps } from '@chakra-ui/react';

export const wrapperStyles = {
  alignItems: 'center',
  justifyContent: 'center',
  direction: 'column',
  width: { lg: '24.375rem' },
  maxWidth: '24.375rem',
  pt: 0,
} as BoxProps;

export const descriptionStyle = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: 'normal',
};
export const buttonsStyle = {
  display: 'flex',
  flexFlow: 'row',
  height: 'auto',
  justifyContent: 'space-between',
  alignContent: 'baseline',
  flexDirection: 'row',
  width: '100%',
  mt: 'md',
  pb: 'md',
} as FlexProps;

export const allowButtonGB = {
  width: '186px',
};

export const allowButtonDE = {
  width: {
    mobile: '120px',
    xs: '186px',
  },
};

export const modalBodyStyle = {
  overflow: { mobile: 'hidden', sm: 'auto' },
  pt: 'inherit',
  width: '100%',
};

export const modalBodyBoxStyle = {
  maxHeight: { sm: '66vh' },
};

export const modalContentStyle = {
  maxW: 'auto',
  maxH: 'auto',
  overflow: 'auto',
  height: { sm: 'auto' },
  my: 0,
  borderRadius: 'var(--xs, 0.25rem)',
  boxShadow: ' 0rem 0.25rem 0.5rem 0rem rgba(0, 0, 0, 0.35)',
  border: ' 1px solid var(--neutral-300-light-grey-4, #E0E0E0)',
  px: 0,

  position: 'fixed',
  bottom: { lg: 'lg', md: 'lg', sm: 'lg', mobile: 'md' },
  right: { lg: 'lg', md: 'lg', sm: 'lg', mobile: 'auto' },
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'flex-end',
} as ModalContentProps;

export const headerContentStyle = {
  my: 'sm',
  gridTemplateColumns: '1fr auto',
  alignItems: 'center',
  width: '100%',
};

export const delimiterModalStyle = {
  w: 'full',
  h: '0.063rem',
  bgColor: 'lightGrey4',
};

export const headerTitleStyle = {
  color: 'darkGrey1',
  justifyContent: 'flex-start',
  fontSize: 'xl',
  lineHeight: '3',
  fontFamily: 'header',
  padding: 0,
  fontWeight: 'bold',
};

export const closeButtonStyle = {
  background: 'baseWhite',
  color: 'var(--chakra-colors-darkGrey3)',
  border: '1px solid var(--chakra-colors-darkGrey3)',
  _hover: {
    boxShadow: '0rem 0.25rem 0.5rem 0rem #ccc',
  },
};

export const modalCloseButtonStyle = {
  h: 'var(--chakra-space-xl)',
  w: 'var(--chakra-space-xl)',
  ml: 'auto',
  position: 'static',
  _focus: {
    boxShadow: 'none',
  },
  _hover: {
    bgColor: 'transparent',
  },
  _active: {
    bgColor: 'transparent',
  },
} as ButtonProps;
