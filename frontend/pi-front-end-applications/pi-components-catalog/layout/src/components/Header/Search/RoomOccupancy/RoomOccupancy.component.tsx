'use client';

import { FormInnB, SearchRoomType } from '@whitbread-eos/api';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
  Button,
  ErrorTooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogHeader,
  DialogFooter,
} from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useState, useEffect, useRef } from 'react';

import { RoomOccupancyDropdown } from './RoomOccupancyDropdown';

interface Props {
  formLabels: FormInnB | Record<string, never>;
  rooms: Record<string, SearchRoomType>;
  updateRoom: (id: string, newData: SearchRoomType) => void;
  addRoom: () => void;
  removeRoom: (roomId: string) => void;
  icons: Record<string, string>;
  addRoomIcon: string;
  userRole: string;
  showError: boolean;
  onOpenChange: (open: boolean) => void;
  maxRooms: number;
  onDiscardChanges?: () => void;
  isMobile?: boolean;
}

const getSumLabel = (
  value: number,
  singleLabel: string,
  multipleLabel: string,
  separator = false
) => {
  if (value) {
    return `${value} ${value > 1 ? multipleLabel : singleLabel}${separator ? ', ' : ''}`;
  }
  return '';
};

const RoomOccupancy = ({
  rooms,
  updateRoom,
  formLabels,
  icons,
  addRoomIcon,
  addRoom,
  removeRoom,
  userRole,
  showError,
  onOpenChange,
  maxRooms,
  onDiscardChanges,
  isMobile,
}: Readonly<Props>) => {
  const { t } = useTranslation();

  const dropdownRef = useRef<HTMLDivElement>(null);

  const [popoverOpen, setPopoverOpen] = useState(false);

  const toggleDropdown = () => {
    setPopoverOpen(!popoverOpen);
  };

  const getLabel = (type?: string) => {
    const guests = Object.values(rooms)?.reduce(
      (guests, room) => {
        guests.adults += room?.adults;
        guests.children += room?.children;
        return guests;
      },
      { adults: 0, children: 0 }
    );

    const adultsText = getSumLabel(
      guests.adults,
      t('content.global.adult'),
      t('content.global.adults'),
      true
    );
    const childrenText = getSumLabel(
      guests.children,
      t('content.global.child'),
      t('content.global.children'),
      true
    );
    const roomsText = getSumLabel(
      Object.keys(rooms).length,
      t('content.global.room'),
      t('content.global.rooms')
    );

    switch (type) {
      case 'guests':
        return `${getSumLabel(
          guests.adults,
          t('content.global.adult'),
          t('content.global.adults'),
          !!guests.children
        )}${getSumLabel(guests.children, t('content.global.child'), t('content.global.children'))}`;
      case 'rooms':
        return roomsText;
      default:
        return `${adultsText}${childrenText}${roomsText}`;
    }
  };

  const handleMobileDialogClose = (open: boolean) => {
    onDiscardChanges?.();
    setPopoverOpen(open);
  };

  useEffect(() => {
    if (dropdownRef?.current) {
      dropdownRef.current.scrollTo({
        top: dropdownRef?.current?.scrollHeight,
        behavior: 'smooth',
      });
    }
  }, [Object.keys(rooms).length]);

  const renderButton = (
    <Button
      variant="roomOccupancyButton"
      size="roomOccupancyButton"
      className={`${buttonStyle}  ${showError ? errorStyle : ''} ${
        popoverOpen ? focusedButtonStyle : ''
      } `}
      data-testid="Room-Occupancy-Button"
    >
      <Image
        className={iconStyle}
        src={formatIBAssetsUrl(formLabels?.guestIcon)}
        width={24}
        height={24}
        alt="Room Occupancy Icon"
      />
      <span className={labelStyle}>{getLabel()}</span>
    </Button>
  );

  useEffect(() => {
    onOpenChange(popoverOpen);
  }, [popoverOpen]);

  const roomDetailsComponent = (
    <RoomOccupancyDropdown
      rooms={rooms}
      formLabels={formLabels}
      icons={icons}
      updateRoom={updateRoom}
      addRoom={addRoom}
      removeRoom={removeRoom}
      addRoomIcon={addRoomIcon}
      userRole={userRole}
      toggleDropdown={toggleDropdown}
      maxRooms={maxRooms}
    />
  );

  return (
    <>
      <Popover open={popoverOpen} onOpenChange={setPopoverOpen}>
        {showError ? (
          <ErrorTooltip
            icon={formatIBAssetsUrl(icons['icon.notification.error'])}
            content={t('content.form.invalidRooms')}
            className={errorTooltipStyle}
            open={showError}
            testId="IB-Room-Occupancy-ErrorTooltip"
          >
            <PopoverTrigger asChild>{renderButton}</PopoverTrigger>
          </ErrorTooltip>
        ) : (
          <PopoverTrigger asChild>{renderButton}</PopoverTrigger>
        )}
        {isMobile ? (
          <Dialog open={popoverOpen} onOpenChange={handleMobileDialogClose}>
            <DialogContent
              data-testid={'Room-Occupancy-Mobile-Dialog'}
              className={mobileDialogStyle}
            >
              <DialogHeader className="border-b border-lightGrey3 px-[16px]">
                <DialogTitle>{`${t('content.global.rooms').charAt(0).toUpperCase()}${t(
                  'content.global.rooms'
                ).slice(1)}`}</DialogTitle>
              </DialogHeader>
              <div
                className={dropdownMobileContentWrapper}
                data-testid="RoomOccupancyDropdown-Mobile-Content-Wrapper"
              >
                {roomDetailsComponent}
              </div>
              <DialogFooter>
                <div className="flex bg-lightGrey5 justify-center">
                  <div className="flex max-w-[572px] px-[16px] w-full">
                    <div className="flex flex-col w-[50%] p-[12px]">
                      <span className="text-sm">Guests</span>
                      <span className="font-bold">{getLabel('guests')}</span>
                    </div>
                    <div className="flex flex-col w-[50%] p-[12px]">
                      <span className="text-sm">Rooms</span>
                      <span className="font-bold">{getLabel('rooms')}</span>
                    </div>
                  </div>
                </div>
                <div className="flex justify-center">
                  <Button
                    data-testid="Room-Occupancy-Mobile-Done-Button"
                    variant="default"
                    onClick={toggleDropdown}
                    className="mx-[16px] w-full max-w-[546px]"
                  >
                    {t('content.global.done')}
                  </Button>
                </div>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        ) : (
          <PopoverContent
            align="start"
            avoidCollisions={false}
            data-testid="Room-Occupancy-Dropdown"
            className={popoverStyle}
            ref={dropdownRef}
          >
            {roomDetailsComponent}
          </PopoverContent>
        )}
      </Popover>
      <ErrorTooltip
        icon={formatIBAssetsUrl(icons['icon.notification.error'])}
        content={t('content.form.invalidRooms')}
        open={showError}
        testId="IB-Room-Occupancy-ErrorTooltip-Mobile"
        mobile
      />
    </>
  );
};

