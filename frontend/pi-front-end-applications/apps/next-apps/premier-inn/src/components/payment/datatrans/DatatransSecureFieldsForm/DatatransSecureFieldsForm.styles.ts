import {
  BoxProps,
  FormErrorMessageProps,
  FormLabelProps,
  SelectProps,
  StackProps,
  TextProps,
} from '@chakra-ui/react';

export const containerStyle = {
  my: 'md',
  flex: 1,
} as BoxProps;

export const headerStyle = {
  justify: 'space-between',
  align: 'center',
  mb: 'lg',
} as StackProps;

export const titleStyle = {
  fontFamily: 'SunsetSans, helvetica, arial, sans-serif',
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '120%',
  letterSpacing: '0',
  verticalAlign: 'middle',
  color: 'darkGrey1',
} as TextProps;

// ─── Iframe field ─────────────────────────────────────────────────────────────
// The FormControl wrapper is position:relative so the floating label and the
// iframe overlay both anchor to it correctly.
export const iframeFormControlStyle = {
  position: 'relative',
  borderRadius: 'xs',
  zIndex: 0,
} as BoxProps;

// ChakraInput shell — receives identical props to the Input atom's inner input.
// isReadOnly + tabIndex=-1 + userSelect=none prevents any direct interaction;
// the Datatrans iframe (absolutely positioned on top) handles all input.
export const iframeInputShellStyle = {
  h: '3.5rem',
  borderWidth: '1px',
  borderColor: 'var(--chakra-colors-lightGrey1)',
  _hover: { borderColor: 'darkGrey1' },
  _focus: { zIndex: 0, borderWidth: '2px', borderColor: 'primary' },
  userSelect: 'none',
  cursor: 'default',
} as BoxProps;

/**
 * Shell style variant for when the iframe field has been touched and is invalid.
 * Drives the outer border to error colour — mirrors the error state of the
 * merchant-owned ChakraInput fields (cardholderName, expiry).
 */
export const iframeInputShellErrorStyle = {
  h: '3.5rem',
  borderWidth: '2px',
  borderColor: 'error',
  _hover: { borderColor: 'error' },
  _focus: { zIndex: 0, borderWidth: '2px', borderColor: 'error' },
  userSelect: 'none',
  cursor: 'default',
} as BoxProps;

// Datatrans iframe overlay — fills the ChakraInput shell exactly.
// pointer-events:all re-enables mouse/keyboard for the iframe content.
export const iframeOverlayStyle = {
  position: 'absolute',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  zIndex: 1,
  overflow: 'hidden',
  borderRadius: 'md',
} as BoxProps;

// ─── Floating label (mirrors labelStyle() in Input.component.tsx exactly) ─────
export const floatingLabelStyle = (
  error: string | undefined,
  value: string | undefined
): FormLabelProps => ({
  pos: 'absolute',
  h: '1.25rem',
  w: 'fit-content',
  fontSize: 'sm',
  px: 'xs',
  fontWeight: 'normal',
  ml: '0.750rem',
  top: '-0.625rem',
  backgroundColor: 'baseWhite',
  zIndex: 1,
  color: error ? 'error' : 'darkGrey1',
  // Hidden when no value — the placeholder text inside the input acts as label
  display: value ? 'block' : 'none',
  lineHeight: '1',
  mb: 0,
});

// ─── Error message (mirrors renderFieldErrorMessage() in Input.component.tsx) ──
export const errorMessageStyle = {
  color: 'error',
  ml: 'md',
  mt: 'sm',
  fontSize: 'xs',
} as FormErrorMessageProps;

// ─── Expiry selects ────────────────────────────────────────────────────────────
// Same FormControl/FormLabel/Select structure as Input atom's FormControl
// + ChakraInput, but using a <select> element instead of <input>.
// CSS variables override the Chakra Select component defaults so dimensions,
// padding and font size match the Input atom's inner <input> exactly.
export const selectStyle = {
  borderWidth: '1px',
  borderRadius: 'md',
  borderColor: 'var(--chakra-colors-lightGrey1)',
  color: 'darkGrey1',
  _hover: { borderColor: 'darkGrey1' },
  _focus: { zIndex: 0, borderWidth: '2px', borderColor: 'primary' },
  sx: {
    // --input-height drives the inner <select> element height (not the wrapper h prop)
    '--input-height': '3.5rem',
    // --input-padding drives paddingInlineStart (matches Input atom's space.4 = 1rem)
    '--input-padding': '1rem',
    // --input-font-size drives the select font size (matches fontSizes.md = 1rem)
    '--input-font-size': '1rem',
    // Placeholder option colour matches Input atom's _placeholder.color
    '> option[value=""]': { color: 'var(--chakra-colors-darkGrey2)' },
  },
} as SelectProps;

export const selectErrorStyle = {
  borderWidth: '2px',
  borderRadius: 'md',
  borderColor: 'error',
  color: 'darkGrey1',
  errorBorderColor: 'error',
  _hover: { borderColor: 'error' },
  _focus: { zIndex: 0, borderWidth: '2px', borderColor: 'error' },
  sx: {
    '--input-height': '3.5rem',
    '--input-padding': '1rem',
    '--input-font-size': '1rem',
    '> option[value=""]': { color: 'var(--chakra-colors-darkGrey2)' },
  },
} as SelectProps;

// Styles passed to Datatrans SecureFields init() — applied to the <input> element
// INSIDE each iframe. Font spec matches the design token exactly:
// Proxima Nova, 400, 1rem (body-M), line-height 150%, color #1C1C1C.
// No border (the ChakraInput shell provides the visual border).
// See: https://docs.datatrans.ch/docs/secure-fields-options
export const datatransIframeStyles: Record<string, string> = {
  '*': [
    'border: none',
    'outline: none',
    'padding: 0 1rem',
    'width: 100%',
    'height: 100%',
    'font-family: Proxima Nova, helvetica, arial, sans-serif',
    'font-size: 1rem',
    'font-weight: 400',
    'font-style: normal',
    'line-height: 150%',
    'color: #1C1C1C',
    'background: transparent',
    '-webkit-appearance: none',
    'box-sizing: border-box',
  ].join('; '),
  '*::placeholder': 'color: #1C1C1C; font-style: normal;',
  '*:-ms-input-placeholder': 'color: #1C1C1C;',
  // Datatrans automatically applies the .invalid class to the input inside the
  // iframe when the field value fails validation. This colours the text red to
  // match the error state of the merchant-owned fields.
  'cardNumber.invalid': 'color: var(--chakra-colors-error, #E53E3E);',
  'cvv.invalid': 'color: var(--chakra-colors-error, #E53E3E);',
};

export const expiryGroupStyle = {
  spacing: 'md',
  align: 'flex-start',
  w: 'full',
} as StackProps;

// ─── Loading skeletons ─────────────────────────────────────────────────────────
// Dimensions match the real fields exactly so there is no layout shift once
// Datatrans finishes loading.
export const skeletonFieldStyle = {
  h: '3.5rem',
  borderRadius: 'md',
  startColor: 'lightGrey5',
  endColor: 'lightGrey4',
} as BoxProps;

export const skeletonHeaderStyle = {
  h: '1.5rem',
  w: '8rem',
  borderRadius: 'xs',
  startColor: 'lightGrey5',
  endColor: 'lightGrey4',
} as BoxProps;
