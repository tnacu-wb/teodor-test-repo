import { FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import { CreatePromotionCodeFormDetails } from '@whitbread-eos/organisms';
import { formatDataTestId } from '@whitbread-eos/utils';

import validateFormCreatePromotions from './formValidationCreatePromotion';

interface CreatePromotionFormConfigType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: any, event: React.MouseEvent<HTMLButtonElement>) => void;
  baseDataTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
}

export const createPromotionFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestId,
  t,
  currentLang,
}: CreatePromotionFormConfigType) => {
  const { formValidationSchemaCreatePromotions } = validateFormCreatePromotions({ t, currentLang });

  const config = {
    id: 'createPromotionCodeForm',
    testid: formatDataTestId(baseDataTestId, 'createPromotionCodeForm'),
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'create-promotion-form',
          label: '',
          testid: formatDataTestId(baseDataTestId, 'Form'),
          Component: CreatePromotionCodeFormDetails,
        },
      ],
      onSubmitAction: onSubmit,
    },
    defaultValues,
    validationSchema: formValidationSchemaCreatePromotions,
    getFormState,
  } as FormProps;

  return config;
};
