import { Box } from '@chakra-ui/react';
import { type FormDynamicFieldCompProps, AddSubtract } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, useMemo } from 'react';
import { useWatch } from 'react-hook-form';

export default function RoomCounter({
  formField,
  field,
  control,
}: Readonly<FormDynamicFieldCompProps>) {
  const isAccessibleRoom = useWatch({
    control,
    name: 'isAccessibleRoom',
  });

  const isTravellingWithChild = useWatch({
    control,
    name: 'isTravellingWithChild',
  });

  const isSchoolOrYouth = useWatch({
    control,
    name: 'isSchoolOrYouth',
  });

  useEffect(() => {
    if (
      !isTravellingWithChild &&
      ['familyOf21A1C', 'familyOf32A1C', 'familyOf31A2C', 'familyOf42A2C'].includes(field.name)
    ) {
      field.onChange(0);
    }
    if (
      !isAccessibleRoom &&
      ['accessibleSingle', 'accessibleDouble', 'accessibleTwin'].includes(field.name)
    ) {
      field.onChange(0);
    }
  }, [isTravellingWithChild, isAccessibleRoom]);

  const hideRoomCounter = useMemo(() => {
    const hiddenProp = formField?.props?.hidden;

    return (
      (hiddenProp === 'isTravellingWithChild' && !isSchoolOrYouth && !isTravellingWithChild) ||
      (hiddenProp === 'isAccessibleRoom' && !isAccessibleRoom)
    );
  }, [isAccessibleRoom, isTravellingWithChild, formField?.props?.hidden, isSchoolOrYouth]);

  if (hideRoomCounter) {
    return null;
  }

  return renderAddSubtractController({
    baseTestId: 'GroupBookingsPage',
    onPlus: () => field.onChange(field.value + 1),
    onSubtract: () => field.onChange((field.value as any) - 1),
    handleInputChange: (value: number) => {
      field.onChange(isNaN(value) ? 0 : value);
    },
    roomItem: formField.props,
    count: field.value,
  });
}

const renderAddSubtractController = ({
  baseTestId,
  onPlus,
  onSubtract,
  roomItem,
  count,
  handleInputChange,
}: any) => {
  return (
    <Box
      {...roomCounterStyles}
      data-testid={formatDataTestId(baseTestId, `AddSubtractControls-${roomItem.roomType}`)}
    >
      <Box>
        <strong>{roomItem.roomLabel}</strong>
        <span>{roomItem.roomOccupancy}</span>
      </Box>
      <AddSubtract
        prefixDataTestId={formatDataTestId(baseTestId, `AddSubtractControls-${roomItem.roomType}`)}
        onPlus={onPlus}
        onSubtract={onSubtract}
        isSubtractDisable={count === 0}
        value={count}
        isPlusDisable={count === 99}
        isPlusHidden={false}
        isSubtractHidden={false}
        isEditable={true}
        maxLength={2}
        handleInputChange={handleInputChange}
      />
    </Box>
  );
};

const roomCounterStyles = {
  border: '1px solid var(--chakra-colors-lightGrey2)',
  borderRadius: 'sm',
  padding: 'md',
  marginBottom: 'lg',
  display: {
    base: 'block',
    xs: 'block',
    sm: 'flex',
  },
  sx: {
    strong: {
      fontWeight: 'semibold',
      display: 'block',
    },
    span: {
      fontSize: 'sm',
      color: 'darkGrey2',
    },
    '> div:first-of-type': {
      paddingRight: 'md',
      fontSize: 'xl',
      flex: '1',
    },
    'div:last-child > div': {
      paddingLeft: 'md',
      borderLeft: {
        base: '0',
        xs: '0',
        sm: '1px solid var(--chakra-colors-lightGrey3)',
      },
    },
    'div div > p, div div > input': {
      border: '1px solid var(--chakra-colors-lightGrey2)',
      width: 'var(--chakra-space-5xl)',
      margin: '0 10px',
      borderRadius: '3px',
      fontSize: 'xl',
    },
  },
};
