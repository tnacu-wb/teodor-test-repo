import {
  BoxProps,
  InputGroup,
  InputLeftElement,
  NumberInput,
  NumberInputField,
  Text,
} from '@chakra-ui/react';
import { Error, Icon, NoOfNights as NightsIcon } from '@whitbread-eos/atoms';
import dynamic from 'next/dynamic';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

export interface Props {
  inputPlaceholder?: string;
  inputValue: number;
  handleOnChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleOnBlur?: (event: React.ChangeEvent<HTMLInputElement>) => void;
  maxNights: number;
  noOfNightsError: string;
  isLessThanSm?: boolean;
  styles?: { inputElementStyles: BoxProps; iconStyles: BoxProps; tooltipStyles: BoxProps };
  hideErrorForMinNights?: boolean;
  isDisabled?: boolean;
}

export default function NumberOfNights({
  inputPlaceholder,
  inputValue,
  handleOnChange,
  maxNights,
  noOfNightsError,
  isLessThanSm,
  styles,
  handleOnBlur,
  hideErrorForMinNights = false,
  isDisabled = false,
}: Readonly<Props>) {
  const isError = inputValue > maxNights || (inputValue === 0 && !hideErrorForMinNights);

  const { noOfNightsInputElementStyles, alertStyles, iconStyles, tooltipStyles } =
    getNumberOfNightsStyles(isLessThanSm, isError, styles);

  return (
    <NumberInput
      max={maxNights}
      keepWithinRange={false}
      clampValueOnBlur={false}
      value={inputValue}
      defaultValue={hideErrorForMinNights ? 0 : 1}
      aria-label="datepicker-input"
    >
      {isError && (
        <Tooltip
          description={noOfNightsError}
          variant="inlineError"
          defaultIsOpen={true}
          svg={<Error />}
          placement="bottom-start"
          position="relative"
          alertElementStyles={alertStyles}
          {...tooltipStyles}
        >
          {''}
        </Tooltip>
      )}
      <InputGroup>
        <InputLeftElement
          pointerEvents="none"
          justifyContent="space-around"
          color={isDisabled ? 'lightGrey1' : ''}
          {...iconStyles}
        >
          <Icon svg={<NightsIcon color={isDisabled ? 'var(--chakra-colors-lightGrey1)' : ''} />} />
          <Text ml="md" as="span">
            |
          </Text>
        </InputLeftElement>
        <NumberInputField
          disabled={isDisabled}
          onChange={handleOnChange}
          onBlur={handleOnBlur}
          maxLength={3}
          placeholder={inputPlaceholder}
          style={{
            ...(isError && {
              border: '2px solid var(--chakra-colors-error)',
              borderRadius: 'var(--chakra-radii-base)',
            }),
          }}
          {...noOfNightsInputElementStyles}
        />
      </InputGroup>
    </NumberInput>
  );
}

const getNumberOfNightsStyles = (
  isLessThanSm: boolean | undefined,
  isError: boolean | undefined,
  otherStyles?: { inputElementStyles: BoxProps; iconStyles: BoxProps; tooltipStyles: BoxProps }
) => {
  const alertStyles = {
    py: 'sm',
    paddingRight: '0',
  };

  const inputElementStyles = {
    borderRight: '1px solid var(--chakra-colors-lightGrey4)',
    borderTopRightRadius: isLessThanSm ? 'var(--chakra-space-xs)' : 0,
    borderBottomRightRadius: isLessThanSm ? 'var(--chakra-space-xs)' : 0,
    color: 'darkGrey1',
    sx: {
      '::placeholder': {
        color: 'darkGrey2',
      },
    },
  };

  const noOfNightsInputElementStyles = {
    ...(otherStyles?.inputElementStyles ?? {
      ...inputElementStyles,
      border: {
        mobile: '1px solid var(--chakra-colors-lightGrey1)',
        xs: '1px solid var(--chakra-colors-lightGrey1)',
        sm: '0',
      },
      borderRight: { md: '1px solid var(--chakra-colors-lightGrey4)' },
      borderRadius: {
        mobile: 'base',
        sm: '0',
      },
      _hover: { border: '1px solid var(--chakra-colors-darkGrey1)', borderRadius: 'base' },
      _focus: {
        border: '2px solid var(--chakra-colors-primary)',
        cursor: 'auto',
        borderRadius: 'base',
      },
      padding: 'var(--chakra-space-lg) var(--chakra-space-lg) var(--chakra-space-lg) 3.7rem',
      w: {
        mobile: '8.75rem',
        xs: '10.25rem',
        sm: '6.75rem',
        md: '6.313rem',
        lg: '9.688rem',
      },
      h: {
        mobile: 'var(--chakra-space-4xl)',
        xs: 'var(--chakra-space-4xl)',
        sm: 'var(--chakra-space-4xl)',
        md: 'var(--chakra-space-4xl)',
        lg: 'var(--chakra-space-6xl)',
      },
    }),
    ...(isError
      ? {
          border: 'none',
          _hover: {
            border: 'none',
          },
          _focus: {
            border: 'none',
          },
        }
      : {}),
  };

  const iconStyles = otherStyles?.iconStyles ?? {
    top: {
      mobile: 'var(--chakra-space-sm)',
      xs: 'var(--chakra-space-sm)',
      sm: 'var(--chakra-space-md)',
      md: 'var(--chakra-space-sm)',
      lg: 'var(--chakra-space-md)',
    },
    left: ' var(--chakra-space-md)',
  };

  const tooltipStyles = otherStyles?.tooltipStyles ?? {
    w: { xs: '10.25rem', md: '6.313rem', lg: '9.688rem' },
    marginLeft: { xs: 'var(--chakra-space-xmd)', sm: '0' },
    top: { xs: 'xl', sm: '2xl', md: '4xl' },
    fontSize: 'sm',
    lineHeight: '3',
    fontWeight: 'normal',
  };

  return {
    alertStyles,
    noOfNightsInputElementStyles,
    iconStyles,
    tooltipStyles,
  };
};
