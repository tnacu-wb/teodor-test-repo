import { Box } from '@chakra-ui/react';
import { SearchBrandType } from '@whitbread-eos/api';
import { CSSProperties } from 'react';

import { LogoHubSimple, LogoZipSimple, PremierInnLogo } from '../assets/icons';
import Icon from '../components/Icon';

export function getLogoByBrand(brand: SearchBrandType, code: string) {
  const logoStyle = {
    position: 'relative',
    transform: 'scale(0.45)',
  } as CSSProperties;

  const iconStyle = {
    width: 'var(--chakra-space-lg)',
    height: 'var(--chakra-space-lg)',
    marginRight: 'var(--chakra-space-md)',
  };

  if (brand === 'PI' || brand === 'PID') {
    return (
      <Box mr="md" key={code} style={{ position: 'relative', top: '-0.125rem' }}>
        <PremierInnLogo />
      </Box>
    );
  }
  if (brand === 'HUB') {
    return (
      <Icon
        key={code}
        style={iconStyle}
        svg={<LogoHubSimple style={{ ...logoStyle, top: '-1.25rem', left: '-1.125rem' }} />}
      />
    );
  }
  if (brand === 'ZIP') {
    return (
      <Icon
        key={code}
        style={iconStyle}
        svg={<LogoZipSimple style={{ ...logoStyle, top: '-0.375rem', left: '-1.5rem' }} />}
      />
    );
  }
}
