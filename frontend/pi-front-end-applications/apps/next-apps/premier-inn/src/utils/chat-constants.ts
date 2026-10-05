// Chat cookie constants
export const ACTIVE_CHAT_COOKIE = 'activeChat';
export const ACTIVE_CHAT_COOKIE_EXPIRY = 60 * 24; // 1 day in minutes

// Amazon Connect Chat bot status constants
export const CHAT_BOT_STATUS = {
  HIDE: 'HIDE',
  SHOW_ALL_PAGES: 'SHOW_ALL_PAGES',
  SHOW_LIMITED_PAGES: 'SHOW_LIMITED_PAGES',
} as const;

// Type for chatBotStatus
export type ChatBotStatus = (typeof CHAT_BOT_STATUS)[keyof typeof CHAT_BOT_STATUS];