export default RoomOccupancy;

const buttonStyle =
  'border-lightGrey2 flex justify-start gap-3 border-r-0 w-full min-w-[16.5rem] tablet:min-w-48 relative mobile:border-r mobile:w-full mobile:min-w-[unset] mobile:border-l-[1px] mobile:rounded hover:border-darkGrey1 hover:border';
const focusedButtonStyle =
  '-outline-offset-2 outline outline-primaryColor outline-2 hover:-outline-offset-2 hover:outline hover:outline-primaryColor hover:outline-2';
const iconStyle = 'w-6 h-6';
const labelStyle = 'truncate';
const popoverStyle =
  'mobile:hidden py-6 w-80 overflow-scroll max-h-[calc(100vh-100px)] mobile:w-full mobile:rounded-none mobile:h-[calc(100svh-5rem)] mobile:max-h-[unset]';
const errorStyle =
  '-outline-offset-2 outline outline-2 outline-error hover:-outline-offset-2 hover:outline hover:outline-error hover:outline-2';
const errorTooltipStyle = 'max-w-[90%]';
const mobileDialogStyle =
  'hidden mobile:flex mobile:px-[0px] mobile:gap-0 !absolute !top-0 !left-0 !right-0 !bottom-0 !translate-x-0 !translate-y-0 !rounded-none !max-w-none !w-screen mobile:!h-dvh mobile:!overflow-hidden';
const dropdownMobileContentWrapper = 'flex flex-col grow w-full items-center overflow-scroll';
