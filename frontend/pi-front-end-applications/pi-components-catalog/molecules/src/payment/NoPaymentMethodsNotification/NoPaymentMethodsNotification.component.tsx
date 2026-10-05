import { Box, BoxProps, Text, TextProps } from '@chakra-ui/react';
import { Error, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

export default function NoPaymentMethodsNotification() {
  const { t } = useTranslation(['common']);
  const baseDataTestId = 'NoPaymentOptions';

  return (
    <>
      <Text data-testid={formatDataTestId(baseDataTestId, 'HeaderText')} {...headerProps}>
        {t('booking.header.payment')}
      </Text>
      <Notification
        variant="error"
        status="error"
        description={
          <Box data-testid={formatDataTestId(baseDataTestId, 'ErrorBox')}>
            <Text data-testid={formatDataTestId(baseDataTestId, 'TitleText')} {...titleTextProps}>
              {t('cc.noMethods')}
            </Text>
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'DescriptionText')}
              {...descriptionTextProps}
            >
              {t('cc.noMethodsAuthorised')}
            </Text>
          </Box>
        }
        svg={<Error />}
        wrapperStyles={paymentTypeInfoMsgStyle}
      />
    </>
  );
}

const paymentTypeInfoMsgStyle = {
  w: {
    mobile: 'full',
    xs: 'full',
    md: 'full',
    lg: 'full',
    xl: 'full',
  },
} as BoxProps;

const headerProps = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
  lineHeight: 5,
  marginBottom: 'lg',
} as BoxProps;

const titleTextProps = {
  fontSize: 'sm',
  fontWeight: 'semibold',
  lineHeight: 2,
} as TextProps;

const descriptionTextProps = {
  fontSize: 'sm',
  lineHeight: 2,
} as TextProps;
