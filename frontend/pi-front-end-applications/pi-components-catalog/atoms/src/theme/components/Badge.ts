import type { ComponentStyleConfig } from '@chakra-ui/theme';

const Badge: ComponentStyleConfig = {
  baseStyle: (props) => ({
    bg: props.badgecolor || 'darkPink',
    color: 'baseWhite',
    borderRadius: '0.687rem',
    px: '0.5rem',
    py: '0.125rem',
    textTransform: 'initial',
    fontSize: '13px',
  }),
  variants: {
    primary: {},
    secondary: (props) => ({
      bg: 'none',
      color: 'darkGrey1',
      border: `1px solid var(--chakra-colors-${props.badgecolor || 'darkPink'})`,
    }),
    ZIP: {
      bg: 'zipPrimary',
      borderLeftRadius: 0,
    },
    hub: {
      bg: 'hubPrimary',
      borderLeftRadius: 0,
    },
    square: {
      borderRadius: '0.25rem',
      bg: 'hubPrimary',
    },
  },
};

export default Badge;
