import { Box, Collapse as ChakraCollapse, type CollapseProps } from '@chakra-ui/react';

import Button from '../Button';

type CollapseWithChildrenProps = React.PropsWithChildren<CollapseProps>;
const Collapse = ChakraCollapse as unknown as React.ForwardRefExoticComponent<
  CollapseWithChildrenProps & React.RefAttributes<HTMLDivElement>
>;

interface Props extends CollapseProps {
  children?: React.ReactNode;
  onClick?: () => void;
  show?: boolean;
  buttonText: string;
}

export default function ExpandText(props: Readonly<Props>) {
  const { show, onClick, buttonText, children, ...collapseProps } = props;

  return (
    <>
      <Collapse in={show} {...collapseProps} data-testid="wrapper">
        {children}
      </Collapse>

      <Box
        mt="-5rem"
        height="5rem"
        position="relative"
        bg={`${
          !show &&
          'linear-gradient(to bottom, var(--chakra-colors-fadedBaseWhite) 0%, var(--chakra-colors-baseWhite) 75%)'
        }`}
      />

      <Button size="sm" variant="tertiary" onClick={onClick}>
        {buttonText}
      </Button>
    </>
  );
}
