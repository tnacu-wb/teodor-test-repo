import { Global } from '@emotion/react';
import getConfig from 'next/config';

type FontsProps = {
  baseUrl?: string;
};

const Fonts = ({ baseUrl }: FontsProps) => {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const premierInnFontPath = `${
    baseUrl ?? publicRuntimeConfig.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC
  }/etc.clientlibs/premierinn/clientlibs/styles/resources/`;
  return (
    <Global
      styles={`
      @import url("https://p.typekit.net/p.css?s=1&k=hta4qem&ht=tk&f=139.173.175.176.5474.5475.25136&a=1511988&app=typekit&e=css");

      @font-face {
      font-family:"Proxima Nova Sans";
      src:url("https://use.typekit.net/af/949f99/00000000000000003b9b3068/27/l?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n7&v=3") format("woff2"),url("https://use.typekit.net/af/949f99/00000000000000003b9b3068/27/d?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n7&v=3") format("woff"),url("https://use.typekit.net/af/949f99/00000000000000003b9b3068/27/a?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n7&v=3") format("opentype");
      font-display:auto;font-style:normal;font-weight:700;font-stretch:normal;
      }

      @font-face {
      font-family:"Proxima Nova Sans";
      src:url("https://use.typekit.net/af/576d53/00000000000000003b9b3066/27/l?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n6&v=3") format("woff2"),url("https://use.typekit.net/af/576d53/00000000000000003b9b3066/27/d?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n6&v=3") format("woff"),url("https://use.typekit.net/af/576d53/00000000000000003b9b3066/27/a?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n6&v=3") format("opentype");
      font-display:auto;font-style:normal;font-weight:600;font-stretch:normal;
      }

      @font-face {
      font-family:"Proxima Nova Sans";
      src:url("https://use.typekit.net/af/705e94/00000000000000003b9b3062/27/l?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n4&v=3") format("woff2"),url("https://use.typekit.net/af/705e94/00000000000000003b9b3062/27/d?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n4&v=3") format("woff"),url("https://use.typekit.net/af/705e94/00000000000000003b9b3062/27/a?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n4&v=3") format("opentype");
      font-display:auto;font-style:normal;font-weight:400;font-stretch:normal;
      }

      @font-face {
      font-family:"Proxima Nova Sans";
      src:url("https://use.typekit.net/af/5c70f2/00000000000000003b9b3063/27/l?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=i4&v=3") format("woff2"),url("https://use.typekit.net/af/5c70f2/00000000000000003b9b3063/27/d?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=i4&v=3") format("woff"),url("https://use.typekit.net/af/5c70f2/00000000000000003b9b3063/27/a?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=i4&v=3") format("opentype");
      font-display:auto;font-style:italic;font-weight:400;font-stretch:normal;
      }

      @font-face {
      font-family:"Proxima Nova Sans";
      src:url("https://use.typekit.net/af/cebe0e/00000000000000003b9b3060/27/l?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n3&v=3") format("woff2"),url("https://use.typekit.net/af/cebe0e/00000000000000003b9b3060/27/d?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n3&v=3") format("woff"),url("https://use.typekit.net/af/cebe0e/00000000000000003b9b3060/27/a?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n3&v=3") format("opentype");
      font-display:auto;font-style:normal;font-weight:300;font-stretch:normal;
      }

      @font-face {
      font-family:"Proxima Nova Sans";
      src:url("https://use.typekit.net/af/40ff7f/00000000000000003b9b3061/27/l?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=i3&v=3") format("woff2"),url("https://use.typekit.net/af/40ff7f/00000000000000003b9b3061/27/d?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=i3&v=3") format("woff"),url("https://use.typekit.net/af/40ff7f/00000000000000003b9b3061/27/a?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=i3&v=3") format("opentype");
      font-display:auto;font-style:italic;font-weight:300;font-stretch:normal;
      }

      @font-face {
      font-family:"Proxima Nova Sans";
      src:url("https://use.typekit.net/af/6e816b/00000000000000003b9b3064/27/l?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n5&v=3") format("woff2"),url("https://use.typekit.net/af/6e816b/00000000000000003b9b3064/27/d?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n5&v=3") format("woff"),url("https://use.typekit.net/af/6e816b/00000000000000003b9b3064/27/a?primer=7cdcb44be4a7db8877ffa5c0007b8dd865b3bbc383831fe2ea177f62257a9191&fvd=n5&v=3") format("opentype");
      font-display:auto;font-style:normal;font-weight:500;font-stretch:normal;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-Heavy.ttf") format("truetype");
        font-weight: 900;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-HeavyItalic.ttf") format("truetype");
        font-weight: 900;font-style: italic;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-Bold.ttf") format("truetype");
        font-weight: 700;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-BoldItalic.ttf") format("truetype");
        font-weight: 700;font-style: italic;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-Regular.ttf") format("truetype");
        font-weight: 400;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-Italic.ttf") format("truetype");
        font-weight: 400;font-style: italic;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-Light.ttf") format("truetype");
        font-weight: 300;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-LightItalic.ttf") format("truetype");
        font-weight: 300;font-style: italic;font-display: auto;
      }
  
      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-Thin.ttf") format("truetype");
        font-weight: 100;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Premierinn-Sans";
        src: url("${premierInnFontPath}PremierInnSans-ThinItalic.ttf") format("truetype");
        font-weight: 100;font-style: italic;font-display: auto;
      }

      @font-face {
        font-family: "Sunset-Sans";
        src: url("${premierInnFontPath}SunsetSans_W_Regular.woff2") format("woff2"),
             url("${premierInnFontPath}SunsetSans_W_Regular.woff") format("woff");
        font-weight: 400;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Sunset-Sans";
        src: url("${premierInnFontPath}SunsetSans_W_Medium.woff2") format("woff2"),
             url("${premierInnFontPath}SunsetSans_W_Medium.woff") format("woff");
        font-weight: 500;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Sunset-Sans";
        src: url("${premierInnFontPath}SunsetSans_W_SemiBold.woff2") format("woff2"),
             url("${premierInnFontPath}SunsetSans_W_SemiBold.woff") format("woff");
        font-weight: 600;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Sunset-Sans";
        src: url("${premierInnFontPath}SunsetSans_W_Bold.woff2") format("woff2"),
             url("${premierInnFontPath}SunsetSans_W_Bold.woff") format("woff");
        font-weight: 700;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Sunset-Sans";
        src: url("${premierInnFontPath}SunsetSans_W_XBold.woff2") format("woff2"),
             url("${premierInnFontPath}SunsetSans_W_XBold.woff") format("woff");
        font-weight: 800;font-style: normal;font-display: auto;
      }

      @font-face {
        font-family: "Sunset-Sans";
        src: url("${premierInnFontPath}SunsetSans_W_Black.woff2") format("woff2"),
             url("${premierInnFontPath}SunsetSans_W_Black.woff") format("woff");
        font-weight: 900;font-style: normal;font-display: auto;
      }
      `}
    />
  );
};

export default Fonts;
