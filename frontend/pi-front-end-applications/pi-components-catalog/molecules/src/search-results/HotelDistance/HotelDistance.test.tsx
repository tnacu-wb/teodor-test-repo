import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelDistance, { getLabel } from './HotelDistance.component';

const mockedData = {
  distance: 10,
  unit: 'mile',
};

const labels = {
  distanceUnitPlural: 'miles',
  fromLocation: 'from your search',
};

describe('SRP - Hotel Distance from searched location', () => {
  it('should render the component', () => {
    const { getByTestId } = render(
      <HotelDistance
        distance={mockedData.distance}
        unit={mockedData.unit}
        labels={labels}
        testId="SRP-hotel-distance"
      />
    );
    expect(getByTestId('SRP-hotel-distance')).toBeInTheDocument();
  });
});

describe('getLabel function', () => {
  it('should return 1 mile from your search location', () => {
    expect(getLabel(1, 'mile', labels)).toBe(`1 mile ${labels.fromLocation}`);
  });
  it('should return 5 km from your search location', () => {
    expect(getLabel(5, 'km', labels)).toBe(`5 km ${labels.fromLocation}`);
  });
  it('should return 5 miles from your search location', () => {
    expect(getLabel(5, 'mile', labels)).toBe(
      `5 ${labels.distanceUnitPlural} ${labels.fromLocation}`
    );
  });
});
