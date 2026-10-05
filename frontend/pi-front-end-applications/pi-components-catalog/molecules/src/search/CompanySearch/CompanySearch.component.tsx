import { BoxProps, StackProps, VStack } from '@chakra-ui/react';
import { Company, SEARCH_COMPANIES, SEARCH_COMPANY_BY_ID_OR_CORP_ID } from '@whitbread-eos/api';
import { Button, InfoMessage, Input } from '@whitbread-eos/atoms';
import { formatDataTestId, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, FocusEvent, SetStateAction, useEffect, useRef, useState } from 'react';
import { string } from 'yup';

import CompanySelection from '../../common/CompanySelection';
import { COMPANY_MODAL_TYPE } from '../../common/CompanySelection/modalType.enum';

interface SelectedCompany extends Company {
  searchCriteriaId?: string;
}

interface Props {
  showCompanyIdInput: boolean;
  setSearchDisabled: Dispatch<SetStateAction<boolean>>;
  contractRateCompanyState: [
    SelectedCompany | null,
    Dispatch<SetStateAction<SelectedCompany | null>>,
  ];
}

interface validationType {
  MATCHES: RegExp;
  MIN: number;
  MAX: number;
}

const COMPANY_NAME_VALIDATIONS: validationType = {
  MATCHES: /^[a-zA-Z0-9\s.,:;_!?"*%=+£$€¥&@#()\-'À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/g,
  MIN: 2,
  MAX: 50,
};

const COMPANY_ID_VALIDATIONS: validationType = {
  MATCHES: /^\d+$/,
  MIN: 2,
  MAX: 50,
};

export default function CompanySearch({
  showCompanyIdInput = false,
  setSearchDisabled,
  contractRateCompanyState,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const [inputNameValue, setInputNameValue] = useState('');
  const [inputIdValue, setInputIdValue] = useState('');

  const [disabledInput, setDisabledInput] = useState('');
  const [showCheckCompany, setShowCheckCompany] = useState(false);
  const [companyNameError, setCompanyNameError] = useState('');
  const [companyIdError, setCompanyIdError] = useState('');
  const [searchByName, setSearchByName] = useState(true);
  const [showCompanySelectionModal, setShowCompanySelectionModal] = useState(false);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [checkCompanyClicked, setCheckCompanyClicked] = useState(false);
  const [verifiedCompany, setVerifiedCompany] = useState<SelectedCompany | null>(null);
  const nameInputRef = useRef<HTMLInputElement>(null);
  const idInputRef = useRef<HTMLInputElement>(null);
  const [lastFocus, setLastFocus] = useState('');
  const inputNamePlaceholder = t('ccui.search.companyName.placeholder');
  const inputIdPlaceholder = t('ccui.search.companyId.placeholder');
  const baseDataTestId = 'CompanyName';
  const baseDataIdTestId = 'CompanyId';
  const companyNameInput = 'companyName';
  const companyIdInput = 'companyId';
  const [contractRateCompany, setContractRateCompany] = contractRateCompanyState;

  const inputNameChangeValidationSchema = string().when('$value', ([value]) =>
    value?.length > 0
      ? string()
          .matches(COMPANY_NAME_VALIDATIONS.MATCHES, t('ccui.company.invalidCompanyError'))
          .max(COMPANY_NAME_VALIDATIONS.MAX, t('ccui.company.characterLengthError'))
      : string().notRequired()
  );

  const inputIdChangeValidationSchema = string().when('$value', ([value]) =>
    value?.length > 0
      ? string()
          .matches(COMPANY_ID_VALIDATIONS.MATCHES, t('ccui.company.invalidCompanyError'))
          .max(COMPANY_ID_VALIDATIONS.MAX, t('ccui.company.characterLengthError'))
      : string().notRequired()
  );

  const submitValidationSchema = (inputValidation: validationType) =>
    string()
      .required(t('ccui.company.invalidCompanyError'))
      .matches(inputValidation.MATCHES, t('ccui.company.invalidCompanyError'))
      .min(inputValidation.MIN, t('ccui.company.minCharacterLengthError'))
      .max(inputValidation.MAX, t('ccui.company.characterLengthError'));

  const {
    data: companiesData,
    isFetching: companiesRequestFetching,
    isError: companiesRequestError,
    refetch: fetchCompanies,
  } = useQueryRequest(
    ['searchCompanies', inputNameValue, true, 50, 1],
    SEARCH_COMPANIES,
    {
      searchTerm: inputNameValue,
      negotiatedRateCompanies: true,
      limit: 50,
      offset: 1,
    },
    { enabled: false },
    undefined,
    true
  );

  const {
    data: companyData,
    isFetching: companyDataRequestFetching,
    isError: companyDataRequestError,
    refetch: fetchCompanyData,
  } = useQueryRequest(
    ['searchCompany', inputIdValue],
    SEARCH_COMPANY_BY_ID_OR_CORP_ID,
    {
      id: inputIdValue,
    },
    { enabled: false },
    undefined,
    true
  );

  useEffect(() => {
    if (searchByName && !companiesRequestFetching && checkCompanyClicked) {
      if (companiesRequestError) {
        setCompanies([]);
        setCompanyNameError(t('ccui.company.technicalerror'));
      } else {
        validateCompanies(companiesData?.searchCompanies?.companies);
      }

      setCheckCompanyClicked(false);
    }
  }, [
    companiesRequestFetching,
    companiesData,
    companiesRequestError,
    checkCompanyClicked,
    searchByName,
  ]);

  useEffect(() => {
    if (!searchByName && !companyDataRequestFetching && checkCompanyClicked) {
      if (companyDataRequestError) {
        setCompanies([]);
        setCompanyIdError(t('ccui.company.technicalerror'));
      } else if (companyData) {
        validateCompanies(
          companyData.companyProfileById.companyId ? [companyData.companyProfileById] : []
        );
      }

      setCheckCompanyClicked(false);
    }
  }, [
    companyDataRequestFetching,
    companyData,
    companyDataRequestError,
    checkCompanyClicked,
    searchByName,
  ]);

  useEffect(() => {
    if (contractRateCompany) {
      setVerifiedCompany(contractRateCompany);
      setInputNameValue(contractRateCompany.name);
      setInputIdValue(contractRateCompany.searchCriteriaId ?? '');
    }
  }, [contractRateCompany]);

  const validateCompanies = (companies: Company[]) => {
    if (companies && companies.length > 0) {
      setCompanies(companies);
      setShowCompanySelectionModal(true);
    } else {
      setCompanies([]);

      if (searchByName) {
        setCompanyNameError(t('ccui.company.nocompanies'));
      } else {
        setCompanyIdError(t('ccui.company.nocompanies'));
      }
    }
  };

  const handleFocusLost = (event: FocusEvent) => {
    if (!event.relatedTarget || !event.currentTarget.contains(event.relatedTarget)) {
      setShowCheckCompany(false);
      const searchEnabled =
        (verifiedCompany && verifiedCompany.name === inputNameValue) ||
        (inputNameValue === '' && inputIdValue === '');
      setSearchDisabled(!searchEnabled);
    }
    setLastFocus(event.target.id);
    setDisabledInput('');
  };

  const handleFocus = (event: FocusEvent) => {
    const isVerifiedCompany = searchByName
      ? inputNameValue === verifiedCompany?.name
      : inputIdValue === verifiedCompany?.corpId || inputIdValue === verifiedCompany?.companyId;
    setShowCheckCompany(!isVerifiedCompany);
    setSearchDisabled(!isVerifiedCompany);
    if (event.target.id === companyNameInput) {
      setDisabledInput(companyIdInput);
    } else if (event.target.id === companyIdInput) {
      setDisabledInput(companyNameInput);
    }
  };

  const handleInputErrors = async (value: string, fieldValidation: any, fieldType: string) => {
    const validationResult = await fieldValidation
      .validate(value.toUpperCase(), { context: { value } })
      .catch((err: any) => {
        return err;
      });

    const hasValidationError = validationResult?.errors && validationResult.errors.length > 0;
    if (fieldType === companyIdInput) {
      setCompanyIdError(validationResult?.errors?.[0] ?? '');
    } else {
      setCompanyNameError(validationResult?.errors?.[0] ?? '');
    }

    return { hasError: hasValidationError, error: validationResult.errors?.[0] };
  };

  const onChangeNameFn = (val: string) => {
    handleInputErrors(val, inputNameChangeValidationSchema, companyNameInput);
    const isVerifiedCompany = val === verifiedCompany?.name;
    setShowCheckCompany(!isVerifiedCompany);
    setSearchDisabled(!isVerifiedCompany);
    setContractRateCompany(isVerifiedCompany ? verifiedCompany : null);
    if (val) {
      setInputIdValue('');
      setCompanyIdError('');
    }
    setInputNameValue(val);
  };

  const onChangeIdFn = (val: string) => {
    handleInputErrors(val, inputIdChangeValidationSchema, companyIdInput);
    const isVerifiedCompany = [verifiedCompany?.corpId, verifiedCompany?.companyId].includes(val);
    setShowCheckCompany(!isVerifiedCompany);
    setSearchDisabled(!isVerifiedCompany);
    setContractRateCompany(isVerifiedCompany ? verifiedCompany : null);
    if (val) {
      setInputNameValue('');
      setCompanyNameError('');
    }
    setInputIdValue(val);
  };

  const handleCheckCompanyClicked = async () => {
    const searchValue = lastFocus === companyNameInput ? inputNameValue : inputIdValue;
    const validationSchema =
      lastFocus === companyNameInput ? COMPANY_NAME_VALIDATIONS : COMPANY_ID_VALIDATIONS;
    const { hasError } = await handleInputErrors(
      searchValue,
      submitValidationSchema(validationSchema),
      lastFocus
    );

    if (!hasError) {
      if (lastFocus === companyNameInput) {
        fetchCompanies();
      } else {
        fetchCompanyData();
      }
      setCheckCompanyClicked(true);
      setSearchByName(lastFocus === companyNameInput);
    }
  };

  const handleCompanySelectionModalClosed = () => {
    setShowCompanySelectionModal(false);
    setSearchDisabled(true);
    setShowCheckCompany(true);
  };

  const handleCompanyVerified = (company: Company) => {
    setInputNameValue(company.name);
    const searchCriteriaId = searchByName ? company.corpId : inputIdValue;

    if (inputNameValue) {
      setInputIdValue(company?.corpId ?? '');
    }
    setShowCompanySelectionModal(false);
    setSearchDisabled(false);
    setShowCheckCompany(false);
    setVerifiedCompany({ ...company, searchCriteriaId });
    setContractRateCompany({ ...company, searchCriteriaId });
  };

  return (
    <>
      <VStack
        spacing={4}
        align="stretch"
        alignSelf="end"
        {...checkCompanyStyles}
        data-testid={formatDataTestId(baseDataTestId)}
        onFocus={handleFocus}
        onBlur={handleFocusLost}
      >
        <Input
          name={companyNameInput}
          placeholderText={inputNamePlaceholder}
          value={inputNameValue}
          onChange={onChangeNameFn}
          label={inputNamePlaceholder}
          data-testid={formatDataTestId(baseDataTestId, 'Input')}
          styles={inputStyles}
          inputRef={nameInputRef}
          error={showCheckCompany && !!companyNameError}
          isDisabled={disabledInput === companyNameInput}
        />
        {showCheckCompany && companyNameError && (
          <InfoMessage
            infoMessage={companyNameError}
            otherStyles={errorMessageStyles}
            data-testid={formatDataTestId(baseDataTestId, 'Error')}
          />
        )}
        {showCompanyIdInput && (
          <Input
            name={companyIdInput}
            placeholderText={inputIdPlaceholder}
            value={inputIdValue}
            onChange={onChangeIdFn}
            label={inputIdPlaceholder}
            data-testid={formatDataTestId(baseDataIdTestId, 'Input')}
            styles={inputStyles}
            inputRef={idInputRef}
            error={showCheckCompany && !!companyIdError}
            isDisabled={disabledInput === companyIdInput}
          />
        )}
        {showCheckCompany && companyIdError && (
          <InfoMessage
            infoMessage={companyIdError}
            otherStyles={errorMessageStyles}
            data-testid={formatDataTestId(baseDataIdTestId, 'Error')}
          />
        )}
        {showCheckCompany && (
          <Button
            size="full"
            variant="tertiary"
            transition={'unset'}
            data-testid={formatDataTestId(baseDataTestId, 'CheckCompanyBtn')}
            isDisabled={companiesRequestFetching || companyDataRequestFetching}
            onClick={handleCheckCompanyClicked}
          >
            {t('ccui.company.checkCompany')}
          </Button>
        )}
      </VStack>
      {showCompanySelectionModal && (
        <CompanySelection
          showCompanySelectionModal={showCompanySelectionModal}
          companyModalType={COMPANY_MODAL_TYPE.CONTRACT_RATE_CODE}
          companies={companies}
          finalFocusRef={lastFocus === companyNameInput ? nameInputRef : idInputRef}
          onClose={handleCompanySelectionModalClosed}
          onCompanyVerified={handleCompanyVerified}
        />
      )}
    </>
  );
}

const checkCompanyStyles: StackProps = {
  w: {
    mobile: '21.4375rem',
    md: '17.625rem',
    lg: '17.625rem',
    xl: '26rem',
  },
};

const errorMessageStyles: BoxProps = {
  position: 'relative',
  w: 'full',
  marginTop: '0',
};

const inputStyles = {
  inputElementStyles: {
    p: '1rem',
    borderRadius: 'var(--chakra-space-radiusSmall)',
    borderColor: 'lightGrey1',
    color: 'darkGrey1',
    bg: 'baseWhite',
    boxShadow: {
      base: 'none',
      sm: '0px 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
    },
    _hover: {
      borderColor: 'darkGrey1',
    },
  },
};
