import { Box, BoxProps, Link, useBreakpointValue } from '@chakra-ui/react';
import { formatAssetsUrl } from '@whitbread-eos/utils';

import {
  LogoHub,
  LogoHubSimple,
  LogoPi,
  LogoPiIcon,
  LogoPiSimple,
  LogoZip,
  LogoZipSimple,
} from '../../assets/icons';
import Icon from '../Icon';

export interface LogoProps extends BoxProps {
  href?: string;
  src?: string;
  alt?: string;
  isHeaderLogo?: boolean;
  useNextImage?: boolean;
  variant?:
    | 'pi'
    | 'pid'
    | 'pi-simple'
    | 'pid-simple'
    | 'pi-icon'
    | 'hub'
    | 'hub-simple'
    | 'zip'
    | 'zip-simple';
}

export default function Logo({
  href,
  variant = 'pi',
  src,
  alt,
  isHeaderLogo,
  useNextImage,
  ...otherProps
}: Readonly<LogoProps>) {
  const svgMapper = {
    pi: <LogoPi />,
    pid: <LogoPi />,
    'pi-simple': <LogoPiSimple />,
    'pid-simple': <LogoPiIcon />,
    'pi-icon': <LogoPiIcon />,
    hub: isHeaderLogo ? (
      <LogoHub />
    ) : (
      <Icon
        alt={alt}
        src={src && formatAssetsUrl(src)}
        useNextImage={useNextImage}
        {...iconLogoHubStyle}
      />
    ),
    'hub-simple': <LogoHubSimple />,
    zip: <LogoZip />,
    'zip-simple': <LogoZipSimple />,
  };

  const resolution = useBreakpointValue({
    mobile: 'smallResolution',
    sm: 'bigResolution',
  });

  return (
    <Box
      data-testid={`logo-container-${variant}`}
      transform={
        otherProps.transform
          ? otherProps.transform
          : { mobile: 'scale(0.667)', sm: 'scale(0.833)', lg: 'scale(1)' }
      }
    >
      {href ? (
        <Link data-testid="logo-test-id" href={href} _focus={{ boxShadow: 'none' }}>
          {variant !== 'hub' &&
          variant !== 'hub-simple' &&
          variant !== 'zip' &&
          variant !== 'zip-simple' &&
          resolution !== 'smallResolution' ? (
            <Icon
              alt={alt}
              src={src}
              maxHeight="var(--chakra-space-3xl)"
              maxWidth="9.87rem"
              useNextImage={useNextImage}
            />
          ) : (
            svgMapper[variant]
          )}
        </Link>
      ) : (
        svgMapper[variant]
      )}
    </Box>
  );
}

const iconLogoHubStyle = {
  height: '4rem',
  width: '10.75rem',
};
