import { Flex, Link as ChakraLink, List, ListItem } from '@chakra-ui/react';
import { NavItems } from '@whitbread-eos/api';

import {
  businessListStyles,
  linkStyles,
  listItemStyles,
  listStyles,
} from '../NavigationMenu.style';

interface Props {
  labels: NavItems;
  onLogout: () => void;
}

export default function AuthSubNav({ labels, onLogout }: Readonly<Props>) {
  const accountLinks = labels?.accountLinks;

  return (
    <Flex {...businessListStyles}>
      <List {...listStyles}>
        {accountLinks?.map((accountLink) => (
          <ListItem {...listItemStyles} data-testid="listItem" key={accountLink.title}>
            <ChakraLink
              href={accountLink.url}
              {...linkStyles}
              data-testid={`${accountLink.title}Button`}
            >
              {accountLink.title}
            </ChakraLink>
          </ListItem>
        ))}
        <ListItem {...listItemStyles} data-testid="listItem">
          <ChakraLink {...linkStyles} onClick={onLogout} data-testid="Global-Logout-Desktop">
            {labels.logOut}
          </ChakraLink>
        </ListItem>
      </List>
    </Flex>
  );
}
