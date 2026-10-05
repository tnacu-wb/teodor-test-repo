import { FormInnB, SearchRoomType, ROOM_CODES } from '@whitbread-eos/api';
import { Select, Switch } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { useEffect, useRef } from 'react';

interface Props {
  formLabels: FormInnB | Record<string, never>;
  icons: Record<string, string>;
  room: SearchRoomType;
  roomNumber: number;
  roomId: string;
  hasSeparator?: boolean;
  updateRoom: (id: string, newData: SearchRoomType) => void;
  removeRoom: (roomId: string) => void;
  roomsCount: number;
  isRoomNewlyAdded?: boolean;
}

const defaultRoomType = ROOM_CODES.double;

const Room = ({
  room,
  updateRoom,
  formLabels,
  roomNumber,
  roomsCount,
  roomId,
  icons,
  hasSeparator = false,
  removeRoom,
  isRoomNewlyAdded = false,
}: Readonly<Props>) => {
  const { t } = useTranslation();
  const removeButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (isRoomNewlyAdded && removeButtonRef.current) {
      removeButtonRef.current.focus();
    }
  }, [isRoomNewlyAdded]);

  const handleChildrenChange = (newValue: number) => {
    if (newValue) {
      updateRoom(roomId, { ...room, children: newValue, roomType: ROOM_CODES.family });
    } else {
      updateRoom(roomId, { ...room, children: newValue, roomType: defaultRoomType });
    }
  };

  const handleAdultsChange = (newValue: number) => {
    if (
      (newValue === 1 && room.roomType === ROOM_CODES.twin) ||
      (newValue > 1 && room.roomType === ROOM_CODES.single)
    ) {
      updateRoom(roomId, { ...room, adults: newValue, roomType: defaultRoomType });
    } else {
      updateRoom(roomId, { ...room, adults: newValue });
    }
  };

  const handleRoomTypeOptions = () => {
    if (!room.children) {
      if (room.adults === 1) {
        return [
          {
            value: ROOM_CODES.single,
            displayValue: `${t('content.global.single')}`,
          },
          {
            value: ROOM_CODES.double,
            displayValue: `${t('content.global.double')}`,
          },
          {
            value: ROOM_CODES.accessible,
            displayValue: `${t('content.global.accessible')}`,
          },
        ];
      }
      if (room.adults > 1) {
        return [
          {
            value: ROOM_CODES.double,
            displayValue: `${t('content.global.double')}`,
          },
          {
            value: ROOM_CODES.twin,
            displayValue: `${t('content.global.twin')}`,
          },
          {
            value: ROOM_CODES.accessible,
            displayValue: `${t('content.global.accessible')}`,
          },
        ];
      }
    }

    return [
      {
        value: ROOM_CODES.family,
        displayValue: `${t('content.global.family')}`,
      },
    ];
  };
  return (
    <div className={roomContainerStyle} data-testid={`IB-Room-${roomNumber}`}>
      <span className={roomLabelStyle}>
        <div className={roomStyle}>
          {t('content.form.room')} {roomNumber}
        </div>
        {roomsCount > 1 && (
          <button
            ref={removeButtonRef}
            data-testid={`IB-Remove-Room-${roomNumber}-Button`}
            className={removeRoomStyle}
            onClick={() => removeRoom(roomId)}
          >
            {t('content.form.removeRoom')}
          </button>
        )}
      </span>
      <div className="flex gap-4">
        <div className="flex flex-col gap-2 w-full">
          <Select
            value={room.adults}
            onOptionChange={handleAdultsChange}
            triggerIcon={formatIBAssetsUrl(formLabels?.guestIcon)}
            triggerContent={''}
            label={t('content.global.adultsLabel')}
            arrowIcon={formatIBAssetsUrl(icons['icon.chevron.down'])}
            testId={`IB-RoomOccupancy-Adults-Dropdown-${roomNumber}`}
            options={[
              {
                value: 1,
                displayValue: `1 ${t('content.global.adult')}`,
              },
              {
                value: 2,
                displayValue: `2 ${t('content.global.adults')}`,
              },
            ]}
            showValueInstead={true}
          />
          <span className={helperStyle}>{t('content.form.adultsHelperText')}</span>
        </div>
        <div className="flex flex-col gap-2 w-full">
          <Select
            value={room.children}
            onOptionChange={handleChildrenChange}
            triggerContent={''}
            label={t('content.global.childrenLabel')}
            arrowIcon={formatIBAssetsUrl(icons['icon.chevron.down'])}
            testId={`IB-RoomOccupancy-Children-Dropdown-${roomNumber}`}
            options={[
              {
                value: 0,
                displayValue: `0 ${t('content.global.children')}`,
              },
              {
                value: 1,
                displayValue: `1 ${t('content.global.child')}`,
              },
              {
                value: 2,
                displayValue: `2 ${t('content.global.children')}`,
              },
            ]}
            showValueInstead={true}
          />
          <span className={helperStyle}>{t('content.form.childrenHelperText')}</span>
        </div>
      </div>

      <div className={cotContainerStyle}>
        <Switch
          checked={room.shouldIncludeCot}
          onCheckedChange={(newValue: boolean) =>
            updateRoom(roomId, { ...room, shouldIncludeCot: newValue })
          }
          data-testid={`IB-shouldIncludeCot-Switch-${roomNumber}`}
        />
        <div className={cotLabelContainerStyle}>
          <span className={cotLabelStyle}>{t('content.form.includeCot')}</span>
          <span className={cotHelperStyle}>{t('content.form.cotLimit')}</span>
        </div>
      </div>

      <Select
        value={room.roomType}
        onOptionChange={(newValue: string) => updateRoom(roomId, { ...room, roomType: newValue })}
        triggerContent={''}
        label={t('content.form.roomType')}
        arrowIcon={formatIBAssetsUrl(icons['icon.chevron.down'])}
        testId={`IB-RoomOccupancy-RoomType-${roomNumber}`}
        options={handleRoomTypeOptions()}
      />

      {hasSeparator && <div className={separatorStyle} />}
    </div>
  );
};

export default Room;

const roomContainerStyle = 'flex flex-col gap-6';
const roomLabelStyle = 'flex justify-between';
const helperStyle = 'text-[0.813rem] ml-4 text-darkGrey1';
const cotContainerStyle = 'flex gap-4 items-center';
const cotLabelContainerStyle = 'flex gap-2 items-center';
const cotLabelStyle = 'flex items-center font-semibold';
const cotHelperStyle = 'flex items-center text-sm';
const separatorStyle = 'bg-lightGrey4 w-full h-[1px]';
const removeRoomStyle =
  'text-sm font-medium text-secondaryColor underline focus:outline-none focus-visible:ring-2 focus-visible:ring-primaryColor rounded-[2px]';
const roomStyle = 'text-lg font-semibold text-darkGrey1';
