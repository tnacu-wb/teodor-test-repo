import { Box, Flex, Heading } from '@chakra-ui/react';
import { BOOKERS_REFERENCE_MAX_LENGTH, BookersReferencesDetailsType } from '@whitbread-eos/api';
import { Input } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction, useState } from 'react';

import { descriptionStyles, subHeaderStyles } from '../styles';

interface Props {
  bookerReferencesDetails: BookersReferencesDetailsType;
  setBookerReferencesDetails: Dispatch<SetStateAction<BookersReferencesDetailsType>>;
  setHasError: Dispatch<SetStateAction<boolean>>;
}

export default function BookersReferenceDetails({
  bookerReferencesDetails,
  setBookerReferencesDetails,
  setHasError,
}: Readonly<Props>) {
  const [errorPurchaseOrderNumber, setErrorPurchaseOrderNumber] = useState<string>('');
  const [errorCompanyReference, setErrorCompanyReference] = useState<string>('');

  const { t } = useTranslation(['common']);

  return (
    <Flex mb={'5xl'} flexDir="column" data-testid="bookersReferenceDetailsSection" mt={16}>
      <Box pb={4} data-testid="bookersReferenceDetailsSection_purchaseOrderNumber_title">
        <Heading as="h3" {...subHeaderStyles}>
          {t('ccui.purchaseOrderNumber.title')}
        </Heading>
      </Box>
      <Box pb={8} data-testid="bookersReferenceDetailsSection_purchaseOrderNumber_info">
        <Heading
          as="h6"
          {...descriptionStyles}
          data-testid="bookersReferenceDetailsSection_purchaseOrderNumber_info-heading"
        >
          {t('ccui.purchaseOrderNumber.description')}
        </Heading>
      </Box>
      <Box data-testid="bookersReferenceDetailsSection_purchaseOrderNumber_input" {...boxStyles}>
        <Input
          name="purchaseOrderNumber"
          value={bookerReferencesDetails.purchaseOrderNumber}
          onChange={onPurchaseOrderNumberChange}
          placeholderText={t('ccui.purchaseOrderNumber.placeholder')}
          error={errorPurchaseOrderNumber}
        />
      </Box>
      <Box mt={16} pb={4} data-testid="bookersReferenceDetailsSection_companyReference_title">
        <Heading as="h3" {...subHeaderStyles}>
          {t('ccui.companyReference.title')}
        </Heading>
      </Box>
      <Box pb={8} data-testid="bookersReferenceDetailsSection_companyReference_info">
        <Heading
          as="h6"
          {...descriptionStyles}
          data-testid="bookersReferenceDetailsSection_companyReference_info-heading"
        >
          {t('ccui.companyReference.description')}
        </Heading>
      </Box>
      <Box
        data-testid="bookersReferenceDetailsSection_companyReference_input"
        pt={6}
        {...boxStyles}
      >
        <Input
          name="companyReference"
          value={bookerReferencesDetails.companyReference}
          onChange={onCompanyReferenceChange}
          placeholderText={t('ccui.companyReference.placeholder')}
          error={errorCompanyReference}
        />
      </Box>
    </Flex>
  );

  function onPurchaseOrderNumberChange(purchaseOrderNumber: string) {
    validateBookersReferenceDetails(
      purchaseOrderNumber,
      BOOKERS_REFERENCE_MAX_LENGTH,
      t('ccui.purchaseOrderNumber.maxLengthMessage'),
      setErrorPurchaseOrderNumber
    );
    setBookerReferencesDetails((prevState) => ({
      ...prevState,
      purchaseOrderNumber,
    }));
  }

  function onCompanyReferenceChange(companyReference: string) {
    validateBookersReferenceDetails(
      companyReference,
      BOOKERS_REFERENCE_MAX_LENGTH,
      t('ccui.companyReference.maxLengthMessage'),
      setErrorCompanyReference
    );
    setBookerReferencesDetails((prevState) => ({
      ...prevState,
      companyReference,
    }));
  }

  function validateBookersReferenceDetails(
    name: string,
    lengthLimit: number,
    limitMessage: string,
    handleError: Dispatch<SetStateAction<string>>
  ) {
    if (name.length > lengthLimit) {
      handleError(limitMessage);
      setHasError(true);
    } else {
      handleError('');
      setHasError(false);
    }
  }
}

const boxStyles = {
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '27.563rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
};
