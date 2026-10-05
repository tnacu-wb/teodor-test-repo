import { cn } from '@whitbread-eos/utils';
import * as React from 'react';

export type InputProps = React.InputHTMLAttributes<HTMLInputElement>;

interface IBInputProps extends InputProps {
  error?: string;
  hasLabels?: boolean;
}

const Input = React.forwardRef<HTMLInputElement, IBInputProps>(
  ({ className, type, ...props }, ref) => {
    const { placeholder, id, disabled, value, onChange, error, hasLabels = true } = props;

    const handleChange = (event: any) => {
      if (onChange) onChange(event?.target?.value);
    };

    return (
      <div className="relative w-full">
        <input
          data-testid={`${id}-Input`}
          id={`${id}-Input`}
          type={type}
          className={cn(
            'peer flex h-14 text-darkGrey1 border-lightGrey1 focus:outline-primaryColor focus:outline-2 hover:border-darkGrey1 w-full rounded border bg-transparent p-4 text-base transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-darkGrey2 disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:border-lightGrey1',
            className
          )}
          ref={ref}
          value={value}
          disabled={disabled}
          placeholder={placeholder}
          onChange={(e) => handleChange(e)}
        />
        {hasLabels && (
          <label
            htmlFor={`${id}-Input`}
            className={`hidden absolute peer-focus:!block text-darkGrey1 peer-focus:text-primaryColor -top-2.5 ml-3 text-sm px-1 bg-baseWhite ${
              error ? 'text-red' : ''
            } ${value ? '!block' : ''}`}
            data-testid={`${id}-Label`}
          >
            {placeholder}
          </label>
        )}
      </div>
    );
  }
);
Input.displayName = 'Input';

export { Input };
