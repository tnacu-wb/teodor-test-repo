import type { BoxProps } from '@chakra-ui/react';
import { Flex } from '@chakra-ui/react';
import { LoadingSpinner } from '@whitbread-eos/atoms';

interface Props {
  text: string;
}

export default function PageLoader({ text }: Readonly<Props>) {
  return (
    <Flex {...loadingStyle}>
      <LoadingSpinner loadingText={text} />
    </Flex>
  );
}

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 1,
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;
