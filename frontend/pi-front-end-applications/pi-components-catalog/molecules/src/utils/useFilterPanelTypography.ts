import { ButtonProps, TextProps } from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';

interface FilterPanelTypography {
  headingTypography: Partial<TextProps>;
  labelTypography: Partial<TextProps>;
  mergedButtonProps: ButtonProps;
}

export function useFilterPanelTypography(buttonProps?: ButtonProps): FilterPanelTypography {
  const getTypographyProps = useSemanticTypography();

  const headingTypography = getTypographyProps(
    { fontSize: 'md', fontWeight: 'bold', lineHeight: '3' },
    { textStyle: 'body-m-emphasis' }
  );
  const secondaryTypography = getTypographyProps({}, { textStyle: 'body-m-regular' });
  const mergedButtonProps = { ...secondaryTypography, ...buttonProps } as ButtonProps;

  return { headingTypography, labelTypography: secondaryTypography, mergedButtonProps };
}
