const overrides = {
  global: {
    '.mapMarker': {
      background: 'var(--chakra-colors-baseWhite)!important',
      padding: '2px 8px',
      border: '1px solid #cccccc',
      boxShadow: '0 1px 2px rgba(204, 204, 204, 0.72)',
      borderRadius: '10px',
      height: '24px',
      width: 'auto',
      minW: '32px',
      fontSize: '14px',
      fontWeight: '700',
      lineHeight: '20px',
      color: 'var(--chakra-colors-darkGrey1) !important',
      transition: '0.5s',
      transform: 'scale(1)',
      cursor: 'pointer',
    },
    '.hoverMarker': {
      transform: 'scale(1.1)',
    },
    '.activeMarker': {
      background: 'var(--chakra-colors-primary)!important',
      color: 'var(--chakra-colors-baseWhite)!important',
    },
    '.activeHover': {
      background: 'var(--chakra-colors-primary)!important',
      color: 'var(--chakra-colors-baseWhite)!important',
    },
    // Pill-shaped marker redesign — only applied when the split map view feature is enabled
    '.mapMarker.splitViewMarker': {
      padding: '7px 10px',
      border: '3px solid var(--chakra-colors-piMapMarker)',
      boxShadow: '0 2px 5px rgba(0, 0, 0, 0.48)',
      borderRadius: '50px',
      height: 'auto',
      lineHeight: 'normal',
      fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
      textAlign: 'center',
      overflow: 'hidden',
      display: 'inline-block',
      color: 'var(--chakra-colors-baseBlack) !important',
      transition: 'transform 0.3s, background 0.3s, color 0.3s',
    },
    '.mapMarker.splitViewMarker.hubMarker': {
      borderColor: 'var(--chakra-colors-hubPrimary) !important',
      borderWidth: '3px',
    },
    '.mapMarker.splitViewMarker.standardMarker': {
      borderColor: 'var(--chakra-colors-piMapMarker) !important',
      borderWidth: '3px',
    },
    '.mapMarker.splitViewMarker.hubMarker.hoverMarker, .mapMarker.splitViewMarker.hubMarker.activeMarker, .mapMarker.splitViewMarker.hubMarker.activeHover':
      {
        background: 'var(--chakra-colors-hubPrimary) !important',
        borderColor: 'var(--chakra-colors-hubPrimary) !important',
        color: 'var(--chakra-colors-baseWhite) !important',
        transform: 'scale(1.1)',
      },
    '.mapMarker.splitViewMarker.standardMarker.hoverMarker, .mapMarker.splitViewMarker.standardMarker.activeMarker, .mapMarker.splitViewMarker.standardMarker.activeHover':
      {
        background: 'var(--chakra-colors-piMapMarker) !important',
        borderColor: 'var(--chakra-colors-piMapMarker) !important',
        color: 'var(--chakra-colors-baseWhite) !important',
        transform: 'scale(1.1)',
      },
    '.mapMarker.splitViewMarker.zipMarker': {
      borderColor: 'var(--chakra-colors-zipPrimary) !important',
      borderWidth: '3px',
    },
    '.mapMarker.splitViewMarker.zipMarker.hoverMarker, .mapMarker.splitViewMarker.zipMarker.activeMarker, .mapMarker.splitViewMarker.zipMarker.activeHover':
      {
        background: 'var(--chakra-colors-zipPrimary) !important',
        borderColor: 'var(--chakra-colors-zipPrimary) !important',
        color: 'var(--chakra-colors-baseWhite) !important',
        transform: 'scale(1.1)',
      },
    '[data-testid="DropdownComp-Wrapper"]:has([data-testid="DropdownComp-sort-by-menuButton"])': {
      zIndex: 9,
    },
    // Cap the room-picker dropdown height while split map view is active (toggled via the
    // `srp-split-view-active` body class) so it doesn't grow unbounded in the constrained layout.
    '.srp-split-view-active [data-testid="roomPickerMenu"]': {
      maxHeight: '500px !important',
      overflowY: 'scroll',
    },
    '.soldOut': {
      backgroundColor: 'var(--chakra-colors-lightGrey5) !important',
    },
    '.soldOut.splitViewMarker': {
      paddingLeft: '16px !important',
      paddingRight: '16px !important',
    },
    '.locationMarker': {
      position: 'absolute',
      display: 'inline-block',
      transform: 'translate(-50%, -100%)',
      zIndex: 0,
    },
    '.bart': {
      backgroundColor: 'var(--chakra-colors-zipSecondary) !important',
    },
    '.gm-style-iw': {
      boxShadow: 'none',
      borderRadius: '0 !important',
      left: '0 !important',
      overflow: 'inherit !important',
      padding: '0 !important',
      width: 'auto',
      height: 'auto',
      fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
    },

    '.gm-style-iw:after': {
      display: 'none',
    },

    '.gm-style-iw > div > div': {
      overflow: 'inherit !important',
      borderRadius: '0 !important',
      display: 'contents !important',
    },

    '.gm-svpc': {
      top: { mobile: '-330px!important', sm: '72px!important' },
    },

    '.gm-svpc > div': {
      width: '30px',
      height: '30px',
    },

    '.gm-style-iw a': {
      textDecoration: 'none',
    },

    '.gm-style-iw .gm-style-iw-c': {
      padding: '0 !important',
      borderRadius: '0 !important',
      overflow: 'inherit !important',
    },

    '.gm-style .gm-style-iw-c': {
      minWidth: '288px !important',
      backgroundColor: 'transparent!important',
      boxShadow: 'none!important',
    },

    '.gm-style img': {
      maxWidth: '100%!important',
    },

    '.gm-style-iw > div': {
      borderRadius: '0 !important',
    },

    '.gm-ui-hover-effect': {
      position: 'absolute !important',
      top: '-27px !important',
      right: '0 !important',
      backgroundColor: 'var(--chakra-colors-darkGrey1)!important',
      width: '24px !important',
      height: '24px !important',
      borderRadius: '100% !important ',
      opacity: '1 !important',
    },

    '.gm-ui-hover-effect > span': {
      backgroundColor: 'var(--chakra-colors-white)!important',
      width: '15px !important',
      height: '15px !important',
      margin: 'auto !important',
    },

    '.gm-style-iw .img_wrapper': {
      overflow: 'hidden',
      textAlign: 'center',
      margin: '0px auto',
    },

    '.gm-style-iw .img_wrapper > img': {
      height: 'auto',
    },

    '.gm-style-iw .property_content_wrap': {
      padding: '0px 20px',
    },

    '.gm-style-iw .property_title': {
      minHeight: 'auto',
    },

    '.gm-style-iw-tc': {
      display: 'none',
    },

    '.gm-style-iw-d': {
      overflow: 'hidden !important',
      maxHeight: 'auto !important',
    },
    '@media screen and (max-width: 768px)': {
      '.gm-style .gm-style-iw-t': {
        bottom: '-15rem !important',
        position: 'fixed !important',
      },
    },
    '.poi-info-window': {
      padding: '12px',
    },
    '.SDYZEU-keyboard-shortcuts-dialog-view': {
      '.gm-ui-hover-effect': {
        backgroundColor: 'var(--chakra-colors-white)!important',
        position: 'static !important',
        borderRadius: '0 !important ',
        opacity: '.6 !important',
        ':hover': {
          opacity: '1 !important',
        },
        '> span': {
          backgroundColor: 'var(--chakra-colors-black)!important',
          width: '24px !important',
          height: '24px !important',
          color: '#000',
        },
        '.focus-visible': {
          outline: '2px solid #007aff',
          outlineOffset: '2px',
          opacity: '1',
        },
      },
    },
    'gmp-internal-camera-control': {
      display: 'none',
    },
    //restaurant header and footer styles
    '.form-heading-title': {
      textTransform: 'none',
      m: '0',
      my: '40px',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      color: '#61375a',
      textAlign: 'center',
      fontSize: '2rem',
      fontStyle: 'normal',
      fontWeight: '700',
      lineHeight: '40px',
    },
    '.beefeater-form-heading-title': {
      color: '#fff',
    },
    '.tabletable-form-heading-title': {
      color: '#fff',
    },
    '.cookhouseandpub-form-heading-title': {
      color: '#fff',
    },
    '.whitbreadinns-form-heading-title': {
      color: '#fff',
    },
    '.barandblock-form-heading-title': {
      color: '#fff',
    },
    '.brewersfayre-form-heading-title': {
      color: '#fff',
    },
    '.restaurant-premierinn-form-heading-title': {
      color: '#fff',
    },
  },
};

export default overrides;
