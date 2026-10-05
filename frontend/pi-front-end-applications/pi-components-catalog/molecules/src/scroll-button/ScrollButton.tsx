import { ChevronLeftIcon, ChevronRightIcon } from '@chakra-ui/icons';
import { Button, Icon } from '@chakra-ui/react';
import React from 'react';

interface ScrollButtonProps {
  direction: 'left' | 'right';
  onClick: () => void;
}

const ScrollButton: React.FC<ScrollButtonProps> = ({ direction, onClick }) => {
  const ArrowIcon = direction === 'left' ? ChevronLeftIcon : ChevronRightIcon;

  return (
    <Button
      onClick={onClick}
      aria-label={`${direction} scroll button`}
      rounded="full"
      w="36px"
      h="36px"
      minW="unset"
      p={0}
      mx={1}
      mt={'20px'}
      bg="transparent"
      color="#58595B"
      _hover={{ bg: '#026575', color: '#ffffff' }}
      _active={{
        bg: 'rgba(2, 101, 117, 0.2)',
        color: '#026575',
        transform: 'scale(0.95)',
      }}
      _focus={{
        outline: 'none',
      }}
      _focusVisible={{
        outline: '2px solid #026575',
        outlineOffset: '2px',
      }}
      transition="all 0.1s ease-in-out"
    >
      <Icon as={ArrowIcon} boxSize={6} />
    </Button>
  );
};

export default ScrollButton;
