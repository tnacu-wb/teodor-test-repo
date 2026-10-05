import { BB_CO_BROWSE, BB_LIVE_ASSIST } from '@whitbread-eos/api';
import { formatAssetsUrl, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Script from 'next/script';

interface LiveAssistScriptEmbedProps {
  isInnBusinessContext?: boolean;
}

export default function LiveAssistScriptEmbed({
  isInnBusinessContext = false,
}: LiveAssistScriptEmbedProps) {
  return (
    <>
      <Script
        src={
          isInnBusinessContext ? formatIBAssetsUrl(BB_LIVE_ASSIST) : formatAssetsUrl(BB_LIVE_ASSIST)
        }
        strategy="lazyOnload"
        id="liveassist-script"
      ></Script>
      <Script
        src={isInnBusinessContext ? formatIBAssetsUrl(BB_CO_BROWSE) : formatAssetsUrl(BB_CO_BROWSE)}
        strategy="lazyOnload"
        id="cobrowse-script"
      ></Script>
    </>
  );
}
