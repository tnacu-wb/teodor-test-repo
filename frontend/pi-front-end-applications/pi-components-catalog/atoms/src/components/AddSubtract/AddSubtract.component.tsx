import { CSSObject, Flex, FlexProps, Text, TextProps, Input } from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';

import { Path, Rectangle } from '../../assets/icons';
import { formatDataTestId } from '../../utils/formatters';
import Button from '../Button';
import Icon from '../Icon';

interface Props {
  onPlus: () => void;
  onSubtract: () => void;
  isPlusDisable: boolean;
  isSubtractDisable: boolean;
  isPlusHidden: boolean;
  isSubtractHidden: boolean;
  value: number;
  prefixDataTestId?: string;
  label?: string;
  isEditable?: boolean;
  maxLength?: number;
  handleInputChange?: (val: number) => void | React.ChangeEvent<HTMLInputElement>;
}

export default function AddSubtract({
  onSubtract,
  onPlus,
  value,
  label,
  isSubtractDisable,
  prefixDataTestId,
  isPlusDisable,
  isPlusHidden = false,
  isSubtractHidden = false,
  isEditable = false,
  handleInputChange,
  maxLength,
}: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();
  return (
    <Flex {...wrapperContainer}>
      <Flex {...wrapperController}>
        {!isSubtractHidden && (
          <Button
            size="xs"
            variant="circle"
            onClick={onSubtract}
            isDisabled={isSubtractDisable}
            aria-label="Decrease quantity"
            data-testid={formatDataTestId(prefixDataTestId, 'SubtractButton')}
            sx={onActiveIconStyle(isSubtractDisable) as CSSObject | undefined}
          >
            <Icon svg={<Rectangle color={choseIconColor(isSubtractDisable)} />} />
          </Button>
        )}
        {isEditable ? (
          <Input
            value={isNaN(value) ? 0 : value}
            onChange={(e) => {
              handleInputChange?.(parseInt(e?.target?.value));
            }}
            maxLength={maxLength}
            data-testid={formatDataTestId(prefixDataTestId, 'inputValue')}
          />
        ) : (
          <Text
            {...valueStyle}
            {...getTypographyProps(valueLegacyTypography, valueSemanticTypography)}
            data-testid={formatDataTestId(prefixDataTestId, 'Value')}
          >
            {value}
          </Text>
        )}
        {!isPlusHidden && (
          <Button
            size="xs"
            variant="circle"
            onClick={onPlus}
            isDisabled={isPlusDisable}
            aria-label="Increase quantity"
            data-testid={formatDataTestId(prefixDataTestId, 'AddButton')}
            sx={onActiveIconStyle(isPlusDisable) as CSSObject | undefined}
          >
            <Icon svg={<Path color={choseIconColor(isPlusDisable)} />} />
          </Button>
        )}
      </Flex>
      <Text
        {...labelStyle}
        {...getTypographyProps(labelLegacyTypography, labelSemanticTypography)}
        data-testid={formatDataTestId(prefixDataTestId, 'Label')}
      >
        {label}
      </Text>
    </Flex>
  );
}

const onActiveIconStyle = (isDisabled: boolean) =>
  isDisabled ? '' : { '&:active > div > svg': { path: { fill: 'white' } } };

const choseIconColor = (isDisable: boolean) =>
  isDisable ? 'var(--chakra-colors-darkGrey2)' : 'var(--chakra-colors-primary)';

const valueStyle = {
  width: 'var(--chakra-space-3xl)',
  px: '1.063rem',
  color: 'darkGrey1',
  align: 'center',
} as TextProps;

const valueLegacyTypography = {
  fontSize: '2xl',
  fontWeight: 'medium',
  lineHeight: '4',
} as TextProps;

const valueSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const labelStyle = {
  color: 'darkGrey1',
} as TextProps;

const labelLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'medium',
  lineHeight: '3',
} as TextProps;

const labelSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const wrapperContainer = {
  flexDir: 'column',
  alignItems: 'center',
  justifyContent: 'center',
} as FlexProps;

const wrapperController = {
  alignItems: 'center',
  justifyContent: 'center',
} as FlexProps;
