export function tabbingAccessibility(
  event: React.KeyboardEvent,
  focusableRefs: React.MutableRefObject<HTMLElement[]>,
  isOpen?: boolean
): void {
  if (event.key !== 'Tab') return;
  const focusable = focusableRefs.current;

  if (!focusable.length) return;

  const activeElement = document.activeElement as HTMLElement | null;
  const currentIndex = activeElement ? focusable.indexOf(activeElement) : -1;

  if (isOpen) {
    event.preventDefault();
  }

  const direction = event?.shiftKey ? -1 : 1;

  let nextIndex = currentIndex + direction;

  if (nextIndex < 0) nextIndex = focusable.length - 1;
  if (nextIndex >= focusable.length) nextIndex = 0;
  focusable[nextIndex]?.focus();
}
