import { Box, Flex, FlexProps, ModalBody } from '@chakra-ui/react';
import { type NavItems } from '@whitbread-eos/api';

import HeaderSideNav from '../HeaderSideNav';
import { containerItemStyle } from '../NavigationMenuMobile.style';

interface Props {
  onClickHeaderTitle: () => void;
  labels: NavItems;
}

export default function DiscoverSideNav({ onClickHeaderTitle, labels }: Readonly<Props>) {
  const subNavItems = labels?.subNav?.filter((item) => !item?.title?.includes('Business'));

  return (
    <>
      <HeaderSideNav title={labels.discoverPI} onClickTitle={onClickHeaderTitle} />
      <ModalBody p="0" data-testid="discoverSideNav">
        <Flex flexDir="column">
          {subNavItems?.map?.((category) => {
            return (
              <Flex {...customContainerItemStyle} key={`category-${category.title}`}>
                <Box>{category.title}</Box>
                <Flex flexDir="column" pl="0.81rem">
                  {category.navOptions.map((option) => {
                    return (
                      <Box lineHeight="2.5rem" key={option.title}>
                        <a href={option.url ?? '/'}>{option.title}</a>
                      </Box>
                    );
                  })}
                </Flex>
              </Flex>
            );
          })}
        </Flex>
      </ModalBody>
    </>
  );
}

const customContainerItemStyle = {
  ...containerItemStyle,
  flexDirection: 'column',
  ml: '0',
  pl: 'md',
  w: '100%',
} as FlexProps;
