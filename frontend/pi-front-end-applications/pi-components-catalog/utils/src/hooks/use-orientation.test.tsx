import React from 'react';

import { AppData, AppDataProvider } from '../store/AppDataContext';
import { render, screen } from '../utils/test-utils';
import useSetOrientation, { useOrientation } from './use-orientation';

const mockUseMediaQuery = jest.fn();
jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: (...args: string[]) => mockUseMediaQuery(...args),
}));

function Orientation() {
  useSetOrientation();
  const orientations = useOrientation();
  const isInitial = Object.values(orientations).every((orientation) => orientation === undefined);
  const currentOrientation =
    (Object.keys(orientations) as Array<keyof typeof orientations>).filter(
      (key) => orientations[key]
    )[0] || 'none';

  return <>{`Initial: ${isInitial} and orientation: ${currentOrientation}`}</>;
}

const initialAppData: AppData = {
  screenSize: undefined,
  orientation: undefined,
};

describe('use-orientation hooks', () => {
  it('should not dispatch any orientation', () => {
    mockUseMediaQuery.mockReturnValue([false, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <Orientation />
      </AppDataProvider>
    );
    screen.getByText('Initial: true and orientation: none');
  });
  it('should dispatch portrait orientation', () => {
    mockUseMediaQuery.mockReturnValue([true, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <Orientation />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and orientation: isPortrait');
  });
  it('should dispatch landscape orientation', () => {
    mockUseMediaQuery.mockReturnValue([false, true]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <Orientation />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and orientation: isLandscape');
  });
});
