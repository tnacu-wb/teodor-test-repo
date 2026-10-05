import { FlexProps, StyleProps } from '@chakra-ui/react';

export const containerStyles = {
  mb: {
    mobile: 'xl',
    sm: '3xl',
  },
};

export const containerWithErrorStyles = {
  mb: 'lg',
};

export const inputWithErrorStyles = {
  mb: 'md',
};

export const inputStyles = {
  height: 'var(--chakra-space-4xl)',
  borderColor: 'var(--chakra-colors-lightGrey1)',

  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
  _active: { borderColor: 'var(--chakra-colors-darkGrey1)' },
  _hover: { borderColor: 'var(--chakra-colors-darkGrey1)' },
} as StyleProps;

export const inputIconStyles = {
  top: 'var(--chakra-space-sm)',
};

export const datepickerInputStyle = {
  w: {
    mobile: '100%',
    lg: '27.25rem',
    xl: '30.5rem',
  },
  mr: {
    mobile: '0px',
    lg: '1.913rem',
    xl: 'lg',
  },
  h: '56px',
  mb: {
    mobile: 'lg',
    sm: '1.563rem',
    lg: 'unset',
  },
};

export const clearTextStyle = {
  color: 'btnSecondaryEnabled',
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
  _hover: {
    cursor: 'pointer',
  },
  backgroundColor: 'transparent',
  minW: 'unset',
  w: 'unset',
  paddingLeft: '0rem',
  paddingRight: '0rem',
  _active: { backgroundColor: 'transparent' },
};

export const clearButton = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  w: {
    mobile: '50%',
    lg: 'unset',
  },
  h: '3.5rem',
};

export const setFindButtonStyles = (language: string) => {
  return {
    w: {
      mobile: '100%',
      xs: '100%',
      sm: '50%',
      md: '50%',
      lg: language === 'de' ? '9.625rem' : '10.625rem',
    },
    mr: {
      mobile: '0px',
      lg: '1.913rem',
      xl: 'lg',
    },
  };
};

export const paragraphStyles = {
  mt: 'md',
  mb: 'md',
};

export const searchInputStyles = {
  w: {
    mobile: '100%',
    lg: '27.25rem',
    xl: '30.5rem',
  },
  mr: {
    mobile: '0px',
    lg: '1.913rem',
    xl: 'lg',
  },
  mb: {
    mobile: 'lg',
    sm: '1.563rem',
    lg: 'unset',
  },
};

export const buttonsWrapperStyles = {
  display: 'flex',
  flexDirection: {
    mobile: 'column',
    sm: 'row',
  },
  alignItems: 'center',
  w: {
    mobile: '100%',
    lg: 'unset',
  },
  h: {
    sm: '3.5rem',
  },
} as FlexProps;

export const inputElementStyles = {
  borderColor: 'var(--chakra-colors-lightGrey1)',

  _hover: { borderColor: 'var(--chakra-colors-darkGrey1)' },
  _active: { borderColor: 'var(--chakra-colors-darkGrey1)' },
};
