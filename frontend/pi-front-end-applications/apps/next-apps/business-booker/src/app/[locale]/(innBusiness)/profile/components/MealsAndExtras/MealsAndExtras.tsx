'use client';

import { Customer } from '@whitbread-eos/api';
import { Button, ButtonVariantDescriptor } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

import { TrackableComponent } from '../ChangePassword/ChangePassword';
import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';
import { MealsAndExtrasForm } from './MealsAndExtrasForm';

type Props = {
  baseDataTestId: string;
  icons: Record<string, string>;
  profileDetails: Customer;
} & TrackableComponent;

export function MealsAndExtras({
  baseDataTestId,
  icons,
  profileDetails,
  isEditable,
  onIsEditableToggle,
  ...trackableProps
}: Props) {
  const { t } = useTranslation('profile');

  const handleEditMode = () => onIsEditableToggle(CHANGES_TRACKER.MEALS_EXTRAS, !isEditable);

  return (
    <div data-testid={`${baseDataTestId}-Meals-And-Extras-Container`} className={containerStyle}>
      <h4 data-testid={`${baseDataTestId}-Meals-And-Extras-Title`} className={titleStyle}>
        {t('extraspreferences.title')}
      </h4>
      <p
        className={descriptionStyle}
        data-testid={`${baseDataTestId}-Meals-And-Extras-Description`}
      >
        {t('extraspreferences.edit.description')}
      </p>
      {!isEditable ? (
        <div data-testid={`${baseDataTestId}-Meals-And-Extras-Content`}>
          <Button
            variant="outline"
            size="lg"
            className={buttonStyle}
            data-testid={`${baseDataTestId}-Meals-And-Extras-Edit-Button`}
            onClick={() => handleEditMode()}
          >
            {t('extraspreferences.button.edit')}
          </Button>
        </div>
      ) : (
        <MealsAndExtrasForm
          icons={icons}
          baseDataTestId={baseDataTestId}
          profileDetails={profileDetails}
          handleEditMode={handleEditMode}
          {...trackableProps}
        />
      )}
    </div>
  );
}

const containerStyle = 'leading-[2rem] text-darkGrey1 mt-[4rem] border-b-[1px] border-lightGrey3';
const titleStyle = 'font-semibold text-[1.438rem]';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] mt-[1.5rem] mb-[4rem] ml-auto leading-[1.5rem] rounded-sm py-[1rem] ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const descriptionStyle = 'leading-[1.5rem] mt-[.5rem]';
