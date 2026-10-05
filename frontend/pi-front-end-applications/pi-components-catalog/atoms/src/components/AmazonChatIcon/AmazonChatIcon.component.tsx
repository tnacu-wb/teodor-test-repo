import { formatAssetsUrl } from '@whitbread-eos/utils';
import { useEffect } from 'react';

interface Props {
  amazonChatIcon?: string;
}

export default function AmazonChatIcon({ amazonChatIcon }: Readonly<Props>) {
  useEffect(() => {
    const style = document.createElement('style');
    style.innerHTML = `
      #amazon-connect-open-widget-button {
        background: unset !important;
        width: unset !important;
        box-shadow: unset !important;
        max-width: 220px;
        transition: unset;
      }
      #amazon-connect-close-widget-button #live-chat-gif-icon,
      #amazon-connect-open-widget-button > svg {
        display: none !important;
      }
      #amazon-connect-open-widget-button[class^="acOpenButton"] {
        outline: none;
      }
    `;
    document.head.appendChild(style);

    const observer = new MutationObserver((mutations, obs) => {
      const button = document.querySelector('#amazon-connect-open-widget-button');
      if (button) {
        button?.setAttribute('aria-label', 'Open chat support');
        const img = document.createElement('img');
        img.id = 'live-chat-gif-icon';
        img.src = formatAssetsUrl(amazonChatIcon as string);
        button.appendChild(img);

        obs.disconnect();
      }
    });

    observer.observe(document.body, { childList: true, subtree: true });

    return () => {
      observer.disconnect();
      document.head.removeChild(style);
    };
  }, []);

  return null;
}
