import {
  Textarea as TextareaWrapper,
  TextareaProps as TextareaWrapperProps,
  FormErrorMessage,
  FormControl,
} from '@chakra-ui/react';

import { formatDataTestId } from '../../utils/formatters';

interface Props extends TextareaWrapperProps {
  error?: string;
  maxLength?: number;
}
const baseDataTestId = 'textarea';

export default function Textarea(props: Readonly<Props>) {
  const isError = props?.error !== undefined;
  return (
    <FormControl {...wrapperStyles} isInvalid={isError}>
      <TextareaWrapper maxLength={props?.maxLength} {...props}></TextareaWrapper>
      {props?.error && renderFieldErrorMessage(props)}
    </FormControl>
  );
}

function renderFieldErrorMessage(props: Readonly<Props>) {
  return (
    <FormErrorMessage
      data-testid={formatDataTestId(baseDataTestId, `${name}-FormErrorMessage`)}
      {...errorMsgStyle}
    >
      {props?.error}
    </FormErrorMessage>
  );
}

const errorMsgStyle = {
  fontSize: 'xs',
  color: 'error',
  ml: 'md',
  mt: 'sm',
};

const wrapperStyles = {
  borderRadius: 'xs',
  zIndex: 0,
  borderColor: 'lightGrey1',
  _hover: { borderColor: 'darkGrey1' },
  _focus: { zIndex: '0' },
  sx: {
    textarea: {
      ':focus': { borderColor: '#00798e' },
    },
  },
};
