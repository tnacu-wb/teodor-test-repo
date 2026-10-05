import {
  Input as ChakraInput,
  FormControl,
  FormLabel,
  FormErrorMessage,
  Flex,
  Text,
  BoxProps,
  InputGroup,
} from '@chakra-ui/react';
//custom import
import { formatDataTestId } from '@whitbread-eos/utils';
import { FormEvent } from 'react';

interface Props extends BoxProps {
  name: string;
  value?: string;
  optionalText?: string;
  onChange?: (val: string | number | object) => void | React.ChangeEvent<HTMLInputElement>;
  onBlur?: () => void | React.ChangeEvent<HTMLInputElement>;
  onClick?: () => void | React.ChangeEvent<HTMLInputElement>;
  onKeyDown?: any;
  inputRef?: any;
  type?: string;
  label?: string;
  isAutoFocused?: boolean;
  isInputAriaRequired?: boolean;
  className?: string;
  charLimit?: number;
  error?: any;
  showIcon?: boolean;
  showLabel?: boolean;
  optional?: boolean;
}

interface GetDisplayParams {
  label?: string;
  charLimit?: number;
  error: any;
  value?: string;
  showLabel?: boolean;
}
const getDisplayProps = ({ label, showLabel, charLimit, error, value }: GetDisplayParams) => {
  return {
    displayLabel: label && showLabel,
    displayUnderField: charLimit ?? error,
    isValid: !error && value,
  };
};

export default function Input({
  type = 'text',
  label,
  name,
  showLabel = true,
  isAutoFocused,
  charLimit,
  error,
  value,
  onChange,
  onBlur,
  onClick,
  onKeyDown,
  inputRef,
  className,
  optional,
  optionalText,
  isInputAriaRequired = false,
}: Props) {
  const baseDataTestId = 'input';
  const { displayLabel } = getDisplayProps({
    label,
    showLabel,
    charLimit,
    error,
    value,
  });

  const inputElementStyles = {
    ...inputStyle(error),
  };

  return (
    <FormControl isInvalid={error}>
      {displayLabel && (
        <FormLabel
          data-testid={formatDataTestId(baseDataTestId, `${name}-label`)}
          {...labelStyle()}
          htmlFor={name}
        >
          {label}{' '}
          {optional && (
            <Text as="span" fontWeight="normal">
              ({optionalText})
            </Text>
          )}
        </FormLabel>
      )}
      <InputGroup>
        <ChakraInput
          variant="filled"
          data-testid={formatDataTestId(baseDataTestId, `${name}`)}
          {...inputElementStyles}
          errorBorderColor={error && 'error'}
          maxLength={charLimit}
          ref={inputRef}
          id={name}
          onBlur={onBlur}
          autoFocus={isAutoFocused}
          className={className || ''}
          onChange={handleChange}
          onClick={onClick}
          onKeyDown={onKeyDown}
          value={value}
          type={type}
          isRequired={isInputAriaRequired}
        />
      </InputGroup>
      {error && (
        <Flex w="full" dir="column">
          {renderFieldErrorMessage()}
        </Flex>
      )}
    </FormControl>
  );

  function handleChange(event: FormEvent<HTMLInputElement>) {
    if (onChange) onChange((event?.target as HTMLInputElement)?.value);
  }

  function renderFieldErrorMessage() {
    return (
      <FormErrorMessage
        data-testid={formatDataTestId(baseDataTestId, `${name}-FormErrorMessage`)}
        {...errorMsgStyle}
      >
        {error}
      </FormErrorMessage>
    );
  }
}

const inputStyle = (error: string | undefined) => {
  return {
    h: '3.1rem',
    zIndex: 0,
    borderWidth: '1px',
    _focus: {
      zIndex: '0',
      borderWidth: '2px',
      borderColor: error ? 'error' : 'var(--chakra-colors-primary)',
    },
  };
};

const labelStyle = () => {
  return {
    w: 'fit-content',
    fontSize: 'md',
    align: 'center',
    fontWeight: 'bold',
    zIndex: '1',
    color: '#333333',
  };
};

const errorMsgStyle = {
  fontSize: 'xs',
  color: 'error',
  px: 'xs',
  mt: 'sm',
};
