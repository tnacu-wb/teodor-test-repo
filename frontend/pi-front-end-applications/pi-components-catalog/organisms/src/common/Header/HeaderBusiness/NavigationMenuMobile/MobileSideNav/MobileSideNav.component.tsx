import { Box, Flex, FlexProps, ModalBody, Text } from '@chakra-ui/react';
import { type SubNavCategory } from '@whitbread-eos/api';
import { formatDataTestId } from '@whitbread-eos/utils';
import NextLink from 'next/link';

import HeaderSideNav from '../../../HeaderLeisure/NavigationMenuMobile/HeaderSideNav';
import { containerItemStyle } from '../../../HeaderLeisure/NavigationMenuMobile/NavigationMenuMobile.style';

interface Props {
  onClickHeaderTitle: () => void;
  title: string;
  labels?: SubNavCategory[];
  baseTestId: string;
}

export default function MobileSideNav({
  onClickHeaderTitle,
  labels,
  title,
  baseTestId,
}: Readonly<Props>) {
  return (
    <>
      <HeaderSideNav title={title} onClickTitle={onClickHeaderTitle} />
      <ModalBody p="0" data-testid={formatDataTestId(baseTestId, 'Container')}>
        <Flex flexDir="column">
          {labels?.map?.((label) => {
            return (
              <Flex flexDir="column" key={`${label.navOptions[0]?.title}`}>
                {label.navOptions.map((option) => {
                  return (
                    <Box
                      {...customContainerItemStyle}
                      {...customMobileSubMenuStyle}
                      key={option?.title}
                      data-testid={formatDataTestId(baseTestId, option.title)}
                    >
                      <NextLink href={option.url ?? '/'} passHref legacyBehavior>
                        <Text>{option.title}</Text>
                      </NextLink>
                    </Box>
                  );
                })}
              </Flex>
            );
          })}
        </Flex>
      </ModalBody>
    </>
  );
}

const customMobileSubMenuStyle = {
  lineHeight: '1rem',
  pl: '1rem',
};

const customContainerItemStyle = {
  ...containerItemStyle,
  flexDirection: 'column',
  ml: '0',
  pl: 'md',
  w: '100%',
} as FlexProps;
