import type { SwitchState } from '@whitbread-eos/api';
import { Switch } from '@whitbread-eos/atoms/ui';

export const RenderSwitch = (
  switchName: keyof SwitchState,
  switchState: SwitchState,
  baseTestId: string,
  handleCheckedChange: (switchName: keyof SwitchState) => void,
  label?: string,
  descriptionLabel?: string
) => (
  <div className={switchContainerStyle} key={switchName}>
    <Switch
      checked={switchState[switchName]}
      key={switchName}
      onCheckedChange={() => handleCheckedChange(switchName)}
      data-testid={`${baseTestId}-${switchName}-switcher`}
      className={switchStyle}
    />
    <div className={switchLabelStyle} data-testid={`${baseTestId}-${switchName}-label-container`}>
      <h4
        data-testid={`${baseTestId}-${switchName}-label`}
        className={'text-darkGrey1 font-bold text-base'}
      >
        {label}
      </h4>
      <span className={'text-darkGrey2 text-normal text-sm'}>{descriptionLabel}</span>
    </div>
  </div>
);
const switchContainerStyle = 'flex gap-4 items-center';
const switchLabelStyle = 'flex flex-col tablet:order-2 mobile:order-1';
const switchStyle = 'mobile:order-2';
