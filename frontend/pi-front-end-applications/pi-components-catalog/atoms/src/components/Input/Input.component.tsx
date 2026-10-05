import {
  Box,
  BoxProps,
  Flex,
  FormControl,
  FormErrorMessage,
  FormHelperText,
  FormLabel,
  FormLabelProps,
  Input as ChakraInput,
  InputGroup,
  InputRightElement,
  Text,
  InputProps,
  useTheme,
} from '@chakra-ui/react';
import {
  getInputTypographyOverride,
  getTextStyleTypographyOverride,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import React, { useState } from 'react';

import { Error, Error24, Success24 } from '../../assets/icons';
import { formatDataTestId } from '../../utils/formatters';

const Tooltip = dynamic(
  async () => {
    const { default: Tooltip } = await import('../Tooltip/Tooltip.component');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

interface Props extends BoxProps {
  autoComplete?: string;
  name: string;
  value?: string;
  onChange?: (val: any) => void | React.ChangeEvent<HTMLInputElement>;
  onBlur?: () => void | React.ChangeEvent<HTMLInputElement>;
  onClick?: () => void | React.ChangeEvent<HTMLInputElement>;
  onKeyDown?: any;
  type?: string;
  placeholderText?: string;
  label?: string;
  isDisabled?: boolean;
  isAutoFocused?: boolean;
  helperText?: string;
  charLimit?: number;
  maxLength?: number;
  error?: any;
  useTooltip?: boolean;
  useCustomTooltip?: boolean;
  showIcon?: boolean;
  showRightElement?: boolean;
  showLabel?: boolean;
  showText?: string;
  hideText?: string;
  inputRef?: any;
  className?: string;
  styles?: {
    inputWrapperStyles?: BoxProps;
    inputElementStyles?: BoxProps;
    labelElementStyles?: FormLabelProps;
  };
  isInputAriaRequired?: boolean;
  extraTooltipStyles?: any;
  emailFieldProps?: InputProps;
}

interface GetDisplayParams {
  label?: string;
  showLabel?: boolean;
  isDisabled?: boolean;
  helperText?: string;
  charLimit?: number;
  error: any;
  showIcon: boolean;
  value?: string;
}

const stripTrailingAsterisk = (text?: string) => {
  const trimmed = text?.trimEnd();
  if (!trimmed?.endsWith('*')) return { textPart: text, hasMark: false };
  return { textPart: trimmed.slice(0, -1).trimEnd(), hasMark: true };
};

const hasTrailingAsterisk = (text?: string) => !!text?.trimEnd().endsWith('*');

const getDisplayProps = ({
  label,
  showLabel,
  isDisabled,
  helperText,
  charLimit,
  error,
  showIcon,
  value,
}: Readonly<GetDisplayParams>) => {
  return {
    displayLabel: label && (showLabel || !isDisabled),
    displayUnderField: helperText ?? charLimit ?? error,
    displayHelperText: (helperText && !error) || (helperText && error && isDisabled),
    displayCharLimit: charLimit && !isDisabled,
    displayIcon: showIcon && !isDisabled,
    isValid: !error && value,
  };
};
export default function Input(props: Readonly<Props>) {
  const theme = useTheme();
  const { t } = useTranslation(['common']);
  const {
    autoComplete = 'on',
    type = 'text',
    label,
    placeholderText,
    name,
    showLabel,
    isDisabled,
    isAutoFocused,
    helperText,
    charLimit,
    maxLength,
    error,
    useTooltip,
    useCustomTooltip,
    value,
    onChange,
    onBlur,
    onClick,
    onKeyDown,
    showRightElement = false,
    showIcon = false,
    showText = 'Show',
    hideText = 'Hide',
    inputRef,
    className,
    styles,
    isInputAriaRequired = false,
    extraTooltipStyles = {},
    emailFieldProps = {},
  } = props;
  const getTypographyProps = useSemanticTypography();
  const {
    displayLabel,
    displayUnderField,
    displayHelperText,
    displayCharLimit,
    displayIcon,
    isValid,
  } = getDisplayProps({
    label,
    showLabel,
    isDisabled,
    helperText,
    charLimit,
    error,
    showIcon,
    value,
  });

  const [isPasswordVisible, setIsPasswordVisible] = useState(false);
  const [focused, setFocused] = useState(false);
  const handleFocus = (val: boolean) => setFocused(val);
  const handleClick = () => setIsPasswordVisible(!isPasswordVisible);
  const baseDataTestId = 'input';

  const labelParts = stripTrailingAsterisk(label);
  const placeholderParts = stripTrailingAsterisk(placeholderText);
  const isRequired =
    isInputAriaRequired || labelParts.hasMark || hasTrailingAsterisk(placeholderText);
  const accessibleLabelText = labelParts.textPart || placeholderParts.textPart || undefined;
  // Reuse the existing common AEM label until a generic shared-input "required" key exists.
  const requiredLabelSuffix = t('cardNotPresent.memorableWordRequired', {
    defaultValue: 'required',
  });
  const accessibleLabel =
    isRequired && accessibleLabelText ? `${accessibleLabelText} ${requiredLabelSuffix}` : undefined;

  const wrapperStyles = {
    ...inputWrapperStyle,
    ...styles?.inputWrapperStyles,
  };

  const inputElementStyles = {
    ...inputStyle(error),
    ...styles?.inputElementStyles,
  };

  const inputTypographyOverride = getInputTypographyOverride(
    styles?.inputElementStyles,
    theme?.textStyles
  );

  const labelTypographyOverride = getTextStyleTypographyOverride(
    styles?.labelElementStyles,
    theme?.textStyles
  );

  const labelElementStyles = {
    ...labelStyle(error, value),
    ...styles?.labelElementStyles,
    ...labelTypographyOverride,
  };

  return (
    <FormControl {...wrapperStyles} isInvalid={error && !isDisabled}>
      {displayLabel && (
        <FormLabel
          data-testid={formatDataTestId(baseDataTestId, `${name}-label`)}
          pos="absolute"
          {...labelElementStyles}
          htmlFor={name}
        >
          {label}
        </FormLabel>
      )}
      <Tooltip
        {...extraTooltipStyles}
        {...tooltipStyle}
        description={error}
        variant="inlineError"
        placement="bottom-start"
        isDisabled={!error || !useTooltip}
        isOpen={error && useTooltip}
        svg={<Error24 />}
      >
        <InputGroup>
          <ChakraInput
            autoComplete={autoComplete}
            data-testid={formatDataTestId(baseDataTestId, `${name}`)}
            {...inputElementStyles}
            {...inputTypographyOverride}
            errorBorderColor={error && 'error'}
            maxLength={maxLength ?? charLimit}
            ref={inputRef}
            isDisabled={isDisabled}
            id={name}
            placeholder={placeholderText}
            value={value}
            onChange={handleChange}
            onBlur={onBlur}
            onBlurCapture={() => handleFocus(false)}
            autoFocus={isAutoFocused}
            type={type}
            className={className ?? ''}
            onFocus={() => handleFocus(true)}
            onClick={onClick}
            onKeyDown={onKeyDown}
            {...emailFieldProps}
            aria-label={accessibleLabel}
            aria-required={isRequired ? true : undefined}
          />
          {type === 'password' && showRightElement && (
            <InputRightElement width="4.5rem">
              <Text
                data-testid={formatDataTestId(baseDataTestId, `${name}-SwitchShowPass`)}
                h="1.75rem"
                size="sm"
                onClick={handleClick}
              >
                {isPasswordVisible ? hideText : showText}
              </Text>
            </InputRightElement>
          )}
          {displayIcon && error && !focused && (
            <Box {...iconStyle} data-testid="inputIconError">
              <Error24 />
            </Box>
          )}
          {displayIcon && isValid && !focused && (
            <Box {...iconStyle} data-testid="inputIconSuccess">
              <Success24 />
            </Box>
          )}
        </InputGroup>
      </Tooltip>
      {displayUnderField && (
        <Flex w="full" dir="column" justify={!helperText && !error ? 'flex-end' : 'space-between'}>
          {error && !useTooltip && renderFieldErrorMessage()}
          {displayHelperText && (
            <FormHelperText
              data-testid={formatDataTestId(baseDataTestId, `${name}-FormHelperText`)}
              {...helperTextStyle(isDisabled)}
            >
              {helperText}
            </FormHelperText>
          )}
          {handleDisplayCharLimit(
            displayCharLimit as boolean,
            baseDataTestId,
            error,
            value as string,
            charLimit as number,
            name
          )}
        </Flex>
      )}
    </FormControl>
  );

  function handleChange(event: any) {
    if (onChange) onChange(event?.target?.value);
  }

  function renderFieldErrorMessage() {
    const typographyProps = getTypographyProps({ fontSize: 'xs' }, { textStyle: 'body-s-regular' });
    const errorTypographyProps = getTextStyleTypographyOverride(typographyProps, theme?.textStyles);
    const errorMsgStyle = {
      ...typographyProps,
      ...errorTypographyProps,
      color: 'error',
      ml: 'md',
      mt: 'sm',
    };
    return !useCustomTooltip ? (
      typeof error !== 'boolean' && (
        <FormErrorMessage
          data-testid={formatDataTestId(baseDataTestId, `${name}-FormErrorMessage`)}
          {...errorMsgStyle}
        >
          {error}
        </FormErrorMessage>
      )
    ) : (
      <FormErrorMessage
        data-testid={formatDataTestId(baseDataTestId, `${name}-FormErrorMessage`)}
        {...customErrorMsgStyle}
      >
        <Error />
        <Box pl="0.5rem">{error}</Box>
      </FormErrorMessage>
    );
  }
}
const handleDisplayCharLimit = (
  displayCharLimit: boolean,
  baseDataTestId: string,
  error: string | undefined,
  value: string,
  charLimit: number,
  name: string
) => {
  if (displayCharLimit) {
    return (
      <Text
        data-testid={formatDataTestId(baseDataTestId, `${name}-CharLimit`)}
        {...charLimitStyle(error)}
      >
        {value?.length ?? 0}/{charLimit}
      </Text>
    );
  }
};

const inputWrapperStyle = {
  borderRadius: 'xs',
  zIndex: 0,
  borderColor: 'lightGrey1',
  _hover: { borderColor: 'darkGrey1' },
  _focus: { zIndex: '0' },
};

const inputStyle = (error: string | undefined) => {
  return {
    h: '3.5rem',
    zIndex: 0,
    borderWidth: '1px',
    borderColor: 'var(--chakra-colors-lightGrey1)',
    _hover: { borderColor: 'none' },
    _focus: { zIndex: '0', borderWidth: '2px', borderColor: error ? 'error' : 'primary' },

    _placeholder: {
      color: 'darkGrey2',
      fontStyle: 'normal',
      _focus: { color: 'success' },
      _hover: { color: 'primary' },
    },
    _autofill: {
      boxShadow: '0 0 0 1000px #FFFFFF inset',
    },
  };
};

const labelStyle = (error: string | undefined, value: string | undefined) => {
  return {
    h: '1.25rem',
    w: 'fit-content',
    fontSize: 'sm',
    align: 'center',
    px: 'xs',
    fontWeight: 'normal',
    ml: '0.750rem',
    top: '-0.625rem',
    backgroundColor: 'baseWhite',
    zIndex: '1',
    color: error ? 'error' : 'darkGrey1',
    display: value ? 'block' : 'none',
    _focus: { color: error ? 'error' : 'primary', display: 'block' },
    lineHeight: '1',
  };
};

const helperTextStyle = (isDisabled: boolean | undefined) => {
  return {
    fontSize: 'xs',
    color: isDisabled ? 'lightGrey1' : 'darkGrey1',
    ml: 'md',
    mt: '0.375rem',
  };
};

const charLimitStyle = (error: string | undefined) => {
  return {
    fontSize: 'xs',
    color: error ? 'error' : 'darkGrey2',
    mr: 'sm',
    mt: '0.375rem',
  };
};

const customErrorMsgStyle = {
  fontSize: 'sm',
  mt: 'xmd',
  w: 'full',
  h: '2.5rem',
  bgColor: 'tooltipError',
  color: 'darkGrey1',
  padding: '1rem',
  borderRadius: 'var(--chakra-space-xs)',
  _before: {
    content: '" "',
    position: 'absolute',
    top: '4rem',
    left: '1.531rem',
    width: 'var(--chakra-space-xmd)',
    height: 'var(--chakra-space-xmd)',
    transform: 'rotate(-45deg)',
    bgColor: 'tooltipError',
  },
};

const tooltipStyle = {
  h: '2.25rem',
  display: 'flex',
  alignContent: 'center',
};

const iconStyle = {
  ml: 'md',
  alignSelf: 'center',
};
