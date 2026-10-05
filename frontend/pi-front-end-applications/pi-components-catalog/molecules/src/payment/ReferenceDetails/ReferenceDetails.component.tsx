import { Box, Text } from '@chakra-ui/react';
import { Input } from '@whitbread-eos/atoms';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

type ReferenceDetailsType = {
  reference: string;
  purchaseOrderNumber: string;
  [key: string]: string;
};

type FieldsErrorsType = {
  reference: string;
  purchaseOrderNumber: string;
  [key: string]: string;
};

interface Props {
  referenceDetails: ReferenceDetailsType;
  updateReference: Dispatch<SetStateAction<ReferenceDetailsType>>;
  setReferenceDetailsErrors?: (value: boolean) => void;
}

export default function ReferenceDetails({
  referenceDetails,
  updateReference,
  setReferenceDetailsErrors,
}: Readonly<Props>) {
  const baseDataTestId = 'ReferenceDetails';
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const [fieldsErrors, setFieldsErrors] = useState<FieldsErrorsType>({
    reference: '',
    purchaseOrderNumber: '',
  });

  const inputTypographyStyles = {
    inputElementStyles: getTypographyProps({}, inputFieldSemanticTypography),
  };

  useEffect(() => {
    const newState: any = {};
    let refDetailsError: any = false;
    Object.keys(referenceDetails).forEach((key: keyof ReferenceDetailsType) => {
      if (referenceDetails[key].length > 50) {
        if (fieldsErrors[key] === '') {
          newState[key] =
            key === 'reference'
              ? t('booking.bac.referenceDetails.maxLengthMessage')
              : t('booking.bac.purchaseOrder.maxLengthMessage');
          refDetailsError = true;
        }
      }
      newState[key] = '';
    });
    if (Object.keys(newState).length) {
      setFieldsErrors((data) => ({
        ...data,
        ...newState,
      }));
      if (setReferenceDetailsErrors) {
        setReferenceDetailsErrors(refDetailsError);
      }
    }
  }, [referenceDetails]);

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Container')} mb="2xl">
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'Title')}
        {...titleLayoutStyles}
        {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
      >
        {t('booking.bac.referenceDetails')}
      </Text>
      <Box maxW="var(--chakra-space-breakpoint-m)" mb="md">
        <Input
          data-testid={formatDataTestId(baseDataTestId, 'CustomerReference')}
          type="text"
          placeholderText={`${t('booking.bac.customerReference')} ${t(
            'booking.login.labelOptional'
          )}`}
          name="reference"
          error={fieldsErrors.reference}
          value={referenceDetails.reference}
          styles={inputTypographyStyles}
          onChange={(val) => updateReference({ ...referenceDetails, reference: val })}
        />
      </Box>

      <Box maxW="var(--chakra-space-breakpoint-m)">
        <Input
          data-testid={formatDataTestId(baseDataTestId, 'PurchaseOrderNumber')}
          type="text"
          placeholderText={`${t('booking.bac.purchaseOrder')} ${t('booking.login.labelOptional')}`}
          name="purchaseOrderNumber"
          error={fieldsErrors.purchaseOrderNumber}
          value={referenceDetails.purchaseOrderNumber}
          styles={inputTypographyStyles}
          onChange={(val) => updateReference({ ...referenceDetails, purchaseOrderNumber: val })}
        />
      </Box>
    </Box>
  );
}

const titleLayoutStyles = {
  mb: 'md',
  color: 'darkGrey1',
};

const titleLegacyTypography = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '3',
};

const titleSemanticTypography = {
  textStyle: 'title-m-emphasis',
} as const;

const inputFieldSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;
