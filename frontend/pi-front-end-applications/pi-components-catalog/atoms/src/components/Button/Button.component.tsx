import { Button as Btn, ButtonProps as BtnProps, useTheme } from '@chakra-ui/react';
import { getTextStyleTypographyOverride } from '@whitbread-eos/utils';
import { ReactNode } from 'react';

export interface ButtonProps extends BtnProps {
  disabled?: boolean;
  onClick?: () => void;
  isDisabled?: boolean;
  size?: 'xxs' | 'xs' | 'xsm' | 'md' | 'sm' | 'full';
  variant:
    | 'primary'
    | 'secondary'
    | 'tertiary'
    | 'default'
    | 'generic'
    | 'genericSecondary'
    | 'marketingDefault'
    | 'marketingSolid'
    | 'circle'
    | 'unbrandedRestaurant'
    | 'login';
  children: ReactNode;
}

export default function Button(props: Readonly<ButtonProps>) {
  const theme = useTheme();
  const typographyProps = getTextStyleTypographyOverride(props, theme?.textStyles);

  return (
    <Btn {...props} {...typographyProps} isDisabled={props?.isDisabled}>
      {props.children}
    </Btn>
  );
}
