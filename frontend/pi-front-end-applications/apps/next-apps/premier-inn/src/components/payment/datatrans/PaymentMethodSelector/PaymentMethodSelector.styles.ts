import { BoxProps, FlexProps, TextProps } from '@chakra-ui/react';

// Figma: row wrap, 27px gap between tiles, 16px row gap
export const gridStyle = {
  wrap: 'wrap',
  gap: '27px',
  rowGap: 'md',
  alignItems: 'stretch',
} as FlexProps;

// Outer wrapper: column, no border/bg — just stacks the box + label with 4px gap
const baseTileWrapperStyle = {
  flexBasis: 'calc(33.333% - 18px)',
  flexGrow: 0,
  flexShrink: 0,
  display: 'flex',
  flexDirection: 'column',
  gap: 'xs', // 4px between box and label (Figma gap: 4px)
  cursor: 'pointer',
  userSelect: 'none',
  _focusVisible: {
    outline: '2px solid',
    outlineColor: 'primary',
    outlineOffset: '2px',
  },
} as BoxProps;

export const tileWrapperStyle = baseTileWrapperStyle;

export const disabledWrapperStyle = {
  ...baseTileWrapperStyle,
  opacity: 0.4,
  cursor: 'not-allowed',
  pointerEvents: 'none',
} as BoxProps;

// The visible bordered box — 56px fixed height, 4px radius, centred content
const baseBoxStyle = {
  position: 'relative',
  overflow: 'hidden', // clips the triangle badge to the box corner
  borderRadius: '4px',
  padding: 'md', // 16px
  height: '56px',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  width: '100%',
  transition: 'border-color 0.18s ease, background-color 0.18s ease, border-width 0.18s ease',
} as BoxProps;

export const defaultTileStyle = {
  ...baseBoxStyle,
  borderWidth: '1px',
  borderStyle: 'solid',
  borderColor: '#BFBFBF',
  bg: 'baseWhite',
} as BoxProps;

export const selectedTileStyle = {
  ...baseBoxStyle,
  borderWidth: '2px',
  borderStyle: 'solid',
  borderColor: '#642587',
  bg: 'lightGrey5', // #F8F8F8
} as BoxProps;

export const disabledTileStyle = {
  ...baseBoxStyle,
  borderWidth: '1px',
  borderStyle: 'solid',
  borderColor: '#BFBFBF',
  bg: 'baseWhite',
} as BoxProps;

export const iconContainerStyle = {
  justify: 'center',
  align: 'center',
} as FlexProps;

// Figma: Proxima Nova Regular 14px, #333333, centered
export const labelStyle = {
  fontSize: 'sm', // 14px
  fontWeight: 'normal', // 400
  color: 'darkGrey1', // #333333
  textAlign: 'center',
  lineHeight: '1.4em',
} as TextProps;

// Invisible spacer: same flex dimensions as a real wrapper but no visible content
export const spacerTileStyle = {
  flexBasis: 'calc(33.333% - 18px)',
  flexGrow: 0,
  flexShrink: 0,
  visibility: 'hidden',
} as BoxProps;

// Figma: "Payment details" — Proxima Nova Sans Semibold 26px, 120% line-height, darkGrey1
export const titleStyle = {
  fontFamily: 'heading', // Proxima Nova Sans (secondary/heading font family)
  fontSize: '2xl', // 23px closest — override with literal for exact 26px
  fontWeight: 'semibold', // 600
  lineHeight: '120%',
  letterSpacing: '0',
  color: 'darkGrey1',
  style: { fontSize: '26px' },
} as TextProps;
