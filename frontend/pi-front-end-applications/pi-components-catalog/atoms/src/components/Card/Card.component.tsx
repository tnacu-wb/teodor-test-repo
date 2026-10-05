import { Flex, FlexProps } from '@chakra-ui/react';
import { ReactNode } from 'react';

interface Props extends FlexProps {
  children: ReactNode;
}

const cardStyle = {
  border: '1px solid var(--chakra-colors-lightGrey2)',
  boxShadow: '0px 0px 12px  var(--chakra-colors-lightGrey4)',
  borderRadius: '3px',
  bgColor: 'baseWhite',
};

export default function Card({ children, ...otherProps }: Readonly<Props>) {
  return (
    <Flex {...cardStyle} {...otherProps} p={otherProps.padding || 'md'}>
      {children}
    </Flex>
  );
}
