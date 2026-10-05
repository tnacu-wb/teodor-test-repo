import { type BoxProps, type FlexProps, type GridProps } from '@chakra-ui/react';
import { Language } from '@whitbread-eos/api';

export const containerWrapperStyles = (language: Language) => {
  return {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    maxW: 'var(--chakra-space-breakpoint-xl)',
    h: 'full',
    pl:
      language === 'en'
        ? { mobile: 'md', xs: '5', sm: 'md', md: 'lg', lg: '4.125rem' }
        : { mobile: 'md', xs: '5', sm: 'md', md: 'lg', lg: '1.75rem', xl: '4.125rem' },
    pr:
      language === 'en'
        ? { mobile: 'md', md: '1.688rem', lg: '4.125rem' }
        : { mobile: 'md', md: '1.688rem', lg: '1.75rem', xl: '4.125rem' },
  };
};

export const contentStyles = {
  w: 'full',
  templateColumns: 'auto 1fr',
  alignItems: 'center',
} as GridProps;

export const containerLogoStyle = {
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

export const headerWrapperStyles = {
  w: 'full',
  bg: 'white',
  boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
  h: { mobile: 'var(--chakra-space-4xl)', lg: 'var(--chakra-space-6xl)' },
} as BoxProps;
