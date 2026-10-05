import { Box, BoxProps, Flex, Grid, GridProps, Text, VStack } from '@chakra-ui/react';
import { Company } from '@whitbread-eos/api';
import { Alert, Checkbox, Notification } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React from 'react';

import { COMPANY_MODAL_TYPE } from './modalType.enum';
import { getFormattedAddress } from './tableConfig';

interface Props {
  companyModalType: COMPANY_MODAL_TYPE;
  selectedCompany: Company | null;
}

function CompanyProfile({ companyModalType, selectedCompany }: Readonly<Props>) {
  const { t } = useTranslation();

  if (!selectedCompany) {
    return null;
  }

  return (
    <Box {...boxStyles}>
      <VStack spacing={4} {...topSectionStyles}>
        <Text {...companyNameStyles}>{selectedCompany.name}</Text>
        <Flex gap={4}>
          <Text {...fieldValueStyles}>{getFormattedAddress(selectedCompany.address)}</Text>
          <Text {...fieldValueStyles}>{`${t('ccui.companyModals.tel')}: ${
            selectedCompany.telephoneNumber
          }`}</Text>
        </Flex>
      </VStack>
      <Grid {...mainSectionStyles}>
        <VStack spacing={6} {...columnStyles(companyModalType)}>
          {createLabelValuePair(t('ccui.companyModals.profileType'), selectedCompany.profileType)}
          {createLabelValuePair(t('ccui.companyModals.corpID'), selectedCompany.corpId ?? 'NA')}
          {createLabelValuePair(t('ccui.companyModals.companyID'), selectedCompany.companyId ?? '')}
        </VStack>
        <VStack spacing={6} {...columnStyles(companyModalType)}>
          {createLabelValuePair(
            t('ccui.companyModals.language'),
            selectedCompany.language.toUpperCase()
          )}
          {createReadonlyCheckBox(t('ccui.companyModals.active'), selectedCompany.active)}
          {companyModalType === COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY &&
            createLabelValuePair(
              t('ccui.companyModals.arNumber'),
              selectedCompany.arNumber ?? 'NA'
            )}
        </VStack>
        <VStack {...columnStyles(companyModalType)}>
          {companyModalType === COMPANY_MODAL_TYPE.CONTRACT_RATE_CODE &&
            createReadonlyCheckBox(
              t('ccui.companyModals.negotiatedRates'),
              selectedCompany.negotiatedRateEnabled
            )}
          {companyModalType === COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY &&
            createReadonlyCheckBox(t('ccui.companyModals.restricted'), selectedCompany.restricted)}
          {companyModalType === COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY &&
            selectedCompany.restricted && (
              <Notification
                maxWidth="full"
                variant="alert"
                status="error"
                title={t('ccui.companyModals.restrictedReasonTitle')}
                description={selectedCompany.restrictedReason ?? ''}
                svg={<Alert />}
                overflow={'visible'}
              />
            )}
        </VStack>
      </Grid>
    </Box>
  );
}

const createLabelValuePair = (label: string, value: string) => {
  return (
    <VStack spacing={2.5} align="stretch">
      <Text {...fieldLabelStyles}>{label} </Text>
      <Text {...fieldValueStyles}>{value}</Text>
    </VStack>
  );
};

const createReadonlyCheckBox = (label: string, isChecked: boolean) => {
  return (
    <Checkbox isChecked={isChecked} isReadOnly checkboxWrapperStyles={{ my: '0' }}>
      <Text {...checkBoxLabelStyles}>{label} </Text>
    </Checkbox>
  );
};

const boxStyles: BoxProps = {
  h: 'full',
  alignItems: 'stretch',
  marginInlineStart: 'var(--chakra-space-8)',
  marginInlineEnd: 'var(--chakra-space-8)',
  marginTop: 'var(--chakra-space-6)',
};

const topSectionStyles: BoxProps = {
  borderBottom: 'var(--chakra-borders-1px)',
  borderColor: 'var(--chakra-colors-lightGrey2)',
  paddingBottom: 'var(--chakra-space-4)',
  alignItems: 'stretch',
};

const mainSectionStyles: GridProps = {
  paddingTop: 'var(--chakra-space-10)',
  flex: '1 0',
  alignItems: 'stretch',
  gridTemplateColumns: 'repeat(3, minmax(0, 1fr))',
};

const columnStyles = (companyModalType: COMPANY_MODAL_TYPE): BoxProps => {
  return {
    paddingInlineStart: 'var(--chakra-space-8)',
    paddingInlineEnd: 'var(--chakra-space-4)',
    alignItems: 'stretch',

    _notLast: {
      borderRight: 'var(--chakra-borders-1px)',
      borderColor: 'var(--chakra-colors-lightGrey2)',
    },
    _last:
      companyModalType === COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY
        ? {
            borderRight: 'var(--chakra-borders-1px)',
            borderColor: 'var(--chakra-colors-lightGrey2)',
          }
        : {},
  };
};

const companyNameStyles = {
  color: 'darkGrey1',
  fontSize: 'xl',
  fontWeight: '700',
  lineHeight: '3',
  fontFamily: 'header',
};

const fieldLabelStyles = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: '700',
  lineHeight: '3',
  fontFamily: 'header',
};

const fieldValueStyles = {
  color: 'darkGrey2',
  fontSize: 'lg',
  fontWeight: '400',
  lineHeight: '3',
  fontFamily: 'header',
};

const checkBoxLabelStyles = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: '400',
  lineHeight: '3',
  fontFamily: 'header',
};

export default CompanyProfile;
