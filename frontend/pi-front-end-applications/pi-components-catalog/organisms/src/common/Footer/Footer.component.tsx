import {
  Box,
  Flex,
  FlexProps,
  Grid,
  GridProps,
  Hide,
  Link,
  Show,
  SimpleGrid,
  Text,
} from '@chakra-ui/react';
import type { FooterColumn, FooterIcon, FooterTab } from '@whitbread-eos/api';
import type { AccordionItemProp } from '@whitbread-eos/atoms';
import { Accordion, Icon, Tabs } from '@whitbread-eos/atoms';
import { formatAssetsUrl, formatDataTestId, renderSanitizedHtml } from '@whitbread-eos/utils';

export interface Props {
  copyrightData: string;
  socialIcons: FooterIcon[];
  tabsInfo: FooterTab[];
  isPI: boolean;
  language: string;
  baseDataTestId?: string;
}

export default function FooterComponent({
  copyrightData,
  socialIcons,
  tabsInfo,
  isPI,
  language,
  baseDataTestId,
}: Readonly<Props>) {
  const footerWrapperStyles = () => {
    return {
      w: 'full',
      pt:
        language === 'en'
          ? { mobile: '0', md: 'lg', lg: isPI ? '3xl' : 'lg', xl: '3xl' }
          : { mobile: '0', md: '2xl', lg: isPI ? '3xl' : 'lg', xl: '3xl' },
      px: '0',
      pb: isPI ? '0' : { lg: 'lg', xl: '3xl' },
    };
  };

  const acordionStyles = () => ({
    '.chakra-accordion__item': { padding: 'md' },
  });

  const tabsStyles = () => ({
    '.chakra-tabs__tablist': { display: isPI ? 'flex' : 'none' },
    '.chakra-tabs__tab-panel': { padding: 0 },
    '.chakra-tabs__tab':
      tabsInfo?.length === 1
        ? { display: 'none' }
        : {
            color: 'darkGrey1',
            borderBottom: '2px solid var(--chakra-colors-lightGrey4)',
          },
  });

  const columnWrapperStyles = () => {
    return isPI
      ? {
          marginTop: language === 'en' ? { mobile: 'md', md: 'lg' } : '0',
        }
      : { marginTop: { mobile: '2xl', md: 'lg', lg: '0' } };
  };

  const renderColumnItems = (column: FooterColumn) => {
    return column.linkItems.map((item: any, index: number) => {
      return (
        <Link
          {...linkItemStyles}
          mt={index !== 0 ? { mobile: 'sm', md: 'xs' } : '0'}
          key={item.name}
          href={item.linkSrc}
          isExternal={isPI ? item.openInNewTab : true}
          data-testid={formatDataTestId(baseDataTestId, 'LinkItem')}
        >
          {item.name}
        </Link>
      );
    });
  };
  const renderGermanTabContent = (tab: FooterTab) => {
    const showSectionTitle = tab.columns.filter((column) => column.name.length > 0).length === 0;
    return (
      <Box w="full">
        {!showSectionTitle ? (
          // section with multiple titles in german
          <Box data-testid={formatDataTestId(baseDataTestId, 'MultipleTitlesTabContent')}>
            <Grid {...gridWrapperStyles}>
              {tab.columns.map((column) => {
                return (
                  <Box {...columnWrapperStyles()} mb={isPI ? '5xl' : '0'} key={column.name}>
                    {column.name && <Text {...columnTitleStyles}>{column.name}</Text>}
                    <Flex {...columnInnerWrapperStyles}>{renderColumnItems(column)}</Flex>
                  </Box>
                );
              })}
            </Grid>
          </Box>
        ) : (
          //  section with one title for footer in german
          <Box
            mb={{ md: '2xl', lg: '6xl' }}
            data-testid={formatDataTestId(baseDataTestId, 'SingleTitleTabContent')}
          >
            <Text {...columnTitleStyles}>{tab.name}</Text>
            <SimpleGrid columns={{ md: 3, lg: 6 }} spacing="lg">
              {tab.columns.map((column) => {
                return (
                  <Flex {...columnInnerWrapperStyles} key={column.name}>
                    {renderColumnItems(column)}
                  </Flex>
                );
              })}
            </SimpleGrid>
          </Box>
        )}
      </Box>
    );
  };

  const renderTabContent = (tab: FooterTab) => {
    return (
      <Box
        w="full"
        mb={{ md: 'md', lg: isPI ? '3xl' : '0' }}
        data-testid={formatDataTestId(baseDataTestId, 'TabContent')}
      >
        {isPI && tab.intro?.description && (
          <Box {...tabDescriptionStyles} className="formatLinks">
            {renderSanitizedHtml(tab.intro.description)}
          </Box>
        )}
        <Grid w="full" {...gridWrapperStyles}>
          {tab.columns.length > 0 &&
            tab.columns.map((column) => (
              <Box
                {...columnWrapperStyles()}
                key={column.name || column.linkItems?.[0]?.name || 'no-name'}
              >
                <Text {...columnTitleStyles}>{column.name}</Text>
                {column.linkItems.length > 0 && (
                  <Flex {...columnInnerWrapperStyles}>{renderColumnItems(column)}</Flex>
                )}
              </Box>
            ))}
        </Grid>
      </Box>
    );
  };

  const renderCopyrightAndSocialSection = () => {
    return (
      <Flex
        data-testid={formatDataTestId(baseDataTestId, 'SocialSection')}
        {...socialSectionStyles}
      >
        {copyrightData && (
          <Box {...copyrightStyles} className="formatLinks">
            {renderSanitizedHtml(copyrightData)}
          </Box>
        )}

        {socialIcons.length > 0 && (
          <Flex flexDir="row">
            {socialIcons.map((icon) => {
              return (
                <Link pl="md" key={icon.iconSrc} href={icon.linkSrc} isExternal>
                  <Icon src={formatAssetsUrl(icon.iconSrc)} />
                </Link>
              );
            })}
          </Flex>
        )}
      </Flex>
    );
  };

  function getAcordionItems() {
    let formattedItems: AccordionItemProp[] = [];
    if (language === 'en') {
      formattedItems = tabsInfo?.map((tab: FooterTab) => ({
        title: tab.name,
        content: renderTabContent(tab),
      }));
    } else {
      tabsInfo.forEach((tab: FooterTab) => {
        if (tab.columns.filter((column) => column.name.length > 0).length === 0) {
          formattedItems = [
            ...formattedItems,
            {
              title: tab.name,
              content: tab.columns.map((column, index) => (
                <Flex
                  direction="column"
                  pt={{ mobile: index === 0 ? '0' : 'md', lg: 'sm' }}
                  key={column.name}
                  data-testid={formatDataTestId(baseDataTestId, 'AcordionItem')}
                >
                  {renderColumnItems(column)}
                </Flex>
              )),
            },
          ];
        } else {
          tab.columns.forEach((column) => {
            formattedItems.push({
              title: column.name,
              content: (
                <Flex
                  direction="column"
                  pt={{ mobile: '0', lg: 'sm' }}
                  key={column.name}
                  data-testid={formatDataTestId(baseDataTestId, 'AcordionItem')}
                >
                  {renderColumnItems(column)}
                </Flex>
              ),
            });
          });
        }
      });
    }
    return formattedItems;
  }

  return (
    <Box
      position="relative"
      _before={{
        content: '""',
        position: 'absolute',
        top: 0,
        left: '50%',
        transform: 'translateX(-50%)',
        width: '100vw',
        height: '1px',
        bg: 'var(--chakra-colors-lightGrey1)',
      }}
      {...footerWrapperStyles()}
      data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
    >
      <Show above="md">
        {language === 'de' ? (
          tabsInfo.map((tab) => <Box key={tab.name}>{renderGermanTabContent(tab)}</Box>)
        ) : (
          <Tabs
            sx={{ ...tabsStyles() }}
            styles={{
              tab: { width: { base: 'full', md: `${100 / tabsInfo.length}%` } },
              tabList: {},
              tabPanel: { padding: '0' },
            }}
            variant="sm"
            options={tabsInfo.map((tab, index) => ({
              index,
              label: tab.name,
              content: renderTabContent(tab),
            }))}
          />
        )}
      </Show>
      <Hide above="md">
        <Accordion
          sx={{ ...acordionStyles() }}
          accordionItems={getAcordionItems()}
          bgColor="baseWhite"
        />
      </Hide>
      {isPI && renderCopyrightAndSocialSection()}
    </Box>
  );
}

const copyrightStyles = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: 1,
  color: 'darkGrey1',
  ml: 0,
};

const socialSectionStyles = {
  justifyContent: 'space-between',
  alignItems: 'center',
  w: 'full',
  p: { mobile: 'md', md: 'lg' },
  borderTop: '1px solid var(--chakra-colors-lightGrey1)',
};

const tabDescriptionStyles = {
  pt: 'lg',
  pb: { mobile: 'sm', lg: 0 },
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: 2,
  color: 'darkGrey1',
  pr: { mobile: 0, sm: '2xl', md: 0 },
};

const columnTitleStyles = {
  fontSize: 'md',
  fontWeight: 'semibold',
  lineHeight: 3,
  color: 'darkGrey1',
};

const columnInnerWrapperStyles = {
  direction: 'column',
  pt: { mobile: 'md', md: 'sm' },
} as FlexProps;

const linkItemStyles = {
  maxW: '11rem',
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: 2,
  color: 'darkGrey1',
};

const gridWrapperStyles = {
  justifyContent: 'space-between',
  gridTemplateColumns: {
    mobile: '6fr',
    md: 'repeat(3, 2fr)',
    lg: 'repeat(6, 1fr)',
  },
} as GridProps;
