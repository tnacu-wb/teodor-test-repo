import {
  GLOBALS,
  formatAssetsUrl,
  analytics,
  getCookie,
  setCookieWithDefaultDomain,
  deleteCookieWithDefaultDomain,
} from '@whitbread-eos/utils';
import getConfig from 'next/config';

import { ACTIVE_CHAT_COOKIE, ACTIVE_CHAT_COOKIE_EXPIRY } from '~utils/chat-constants';

function updateAnalyticsLiveChat(data: Record<string, any>) {
  const currentData = window?.analyticsData?.liveChat ?? {};
  analytics.update({
    liveChat: {
      ...currentData,
      ...data,
    },
  });
}

export const initializeAmazonConnectChat = (language: string) => {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  let amazonChatSnippetId = publicRuntimeConfig.NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_GB;

  if (language === GLOBALS.language.DE) {
    amazonChatSnippetId = publicRuntimeConfig.NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_DE;
  }

  window.amazon_connect =
    window.amazon_connect ||
    function (...props) {
      (window['amazon_connect'].ac = window['amazon_connect'].ac || []).push(props);
    };

  if (window.amazon_connect) {
    updateAnalyticsLiveChat({ available: true });

    window.amazon_connect('styles', {
      iconType: 'CHAT',
      openChat: { color: '#ffffff', backgroundColor: '#521e5a' },
      closeChat: { color: '#ffffff', backgroundColor: '#521e5a' },
    });
    window.amazon_connect('snippetId', amazonChatSnippetId);

    // JWT authentication for secure access to the chat platform
    window.amazon_connect('authenticate', function (callback: (token: string) => void) {
      window.fetch(publicRuntimeConfig.NEXT_PUBLIC_AMAZON_CHAT_AUTH_URL).then((res) => {
        res.json().then((data) => {
          callback(data.token);
        });
      });
    });

    // Chat window customization
    window.amazon_connect('customizationObject', {
      header: {
        dropdown: true,
        dynamicHeader: false,
      },
      transcript: {
        hideDisplayNames: false,
        eventNames: {
          customer: 'Customer',
        },
        eventMessages: {
          participantIdle:
            "Hey, we noticed you haven't responded in a while, do you still need help?",
        },
        displayIcons: true,
        iconSources: {
          botMessage: formatAssetsUrl('/content/dam/global/icons/chat-icons/PI_logo_big.png'),
          systemMessage: formatAssetsUrl('/content/dam/global/icons/chat-icons/PI_logo_big.png'),
          agentMessage: formatAssetsUrl('/content/dam/global/icons/chat-icons/Agent_big.png'),
        },
      },
      composer: {
        disableEmojiPicker: true,
        disableCustomerAttachments: false,
      },
      footer: {
        disabled: false,
        skipCloseChatButton: true,
        buttonFontSize: '10px',
      },
    });

    window.amazon_connect('customStyles', {
      global: {
        frameWidth: '320px',
        frameHeight: '600px',
        textColor: '#000000',
        fontSize: '14px',
        footerHeight: '50px',
      },
      header: {
        headerTextColor: '#FFFFFF',
        headerBackgroundColor: '#521e51',
      },
      transcript: {
        messageFontSize: '12px',
      },
      footer: {
        buttonFontSize: '15px',
      },
      logo: {
        logoMaxHeight: '50px',
        logoMaxWidth: '80%',
      },
    });

    // Callback function to store and clear chat transcript from the cookies
    window.amazon_connect('registerCallback', {
      CONNECTION_ESTABLISHED: () => {
        setCookieWithDefaultDomain(
          ACTIVE_CHAT_COOKIE,
          sessionStorage.getItem('persistedChatSession'),
          ACTIVE_CHAT_COOKIE_EXPIRY
        );
        updateAnalyticsLiveChat({ opened: true });
      },
      CHAT_ENDED: () => {
        deleteCookieWithDefaultDomain(ACTIVE_CHAT_COOKIE);
        updateAnalyticsLiveChat({ endChat: true });
      },
    });

    // To maintain chat persist across multiple windows
    const activeChatValue = getCookie(ACTIVE_CHAT_COOKIE);
    if (activeChatValue) {
      sessionStorage.setItem('persistedChatSession', activeChatValue);
    }
  }
};
