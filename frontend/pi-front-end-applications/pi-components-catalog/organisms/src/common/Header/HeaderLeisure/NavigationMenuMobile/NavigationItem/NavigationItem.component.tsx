import { Box, BoxProps, Flex, Grid, GridProps } from '@chakra-ui/react';
import { ReactNode } from 'react';

import { modalIconStyle } from '../NavigationMenuMobile.style';

interface NavigationItemProp {
  title: ReactNode;
  icon?: ReactNode;
}
export default function NavigationItem({ title, icon }: Readonly<NavigationItemProp>) {
  return (
    <Grid {...navigationItemStyle} data-testid="navigationItem">
      <Flex>{title}</Flex>
      <Box {...(modalIconStyle as BoxProps)}>{icon}</Box>
    </Grid>
  );
}

const navigationItemStyle = {
  templateColumns: '1fr auto',
  justifyContent: 'center',
  _hover: {
    cursor: 'pointer',
  },
} as GridProps;
