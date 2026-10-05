import { Text } from '@chakra-ui/react';
import { Input } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

interface Props {
  value: string;
}
export default function Discount({ value }: Readonly<Props>) {
  const { t } = useTranslation();
  return (
    <>
      <Text {...titleStyles}>{t('ccui.payment.confirmBooking.discount.title')}</Text>
      <Text {...contentStyles}>{t('ccui.payment.discount.description')}</Text>
      <Input
        name="amend-payment-discount"
        isDisabled={true}
        value={value}
        styles={{ inputElementStyles }}
      />
    </>
  );
}

const titleStyles = {
  fontSize: '2xl',
  lineHeight: '4',
  fontWeight: 'semibold',
  color: 'darkGrey1',
  paddingBottom: 'md',
};

const contentStyles = {
  fontSize: 'md',
  lineHeight: '3',
  fontWeight: 'normal',
  color: 'darkGrey1',
  paddingBottom: 'sm',
};
const inputElementStyles = {
  width: '24.5rem',
};
