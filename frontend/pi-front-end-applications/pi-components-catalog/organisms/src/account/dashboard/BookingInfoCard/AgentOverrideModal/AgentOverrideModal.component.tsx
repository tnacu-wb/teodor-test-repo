import { Box, Flex } from '@chakra-ui/react';
import { OverrideReason } from '@whitbread-eos/api';
import { Alert, Form, FormProps, ModalVariants, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import { agentOverrideFormConfig } from './modalFormConfig/agentOverrideFormConfig';

export interface Props {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: FormProps['defaultValues']) => void;
  reasons: OverrideReason[];
  error?: string;
  isVisible: boolean;
  onClose: () => void;
  t: (id: string) => string;
}

export default function AgentOverrideModal({
  getFormState,
  defaultValues,
  onSubmit,
  reasons,
  error,
  isVisible,
  onClose,
  t,
}: Readonly<Props>) {
  const baseDataTestId = 'AgentOverrideModal';
  const [isManagerApprovalNeeded, setIsManagerApprovalNeeded] = useState(false);

  useEffect(() => {
    if ((defaultValues.managerName as string).length > 0) {
      setIsManagerApprovalNeeded(true);
    }
  }, [defaultValues]);

  if (error) {
    return (
      <Notification
        status="error"
        description={error}
        variant="alert"
        maxW="full"
        svg={<Alert />}
      />
    );
  }

  return (
    <ModalVariants
      onClose={onClose}
      variant="gallery"
      isOpen={isVisible}
      variantProps={{
        title: t('ccui.manageBooking.options.agentOverrideModal.title'),
        delimiter: false,
      }}
    >
      <Box data-testid={formatDataTestId(baseDataTestId, 'Container')} {...containerStyles}>
        <Flex mt="2rem" justifyContent="flex-end">
          <Form
            {...agentOverrideFormConfig({
              getFormState,
              defaultValues,
              onSubmit,
              onClose,
              baseDataTestId,
              t,
              reasons,
              isManagerApprovalNeeded,
              setIsManagerApprovalNeeded,
            })}
          />
        </Flex>
      </Box>
    </ModalVariants>
  );
}

const containerStyles = {
  mb: '1.5rem',
  ml: '1.5rem',
  mr: '1.5rem',
};
