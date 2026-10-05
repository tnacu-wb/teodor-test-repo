import { Button as Btn, ButtonProps as BtnProps } from '@chakra-ui/react';
import { ReactNode } from 'react';

export interface ButtonProps extends BtnProps {
  disabled?: boolean;
  onClick?: () => void;
  size: 'xxs' | 'xs' | 'xsm' | 'md' | 'sm' | 'full' | 'contentFit';
  variant?: string;
  children: ReactNode;
}

export default function Button(props: ButtonProps) {
  return <Btn {...props}>{props.children}</Btn>;
}
