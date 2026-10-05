export function handleRefHeightChange(
  node: HTMLDivElement | HTMLUListElement | null,
  setHeight: (arg0: number) => void
) {
  if (node?.clientHeight) {
    setHeight(node.clientHeight);
  }
}
