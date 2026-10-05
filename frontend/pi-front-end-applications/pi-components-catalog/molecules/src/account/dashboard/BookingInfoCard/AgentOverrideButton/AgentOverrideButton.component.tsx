import { BoxProps, Flex, TextProps } from '@chakra-ui/react';
import { Button } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';
import { useWatch } from 'react-hook-form';

export default function AgentOverrideButton({ formField, control, errors, handleResetField }: any) {
  const [disabled, setDisabled] = useState(true);
  const selectReason = useWatch({ name: 'selectReason', control });
  const callerName = useWatch({ name: 'callerName', control });
  const managerName = useWatch({ name: 'managerName', control });
  const errorValues = [...Object.values(errors)];

  const manageBtnBehaviour = () => {
    if (
      selectReason !== '' &&
      callerName !== '' &&
      ((formField.props.isManagerApprovalNeeded && managerName !== '') ||
        !formField.props.isManagerApprovalNeeded) &&
      !Object.values(errors).some((error: any) => error.message?.length > 0) &&
      disabled
    ) {
      setDisabled(false);
    }

    if (
      (selectReason === '' ||
        callerName === '' ||
        (formField.props.isManagerApprovalNeeded && managerName === '') ||
        Object.values(errors).some((error: any) => error.message?.length > 0)) &&
      !disabled
    ) {
      setDisabled(true);
    }
  };

  useEffect(() => {
    // reset managerName when another reason with no manager approval is selected
    if (!formField.props.isManagerApprovalNeeded) {
      handleResetField('managerName', { defaultValue: '' });
    }
  }, [formField.props.isManagerApprovalNeeded, handleResetField]);

  useEffect(() => {
    manageBtnBehaviour();
  }, [selectReason, callerName, managerName, errorValues]);

  const testId = formField.testid || 'Submit';
  return (
    <Flex>
      <Button
        type="submit"
        data-testid={formatDataTestId(testId, 'Button')}
        {...formField.props}
        isDisabled={disabled}
        {...searchButtonStyle}
        {...searchBtnTextStyle}
        onClick={formField.props.action}
      >
        {formField.label}
      </Button>
    </Flex>
  );
}

const searchButtonStyle = {
  mt: 'lg',
  background: 'primary',
  width: 'full',
} as BoxProps;

const searchBtnTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
} as TextProps;
