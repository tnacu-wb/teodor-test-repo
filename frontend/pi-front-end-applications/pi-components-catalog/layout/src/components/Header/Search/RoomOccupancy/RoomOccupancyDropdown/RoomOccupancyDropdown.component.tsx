import { FormInnB, SearchRoomType, BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';
import { Notification, Button, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { RolesRequired, useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useState, useRef } from 'react';

import { Room } from './Room';

interface Props {
  formLabels: FormInnB | Record<string, never>;
  rooms: Record<string, SearchRoomType>;
  updateRoom: (id: string, newData: SearchRoomType) => void;
  addRoom: () => void;
  removeRoom: (roomId: string) => void;
  addRoomIcon: string;
  icons: Record<string, string>;
  userRole: string;
  toggleDropdown: () => void;
  maxRooms: number;
}

const RoomOccupancyDropdown = ({
  rooms,
  formLabels,
  updateRoom,
  addRoomIcon,
  addRoom,
  removeRoom,
  icons,
  userRole,
  toggleDropdown,
  maxRooms,
}: Readonly<Props>) => {
  const { t } = useTranslation();
  const [newlyAddedRoomId, setNewlyAddedRoomId] = useState<string | null>(null);
  const [shouldFocusAddButton, setShouldFocusAddButton] = useState(false);
  const addRoomButtonRef = useRef<HTMLButtonElement>(null);

  const roomsLength = Object.keys(rooms).length;

  const handleAddRoomClick = () => {
    setNewlyAddedRoomId('pending');
    setShouldFocusAddButton(false);
    addRoom();
  };

  const handleRemoveRoom = (roomId: string) => {
    setNewlyAddedRoomId(null);
    setShouldFocusAddButton(true);
    removeRoom(roomId);
  };

  // Focus add room button after removal
  if (shouldFocusAddButton && addRoomButtonRef.current) {
    addRoomButtonRef.current.focus();
    setShouldFocusAddButton(false);
  }

  return (
    <div className={dropdownStyle} data-testid="RoomOccupancyDropdown-Container">
      {!!roomsLength &&
        Object.entries(rooms).map(([roomId, roomDetails], id) => {
          const isLastRoom = id + 1 === roomsLength;
          const isNewlyAdded = isLastRoom && newlyAddedRoomId === 'pending';

          return (
            <Room
              key={roomId}
              roomNumber={id + 1}
              roomId={roomId}
              room={roomDetails}
              updateRoom={updateRoom}
              formLabels={formLabels}
              icons={icons}
              hasSeparator={!isLastRoom}
              removeRoom={handleRemoveRoom}
              roomsCount={roomsLength}
              isRoomNewlyAdded={isNewlyAdded}
            />
          );
        })}
      <RolesRequired
        userRole={userRole}
        requiredRoles={[BUSINESS_BOOKER_USER_ROLES.SUPER, BUSINESS_BOOKER_USER_ROLES.BOOKER]}
      >
        {roomsLength < maxRooms ? (
          <button
            ref={addRoomButtonRef}
            data-testid="IB-Add-Room-Button"
            className={addRoomButtonStyle}
            onClick={handleAddRoomClick}
          >
            <Image
              className={addRoomIconStyle}
              src={addRoomIcon}
              width={40}
              height={40}
              alt={'Add Room Icon'}
            />
            <span>{t('content.global.addRoom')}</span>
          </button>
        ) : (
          <div className="break-words">
            <Notification
              type="warning"
              icon={formatIBAssetsUrl(icons['icon.notification.alert'])}
              title={t('content.results.notifications.groupBookingHeader')}
              message={
                <SanitizedContent>
                  {t('content.results.notifications.groupBookingFormPageMessage')}
                </SanitizedContent>
              }
            />
          </div>
        )}
      </RolesRequired>
      <Button
        data-testid="IB-Room-Occupancy-Done-Button"
        onClick={toggleDropdown}
        variant="alternativeDefault"
        className="mobile:hidden"
      >
        {t('content.global.done')}
      </Button>
    </div>
  );
};

export default RoomOccupancyDropdown;

const dropdownStyle =
  'flex flex-col gap-6 mobile:overflow-y-scroll mobile:px-[16px] mobile:grow mobile:py-[24px] mobile:max-w-[572px] mobile:w-full';
const addRoomButtonStyle =
  'flex items-center gap-4 cursor-pointer focus:outline-primaryColor focus:outline-2 focus:outline-offset-2';
const addRoomIconStyle = 'w-10 h-10';
