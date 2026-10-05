import { Flex, FlexProps, Box } from '@chakra-ui/react';

import Button from '../../../Button';
import { FORM_BUTTON_TYPES } from '../formContants';
import { FormButtonsProps } from '../formTypes';

export default function FormButton({ buttons, isEnquiry, formStepOneCompleted }: FormButtonsProps) {
  return (
    <Flex {...{ ...defaultButtonsContainerStyles }} height="34px">
      {buttons?.map(({ label, testid, type, action }) => {
        return (
          <Box key={testid as string}>
            <Button
              variant="unbrandedRestaurant"
              style={{ textTransform: 'none' }}
              p={0}
              borderRadius={'4px'}
              size="full"
              type={type}
              data-testid={testid}
              onClick={() => {
                if (type !== FORM_BUTTON_TYPES.SUBMIT) action();
              }}
            >
              {!formStepOneCompleted ? label[0] : isEnquiry ? label[1] : label[2]}
            </Button>
          </Box>
        );
      })}
    </Flex>
  );
}

const defaultButtonsContainerStyles = {
  direction: 'column',
  justifyContent: 'flex-start',
  marginBottom: 'var(--chakra-space-lg)',
  width: '100%',
} as FlexProps;
