import { Box, Link as ChakraLink, ModalBody } from '@chakra-ui/react';
import { NavItems } from '@whitbread-eos/api';

import HeaderSideNav from '../HeaderSideNav';
import { containerItemStyle } from '../NavigationMenuMobile.style';

interface Props {
  onClickHeaderTitle: () => void;
  header: string;
  logout: () => void;
  labels: NavItems;
}

export default function AuthSideNav({
  onClickHeaderTitle,
  header,
  logout,
  labels,
}: Readonly<Props>) {
  const accountLinks = labels?.accountLinks;

  return (
    <>
      <HeaderSideNav
        title={header}
        onClickTitle={onClickHeaderTitle}
        dataTestId="Global-UserName-Mobile"
      />
      <ModalBody p="0" data-testid="authSideNav">
        {accountLinks?.map((accountLink) => (
          <Box {...containerItemStyle}>
            <ChakraLink href={accountLink.url} data-testid={`${accountLink.title}Button-Mobile`}>
              {accountLink.title}
            </ChakraLink>
          </Box>
        ))}
        <Box {...containerItemStyle} onClick={logout} data-testid="Global-Logout-Mobile">
          {labels?.logOut}
        </Box>
      </ModalBody>
    </>
  );
}
