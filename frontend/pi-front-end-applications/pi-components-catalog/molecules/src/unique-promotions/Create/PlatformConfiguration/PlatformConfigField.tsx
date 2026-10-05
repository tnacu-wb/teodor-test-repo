import { Checkbox as ChakraCheckbox, Box, Flex, Tag, Text } from '@chakra-ui/react';
import { usePromoTranslation } from '@whitbread-eos/utils';

import { platformConfigStyles } from '../styles';
import { PlatformConfigFieldProps } from '../types';
import {
  COUNTRY_ROWS,
  PLATFORM_ROWS,
  toggleCountryEnabled,
  togglePlatformEnabled,
  toggleSelection,
} from './common';

const PlatformConfigField = ({
  value,
  onChange,
  onBlur,
  label,
  description,
  testId,
}: PlatformConfigFieldProps) => {
  const t = usePromoTranslation();

  return (
    <Box sx={platformConfigStyles.wrapperStyles} data-testid={testId}>
      <Text sx={platformConfigStyles.titleStyles}>{label}</Text>
      <Text sx={platformConfigStyles.descriptionStyles}>{description}</Text>

      {COUNTRY_ROWS.map(({ key: countryKey, label: countryLabel }) => {
        const countryConfig = value[countryKey];

        return (
          <Box key={countryKey} sx={platformConfigStyles.countryCardStyles}>
            <Flex sx={platformConfigStyles.countryHeaderStyles}>
              <ChakraCheckbox
                isChecked={countryConfig.enabled}
                onChange={(e) => {
                  onChange(toggleCountryEnabled(value, countryKey, e.target.checked));
                  onBlur?.();
                }}
              >
                <Text sx={platformConfigStyles.countryLabelStyles}>{countryLabel}</Text>
              </ChakraCheckbox>
            </Flex>

            {countryConfig.enabled && (
              <Box sx={platformConfigStyles.platformTableStyles}>
                <Flex sx={platformConfigStyles.platformHeaderRowStyles}>
                  <Text sx={platformConfigStyles.platformHeaderTextStyles}>{t?.platformLabel}</Text>
                  <Text sx={platformConfigStyles.platformHeaderTextStyles}>{t?.selectedLabel}</Text>
                </Flex>

                {PLATFORM_ROWS.map(({ key: platformKey, label: platformLabel, options }) => {
                  const platform = countryConfig.platforms[platformKey];

                  return (
                    <Flex key={platformKey} sx={platformConfigStyles.platformRowStyles}>
                      <ChakraCheckbox
                        isChecked={platform.enabled}
                        onChange={(e) => {
                          onChange(
                            togglePlatformEnabled(value, countryKey, platformKey, e.target.checked)
                          );
                          onBlur?.();
                        }}
                      >
                        {platformLabel}
                      </ChakraCheckbox>

                      <Flex sx={platformConfigStyles.platformOptionsStyles}>
                        {options.map((option) => (
                          <Tag
                            key={option}
                            as="button"
                            type="button"
                            disabled={!platform.enabled || platformKey === 'CCUI'}
                            onClick={() => {
                              if (!platform.enabled) return;
                              onChange(toggleSelection(value, countryKey, platformKey, option));
                              onBlur?.();
                            }}
                            sx={
                              platform.selected[option]
                                ? platformConfigStyles.optionSelectedStyles
                                : platformConfigStyles.optionUnselectedStyles
                            }
                          >
                            {option === 'web' ? 'Web' : 'App'}
                          </Tag>
                        ))}
                      </Flex>
                    </Flex>
                  );
                })}
              </Box>
            )}
          </Box>
        );
      })}
    </Box>
  );
};

export default PlatformConfigField;
