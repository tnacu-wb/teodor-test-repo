import { StyleProps } from '@chakra-ui/react';

export const styles = {
  dataErrorText: {
    alignItems: 'center',
    color: 'error',
    fontSize: 'xs',
    marginTop: 'xs',
    marginLeft: 'md',
  },
  datePickerError: {
    border: '2px solid red',
    borderRadius: 'md',
  },

  inputStyle: {
    height: 'var(--chakra-space-4xl)',
    _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
    borderColor: 'lightGrey1',
    borderRadius: 'md',
  },
  inputIconStyles: {
    top: 'sm',
  },
  createPromotionContainerStyles: {
    width: '100%',
    flexDirection: 'column',
    gap: { base: 'var(--M, 1rem)', sm: 'var(--L, 1.75rem)' },
  },
  createPromoButtonStyles: {
    display: 'flex',
    justifyContent: { base: 'space-between', xs: 'flex-end' } as const,
    flexDirection: { base: 'column', xs: 'row' } as const,
    gap: 'var(--M, 1rem)',
  },
  btnCancelStyles: {
    color: 'tertiary',
    textAlign: 'center',
    fontSize: '1.125rem',
    fontWeight: '600',
    width: '100%',
    maxWidth: { base: '100%', xs: '7.75rem' },
    minHeight: '2.75rem',
    maxHeight: '2.75rem',
    padding: '0.75rem 2rem',
    borderRadius: '0.25rem',
    border: '1px solid #511E62',
    background: '#FFF',
    _hover: {
      background: '#FFF',
      boxShadow: '0px 4px 5px rgba(0, 0, 0, 0.2)',
    },
    _focus: {
      background: '#FFF',
      color: 'tertiary',
      boxShadow: '0px 4px 5px rgba(0, 0, 0, 0.2)',
    },
  },
  btnSubmitStyles: {
    color: 'white',
    textAlign: 'center',
    fontSize: '1.125rem',
    fontStyle: 'normal',
    fontWeight: '600',
    width: '100%',
    maxWidth: { base: '100%', xs: '7.75rem' },
    minHeight: '2.75rem',
    maxHeight: '2.75rem',
    padding: '0.75rem 2rem',
    borderRadius: '0.25rem',
  },
};

export const inputStyle = {
  height: 'var(--chakra-space-4xl)',
  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
  borderColor: 'lightGrey1',
  borderRadius: 'var(--chakra-radii-md)',
} as StyleProps;

export const platformConfigStyles = {
  wrapperStyles: { width: '100%' },
  titleStyles: { fontSize: '1.125rem', fontWeight: '700' },
  descriptionStyles: {
    fontSize: '0.875rem',
    color: 'darkGrey1',
    marginTop: '0.25rem',
    marginBottom: '0.75rem',
  },
  countryCardStyles: {
    border: '1px solid',
    borderColor: 'lightGrey1',
    borderRadius: 'md',
    marginTop: '0.75rem',
    overflow: 'hidden',
  },
  countryHeaderStyles: {
    alignItems: 'center',
    padding: '0.75rem 1rem',
    background: '#EAF2F3',
  },
  countryLabelStyles: { fontWeight: '700' },
  platformTableStyles: { padding: '0 1rem 0.75rem' },
  platformHeaderRowStyles: {
    justifyContent: 'space-between',
    padding: '0.5rem 0',
    borderBottom: '1px solid',
    borderColor: 'lightGrey1',
  },
  platformHeaderTextStyles: { fontSize: '0.75rem', fontWeight: '600', color: 'darkGrey1' },
  platformRowStyles: {
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '0.75rem 0',
    borderBottom: '1px solid',
    borderColor: 'lightGrey1',
    _last: { borderBottom: 'none' },
  },
  platformOptionsStyles: { gap: '0.5rem' },
  optionSelectedStyles: {
    borderRadius: 'full',
    border: '1px solid',
    borderColor: 'primary',
    color: '#FFFFFF',
    background: 'primary',
    fontWeight: '600',
    padding: '0.25rem 0.75rem',
  },
  optionUnselectedStyles: {
    borderRadius: 'full',
    border: '1px solid',
    borderColor: 'lightGrey1',
    color: 'darkGrey1',
    background: '#F5F5F5',
    fontWeight: '600',
    padding: '0.25rem 0.75rem',
  },
  errorTextStyles: { color: 'error', fontSize: 'xs', marginTop: 'xs' },
};
