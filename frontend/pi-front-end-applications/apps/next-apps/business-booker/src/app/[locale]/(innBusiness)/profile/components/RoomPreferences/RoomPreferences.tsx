'use client';

import { Customer } from '@whitbread-eos/api';
import { Button, ButtonVariantDescriptor } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

import { TrackableComponent } from '../ChangePassword/ChangePassword';
import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';
import { RoomPreferencesForm } from './RoomPreferencesForm';

type Props = {
  baseDataTestId?: string;
  icons: Record<string, string>;
  profileDetails: Customer;
} & TrackableComponent;

export function RoomPreferences({
  baseDataTestId,
  icons,
  profileDetails,
  onReviewChangesToggle,
  onIsEditableToggle,
  onIsUpdatingToggle,
  onIsDirtyToggle,
  isEditable,
  isUpdating,
  isDirty,
}: Props) {
  const { t } = useTranslation('profile');

  return (
    <div data-testid={`${baseDataTestId}-Room-Preferences-Container`} className={containerStyle}>
      <h4 data-testid={`${baseDataTestId}-Room-Preferences-Title`} className={titleStyle}>
        {t('roomrequirements.title')}
      </h4>
      <p
        className={descriptionStyle}
        data-testid={`${baseDataTestId}-Room-Preferences-Description`}
      >
        {t('roomrequirements.description')}
      </p>
      {!isEditable ? (
        <div data-testid={`${baseDataTestId}-Room-Preferences-Content`}>
          <Button
            variant="outline"
            size="lg"
            className={buttonStyle}
            data-testid={`${baseDataTestId}-Room-Preferences-Edit-Button`}
            onClick={() => onIsEditableToggle(CHANGES_TRACKER.ROOM_PREFERENCES, true)}
          >
            {t('roomrequirements.button.edit')}
          </Button>
        </div>
      ) : (
        <RoomPreferencesForm
          icons={icons}
          baseDataTestId={baseDataTestId}
          profileDetails={profileDetails}
          handleEditMode={() => onIsEditableToggle(CHANGES_TRACKER.ROOM_PREFERENCES, false)}
          isUpdating={isUpdating}
          onIsUpdatingToggle={onIsUpdatingToggle}
          onReviewChangesToggle={onReviewChangesToggle}
          onIsDirtyToggle={onIsDirtyToggle}
          onIsEditableToggle={onIsEditableToggle}
          isDirty={isDirty}
        />
      )}
    </div>
  );
}

const containerStyle = 'leading-[2rem] text-darkGrey1 mt-[4rem] border-b-[1px] border-lightGrey3';
const titleStyle = 'font-semibold text-[1.438rem]';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] mt-[1.5rem] mb-[4rem] ml-auto leading-[1.5rem] rounded-sm py-[1rem] ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const descriptionStyle = 'leading-[1.5rem] mt-[.5rem]';
