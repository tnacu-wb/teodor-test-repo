import { Box, BoxProps, Center, Flex, Text } from '@chakra-ui/react';
import { ChevronLeft } from '@whitbread-eos/atoms';

interface Props extends BoxProps {
  goBack: () => void;
  linkText: string | null;
  hasNegativeMargin?: boolean;
}

export default function BackToPage({
  goBack,
  linkText = '',
  hasNegativeMargin = false,
}: Readonly<Props>) {
  return (
    <Box {...getBoxStyles(hasNegativeMargin)} data-testid="backToPage">
      <Flex data-testid="backToPageContainer" onClick={handleClick} {...backToPageContainerStyle}>
        <Center {...chevronLeftStyle}>
          <ChevronLeft />
        </Center>
        <Text {...backToPageTextStyle}>{linkText}</Text>
      </Flex>
    </Box>
  );

  function handleClick() {
    goBack();
  }
}

// Because the Iframe has extra-spacing bellow, we need to move the button up
const getBoxStyles = (hasNegativeMargin: boolean) => {
  return {
    mt: hasNegativeMargin ? 'calc(var(--chakra-space-9xl) * -1)' : 'md',
    mb: 'md',
  };
};

const backToPageContainerStyle = {
  w: 'max-content',
  cursor: 'pointer',
  mw: '11.25rem',
  alignItems: 'flex-start',
};

const chevronLeftStyle = {
  mr: 'sm',
  h: '1.5rem',
  w: '1.5rem',
};

const backToPageTextStyle = {
  lineHeight: '3',
  fontWeight: 'semibold',
  fontSize: 'md',
};
