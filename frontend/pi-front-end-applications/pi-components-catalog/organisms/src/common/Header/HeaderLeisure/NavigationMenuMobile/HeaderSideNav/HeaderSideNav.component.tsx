import {
  Box,
  CloseButtonProps,
  Flex,
  Grid,
  ModalCloseButton,
  ModalHeader,
  ModalHeaderProps,
} from '@chakra-ui/react';
import { ChevronLeft, Dismiss, Icon } from '@whitbread-eos/atoms';

import { modalIconStyle } from '../NavigationMenuMobile.style';

interface Props {
  title?: string;
  onClickTitle?: () => void;
  dataTestId?: string;
}

export default function HeaderSideNav({ title, onClickTitle, dataTestId }: Readonly<Props>) {
  return (
    <ModalHeader {...modalHeaderStyle}>
      <Grid templateColumns="1fr auto" alignItems="center">
        <Box h="var(--chakra-space-lg)">
          {title && (
            <Flex
              alignItems="center"
              _hover={{
                cursor: 'pointer',
              }}
              onClick={onClickTitle}
              data-testid="Global-SideNav-BackButton-Mobile"
            >
              <Icon svg={<ChevronLeft />} />
              <Box py="0" px="md" data-testid={dataTestId}>
                {title}
              </Box>
            </Flex>
          )}
        </Box>
        <ModalCloseButton {...(modalIconStyle as CloseButtonProps)}>
          <Icon svg={<Dismiss data-testid="closeModal" />} />
        </ModalCloseButton>
      </Grid>
    </ModalHeader>
  );
}

const modalHeaderStyle = {
  h: 'var(--chakra-space-14)',
  p: 'md',
  fontWeight: 'medium',
  lineHeight: '3',
  fontSize: 'md',
  alignItems: 'center',
  color: 'primary',
  borderBottom: 'var(--chakra-space-px) solid var(--chakra-colors-lightGrey2)',
} as ModalHeaderProps;
