import {
  Box,
  Popover,
  PopoverArrow,
  PopoverArrowProps,
  PopoverBody,
  PopoverContent,
  PopoverProps,
  PopoverTrigger as OrigPopoverTrigger,
} from '@chakra-ui/react';
import { ReactNode } from 'react';

export const PopoverTrigger: React.FC<{ children: React.ReactNode }> = OrigPopoverTrigger;
interface Props extends PopoverProps {
  children: ReactNode;
  triggerItem: ReactNode;
  styles?: {
    arrowStyles?: PopoverArrowProps;
  };
}

const popoverStyleProps = {
  _focus: {
    boxShadow: '0 2px 8px 0px var(--chakra-colors-lightGrey1)',
  },
  _focusVisible: {
    outline: 'none',
  },
  inset: '10px auto auto 0px',
  boxShadow: '0 2px 8px 0px var(--chakra-colors-lightGrey1)',
  border: '1px solid var(--chakra-colors-lightGrey4)',
  padding: '24px',
  borderRadius: '3px',
  width: 'fit-content',
};

const popoverArrowStyles = {
  boxShadow: '-2px 2px 1px 0 var(--chakra-colors-lightGrey4)!important',
  width: '200% !important;',
  height: '200% !important;',
  top: '-4px !important;',
  left: '-4px !important;',
  transform: 'rotate(135deg) !important',
};

export default function PopoverComponent({ triggerItem, children, styles }: Readonly<Props>) {
  const hasArrowStyles = styles?.arrowStyles;

  const customPopoverArrowStyles = hasArrowStyles
    ? { ...popoverArrowStyles, ...hasArrowStyles }
    : { ...popoverArrowStyles };

  return (
    <Popover closeOnBlur>
      <PopoverTrigger>
        <Box _focusVisible={{ outline: 'none' }}>{triggerItem}</Box>
      </PopoverTrigger>
      <PopoverContent {...popoverStyleProps}>
        <PopoverArrow {...customPopoverArrowStyles} />
        <PopoverBody p="0">{children}</PopoverBody>
      </PopoverContent>
    </Popover>
  );
}
