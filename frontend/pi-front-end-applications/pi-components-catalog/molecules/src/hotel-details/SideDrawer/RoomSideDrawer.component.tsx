import { Box, Flex, FlexProps, Text } from '@chakra-ui/react';
import { TabItem } from '@whitbread-eos/api';
import { Info, Icon, Notification, Dismiss } from '@whitbread-eos/atoms';
import { useScreenSize, renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRef } from 'react';

import { HotelRoomContent } from '../HotelRoomContent';

type Props = {
  visible: boolean;
  onClose: () => void;
  title: string;
  isPremierPlus?: boolean;
  roomStaticDetails?: TabItem;
  brand: string;
};

export const RoomSideDrawer = ({
  visible,
  onClose,
  title,
  isPremierPlus = false,
  roomStaticDetails,
  brand,
}: Readonly<Props>) => {
  const { isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();
  const { t } = useTranslation(['common']);
  const drawerRef = useRef<HTMLDivElement>(null);

  return (
    visible && (
      <>
        <Flex
          className="drawer-overlay"
          {...overlayStyle}
          onClick={onClose}
          data-testid="Room-Drawer-Backdrop"
        />
        <Flex {...drawerStyle} data-testid="Room-Drawer-Container" ref={drawerRef}>
          <Box {...closeButtonStyle} onClick={onClose} data-testid="Dismiss-Room-Drawer">
            <Icon svg={<Dismiss transform="scale(1.1)" />} />
          </Box>

          <Flex {...contentStyle}>
            <Flex {...headerStyle} data-testid="Room-Drawer-Title">
              <Text {...titleStyle}>{title}</Text>
              {isPremierPlus && <Text {...premierPlusBadge}>Premier Plus</Text>}
            </Flex>
            <HotelRoomContent
              tabItems={roomStaticDetails ? [roomStaticDetails] : []}
              isLessThanLg={isLessThanLg}
              isLessThanMd={isLessThanMd}
              isLessThanSm={isLessThanSm}
              containerRef={drawerRef}
            />
            <Box mt="3rem">
              <Notification
                status="info"
                variant="infoGrey"
                svg={<Info />}
                description={<>{renderSanitizedHtml(t(`hotelInfo.disclaimer.${brand}`))}</>}
                prefixDataTestId="Room-Drawer-Disclaimer"
              />
            </Box>
          </Flex>
        </Flex>
      </>
    )
  );
};

const headerStyle = {
  flexDirection: 'row',
  display: 'flex',
  marginRight: '3rem',
} as FlexProps;

const titleStyle = {
  fontWeight: 'semibold',
  lineHeight: '1.5rem',
  color: 'var(--chakra-colors-darkGrey1)',
  fontSize: { mobile: 'xl', sm: '2xl' },
};

const premierPlusBadge = {
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  px: '8px',
  py: '2px',
  fontSize: 'xxs',
  fontWeight: 'semibold',
  color: 'white',
  backgroundColor: 'var(--chakra-colors-primary)',
  borderRadius: '100px',
  marginLeft: '1.5rem',
};

const contentStyle = {
  display: 'flex',
  flexDirection: 'column',
  px: {
    mobile: '1rem',
    sm: '1.25rem',
    md: '1.5rem',
    lg: '1.75rem',
    xl: '4.125rem',
  },
  paddingTop: '2rem',
  pb: '1rem',
  overflow: 'auto',
} as FlexProps;

const closeButtonStyle = {
  position: 'absolute',
  right: {
    mobile: '1rem',
    sm: '1.25rem',
    md: '1.5rem',
    lg: '1.75rem',
    xl: '4.125rem',
  },
  top: '2rem',
  cursor: 'pointer',
  borderRadius: '100%',
  backgroundColor: '#fff',
  height: '1.5rem',
  width: '1.5rem',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  zIndex: 100,
} as FlexProps;

const overlayStyle = {
  position: 'fixed',
  left: 0,
  top: 0,
  right: 0,
  bottom: 0,
  backgroundColor: '#333333',
  zIndex: 998,
  opacity: '75%',
} as FlexProps;

const drawerStyle = {
  flexDirection: 'column',
  position: 'fixed',
  backgroundColor: '#fff',
  transition: 'all 0.3s ease-in-out',
  zIndex: 999,
  borderRadius: { mobile: '1rem 1rem 0 0', sm: '0' },
  right: { mobile: 0, sm: '0' },
  left: { mobile: 0, sm: 'unset' },
  bottom: { mobile: '0', sm: 0 },
  top: { mobile: 'unset', sm: 0 },
  width: { mobile: '100%', sm: '46rem', md: '46rem', lg: '50rem' },
  maxWidth: '100%',
  height: { mobile: '90dvh', sm: '100dvh' },
  transform: 'translateZ(0)',
} as FlexProps;
