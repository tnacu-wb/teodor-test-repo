import { act, renderHook } from '@testing-library/react';
import React from 'react';

import { Wizard } from './Wizard.component';
import { useWizardContext } from './useWizardContext';

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    refresh: jest.fn(),
    prefetch: jest.fn(),
  }),
  usePathname: () => '/test-path',
}));

type TestStateType = {
  value?: string;
};

/* eslint-disable react/display-name */
jest.mock('../Main', () => {
  return ({ children }: { children: React.ReactNode }) => <div>{children}</div>;
});

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <Wizard
    icons={{}}
    header={<div></div>}
    initialState={{
      value: 'test',
    }}
    initialStepId="step2"
    steps={[
      {
        id: 'step1',
        component: <div>{children}</div>,
      },
      {
        id: 'step2',
        component: <div>{children}</div>,
      },
      {
        id: 'step3',
        component: <div>{children}</div>,
      },
    ]}
  />
);

describe('useWizardContext hook', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render useWizardContext hook', async () => {
    const { result } = renderHook(() => useWizardContext<TestStateType>(), {
      wrapper,
    });
    const { wizardState } = result.current;

    expect(wizardState.value).toBe('test');
  });

  it('should go to a specific step', async () => {
    const { result } = renderHook(() => useWizardContext<TestStateType>(), {
      wrapper,
    });

    expect(result.current.currentStep.id).toBe('step2');
    await act(async () => {
      await result.current.goToStep('step3', true);
    });
    expect(result.current.currentStep.id).toBe('step3');
  });

  it('should go to the next step until there is none', async () => {
    const { result } = renderHook(() => useWizardContext<TestStateType>(), {
      wrapper,
    });

    expect(result.current.currentStep.id).toBe('step2');
    await act(async () => {
      await result.current.goToNextStep();
    });
    expect(result.current.currentStep.id).toBe('step3');
    await act(async () => {
      await result.current.goToNextStep();
    });
    expect(result.current.currentStep.id).toBe('step3');
  });

  it('should go to the previous step until there is none', async () => {
    const { result } = renderHook(() => useWizardContext<TestStateType>(), {
      wrapper,
    });

    expect(result.current.currentStep.id).toBe('step2');
    act(() => {
      result.current.goToPreviousStep();
    });
    expect(result.current.currentStep.id).toBe('step1');
    act(() => {
      result.current.goToPreviousStep();
    });
    expect(result.current.currentStep.id).toBe('step1');
  });
});
