import { AccountSelectorSkeleton } from '../homepage/components';

export default function Loading() {
  return (
    <div
      className={
        'p-12 flex flex-col w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto'
      }
    >
      <AccountSelectorSkeleton />
    </div>
  );
}
