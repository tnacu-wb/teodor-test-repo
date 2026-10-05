import { BoxProps, ContainerProps, FlexProps, GridProps } from '@chakra-ui/react';

export const contentStyles = {
  w: 'var(--chakra-sizes-full)',
  templateColumns: 'auto 1fr',
  alignItems: 'center',
} as GridProps;

export const containerWrapperStyles = {
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  h: 'var(--chakra-sizes-full)',
  pl: { mobile: 'md', xs: '5', sm: 'md', md: 'lg', lg: '7', xl: '4.125rem' },
  pr: { mobile: 'md', md: '1.688rem', xl: '5xl' },
} as ContainerProps;

export const containerLogoStyle = {
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

export const headerWrapperStyles = {
  w: 'var(--chakra-sizes-full)',
  backgroundColor: 'var(--chakra-colors-baseWhite)',
  boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
} as BoxProps;
