'use client';

import { createContext, Dispatch, ReactNode, SetStateAction, useState } from 'react';

import Main from '../Main';

export type WizardStep = {
  id: string;
  component: React.ReactNode;
};

export type WizardContextType<T> = {
  state: T;
  setState: Dispatch<SetStateAction<T>>;
  steps: WizardStep[];
  stepId: string;
  setStepId: Dispatch<SetStateAction<string>>;
  icons: Record<string, string>;
};

export const WizardContext = createContext<WizardContextType<any> | undefined>(undefined);

export type WizardProps<T> = {
  initialState: T;
  initialStepId: string;
  steps: WizardStep[];
  header: ReactNode;
  icons: Record<string, string>;
};

export function Wizard<T>({ initialState, initialStepId, steps, header, icons }: WizardProps<T>) {
  const [state, setState] = useState(initialState);
  const [stepId, setStepId] = useState(initialStepId);

  const currentStep = steps.find((step) => step.id === stepId);
  if (!currentStep) {
    throw new Error(`step ${stepId} not found`);
  }

  return (
    <WizardContext.Provider value={{ state, setState, steps, stepId, setStepId, icons }}>
      <div className={containerStyle} data-testid="wizard">
        {header}
        <Main hasSidebar={false} className={mainStyle}>
          {currentStep.component}
        </Main>
      </div>
    </WizardContext.Provider>
  );
}

const containerStyle = 'flex flex-col min-h-screen';
const mainStyle = 'mobile:grow';
