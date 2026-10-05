'use client';

import { useContext } from 'react';

import { WizardContext, WizardContextType } from './Wizard.component';
import { usePayAppAccessValidation } from './usePayAppAccessValidation';

export function useWizardContext<T>() {
  const result = useContext<WizardContextType<T> | undefined>(WizardContext);

  if (!result) {
    throw new Error('useWizardContext can only be used inside a WizardContext.Provider');
  }

  const { state, setState, steps, stepId, setStepId, icons } = result;
  const { withValidation, validatePayAppAccess } = usePayAppAccessValidation<T>(state);

  const currentStep = steps.find((step) => step.id === stepId);
  if (!currentStep) {
    throw new Error(`step ${stepId} not found`);
  }

  const setWizardStateWithValidation = async (newState: T | ((prev: T) => T)) => {
    const isMovingFromLanding = stepId === 'LANDING';
    const isFirstStepAfterLanding = steps.findIndex((step) => step.id === stepId) === 1;

    if (isMovingFromLanding || isFirstStepAfterLanding) {
      setState(newState);
    } else {
      await validatePayAppAccess();
      setState(newState);
    }
  };

  const goToStep = async (id: string, skipValidation?: boolean): Promise<void> => {
    if (skipValidation) {
      setStepId(id);
      return;
    }
    await withValidation(() => setStepId(id));
  };

  const goToNextStep = async (): Promise<void> => {
    const index = steps.findIndex((step) => step.id === stepId);
    if (index === -1 || index + 1 >= steps.length) return;

    const isMovingFromLanding = stepId === 'LANDING';

    if (isMovingFromLanding) {
      setStepId(steps[index + 1].id);
    } else {
      await withValidation(() => setStepId(steps[index + 1].id));
    }
  };

  const goToPreviousStep = (): void => {
    const index = steps.findIndex((step) => step.id === stepId);
    if (index === -1 || index - 1 < 0) return;

    setStepId(steps[index - 1].id);
  };

  return {
    wizardState: state,
    setWizardState: setWizardStateWithValidation,
    steps,
    currentStep,
    goToStep,
    goToNextStep,
    goToPreviousStep,
    icons,
  };
}
