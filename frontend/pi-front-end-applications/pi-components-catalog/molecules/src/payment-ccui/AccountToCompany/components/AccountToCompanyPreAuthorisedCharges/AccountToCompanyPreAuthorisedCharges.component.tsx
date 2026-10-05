import { Box, BoxProps, Divider, Text } from '@chakra-ui/react';
import type { PreAuthorisedChargesData } from '@whitbread-eos/api';
import { Checkbox, Info, Input, Notification } from '@whitbread-eos/atoms';
import { analytics } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';
import { string, StringSchema } from 'yup';

import PRE_AUTHORISED_CHARGES from './preAuthorisedCharges';

interface Props {
  setPreAuthorisedCharges: (charges: PreAuthorisedChargesData[]) => void;
  setCompanyReferenceError: Dispatch<SetStateAction<boolean>>;
  setACCompanyReference: Dispatch<SetStateAction<string>>;
  initialCharges?: PreAuthorisedChargesData[];
  initialCompanyReference?: string;
  isFromChangePaymentBIC?: boolean;
}

const COMPANY_REFERENCE_VALIDATIONS = {
  MATCHES: /^[a-zA-Z0-9\s.,:;!?"*%=+£$€¥&@#()\-'À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/g,
  MAX: 50,
};

export default function AccountToCompanyPreAuthorisedCharges({
  setPreAuthorisedCharges,
  setCompanyReferenceError,
  setACCompanyReference,
  initialCharges,
  initialCompanyReference,
  isFromChangePaymentBIC = false,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [charges, setCharges] = useState<PreAuthorisedChargesData[]>(initialCharges ?? []);
  const [companyReference, setCompanyReference] = useState<string>(initialCompanyReference ?? '');
  const [referenceError, setReferenceError] = useState<string>('');
  const companyReferenceValidationSchema: StringSchema = string().when('$value', ([value]) =>
    value?.length > 0
      ? string()
          .matches(
            COMPANY_REFERENCE_VALIDATIONS.MATCHES,
            t(`ccui.accountToCompanyReference.invalidCharactersError`)
          )
          .max(
            COMPANY_REFERENCE_VALIDATIONS.MAX,
            t(`ccui.accountToCompanyReference.maxCharactersError`)
          )
      : string().notRequired()
  );

  useEffect(() => {
    return () => {
      // reset the error state when the component is removed
      setCompanyReferenceError(false);
    };
  }, []);

  const handleInputErrors = async (value: string, fieldValidation: StringSchema): Promise<void> => {
    setReferenceError('');
    const validationResult = await fieldValidation
      .validate(value.toUpperCase(), { context: { value } })
      .catch((err) => {
        return err;
      });

    const hasValidationError = validationResult?.errors
      ? validationResult.errors.length > 0
      : false;
    setCompanyReferenceError(hasValidationError);
    setReferenceError(validationResult.errors?.[0] ?? '');
  };

  const onChange = (val: string) => {
    setCompanyReference(val);
    setACCompanyReference(val);
    handleInputErrors(val, companyReferenceValidationSchema);
  };

  return (
    <Box data-testid="AccountToCompanyPreAuthorisedCharges" my={16}>
      {!isFromChangePaymentBIC && (
        <>
          <Text {...titleStyles}>{t('ccui.accountToCompanyCharges.title')}</Text>
          <Box {...optionsWrapperStyles} data-testid="AccountToCompanyPreAuthorisedCharges-Options">
            {renderCheckboxes()}
          </Box>
        </>
      )}
      <Box mt={16}>
        <Text {...titleStyles}>{t('ccui.accountToCompanyReference.title')}</Text>
        <Text {...descriptionStyles}>{t('ccui.accountToCompanyReference.description')}</Text>
        <Input
          name="companyReference"
          value={companyReference}
          onChange={onChange}
          label={t('ccui.accountToCompanyReference.placeholder')}
          placeholderText={t('ccui.accountToCompanyReference.placeholder')}
          error={referenceError}
          styles={{ inputWrapperStyles, inputElementStyles }}
        />
      </Box>

      <Box mt={16} w={{ lg: '50.5rem' }}>
        <Notification
          maxWidth="full"
          variant="info"
          status="info"
          description={t('ccui.accountToCompanyCharges.notification')}
          svg={<Info />}
        />
      </Box>
    </Box>
  );

  function renderCheckboxes() {
    const allowances: string[] = charges.map((charge) => charge.label);

    analytics.update({
      allowances: allowances,
    });

    return PRE_AUTHORISED_CHARGES.map((option, index) => {
      const isLast = index === PRE_AUTHORISED_CHARGES.length - 1;
      const isOptionChecked = charges.find((charge) => charge.id === option.id) !== undefined;

      return (
        <Box
          key={option.id}
          data-testid={`AccountToCompanyPreAuthorisedCharges-option-${option.id}`}
        >
          <Box {...optionItemStyle}>
            <Checkbox
              onChange={() => onCheckboxChange(option)}
              data-testid={`AccountToCompanyPreAuthorisedCharges-option-${option.id}-item`}
              isChecked={isOptionChecked}
            >
              <Text {...textStyles}>{t(option.key)}</Text>
            </Checkbox>
          </Box>
          {!isLast && <Divider />}
        </Box>
      );
    });
  }

  function onCheckboxChange(option: PreAuthorisedChargesData) {
    const copy: PreAuthorisedChargesData[] = charges;
    const optionIndex = copy.findIndex((item) => item.id === option.id);

    if (optionIndex !== -1) {
      copy.splice(optionIndex, 1);
    } else {
      copy.push(option);
    }

    setCharges(copy);
    setPreAuthorisedCharges(copy);
  }
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

const inputWrapperStyles = {
  w: { mobile: 'full', lg: '24.5rem', xl: '26.25rem' },
  maxW: '26.25rem',
} as BoxProps;

const inputElementStyles = {
  color: 'darkGrey1',
  _disabled: {
    opacity: '0.4',
    cursor: 'not-allowed',
  },
};

const optionsWrapperStyles = {
  display: 'flex',
  flexDir: 'column',
  maxW: '26.25rem',
  border: '1px',
  borderColor: 'lightGrey4',
  w: {
    mobile: 'full',
    lg: '24.5rem',
    xl: '26.25rem',
  },
} as BoxProps;

const optionItemStyle = {
  w: {
    mobile: 'full',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  px: '1.188rem',
} as BoxProps;

const textStyles = {
  color: 'darkGrey1',
  marginInlineStart: '0.688rem',
};
