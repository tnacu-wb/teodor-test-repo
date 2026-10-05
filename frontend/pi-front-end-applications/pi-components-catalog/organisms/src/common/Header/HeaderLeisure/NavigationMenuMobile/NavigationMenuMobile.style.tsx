export const modalIconStyle = {
  h: 'var(--chakra-space-lg)',
  w: 'var(--chakra-space-lg)',
  position: 'static',
  justifyContent: 'center',
  fontSize: '0.6rem',
  lineHeight: '3',
  fontWeight: '400',
  color: 'darkGrey2',
  _focus: {
    boxShadow: 'none',
  },
  _hover: {
    bgColor: 'transparent',
    cursor: 'pointer',
  },
  _active: {
    bgColor: 'transparent',
  },
};

export const containerItemStyle = {
  p: 'md',
  pl: 0,
  borderBottom: 'var(--chakra-space-px) solid var(--chakra-colors-lightGrey2)',
  w: { mobile: '16.87rem', xs: '20.31rem', sm: '20.75rem', md: '21.69rem' },
  ml: 'auto',
  _hover: {
    cursor: 'pointer',
  },
};
