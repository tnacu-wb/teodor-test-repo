import React from 'react';

import { AppData, AppDataProvider } from '../store/AppDataContext';
import { render, screen } from '../utils/test-utils';
import useSetScreenSize, { useScreenSize } from './use-screensize';

const mockUseMediaQuery = jest.fn();
jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: (...args: string[]) => mockUseMediaQuery(...args),
  useTheme: () => ({
    breakpoints: {
      mobile: '320px',
      xs: '375px',
      sm: '576px',
      md: '768px',
      lg: '1280px',
      xl: '1440px',
    },
  }),
}));

function ScreenSize() {
  useSetScreenSize();
  const sizes = useScreenSize();
  const isInitial = Object.values(sizes).every((size) => size === undefined);

  return (
    <>
      {`Initial: ${isInitial} and less than ${
        Object.values(sizes).filter((size) => size).length
      } breakpoints`}
    </>
  );
}

const initialAppData: AppData = {
  screenSize: undefined,
  orientation: undefined,
};

describe('use-screensize hooks', () => {
  it('should not dispatch any screen size', () => {
    mockUseMediaQuery.mockReturnValue([false, false, false, false, false, false, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: true and less than 0 breakpoints');
  });

  it('should dispatch base screen size', () => {
    mockUseMediaQuery.mockReturnValue([true, false, false, false, false, false, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and less than 6 breakpoints');
  });

  it('should dispatch mobile screen size', () => {
    mockUseMediaQuery.mockReturnValue([false, true, false, false, false, false, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and less than 5 breakpoints');
  });

  it('should dispatch xs screen size', () => {
    mockUseMediaQuery.mockReturnValue([false, false, true, false, false, false, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and less than 4 breakpoints');
  });

  it('should dispatch sm screen size', () => {
    mockUseMediaQuery.mockReturnValue([false, false, false, true, false, false, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and less than 3 breakpoints');
  });

  it('should dispatch md screen size', () => {
    mockUseMediaQuery.mockReturnValue([false, false, false, false, true, false, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and less than 2 breakpoints');
  });

  it('should dispatch lg screen size', () => {
    mockUseMediaQuery.mockReturnValue([false, false, false, false, false, true, false]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and less than 1 breakpoints');
  });

  it('should dispatch xl screen size', () => {
    mockUseMediaQuery.mockReturnValue([false, false, false, false, false, false, true]);
    render(
      <AppDataProvider initialAppData={initialAppData}>
        <ScreenSize />
      </AppDataProvider>
    );
    screen.getByText('Initial: false and less than 0 breakpoints');
  });
});
