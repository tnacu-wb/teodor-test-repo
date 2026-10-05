import { Box, Flex, FlexProps } from '@chakra-ui/react';

import { FORM_BUTTON_TYPES } from '../';
import Button from '../../Button';
import { FormButtonsProps } from '../formTypes';

export default function FormButtons({
  buttonsContainerStyles,
  buttons,
}: Readonly<FormButtonsProps>) {
  return (
    <Flex {...{ ...defaultButtonsContainerStyles, ...buttonsContainerStyles }}>
      {buttons.map((button) => {
        return (
          <Box {...button.styles} key={button.label}>
            <Button
              type={button.type}
              data-testid={button.testid}
              {...button.props}
              onClick={() => {
                if (button.type !== FORM_BUTTON_TYPES.SUBMIT) {
                  button.action();
                }
              }}
            >
              {button.label}
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
