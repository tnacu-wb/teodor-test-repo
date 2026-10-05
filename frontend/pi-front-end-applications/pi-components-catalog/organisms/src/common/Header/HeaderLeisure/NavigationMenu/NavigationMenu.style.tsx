import { ListItemProps } from '@chakra-ui/react';

export const listItemStyles = {
  w: '48',
  mb: '1',
  _focusVisible: {
    outline: 'none',
  },

  _hover: {
    a: {
      textDecoration: 'underline',
    },
  },
} as ListItemProps;

export const businessListStyles = {
  w: '48',
  fontWeight: 'medium',
};

export const listStyles = {
  w: '48',
  mr: '5',
  color: 'darkGrey1',
  mb: '0',
  ml: '0',
  listStyleType: 'none',
};

export const headingStyles = {
  _focusVisible: {
    outline: 'none',
  },
  fontSize: 'md',
  lineHeight: 3,
  mb: 'sm',
  fontWeight: 'semibold',
};

export const linkStyles = {
  cursor: 'pointer',
  _hover: {
    textDecoration: 'underline',
  },
  _focusVisible: {
    outline: 'none',
  },
  fontWeight: 'medium',
  fontSize: 'md',
  mb: 0,
};

export const boxStyles = {
  mr: { lg: '1.875rem', xl: '1.625rem' },
  _focusVisible: {
    outline: 'none',
  },
  mb: 0,
};
