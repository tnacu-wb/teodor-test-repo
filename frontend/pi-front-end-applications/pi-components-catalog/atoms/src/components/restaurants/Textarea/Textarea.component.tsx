import {
  Textarea as TextareaWrapper,
  TextareaProps as TextareaWrapperProps,
} from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';

export default function Textarea(props: TextareaWrapperProps) {
  const baseDataTestId = 'textarea';
  return (
    <TextareaWrapper
      {...textareaStyle()}
      data-testid={formatDataTestId(baseDataTestId, `${props.name}`)}
      {...props}
      id={props.name}
    ></TextareaWrapper>
  );
}
const textareaStyle = () => {
  return {
    h: '3.5rem',
    zIndex: 0,
    borderWidth: '1px',
    borderColor: 'var(--chakra-colors-primary)',
    _hover: { borderColor: 'none', boxShadow: 'none' },
    _focus: {
      boxShadow: 'none',

      zIndex: '0',
      borderWidth: '2px',
      borderColor: 'var(--chakra-colors-primary)',
    },
    boxShadow: 'none',
  };
};
