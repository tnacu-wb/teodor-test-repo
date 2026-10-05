import { Box } from '@chakra-ui/react';
import { timeTrackerSeconds } from '@whitbread-eos/utils';
import { useEffect, useMemo, useRef } from 'react';

interface Props {
  iframeId: string;
  iframeContent: string;
  onIframeLoad: (time: string) => void;
  providerUrl?: string;
  updateIframeHeight?: boolean;
}

export default function IframeEmbed({
  iframeId,
  iframeContent,
  onIframeLoad,
  providerUrl = '',
  updateIframeHeight = false,
}: Readonly<Props>) {
  const paymentContainerRef = useRef<HTMLIFrameElement>(null);

  const html = useMemo(() => {
    if (iframeContent) {
      return Buffer.from(iframeContent, 'base64');
    }

    return null;
  }, [iframeContent]);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  useEffect(() => {
    if (html && !document.getElementById(iframeId)) {
      const iframe = document.createElement('iframe');
      const timer = timeTrackerSeconds();
      const listen = () => {
        onIframeLoad(timer());
        iframe.removeEventListener('load', listen);
      };
      iframe.addEventListener('load', listen);
      iframe.style.width = '100%';
      iframe.style.minHeight = '58rem';
      iframe.style.height = '100%';
      iframe.style.marginBottom = 'var(--chakra-fontSizes-md)';
      paymentContainerRef?.current?.appendChild(iframe);
      iframe.id = iframeId;
      iframe.allow = `payment ${providerUrl}`;
      iframe.contentWindow?.document.open();
      iframe.contentWindow?.document.write(html.toString());
      iframe.contentWindow?.document.close();

      return () => iframe.removeEventListener('load', listen);
    }
  }, [html, onIframeLoad]);

  return (
    <Box
      w="full"
      h={updateIframeHeight ? 'full' : 'auto'}
      data-testid="paymentContainer"
      ref={paymentContainerRef}
    />
  );
}
