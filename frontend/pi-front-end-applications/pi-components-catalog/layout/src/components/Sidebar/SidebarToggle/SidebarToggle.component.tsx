import classNames from 'classnames';
import Image from 'next/image';

interface Props {
  collapseIcon: string;
  expandIcon: string;
  isCollapsed?: boolean;
  onToggle: () => void;
  className?: string;
}

const SidebarToggle = ({
  collapseIcon,
  expandIcon,
  isCollapsed,
  onToggle,
  className,
}: Readonly<Props>) => {
  const baseDataTestId = 'SidebarToggle';

  return (
    <button
      className={classNames(toggleStyle, className)}
      data-testid={`${baseDataTestId}-button`}
      onClick={onToggle}
      aria-expanded={!isCollapsed}
      aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
      type="button"
    >
      <Image
        className={iconStyle}
        src={isCollapsed ? expandIcon : collapseIcon}
        alt=""
        aria-hidden="true"
        width={24}
        height={24}
      />
    </button>
  );
};

export default SidebarToggle;

const toggleStyle =
  'absolute flex justify-center items-center w-8 h-8 rounded-full border border-lightGrey3 bottom-28 -right-4 bg-baseWhite cursor-pointer hover:bg-lightGrey4 active:bg-toggleButtonPressed';
const iconStyle = 'text-transparent';
