import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export declare type validateOverrideFormParams = {
  t: (id: string) => string;
};

export default function validateAgentOverrideForm(params: validateOverrideFormParams): any {
  const { t } = params;
  const formValidationObject = {
    selectReason: yup.string().required(),

    callerName: yup
      .string()
      .matches(
        FORM_VALIDATIONS.AGENT_OVERRIDE_MODAL_TEXT_INPUT.MATCHES,
        t('ccui.manageBooking.options.agentOverrideModal.callerName.invalid')
      )
      .max(
        FORM_VALIDATIONS.AGENT_OVERRIDE_MODAL_TEXT_INPUT.MAX,
        t('ccui.manageBooking.options.agentOverrideModal.callerName.max')
      ),
    managerName: yup.string().when('managerName', {
      is: (managerName: string) => managerName != '',
      then: yup
        .string()
        .matches(
          FORM_VALIDATIONS.AGENT_OVERRIDE_MODAL_TEXT_INPUT.MATCHES,
          t('ccui.manageBooking.options.agentOverrideModal.managerName.invalid')
        )
        .max(
          FORM_VALIDATIONS.AGENT_OVERRIDE_MODAL_TEXT_INPUT.MAX,
          t('ccui.manageBooking.options.agentOverrideModal.managerName.max')
        ),
      otherwise: yup.string().notRequired(),
    }),
  };

  const formValidationSchema = yup.object().shape(formValidationObject, [
    ['selectReason', 'selectReason'],
    ['callerName', 'callerName'],
    ['managerName', 'managerName'],
  ]);

  return { formValidationObject, formValidationSchema };
}
