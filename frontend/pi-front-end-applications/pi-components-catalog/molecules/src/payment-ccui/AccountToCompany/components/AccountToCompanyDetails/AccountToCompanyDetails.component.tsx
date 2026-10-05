import type { BoxProps, FlexProps } from '@chakra-ui/react';
import { Flex, Text, VStack } from '@chakra-ui/react';
import { Company, CompanyResults, paymentOptions, SEARCH_A2C_COMPANIES } from '@whitbread-eos/api';
import { Button, InfoMessage, Input } from '@whitbread-eos/atoms';
import { useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import { string, StringSchema } from 'yup';

import CompanySelection from '../../../../common/CompanySelection';
import { COMPANY_MODAL_TYPE } from '../../../../common/CompanySelection/modalType.enum';

enum FieldType {
  ACCOUNT_NUMBER = 'accountNumber',
  COMPANY_NAME = 'companyName',
}

const ACCOUNT_NUMBAR_VALIDATIONS = {
  MATCHES: /^[a-zA-Z0-9~-]+$/g,
  MIN: 5,
  MAX: 20,
};

const COMPANY_NAME_VALIDATIONS = {
  MATCHES: /^[a-zA-Z0-9\s.,:;_!?"*%=+£$€¥&@#()\-'À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/g,
  MIN: 2,
  MAX: 50,
};

interface Props {
  hotelId: string;
  selectedPaymentDetail: string;
  setVerifiedCompany: Dispatch<SetStateAction<Company | null>>;
}

export default function AccountToCompanyDetails({
  hotelId,
  selectedPaymentDetail,
  setVerifiedCompany,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [accountNumber, setAccountNumber] = useState<string>('');
  const [companyName, setCompanyName] = useState<string>('');
  const [accountNumberError, setAccountNumberError] = useState<string>('');
  const [companyNameError, setCompanyNameError] = useState<string>('');
  const [searchError, setSearchError] = useState<string>('');
  const [queryEnabled, setQueryEnabled] = useState<boolean>(false);
  const [showCompanySelectionModal, setShowCompanySelectionModal] = useState(false);
  const [resultsData, setResultsData] = useState({
    tooManyResults: false,
    companies: [],
  } as CompanyResults);
  const companyNameInputRef = useRef<HTMLInputElement>(null);
  const isSearchDisabled = selectedPaymentDetail !== paymentOptions.PAY_ON_ARRIVAL;

  const inputChangeValidationSchema = (fieldType: FieldType): StringSchema => {
    const validations =
      fieldType === FieldType.ACCOUNT_NUMBER
        ? ACCOUNT_NUMBAR_VALIDATIONS
        : COMPANY_NAME_VALIDATIONS;
    return string().when('$value', ([value]) =>
      value?.length > 0
        ? string()
            .matches(
              validations.MATCHES,
              t(`ccui.accountToCompany.${fieldType}.invalidCharactersError`)
            )
            .max(validations.MAX, t(`ccui.accountToCompany.${fieldType}.maxCharactersError`))
        : string().notRequired()
    );
  };

  const submitValidationSchema = (fieldType: FieldType): StringSchema => {
    const validations =
      fieldType === FieldType.ACCOUNT_NUMBER
        ? ACCOUNT_NUMBAR_VALIDATIONS
        : COMPANY_NAME_VALIDATIONS;
    return string().when('$value', ([value]) =>
      value?.length > 0
        ? string()
            .matches(
              validations.MATCHES,
              t(`ccui.accountToCompany.${fieldType}.invalidCharactersError`)
            )
            .max(validations.MAX, t(`ccui.accountToCompany.${fieldType}.maxCharactersError`))
            .min(validations.MIN, t(`ccui.accountToCompany.${fieldType}.minCharactersError`))
        : string().notRequired()
    );
  };

  const {
    data: companiesData,
    isFetching: companiesRequestFetching,
    isError: companiesRequestError,
  } = useQueryRequest(
    ['searchA2CPaymentCompanies', companyName, accountNumber, hotelId, 50],
    SEARCH_A2C_COMPANIES,
    {
      companyName,
      arNumber: accountNumber,
      hotelId,
      limit: 50,
    },
    { enabled: queryEnabled },
    undefined,
    true
  );

  useEffect(() => {
    if (!companiesRequestFetching && queryEnabled) {
      if (companiesRequestError) {
        setResultsData({ tooManyResults: false, companies: [] });
        setSearchError(t('ccui.accountToCompany.search.technicalError'));
      } else {
        const { companies, hasMore } = companiesData?.searchCompanies ?? {};
        validateCompanies(companies, hasMore);
      }
      setQueryEnabled(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [companiesRequestFetching, companiesData, companiesRequestError, queryEnabled]);

  const validateCompanies = (companies: Company[], tooManyResults: boolean) => {
    if (companies && companies.length > 0) {
      setResultsData({ tooManyResults, companies });
      setShowCompanySelectionModal(true);
    } else {
      setResultsData({ tooManyResults: false, companies: [] });
      setSearchError(t('ccui.accountToCompany.search.noResults'));
    }
  };

  const handleInputErrors = async (value: string, validationSchema: StringSchema) => {
    const validationResult = await validationSchema
      .validate(value.toUpperCase(), { context: { value } })
      .catch((err) => {
        return err;
      });

    const hasValidationError = validationResult?.errors && validationResult.errors.length > 0;
    return { hasError: hasValidationError, error: validationResult.errors?.[0] };
  };

  const onChange = async (val: string, fieldType: FieldType) => {
    setSearchError('');
    fieldType === FieldType.ACCOUNT_NUMBER ? setAccountNumber(val) : setCompanyName(val);

    const validationSchema = inputChangeValidationSchema(fieldType);
    const { hasError, error } = await handleInputErrors(val, validationSchema);
    const errorSetter =
      fieldType === FieldType.ACCOUNT_NUMBER ? setAccountNumberError : setCompanyNameError;
    hasError ? errorSetter(error) : errorSetter('');
  };

  const onAccountNumberChange = (val: string) => {
    onChange(val, FieldType.ACCOUNT_NUMBER);
  };

  const onCompanyNameChange = (val: string) => {
    onChange(val, FieldType.COMPANY_NAME);
  };

  const onSearchCompany = async () => {
    setSearchError('');
    if (accountNumber === '' && companyName === '') {
      setSearchError(t('ccui.accountToCompany.fieldsEmptyError'));
      return;
    }

    // check for errors in account number field
    const accountNumberValidationSchema = submitValidationSchema(FieldType.ACCOUNT_NUMBER);
    const { hasError: accountNumberHasError, error: accountNumberError } = await handleInputErrors(
      accountNumber,
      accountNumberValidationSchema
    );
    accountNumberHasError ? setAccountNumberError(accountNumberError) : setAccountNumberError('');

    // check for errors in company name field
    const companyNameValidationSchema = submitValidationSchema(FieldType.COMPANY_NAME);
    const { hasError: companyNameHasError, error: companyNameError } = await handleInputErrors(
      companyName,
      companyNameValidationSchema
    );
    companyNameHasError ? setCompanyNameError(companyNameError) : setCompanyNameError('');

    if (accountNumberHasError || companyNameHasError) {
      return;
    }

    setQueryEnabled(true);
  };

  const handleCompanySelectionModalClosed = () => {
    setShowCompanySelectionModal(false);
  };

  const handleCompanyVerified = (company: Company) => {
    setShowCompanySelectionModal(false);
    setVerifiedCompany(company);
  };

  return (
    <>
      <Flex {...wrapperStyles} data-testid="AccountToCompanyDetails">
        <Text {...titleStyles}>{t('ccui.accountToCompany.title')}</Text>
        <Text {...descriptionStyles}>{t('ccui.accountToCompany.description')}</Text>
        <VStack spacing={4} align="stretch" {...inputWrapperStyles}>
          <Input
            name="companyName"
            value={companyName}
            onChange={onCompanyNameChange}
            label={t('ccui.accountToCompany.companyName.placeholder')}
            placeholderText={t('ccui.accountToCompany.companyName.placeholder')}
            error={companyNameError}
            inputRef={companyNameInputRef}
            styles={{ inputElementStyles }}
            data-testid="AccountToCompanyDetails-CompanyName"
          />
          <Input
            name="accountNumber"
            value={accountNumber}
            onChange={onAccountNumberChange}
            label={t('ccui.accountToCompany.accountNumber.placeholder')}
            placeholderText={t('ccui.accountToCompany.accountNumber.placeholder')}
            error={accountNumberError}
            styles={{ inputElementStyles }}
            data-testid="AccountToCompanyDetails-AccountNumber"
          />
          {searchError && (
            <InfoMessage
              infoMessage={searchError}
              otherStyles={errorMessageStyles}
              data-testid="AccountToCompanyDetails-Error"
            />
          )}
        </VStack>
        <Button
          type="button"
          variant="tertiary"
          size="full"
          maxW={{ mobile: '18rem', lg: '18rem', xl: '19.375rem' }}
          mt={4}
          onClick={onSearchCompany}
          isDisabled={isSearchDisabled}
          data-testid="AccountToCompanyDetails-Search"
        >
          {t('ccui.accountToCompany.search')}
        </Button>
      </Flex>
      {showCompanySelectionModal && (
        <CompanySelection
          showCompanySelectionModal={showCompanySelectionModal}
          companyModalType={COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY}
          companies={resultsData.companies}
          tooManyResults={resultsData.tooManyResults}
          finalFocusRef={companyNameInputRef}
          onClose={handleCompanySelectionModalClosed}
          onCompanyVerified={handleCompanyVerified}
        />
      )}
    </>
  );
}

const titleStyles = {
  color: 'darkGrey1',
  fontSize: 'xl',
  lineHeight: 3,
  fontWeight: 'semibold',
  mb: 'var(--chakra-space-4)',
};

const descriptionStyles = {
  color: 'darkGrey1',
  fontSize: 'md',
  lineHeight: 3,
  fontWeight: 'normal',
  mb: 'var(--chakra-space-8)',
};

const wrapperStyles = {
  flexDir: 'column',
  mb: '5xl',
} as FlexProps;

const inputWrapperStyles = {
  w: { mobile: 'full', lg: '24.5rem', xl: '26.25rem' },
  maxW: '26.25rem',
  mb: '4',
} as BoxProps;

const inputElementStyles = {
  color: 'darkGrey1',
};

const errorMessageStyles: BoxProps = {
  position: 'relative',
  color: 'darkGrey1',
  w: 'full',
  mb: '4',
};
