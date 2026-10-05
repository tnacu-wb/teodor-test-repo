import {
  Alert as AlertWrapper,
  BoxProps,
  Flex,
  Text,
  Tooltip as TooltipComponent,
  TooltipProps,
} from '@chakra-ui/react';
import { ReactElement, ReactNode } from 'react';

import Icon from '../Icon';

interface Props extends TooltipProps {
  title?: string;
  description: string;
  variant: string;
  children: ReactNode;
  svg?: ReactElement;
  alertElementStyles?: BoxProps;
  closeDelay?: number;
}
export default function Tooltip({
  alertElementStyles,
  children,
  closeDelay = 0,
  ...props
}: Readonly<Props>) {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { itemRef: _, ...extraProps } = props;
  return (
    <TooltipComponent
      closeDelay={closeDelay}
      label={
        <AlertWrapper background="transparent" paddingLeft={0} {...alertElementStyles}>
          {extraProps.svg && <Icon svg={extraProps.svg} mt={extraProps.title && '-20px'} />}
          <Flex direction="column" justifyContent="space-evenly">
            {extraProps.title !== 'undefined' && (
              <Text ml="sm" fontWeight="semibold">
                {extraProps.title}
              </Text>
            )}
            <Text ml="sm">{extraProps.description}</Text>
          </Flex>
        </AlertWrapper>
      }
      {...extraProps}
    >
      {children}
    </TooltipComponent>
  );
}
