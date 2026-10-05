import { BoxProps, Box, Text, Flex } from '@chakra-ui/react';
import { Address, Company } from '@whitbread-eos/api';
import { Button, Info, Input, Notification } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction } from 'react';

interface Props {
  verifiedCompany: Company;
  setVerifiedCompany: Dispatch<SetStateAction<Company | null>>;
}

export default function AccountToCompanyFields({
  verifiedCompany,
  setVerifiedCompany,
}: Readonly<Props>) {
  const { name, address, arNumber } = verifiedCompany;
  const { postalCode } = address;
  const { t } = useTranslation(['common']);

  return (
    <Box data-testid="AccountToCompanyFields">
      <Text {...titleStyles}>{t('ccui.accountToCompanyFields.title')}</Text>
      <Flex {...inputWrapperStyles}>
        <Input
          name="companyName"
          value={name}
          placeholderText={t('ccui.accountToCompanyFields.placeholderName')}
          isDisabled
          styles={{ inputElementStyles }}
        />
        <Input
          name="arNumber"
          value={arNumber ?? ''}
          placeholderText={t('ccui.accountToCompanyFields.placeholderNumber')}
          isDisabled
          styles={{ inputElementStyles }}
        />
        <Input
          name="address"
          value={getFormattedAddress(address)}
          placeholderText={t('ccui.accountToCompanyFields.placeholderAddress')}
          isDisabled
          styles={{ inputElementStyles }}
        />
        <Input
          name="postalCode"
          value={postalCode}
          placeholderText={t('ccui.accountToCompanyFields.placeholderPostcode')}
          isDisabled
          styles={{ inputElementStyles }}
        />
      </Flex>

      <Box w={{ lg: '50.5rem' }}>
        <Notification
          maxWidth="full"
          variant="info"
          status="info"
          title={t('ccui.accountToCompanyFields.notRestrictedTitle')}
          description={t('ccui.accountToCompanyFields.notRestrictedDescription')}
          svg={<Info />}
        />
      </Box>

      <Button
        type="button"
        variant="tertiary"
        size="full"
        maxW={{ mobile: '18rem', lg: '18rem', xl: '19.375rem' }}
        onClick={() => {
          setVerifiedCompany(null);
        }}
        mt={8}
      >
        {t('ccui.accountToCompanyFields.changeCompany')}
      </Button>
    </Box>
  );
}

function getFormattedAddress(address: Address): string {
  const { addressLine1, addressLine2, addressLine3, addressLine4 = '', country } = address;
  return [addressLine1, addressLine2, addressLine3, addressLine4, country]
    .filter(Boolean)
    .join(', ');
}

const titleStyles = {
  color: 'darkGrey1',
  fontSize: 'xl',
  lineHeight: 3,
  fontWeight: 'semibold',
  mb: 'var(--chakra-space-8)',
};

const inputWrapperStyles = {
  w: { mobile: 'full', lg: '24.5rem', xl: '26.25rem' },
  maxW: '26.25rem',
  flexDirection: 'column',
  mb: '0.625rem',
} as BoxProps;

const inputElementStyles = {
  color: 'darkGrey1',
  _disabled: {
    opacity: '0.4',
  },
  mb: '1.875rem',
};
