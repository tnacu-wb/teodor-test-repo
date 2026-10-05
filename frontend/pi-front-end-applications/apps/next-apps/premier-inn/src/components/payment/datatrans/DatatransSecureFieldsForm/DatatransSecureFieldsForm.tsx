import {
  Box,
  Flex,
  FormControl,
  FormErrorMessage,
  HStack,
  Input as ChakraInput,
  InputGroup,
  Skeleton,
  Text,
} from '@chakra-ui/react';
import { yupResolver } from '@hookform/resolvers/yup';
import Image from 'next/image';
import React, { useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import * as yup from 'yup';

import { useDatatransSecureFields } from '~hooks/use-datatrans-secure-fields';

import {
  containerStyle,
  datatransIframeStyles,
  errorMessageStyle,
  iframeFormControlStyle,
  iframeInputShellErrorStyle,
  iframeInputShellStyle,
  iframeOverlayStyle,
  skeletonFieldStyle,
  titleStyle,
} from './DatatransSecureFieldsForm.styles';
import { ExpiryInput } from './ExpiryInput';

export interface DatatransSecureFieldsFormHandle {
  submit: () => void;
  resetForm: () => void;
  /**
   * Tears down the current SDK instance and starts a fresh session.
   * Call after a failed authorize so the user can retry without hitting
   * the "Form expired" error from the consumed iframe session.
   */
  reinit: () => void;
}

interface DatatransSecureFieldsFormProps {
  onSuccess: (data: { transactionId: string; redirect?: string }) => void;
  onError: (error: Error) => void;
  /**
   * Called whenever the initialising state changes. The parent uses this to
   * disable the Pay button while the Secure Fields session is being set up.
   */
  onInitialisingChange?: (isInitialising: boolean) => void;
  /**
   * Called when the SDK validate event fires with errors after a submit attempt.
   * The parent uses this to clear the Pay button's loading state.
   */
  onSubmitValidationFailed?: () => void;
  basketId: string;
  country: string;
  language: string;
  isVisible: boolean;
  /** Guest email from billing data — passed to Datatrans as 3D.cardholder.email for 3DS */
  guestEmail?: string;
  /** Billing address from booking data — passed to Datatrans as 3D.cardholder.billAddr* for 3DS */
  billingAddress?: {
    addressLine1?: string;
    cityName?: string;
    postalCode?: string;
    countryCode?: string | number;
  };
}

interface ExpiryFormValues {
  cardholderName: string;
  expiry: string;
}

const expirySchema = yup.object({
  cardholderName: yup.string().required('Cardholder name is required'),
  expiry: yup
    .string()
    .required('Expiry date is required')
    .matches(/^\d{2} \/ \d{2}$/, 'Enter a valid expiry date (MM / YY)')
    .test('valid-month', 'Invalid month', (val) => {
      const month = parseInt(val?.split(' / ')[0] ?? '0', 10);
      return month >= 1 && month <= 12;
    })
    .test('not-expired', 'Your card has expired', (val) => {
      if (!val?.match(/^\d{2} \/ \d{2}$/)) return true;
      const [mm, yy] = val.split(' / ');
      const expiry = new Date(2000 + parseInt(yy, 10), parseInt(mm, 10) - 1, 1);
      const now = new Date();
      return expiry >= new Date(now.getFullYear(), now.getMonth(), 1);
    }),
});

export const DatatransSecureFieldsForm = React.forwardRef<
  DatatransSecureFieldsFormHandle,
  DatatransSecureFieldsFormProps
>(
  (
    {
      onSuccess,
      onError,
      onInitialisingChange,
      onSubmitValidationFailed,
      basketId,
      country,
      language,
      isVisible,
      guestEmail,
      billingAddress,
    },
    ref
  ) => {
    const [showIframeValidationErrors, setShowIframeValidationErrors] = useState(false);

    const {
      errors: sdkErrors,
      isReady,
      sessionError,
      submitPayment,
      fieldValidity,
      fieldTouched,
      isInitialising,
      reinit,
    } = useDatatransSecureFields({
      basketId,
      country,
      language,
      isVisible,
      onSuccess,
      onError,
      onSubmitValidationFailed,
      styles: datatransIframeStyles,
    });

    const {
      control,
      handleSubmit,
      formState: { errors },
      trigger,
      reset,
    } = useForm<ExpiryFormValues>({
      mode: 'onBlur',
      reValidateMode: 'onChange',
      defaultValues: { cardholderName: '', expiry: '' },
      resolver: yupResolver(expirySchema),
    });

    // Reset expiry field whenever the card form is hidden (user switched payment method).
    // The Datatrans iframes are already reinitialised empty by the hook on next show.
    useEffect(() => {
      if (!isVisible) {
        reset({ cardholderName: '', expiry: '' });
        setShowIframeValidationErrors(false);
      }
    }, [isVisible, reset]);

    useEffect(() => {
      if (sessionError) {
        onError(sessionError);
      }
    }, [sessionError, onError]);

    // Notify the parent whenever the initialising state changes so it can
    // disable the Pay button while the Secure Fields session is being set up.
    const onInitialisingChangeRef = useRef(onInitialisingChange);
    useEffect(() => {
      onInitialisingChangeRef.current = onInitialisingChange;
    }, [onInitialisingChange]);

    useEffect(() => {
      onInitialisingChangeRef.current?.(isInitialising);
    }, [isInitialising]);

    // After reinit completes and iframes become ready, reset the validation error display
    // so blank fresh fields don't immediately show validation errors from the previous attempt.
    useEffect(() => {
      if (isReady) {
        setShowIframeValidationErrors(false);
      }
    }, [isReady]);

    const onValid = useCallback(
      ({ cardholderName, expiry }: ExpiryFormValues) => {
        const [month, year] = expiry.split(' / ');
        submitPayment(month, year, cardholderName, guestEmail, billingAddress);
      },
      [submitPayment, guestEmail, billingAddress]
    );

    const handleImperativeSubmit = useCallback(async () => {
      // Reveal hosted-field errors on first submit attempt (even before blur)
      // so empty untouched card number/CVV fields show feedback immediately.
      setShowIframeValidationErrors(true);
      const isValid = await trigger();
      if (isValid) {
        handleSubmit(onValid)();
      } else {
        // Parent sets button loading before calling submit(); clear it when
        // merchant-owned validation blocks submission.
        onSubmitValidationFailed?.();
      }
    }, [trigger, handleSubmit, onValid, onSubmitValidationFailed]);

    const handleImperativeReset = useCallback(() => {
      reset({ cardholderName: '', expiry: '' });
      setShowIframeValidationErrors(false);
    }, [reset]);

    useImperativeHandle(
      ref,
      () => ({ submit: handleImperativeSubmit, resetForm: handleImperativeReset, reinit }),
      [handleImperativeSubmit, handleImperativeReset, reinit]
    );

    return (
      <Box
        display={isVisible ? 'block' : 'none'}
        data-testid="DatatransSecureFieldsForm-Container"
        {...containerStyle}
      >
        <Flex justify="space-between" align="center" mb="lg">
          <Text {...titleStyle}>Card details</Text>
          <Image src="/images/datatrans/cards.svg" alt="Accepted cards" width={128} height={24} />
        </Flex>

        {/* ── Loading skeleton ─────────────────────────────────────────────── */}
        {!isReady && (
          <Flex
            direction="column"
            gap="md"
            aria-hidden="true"
            data-testid="DatatransSecureFieldsForm-Skeleton"
          >
            <Skeleton {...skeletonFieldStyle} />
            <Skeleton {...skeletonFieldStyle} />
            <HStack spacing="md" align="flex-start" w="full">
              <Skeleton flex={1} {...skeletonFieldStyle} />
              <Skeleton flex={1} {...skeletonFieldStyle} />
            </HStack>
          </Flex>
        )}

        <Flex
          direction="column"
          gap="md"
          visibility={isReady ? 'visible' : 'hidden'}
          h={isReady ? 'auto' : 0}
          overflow={isReady ? 'visible' : 'hidden'}
          aria-hidden={!isReady}
        >
          {/* ── Cardholder name ─────────────────────────────────────────────
            Merchant-owned plain text input. Value is passed to Datatrans via
            secureFields.submit() as 3D.cardholder.cardholderName for 3DS. */}
          <Controller
            name="cardholderName"
            control={control}
            render={({ field }) => {
              const error = errors.cardholderName?.message;
              return (
                <FormControl isInvalid={!!error} position="relative" zIndex={0}>
                  <ChakraInput
                    {...field}
                    id="cardholderName"
                    placeholder="Cardholder name"
                    autoComplete="cc-name"
                    data-testid="DatatransSecureFieldsForm-CardholderName"
                    isInvalid={!!error}
                    errorBorderColor="error"
                    {...iframeInputShellStyle}
                    isReadOnly={false}
                    tabIndex={0}
                    aria-hidden={undefined}
                    userSelect={undefined}
                    cursor={undefined}
                    _placeholder={{
                      color: '#1C1C1C',
                      fontFamily: 'Proxima Nova, helvetica, arial, sans-serif',
                      fontWeight: '400',
                      fontSize: '1rem',
                      lineHeight: '150%',
                    }}
                  />
                  {error && (
                    <Flex w="full">
                      <FormErrorMessage {...errorMessageStyle}>{error}</FormErrorMessage>
                    </Flex>
                  )}
                </FormControl>
              );
            }}
          />

          {/* ── Card number ─────────────────────────────────────────────────────
            Datatrans injects an iframe into the div with id="datatrans-cardNumber".
            No floating label — Datatrans renders the placeholder text "Card number"
            directly inside the iframe via setPlaceholder() in the ready handler.
            The shell border switches to error colour once the field has been
            touched (blurred) and Datatrans reports it as invalid. */}
          {(() => {
            const isCardNumberInvalid =
              (fieldTouched.cardNumber || showIframeValidationErrors) &&
              fieldValidity.cardNumber !== true;
            const cardNumberErrorMessage =
              sdkErrors.cardNumber ?? (isCardNumberInvalid ? 'Card number is invalid' : undefined);
            return (
              <FormControl
                isInvalid={isCardNumberInvalid || !!sdkErrors.cardNumber}
                {...iframeFormControlStyle}
              >
                <InputGroup position="relative">
                  <ChakraInput
                    id="datatrans-cardNumber-shell"
                    isReadOnly
                    tabIndex={-1}
                    aria-hidden="true"
                    errorBorderColor="error"
                    {...(isCardNumberInvalid ? iframeInputShellErrorStyle : iframeInputShellStyle)}
                  />
                  <Box
                    {...iframeOverlayStyle}
                    id="datatrans-cardNumber"
                    data-testid="DatatransSecureFieldsForm-CardNumber"
                  />
                </InputGroup>
                {cardNumberErrorMessage && (
                  <Flex w="full">
                    <FormErrorMessage {...errorMessageStyle}>
                      {cardNumberErrorMessage}
                    </FormErrorMessage>
                  </Flex>
                )}
              </FormControl>
            );
          })()}

          {/* ── Expiry date + CVV ────────────────────────────────────────────
            Side-by-side on the last row, matching the design.
            ExpiryInput is merchant-owned; CVV is a Datatrans iframe. */}
          <HStack spacing="md" align="flex-start" w="full">
            <Box flex={1}>
              <Controller
                name="expiry"
                control={control}
                render={({ field }) => (
                  <ExpiryInput
                    value={field.value}
                    onChange={field.onChange}
                    onBlur={field.onBlur}
                    error={errors.expiry?.message}
                  />
                )}
              />
            </Box>

            <Box flex={1}>
              {(() => {
                const isCvvInvalid =
                  (fieldTouched.cvv || showIframeValidationErrors) && fieldValidity.cvv !== true;
                const cvvErrorMessage =
                  sdkErrors.cvv ?? (isCvvInvalid ? 'CVV is invalid' : undefined);
                return (
                  <FormControl
                    isInvalid={isCvvInvalid || !!sdkErrors.cvv}
                    {...iframeFormControlStyle}
                  >
                    {/* No floating label — Datatrans renders "CVV" placeholder via setPlaceholder() */}
                    <InputGroup position="relative">
                      <ChakraInput
                        id="datatrans-cvv-shell"
                        isReadOnly
                        tabIndex={-1}
                        aria-hidden="true"
                        errorBorderColor="error"
                        {...(isCvvInvalid ? iframeInputShellErrorStyle : iframeInputShellStyle)}
                      />
                      <Box
                        {...iframeOverlayStyle}
                        id="datatrans-cvv"
                        data-testid="DatatransSecureFieldsForm-CVV"
                      />
                    </InputGroup>
                    {cvvErrorMessage && (
                      <Flex w="full">
                        <FormErrorMessage {...errorMessageStyle}>
                          {cvvErrorMessage}
                        </FormErrorMessage>
                      </Flex>
                    )}
                  </FormControl>
                );
              })()}
            </Box>
          </HStack>
        </Flex>
      </Box>
    );
  }
);

DatatransSecureFieldsForm.displayName = 'DatatransSecureFieldsForm';
