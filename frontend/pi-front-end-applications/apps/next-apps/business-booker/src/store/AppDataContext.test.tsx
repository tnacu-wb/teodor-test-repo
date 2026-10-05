import { render, screen, act } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';
import React from 'react';

import { AppDataProvider, useAppData, useAppDataDispatch } from './AppDataContext';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
  },
}));

function TestConsumer() {
  const appData = useAppData();

  return (
    <>
      <div data-testid="screenSize">{appData?.screenSize ?? ''}</div>
      <div data-testid="orientation">{appData?.orientation ?? ''}</div>
    </>
  );
}

function DispatchConsumer() {
  const dispatch = useAppDataDispatch();

  return (
    <>
      <button
        onClick={() =>
          dispatch({
            type: 'ui/screenSize',
            payload: 'md',
          })
        }
      >
        set-screen
      </button>

      <button
        onClick={() =>
          dispatch({
            type: 'ui/orientation',
            payload: 'landscape',
          })
        }
      >
        set-orientation
      </button>
    </>
  );
}

describe('AppDataProvider', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('provides default initial state', () => {
    render(
      <AppDataProvider>
        <TestConsumer />
      </AppDataProvider>
    );

    expect(screen.getByTestId('screenSize').textContent).toBe('');
    expect(screen.getByTestId('orientation').textContent).toBe('');
  });

  it('provides supplied initial state', () => {
    render(
      <AppDataProvider
        initialAppData={{
          screenSize: 'lg',
          orientation: 'portrait',
        }}
      >
        <TestConsumer />
      </AppDataProvider>
    );

    expect(screen.getByTestId('screenSize').textContent).toBe('lg');
    expect(screen.getByTestId('orientation').textContent).toBe('portrait');
  });

  it('updates screen size and sends analytics breakpoint', () => {
    render(
      <AppDataProvider>
        <DispatchConsumer />
        <TestConsumer />
      </AppDataProvider>
    );

    act(() => {
      screen.getByText('set-screen').click();
    });

    expect(screen.getByTestId('screenSize').textContent).toBe('md');

    expect(analytics.update).toHaveBeenCalledWith({
      breakpoint: 'Medium',
    });
  });

  it('updates orientation', () => {
    render(
      <AppDataProvider>
        <DispatchConsumer />
        <TestConsumer />
      </AppDataProvider>
    );

    act(() => {
      screen.getByText('set-orientation').click();
    });

    expect(screen.getByTestId('orientation').textContent).toBe('landscape');
  });

  it('does not call analytics when orientation changes', () => {
    render(
      <AppDataProvider>
        <DispatchConsumer />
      </AppDataProvider>
    );

    act(() => {
      screen.getByText('set-orientation').click();
    });

    expect(analytics.update).not.toHaveBeenCalled();
  });
});
