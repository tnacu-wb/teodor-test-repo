export function isIOSDevice(): boolean {
  return (
    (navigator as any).userAgentData?.platform === 'iOS' ||
    /iPhone|iPad|iPod/.test(navigator.userAgent)
  );
}
