import { Switch, SwitchProps } from '@chakra-ui/react';
import { useState, ChangeEvent } from 'react';

type Override<T1, T2> = Omit<T1, keyof T2> & T2;

export type Props = Override<
  SwitchProps,
  {
    onChange?: (status: { isChecked: boolean }, event?: ChangeEvent<HTMLInputElement>) => void;
  }
>;

//If more custom props are needed, the below can be done
// export interface MoreProps extends Props {
//   //add more props
// }

export default function Switcher({ isChecked = false, onChange = () => null, ...rest }: Props) {
  const [checked, setChecked] = useState(isChecked);

  const handleSwitcher = (e: ChangeEvent<HTMLInputElement>) => {
    onChange({ isChecked: !checked }, e);
    setChecked(!checked);
  };

  return <Switch onChange={handleSwitcher} isChecked={checked} {...rest} />;
}
