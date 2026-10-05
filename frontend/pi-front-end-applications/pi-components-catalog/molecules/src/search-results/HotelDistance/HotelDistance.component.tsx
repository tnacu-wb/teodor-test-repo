import { BoxProps, Text } from '@chakra-ui/react';

interface Props {
  distance: number;
  unit: string;
  testId: string;
  labels: {
    distanceUnitPlural: string;
    fromLocation: string;
  };
  styles?: BoxProps;
}

type Labels = {
  distanceUnitPlural: string;
  fromLocation: string;
};

export default function HotelDistance({ distance, unit, labels, testId, styles }: Readonly<Props>) {
  return (
    <Text {...styles} data-testid={testId}>
      {getLabel(distance, unit, labels)}
    </Text>
  );
}

export function getLabel(distance: number, unit: string, labels: Labels) {
  if (distance === 1 || unit === 'km') {
    return `${distance} ${unit} ${labels.fromLocation}`;
  }

  return `${distance} ${labels.distanceUnitPlural?.toLowerCase()} ${labels.fromLocation}`;
}
