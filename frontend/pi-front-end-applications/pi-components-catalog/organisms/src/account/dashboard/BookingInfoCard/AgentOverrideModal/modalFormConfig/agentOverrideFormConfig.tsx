import { BoxProps, StyleProps, Text, TextProps } from '@chakra-ui/react';
import type { OverrideReason } from '@whitbread-eos/api';
import { FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import { AgentOverrideButton } from '@whitbread-eos/molecules';
import { formatDataTestId } from '@whitbread-eos/utils';
import { Dispatch, SetStateAction } from 'react';

import validateAgentOverrideForm from './validateAgentOverrideForm';

interface AgentOverrideFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: FormProps['defaultValues']) => void;
  onClose: () => void;
  baseDataTestId: string;
  t: (id: string) => string;
  reasons: OverrideReason[];
  isManagerApprovalNeeded: boolean;
  setIsManagerApprovalNeeded: Dispatch<SetStateAction<boolean>>;
  resetForm?: number;
}

export const agentOverrideFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  onClose,
  baseDataTestId,
  resetForm,
  reasons,
  isManagerApprovalNeeded,
  setIsManagerApprovalNeeded,
  t,
}: AgentOverrideFormConfigArgsType) => {
  const { formValidationSchema } = validateAgentOverrideForm({ t });
  const mappedReasons = reasons
    .filter((reason) => reason.active)
    .map((reason) => {
      return { id: reason.code, label: reason.description };
    });

  const config = {
    id: 'agentOverrideForm',
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'selectReasonTitle',
          content: (
            <Text {...selectReasonStyle}>
              {t('ccui.manageBooking.options.agentOverrideModal.selectReason')}
            </Text>
          ),
        },
        {
          type: FORM_FIELD_TYPES.DROPDOWN,
          name: 'selectReason',
          dropdownOptions: mappedReasons,
          testid: formatDataTestId(baseDataTestId, 'SelectReason'),
          styles: { mt: 'lg' },
          props: {
            showStatusIcon: false,
            placeholder: t('ccui.manageBooking.options.agentOverrideModal.selectReason'),
            dropdownStyles: {
              menuListStyles: {
                overflowY: 'auto',
                height: '12.5rem',
              },
            },
          },
          onChange: (value: string) => {
            const selectedReason = reasons.find((reason: OverrideReason) => reason.code === value);
            setIsManagerApprovalNeeded(selectedReason?.managerApprovalNeeded ?? false);
          },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          hidden: !isManagerApprovalNeeded,
          id: 'managerName',
          name: 'managerName',
          label: t('ccui.manageBooking.options.agentOverrideModal.managerName'),
          testid: formatDataTestId(baseDataTestId, 'ManagerName'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'callerName',
          name: 'callerName',
          label: t('ccui.manageBooking.options.agentOverrideModal.callerName'),
          testid: formatDataTestId(baseDataTestId, 'CallerName'),
          styles: { ...inputStyle },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          label: t('ccui.manageBooking.options.agentOverrideModal.saveButton'),
          name: 'agentOverrideSubmitButton',
          action: onSubmit,
          testid: formatDataTestId(baseDataTestId, 'Submit'),
          Component: AgentOverrideButton,
          styles: { mb: '0' },
          props: {
            type: 'submit',
            isManagerApprovalNeeded: isManagerApprovalNeeded,
          },
        },
      ],
      buttons: [
        {
          type: 'button',
          label: t('ccui.manageBooking.options.agentOverrideModal.cancelButton'),
          action: onClose,
          props: {
            variant: 'tertiary',
            size: 'full',
          },
          styles: { ...continueTextStyle, ...continueButtonSectionStyle },
          testid: formatDataTestId(baseDataTestId, 'Close'),
        },
      ],
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    resetForm,
  } as FormProps;

  return config;
};

const continueButtonSectionStyle = {
  background: 'primary',
} as BoxProps;

const continueTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
} as TextProps;

const inputStyle = {
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  p: 0,
} as StyleProps;

const selectReasonStyle = {
  fontSize: 'lg',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
} as StyleProps;
