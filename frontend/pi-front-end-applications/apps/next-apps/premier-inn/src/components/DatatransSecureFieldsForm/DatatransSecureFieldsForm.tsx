import {
  Box,
  FormControl,
  FormErrorMessage,
  FormLabel,
  HStack,
  Select,
  VStack,
} from '@chakra-ui/react';
import React, { useCallback, useEffect, useImperativeHandle, useState } from 'react';

import { useDatatransSecureFields } from '~hooks/use-datatrans-secure-fields';

export interface DatatransSecureFieldsFormHandle {
  submit: () => void;
}

interface DatatransSecureFieldsFormProps {
  onSuccess: (data: { transactionId: string; redirect?: string }) => void;
  onError: (error: Error) => void;
  basketId: string;
  country: string;
  language: string;
  isVisible: boolean;
}

const MONTHS = Array.from({ length: 12 }, (_, i) => String(i + 1).padStart(2, '0'));

const currentYear = new Date().getFullYear();
const YEARS = Array.from({ length: 10 }, (_, i) => String(currentYear + i));

export const DatatransSecureFieldsForm = React.forwardRef<
  DatatransSecureFieldsFormHandle,
  DatatransSecureFieldsFormProps
>(({ onSuccess, onError, basketId, country, language, isVisible }, ref) => {
  const { errors, sessionError, submitPayment } = useDatatransSecureFields({
    basketId,
    country,
    language,
    isVisible,
    onSuccess,
    onError,
  });

  const [expiryMonth, setExpiryMonth] = useState('');
  const [expiryYear, setExpiryYear] = useState('');
  const [localErrors, setLocalErrors] = useState<Record<string, string>>({});

  // Report script-level errors to the consumer
  useEffect(() => {
    if (sessionError) {
      onError(sessionError);
    }
  }, [sessionError, onError]);

  const handleSubmit = useCallback(() => {
    const validationErrors: Record<string, string> = {};

    if (!expiryMonth) {
      validationErrors.expiryMonth = 'Expiry month is required';
    }
    if (!expiryYear) {
      validationErrors.expiryYear = 'Expiry year is required';
    }

    if (Object.keys(validationErrors).length > 0) {
      setLocalErrors(validationErrors);
      return;
    }

    setLocalErrors({});
    submitPayment(expiryMonth, expiryYear, '');
  }, [expiryMonth, expiryYear, submitPayment]);

  // Expose submit method to parent via ref
  useImperativeHandle(
    ref,
    () => ({
      submit: handleSubmit,
    }),
    [handleSubmit]
  );

  return (
    <Box display={isVisible ? 'block' : 'none'} data-testid="DatatransSecureFieldsForm-Container">
      <VStack spacing="md" align="stretch">
        {/* Card Number iframe placeholder — Datatrans injects an iframe into this div by id */}
        <FormControl isInvalid={!!errors.cardNumber}>
          <FormLabel fontWeight="semibold" color="darkGrey1">
            Card number
          </FormLabel>
          <Box
            borderWidth="1px"
            borderColor={errors.cardNumber ? 'error' : 'lightGrey3'}
            borderRadius="md"
            h="3rem"
            w="full"
            overflow="hidden"
          >
            <Box
              id="datatrans-cardNumber"
              data-testid="DatatransSecureFieldsForm-CardNumber"
              h="full"
              w="full"
            />
          </Box>
          {errors.cardNumber && <FormErrorMessage>{errors.cardNumber}</FormErrorMessage>}
        </FormControl>

        {/* CVV iframe placeholder — Datatrans injects an iframe into this div by id */}
        <FormControl isInvalid={!!errors.cvv}>
          <FormLabel fontWeight="semibold" color="darkGrey1">
            CVV
          </FormLabel>
          <Box
            borderWidth="1px"
            borderColor={errors.cvv ? 'error' : 'lightGrey3'}
            borderRadius="md"
            h="3rem"
            w="full"
            overflow="hidden"
          >
            <Box id="datatrans-cvv" data-testid="DatatransSecureFieldsForm-CVV" h="full" w="full" />
          </Box>
          {errors.cvv && <FormErrorMessage>{errors.cvv}</FormErrorMessage>}
        </FormControl>

        {/* Merchant-owned expiry month and year inputs */}
        <HStack spacing="md" align="flex-start">
          <FormControl isInvalid={!!localErrors.expiryMonth}>
            <FormLabel fontWeight="semibold" color="darkGrey1">
              Expiry month
            </FormLabel>
            <Select
              placeholder="MM"
              value={expiryMonth}
              onChange={(e) => {
                setExpiryMonth(e.target.value);
                setLocalErrors((prev) => {
                  const nextErrors = { ...prev };
                  delete nextErrors.expiryMonth;
                  return nextErrors;
                });
              }}
              data-testid="DatatransSecureFieldsForm-ExpiryMonth"
              borderColor={localErrors.expiryMonth ? 'error' : 'lightGrey3'}
            >
              {MONTHS.map((month) => (
                <option key={month} value={month}>
                  {month}
                </option>
              ))}
            </Select>
            {localErrors.expiryMonth && (
              <FormErrorMessage>{localErrors.expiryMonth}</FormErrorMessage>
            )}
          </FormControl>

          <FormControl isInvalid={!!localErrors.expiryYear}>
            <FormLabel fontWeight="semibold" color="darkGrey1">
              Expiry year
            </FormLabel>
            <Select
              placeholder="YYYY"
              value={expiryYear}
              onChange={(e) => {
                setExpiryYear(e.target.value);
                setLocalErrors((prev) => {
                  const nextErrors = { ...prev };
                  delete nextErrors.expiryYear;
                  return nextErrors;
                });
              }}
              data-testid="DatatransSecureFieldsForm-ExpiryYear"
              borderColor={localErrors.expiryYear ? 'error' : 'lightGrey3'}
            >
              {YEARS.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </Select>
            {localErrors.expiryYear && (
              <FormErrorMessage>{localErrors.expiryYear}</FormErrorMessage>
            )}
          </FormControl>
        </HStack>
      </VStack>
    </Box>
  );
});

DatatransSecureFieldsForm.displayName = 'DatatransSecureFieldsForm';
