import type { BoxProps, FlexProps, LinkProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Image, Link, Text } from '@chakra-ui/react';
import type { PrivacyPolicy, SecurityInfoItem } from '@whitbread-eos/api';
import { ChevronDown24, ChevronUp24, Icon, Security16 } from '@whitbread-eos/atoms';
import { formatDataTestId, renderSanitizedHtml, useSemanticTypography } from '@whitbread-eos/utils';
import { useState } from 'react';

interface Props {
  privacyPolicy: PrivacyPolicy;
  prefixDataTestId?: string;
  containerStyle?: BoxProps;
}

export default function DataSecuritySection({
  privacyPolicy,
  prefixDataTestId,
  containerStyle,
}: Readonly<Props>) {
  const [isOpen, setIsOpen] = useState(false);
  const toggleSection = () => setIsOpen(!isOpen);
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'PrivacyPolicy');
  const getTypographyProps = useSemanticTypography();

  return (
    <Flex
      data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
      {...{ ...currentContainerStyle, ...containerStyle }}
    >
      <Box {...iconWrapperStyle}>
        <Icon svg={<Security16 />} />
      </Box>
      <Box>
        <Flex>
          <Text
            {...headingLayoutStyle}
            {...getTypographyProps(headingLegacyTypography, headingSemanticTypography)}
            data-testid={formatDataTestId(baseDataTestId, 'Main-Title')}
          >
            {privacyPolicy.name}
          </Text>
        </Flex>
        <Flex
          direction="column"
          {...mainTextLayoutStyle}
          {...getTypographyProps(mainTextLegacyTypography, mainTextSemanticTypography)}
        >
          <Box
            data-testid={formatDataTestId(baseDataTestId, 'Main-Description')}
            className="formatLinks"
          >
            {renderSanitizedHtml(privacyPolicy?.description ?? '')}
          </Box>
          <Link
            href={privacyPolicy.linkSrc}
            isExternal
            {...privacyNoticeText}
            data-testid={formatDataTestId(baseDataTestId, 'PrivacyNotice')}
          >
            <Text
              as="span"
              {...getTypographyProps(
                privacyNoticeLegacyTypography,
                privacyNoticeSemanticTypography
              )}
            >
              {privacyPolicy.linkLabel}
            </Text>
          </Link>
        </Flex>
        <Flex
          as="button"
          type="button"
          onClick={toggleSection}
          aria-expanded={isOpen}
          {...expandableWrapperStyle(isOpen)}
          data-testid={formatDataTestId(baseDataTestId, 'ExpandButton')}
        >
          <Text
            {...labelHeadingLayoutStyle}
            {...getTypographyProps(labelHeadingLegacyTypography, labelHeadingSemanticTypography)}
          >
            {privacyPolicy.moreInfoLabel}
          </Text>
          {isOpen ? <ChevronUp24 /> : <ChevronDown24 />}
        </Flex>
        {isOpen && (
          <Flex
            direction="column"
            data-testid={formatDataTestId(baseDataTestId, 'Expanded-Wrapper')}
          >
            {privacyPolicy?.moreInfo!.map((item: SecurityInfoItem) => (
              <Box
                key={item.description}
                data-testid={formatDataTestId(baseDataTestId, 'Expanded-Item-Wrapper')}
              >
                {item.image && <Image src={item.image} py="md" alt="" />}
                {item.description && (
                  <Box key={item.description} {...textStyle} className="formatLinks">
                    {renderSanitizedHtml(item.description)}
                  </Box>
                )}
              </Box>
            ))}
          </Flex>
        )}
      </Box>
    </Flex>
  );
}

const mainTextLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const mainTextLegacyTypography = {
  lineHeight: '2',
  fontSize: 'sm',
  fontWeight: 'normal',
} as TextProps;

const mainTextSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const headingLayoutStyle = {
  as: 'h3',
  color: 'darkGrey1',
} as TextProps;

const headingLegacyTypography = {
  fontSize: 'sm',
  fontWeight: 'semibold',
  lineHeight: '2',
} as TextProps;

const headingSemanticTypography = {
  textStyle: 'body-s-emphasis',
} as TextProps;

const privacyNoticeText = {
  textDecoration: 'underline',
  cursor: 'pointer',
  color: 'btnSecondaryEnabled',
} as LinkProps;

const privacyNoticeLegacyTypography = {} as TextProps;

const privacyNoticeSemanticTypography = {
  textStyle: 'link-s-regular',
} as TextProps;

const iconWrapperStyle = {
  py: '0',
  paddingRight: 'sm',
};

const currentContainerStyle = {
  mt: { mobile: 'xl', md: '3xl' },
  padding: 'md',
  border: '1px solid var(--chakra-colors-lightGrey2)',
  borderRadius: 'var(--chakra-space-xs)',
  direction: 'row',
} as FlexProps;

const labelHeadingLayoutStyle = {
  as: 'h3',
} as TextProps;

const labelHeadingLegacyTypography = {
  fontSize: 'sm',
  fontWeight: 'semibold',
  lineHeight: '2',
} as TextProps;

const labelHeadingSemanticTypography = {
  textStyle: 'label-m',
} as TextProps;

const expandableWrapperStyle = (isOpen: boolean) => {
  return {
    display: 'inline-flex',
    alignItems: 'center',
    mt: 'sm',
    cursor: 'pointer',
    direction: 'row',
    pb: isOpen ? 'md' : '0',
    background: 'none',
    border: 'none',
    padding: '0',
  } as FlexProps;
};

const textStyle = {
  lineHeight: '2',
  fontSize: 'sm',
  fontWeight: 'normal',
  pb: 'md',
} as TextProps;
