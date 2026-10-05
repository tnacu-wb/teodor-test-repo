import { useCallback, useEffect, useState } from 'react';

export type QuestionDirtyChangeHandler = (questionId: string, isDirty: boolean) => void;

type UseQuestionDirtyStateProps = {
  questionTrackerId?: string;
  onQuestionDirtyChange?: QuestionDirtyChangeHandler;
};

export const useQuestionDirtyState = ({
  questionTrackerId,
  onQuestionDirtyChange,
}: UseQuestionDirtyStateProps) => {
  const [isFormDirty, setIsFormDirty] = useState(false);

  const updateDirtyState = useCallback(
    (isDirty: boolean) => {
      setIsFormDirty(isDirty);
      if (!questionTrackerId || !onQuestionDirtyChange) {
        return;
      }

      onQuestionDirtyChange(questionTrackerId, isDirty);
    },
    [onQuestionDirtyChange, questionTrackerId]
  );

  useEffect(() => {
    return () => {
      if (!questionTrackerId || !onQuestionDirtyChange) {
        return;
      }

      onQuestionDirtyChange(questionTrackerId, false);
    };
  }, [onQuestionDirtyChange, questionTrackerId]);

  return {
    isFormDirty,
    updateDirtyState,
  };
};
