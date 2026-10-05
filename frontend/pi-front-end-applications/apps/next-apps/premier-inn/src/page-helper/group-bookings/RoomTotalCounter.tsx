import { Box } from '@chakra-ui/react';
import { type FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { useEffect } from 'react';

export default function RoomTotalCounter({
  formField,
  getValues,
  field,
}: Readonly<FormDynamicFieldCompProps>) {
  const formValues = getValues();

  const count = [
    'singleOccupancy',
    'doubleOccupancy',
    'twinRooms',
    'accessibleSingle',
    'accessibleDouble',
    'accessibleTwin',
    'familyOf21A1C',
    'familyOf32A1C',
    'familyOf31A2C',
    'familyOf42A2C',
  ].reduce((total, roomType) => total + formValues[roomType], 0);

  if (count) {
    field.value = count.toString();
  }

  useEffect(() => {
    field.onChange(count);
  }, [count]);

  return (
    <Box textAlign="right" {...formField?.props?.totalStyles}>
      <strong>{formField?.props?.title}: </strong>
      <strong {...formField?.props?.totalCountStyle}>
        {count} {formField?.props?.roomLabel}
      </strong>
    </Box>
  );
}
