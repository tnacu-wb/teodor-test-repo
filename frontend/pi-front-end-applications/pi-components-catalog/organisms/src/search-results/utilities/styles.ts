import type { FlexProps } from '@chakra-ui/react';

export const hotelFlagBannerBaseStyles = (backgroundColour?: string | null): FlexProps => ({
  alignItems: 'center',
  bgColor: backgroundColour || 'tertiary',
  borderTopLeftRadius: 'lg',
  borderTopRightRadius: 'lg',
  color: 'baseWhite',
  gap: 'xs',
  overflowWrap: 'break-word',
  px: 'sm',
  py: 'xs',
});

export const hotelFlagBannerImageStyles = {
  boxSize: '20px',
  flexShrink: 0,
  objectFit: 'contain',
} as const;
