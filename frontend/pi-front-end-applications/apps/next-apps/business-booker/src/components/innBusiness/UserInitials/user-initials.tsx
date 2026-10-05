import { EmployeeDetails } from '@whitbread-eos/api';
import { cn, getInitials } from '@whitbread-eos/utils/server';

export type UserInitialsProps = {
  type: EmployeeDetails;
  className?: string;
};

export async function UserInitials({ type, className }: UserInitialsProps) {
  const baseDataTestId = 'UserInitials';
  const userInitials = getInitials(type.firstName, type.lastName);

  return (
    <div data-testid={baseDataTestId} className={cn(containerStyle, className)}>
      <span data-testid={`${baseDataTestId}-userInitials`} className={initialsStyle}>
        {userInitials}
      </span>
      <div data-testid={`${baseDataTestId}-name-and-email`} className={containerNameEmailStyle}>
        <span data-testid={`${baseDataTestId}-name`} className={nameStyle}>
          {type.firstName} {type.lastName}
        </span>
        <span data-testid={`${baseDataTestId}-email`} className={emailStyle}>
          {type.emailAddress}
        </span>
      </div>
    </div>
  );
}

const emailStyle = 'font-normal text-sm';
const nameStyle = 'font-semibold text-base';
const containerStyle = 'flex items-center gap-4 ';
const containerNameEmailStyle = 'flex flex-col py-2';
const initialsStyle =
  'flex text-center items-center justify-center rounded-full w-10 h-10 bg-lightGrey3 text-base text-darkGrey1 font-semibold mobile:hidden';
