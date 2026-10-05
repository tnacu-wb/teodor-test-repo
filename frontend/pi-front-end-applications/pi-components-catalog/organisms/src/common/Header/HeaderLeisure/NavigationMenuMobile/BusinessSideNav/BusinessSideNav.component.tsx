import { Box, ModalBody } from '@chakra-ui/react';
import { NavItems, SubNavCategoryItem } from '@whitbread-eos/api';

import HeaderSideNav from '../HeaderSideNav';
import { containerItemStyle } from '../NavigationMenuMobile.style';

interface Props {
  onClickHeaderTitle: () => void;
  labels: NavItems;
}

export default function BusinessSideNav({ onClickHeaderTitle, labels }: Readonly<Props>) {
  return (
    <>
      <HeaderSideNav title={labels?.businessSubNav?.title} onClickTitle={onClickHeaderTitle} />
      <ModalBody p="0" data-testid="businessSideNav">
        {labels?.businessSubNav?.navOptions && renderItems(labels.businessSubNav.navOptions)}
      </ModalBody>
    </>
  );

  function renderItems(items: SubNavCategoryItem[]) {
    if (!items) {
      return;
    }

    return (
      <>
        {items.map((option) => {
          return (
            <Box key={option.title} {...containerItemStyle} data-testid="listItem">
              <a href={option.url ?? '/'}>{option.title}</a>
            </Box>
          );
        })}
      </>
    );
  }
}
