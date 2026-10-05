import { Dropdown } from '@whitbread-eos/atoms';

interface Props {
  inputPlaceholder: string;
}

export default function Promotion({ inputPlaceholder }: Readonly<Props>) {
  return (
    <Dropdown
      dataTestId="promotion"
      placeholder={inputPlaceholder}
      dropdownStyles={{ menuButtonStyles: menuButtonStyles, wrapperStyles: wrapperStyles }}
      matchWidth
      disabled
    />
  );
}

const menuButtonStyles = {
  bgColor: 'transparent',
  _hover: {
    border: '1px solid var(--chakra-colors-darkGrey1)',
  },
  _active: {
    border: '2px solid var(--chakra-colors-primary)',
  },
  w: '100%',
  h: '100%',
  ml: {
    mobile: 'var(--chakra-space-sm)',
    xs: 'var(--chakra-space-md)',
    sm: 0,
  },
  borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '0' },
  border: {
    mobile: '1px solid var(--chakra-colors-lightGrey1)',
    xs: '1px solid var(--chakra-colors-lightGrey1)',
    sm: 'none',
  },
  _placeholder: {
    color: 'var(--chakra-colors-darkGrey1)',
  },
};

const wrapperStyles = {
  mt: { mobile: 'var(--chakra-space-md)', sm: 0 },
  w: {
    mobile: '8.75rem',
    xs: '10.25rem',
    sm: '16rem',
    md: '6.063rem',
    lg: '12.875rem',
  },
  h: {
    mobile: 'var(--chakra-space-4xl)',
    lg: 'var(--chakra-space-6xl)',
  },
};
