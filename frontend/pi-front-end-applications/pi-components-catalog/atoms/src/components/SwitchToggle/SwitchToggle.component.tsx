import { formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

export interface SwitchProps {
  onToggle: (isFirstSelected: boolean) => void;
  first: string;
  second: string;
  defaultSelected?: boolean;
  baseDataTestId: string;
  fontSize?: string;
}

export default function SwitchToggle({
  onToggle,
  first,
  second,
  defaultSelected = true,
  baseDataTestId,
  fontSize,
}: Readonly<SwitchProps>) {
  const [firstSelected, setFirstSelected] = useState(defaultSelected);

  useEffect(() => {
    if (firstSelected != defaultSelected) {
      setFirstSelected(defaultSelected);
    }
  }, [defaultSelected]);

  const handleToggle = () => {
    setFirstSelected((prev) => !prev);
    onToggle(!firstSelected);
  };

  const handleKeyUp = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      handleToggle();
    }
  };

  return (
    <div
      role="switch"
      tabIndex={0}
      aria-checked={firstSelected}
      onClick={handleToggle}
      onKeyUp={handleKeyUp}
      style={styles.switchToggle}
      data-testid={formatDataTestId(baseDataTestId, 'SwitchToggle')}
    >
      <span
        data-testid={formatDataTestId(baseDataTestId, 'first')}
        style={{
          ...styles.option,
          ...(fontSize && { fontSize }),
          ...(firstSelected ? styles.selected : {}),
        }}
      >
        {first}
      </span>
      <span
        data-testid={formatDataTestId(baseDataTestId, 'second')}
        style={{
          ...styles.option,
          ...(fontSize && { fontSize }),
          ...(!firstSelected ? styles.selected : {}),
        }}
      >
        {second}
      </span>
    </div>
  );
}

const styles = {
  switchToggle: {
    height: '2.625rem',
    border: '1px solid #cccccc',
    borderRadius: '2.625rem',
    display: 'inline-flex',
    justifyContent: 'space-between',
    padding: '0.188rem',
    cursor: 'pointer',
    userSelect: 'none',
  },
  option: {
    lineHeight: '2.1rem',
    display: 'inline-block',
    textAlign: 'center',
    flexGrow: 1,
    padding: '0 0.875rem',
  },

  selected: {
    backgroundColor: '#00798e',
    borderRadius: '2rem',
    color: '#ffffff',
  },
} as const;
