export function truncateLabel(label: string, labelLengthOverflow: number) {
  return label.substring(0, labelLengthOverflow - 3) + '...';
}
