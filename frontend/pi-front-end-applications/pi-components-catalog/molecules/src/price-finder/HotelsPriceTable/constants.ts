export const TABLE_CONFIG = {
  DAYS_TO_SHOW: 7,
  SCROLL_OFFSET: 7,
  MAX_BACKWARD_DAYS: 0,
  SKELETON_ROWS_COUNT: 20,
  PAGE_SIZE: 20,
  LAZY_LOAD_PAGE_SIZE: 20,
} as const;

export const THEME_COLORS = {
  headerBg: '#FFFFFF',
  sortedBg: '#F8F8F8',
  sortedHoverBg: '#00000026',
  hoverBg: '#F8F8F8',
  priceColor: '#285E61',
  disabledColor: '#9CA3AF',
  sortedCellBg: '#0000000D',
  primaryRowBg: '#F8F8F8',
  alternateRowBg: '#FFFFFF',
  lowestPrice: '#FDB913',
  nearLowestPrice: '#FFEBBB',
  borderColor: '#CCCCCC',
  primaryColor: '#00798E',
  sortTextColor: '#511E62',
  floatingHeaderBg: '#FFFFFF',
} as const;

export const DEFAULT_LOCATION_ID = 'ChIJdd4hrwug2EcRmSrV3Vo6llI';
export const DEFAULT_LOCATION_ID_DE = 'ChIJxZZwR28JvUcRAMawKVBDIgQ';

export const SORT_BY_VALUES = {
  DISTANCE: 'distance',
  PRICE: 'price',
} as const;
