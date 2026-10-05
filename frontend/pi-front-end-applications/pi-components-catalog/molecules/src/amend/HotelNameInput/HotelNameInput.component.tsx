import { Input, InputGroup, InputLeftElement, InputProps } from '@chakra-ui/react';
import { Location } from '@whitbread-eos/atoms';

interface Props {
  hotelName: string;
  onInputChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function HotelNameInput({ hotelName, onInputChange }: Readonly<Props>) {
  return (
    <InputGroup {...inputGroupStyles}>
      <InputLeftElement top="50%" transform="auto" translateY="-50%">
        <Location color="var(--chakra-colors-lightGrey4)" />
      </InputLeftElement>
      <Input
        placeholder={hotelName}
        value={hotelName}
        onChange={onInputChange}
        disabled={true}
        data-testid="Amend-StayDates-HotelNameInput"
        {...inputStyles}
        sx={{
          WebkitTextFillColor: 'var(--chakra-colors-lightGrey4)',
        }}
      />
    </InputGroup>
  );
}

const inputGroupStyles = {
  minW: {
    mobile: '15rem',
    xs: '18.438rem',
    sm: '30.75rem',
    md: '20.25rem',
    lg: '24.75rem',
  },
};

const inputStyles: InputProps = {
  w: 'full',
  maxW: {
    md: '20.25rem',
    lg: '24.75rem',
  },
  h: 'var(--chakra-space-4xl)',
  borderRadius: 'var(--chakra-space-radiusSmall)',
  borderColor: 'lightGrey1',
  color: 'lightGrey4',
  fontSize: 'var(--chakra-fontSizes-md)',
  lineHeight: 'var(--chakra-lineHeights-3)',
  fontWeight: 'var(--chakra-fontWeights-normal)',
  _placeholder: {
    color: 'lightGrey4',
  },
  _disabled: {
    borderColor: 'lightGrey4',
    _placeholder: {
      color: 'lightGrey4',
    },
  },
};
