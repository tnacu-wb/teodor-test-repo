import { actionsLeftStyle, fieldStyle, nameStyle, wrapperDivStyle } from './full-name';

type Props = {
  baseDataTestId: string;
  value: string;
  label: string;
};

export const ContactNumber = ({ baseDataTestId, value, label }: Props) => {
  return (
    <div className={fieldStyle} data-testid={`${baseDataTestId}-Wrapper`}>
      <div className={actionsLeftStyle} data-testid={`${baseDataTestId}`}>
        <div className={wrapperDivStyle}>
          <span data-testid={`${baseDataTestId}-Label`} className={nameStyle}>
            {label}
          </span>
        </div>
      </div>
      <span data-testid={`${baseDataTestId}-Value`}>{value}</span>
    </div>
  );
};
