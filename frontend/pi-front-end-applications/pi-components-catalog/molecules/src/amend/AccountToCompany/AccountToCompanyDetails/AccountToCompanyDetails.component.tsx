import { Text } from '@chakra-ui/react';
import { Input, Notification, Info, Button } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React from 'react';

interface Props {
  data: { number: string; name: string; address: string; postcode: string };
}
export default function AccountToCompanyDetails({ data }: Readonly<Props>) {
  const baseDataTestId = 'amend-payment-a2c';
  const { t } = useTranslation();
  return (
    <>
      <Text data-testid={`${baseDataTestId}_title`} {...titleStyles}>
        {t('ccui.accountToCompany.title')}
      </Text>
      <Input
        data-testid={`${baseDataTestId}_number`}
        name="a2c-number"
        isDisabled={true}
        value={data.number}
        styles={{
          inputWrapperStyles: inputStyles,
        }}
      />
      <Input
        data-testid={`${baseDataTestId}_name`}
        name="a2c-name"
        isDisabled={true}
        value={data.name}
        styles={{
          inputWrapperStyles: inputStyles,
        }}
      />
      <Input
        data-testid={`${baseDataTestId}_address`}
        name="a2c-address"
        isDisabled={true}
        value={data.address}
        styles={{
          inputWrapperStyles: inputStyles,
        }}
      />
      <Input
        data-testid={`${baseDataTestId}_postcode`}
        name="a2c-postcode"
        isDisabled={true}
        value={data.postcode}
        styles={{
          inputWrapperStyles: inputStyles,
        }}
      />
      <Notification
        status="info"
        title={t('ccui.accountToCompanyFields.notRestrictedTitle')}
        description={t('ccui.accountToCompanyFields.notRestrictedDescription')}
        variant="info"
        svg={<Info />}
        wrapperStyles={{ width: '50.5rem', my: 'lg' }}
      />
      <Button size="md" variant="primary" isDisabled={true}>
        {t('ccui.accountToCompanyFields.changeCompany')}
      </Button>
    </>
  );
}

const titleStyles = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '3',
  color: 'darkGrey1',
  paddingBottom: 'lg',
};

const inputStyles = {
  paddingBottom: 'md',
  w: '24.5rem',
};
