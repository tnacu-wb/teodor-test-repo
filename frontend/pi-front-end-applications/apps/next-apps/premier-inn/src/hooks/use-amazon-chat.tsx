import { getCookie } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import { ACTIVE_CHAT_COOKIE, CHAT_BOT_STATUS } from '~utils/chat-constants';
import { initializeAmazonConnectChat } from '~utils/chatBot';

interface UseAmazonChatProps {
  pageProps: any;
  language: string;
}

interface UseAmazonChatResult {
  isAmazonChatBoxEnabled: boolean;
  amazonChatIcon: string | undefined;
}

function getHeaderInformationFromDehydratedState(pageProps: any) {
  const queries = pageProps?.dehydratedState?.queries || [];
  const staticContentQuery = queries.find((query: any) => query?.state?.data?.headerInformation);
  return staticContentQuery?.state?.data?.headerInformation;
}

/**
 * Custom hook to manage Amazon Connect Chat visibility based on chatBotStatus configuration
 *
 * Priority order:
 * 1. CHAT_BOT_STATUS.HIDE - always hide chat (overrides everything)
 * 2. ACTIVE_CHAT_COOKIE - show on all pages if cookie exists (bypasses page restrictions)
 * 3. CHAT_BOT_STATUS.SHOW_ALL_PAGES - show on all pages
 * 4. CHAT_BOT_STATUS.SHOW_LIMITED_PAGES - show only on pages listed in webChatEnabledPages
 */
export function useAmazonChat({ pageProps, language }: UseAmazonChatProps): UseAmazonChatResult {
  const [isAmazonChatBoxEnabled, setIsAmazonChatBoxEnabled] = useState(false);
  const [cachedChatIcon, setCachedChatIcon] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (typeof window !== 'undefined' && typeof document !== 'undefined') {
      const headerInformation = getHeaderInformationFromDehydratedState(pageProps);
      const amazonChat = headerInformation?.config?.amazonChat;
      const chatBotStatus = amazonChat?.chatBotStatus;
      const webChatEnabledPages = amazonChat?.webChatEnabledPages || [];
      const activeChatCookie = getCookie(ACTIVE_CHAT_COOKIE);

      let shouldShowChat = false;

      // Priority 1: If status is HIDE, always hide chat (overrides everything)
      if (chatBotStatus === CHAT_BOT_STATUS.HIDE) {
        shouldShowChat = false;
      }
      // Priority 2: If ACTIVE_CHAT_COOKIE exists, show chat on all pages (bypasses page restrictions)
      else if (activeChatCookie) {
        shouldShowChat = true;
      }
      // Priority 3: If status is SHOW_ALL_PAGES, show on all pages
      else if (chatBotStatus === CHAT_BOT_STATUS.SHOW_ALL_PAGES) {
        shouldShowChat = true;
      }
      // Priority 4: If status is SHOW_LIMITED_PAGES, check page restrictions
      else if (chatBotStatus === CHAT_BOT_STATUS.SHOW_LIMITED_PAGES) {
        const currentPath = window.location.pathname;

        const isPageEnabled = webChatEnabledPages.some((enabledPath: string) => {
          return enabledPath === currentPath || currentPath.includes(enabledPath);
        });

        shouldShowChat = isPageEnabled;
      }

      setIsAmazonChatBoxEnabled(shouldShowChat);

      const chatIcon = amazonChat?.chatIcon;
      if (chatIcon) {
        setCachedChatIcon(chatIcon);
      }

      if (shouldShowChat) {
        initializeAmazonConnectChat(language);
      }
    }
  }, [pageProps?.dehydratedState?.queries, language]);

  const currentChatIcon =
    getHeaderInformationFromDehydratedState(pageProps)?.config?.amazonChat?.chatIcon;

  return {
    isAmazonChatBoxEnabled,
    amazonChatIcon: currentChatIcon ?? cachedChatIcon,
  };
}
