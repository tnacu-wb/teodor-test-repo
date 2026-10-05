import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { CreatePromotionCodeFormDetails } from '@whitbread-eos/organisms';

import { createPromotionFormConfig } from './createPromotionFormConfig';
import validateFormCreatePromotions from './formValidationCreatePromotion';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatDataTestId: jest.fn((base: string, suffix: string) => `${base}-${suffix}`),
}));

jest.mock('./formValidationCreatePromotion', () => ({
  __esModule: true,
  default: jest.fn(),
}));

describe('createPromotionFormConfig', () => {
  const getFormState = jest.fn();
  const onSubmit = jest.fn();
  const t: (id: string) => string = (key) => key;

  beforeEach(() => {
    jest.clearAllMocks();

    (validateFormCreatePromotions as jest.Mock).mockReturnValue({
      formValidationSchemaCreatePromotions: { mock: 'schema' },
    });
  });

  it('returns a valid FormProps configuration', () => {
    const config = createPromotionFormConfig({
      getFormState,
      defaultValues: { promotionCode: '' },
      onSubmit,
      baseDataTestId: 'CreatePromotionCodePage',
      t,
      currentLang: 'en',
    });

    expect(config).toEqual(
      expect.objectContaining({
        id: 'createPromotionCodeForm',
        testid: 'CreatePromotionCodePage-createPromotionCodeForm',
        defaultValues: { promotionCode: '' },
        validationSchema: { mock: 'schema' },
        getFormState,
      })
    );
  });

  it('defines dynamic form field correctly', () => {
    const config = createPromotionFormConfig({
      getFormState,
      defaultValues: {},
      onSubmit,
      baseDataTestId: 'CreatePromotionCodePage',
      t,
      currentLang: 'en',
    });

    expect(config.elements.fields[0]).toEqual(
      expect.objectContaining({
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        name: 'create-promotion-form',
        testid: 'CreatePromotionCodePage-Form',
        Component: CreatePromotionCodeFormDetails,
      })
    );
  });

  it('wires onSubmit action correctly', () => {
    const config = createPromotionFormConfig({
      getFormState,
      defaultValues: {},
      onSubmit,
      baseDataTestId: 'CreatePromotionCodePage',
      t,
      currentLang: 'en',
    });

    if (config.elements?.onSubmitAction) {
      config.elements.onSubmitAction({ test: true });
    }

    expect(onSubmit).toHaveBeenCalledWith({ test: true });
  });

  it('calls validation schema factory with translation and language', () => {
    createPromotionFormConfig({
      getFormState,
      defaultValues: {},
      onSubmit,
      baseDataTestId: 'CreatePromotionCodePage',
      t,
      currentLang: 'en',
    });

    expect(validateFormCreatePromotions).toHaveBeenCalledWith({
      t,
      currentLang: 'en',
    });
  });
});
