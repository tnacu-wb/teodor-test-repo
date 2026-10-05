import { Flex, Box, Text, Button, useMultiStyleConfig } from '@chakra-ui/react';
// import { analytics } from '~utils/analytics';
import { formatDataTestId } from '@whitbread-eos/utils';
import { ReactNode, useCallback, useEffect, useState, useId } from 'react';

import type { TabName } from '../Form/formContants';

export interface TabsOptionsItem {
  index?: number;
  tabLabel?: string;
  tapLabel?: string;
  content?: ReactNode;
}

export default function Tabs({
  tabPanelData,
  variant = 'default',
  tabData,
  onClick,
  baseDataTestId,
  selectedTimeSlot,
  name,
  tabLabel,
  tapLabel,
  error,
  getValues,
  setValue,
}: any) {
  const [selectedSession, setSelectedSession] = useState<string>(selectedTimeSlot);
  const [selectedTab, setSelectedTab] = useState<number>(0);
  const tabsId = useId();
  let sessionType: string = tabData[0]?.name;

  const tabStyles = useMultiStyleConfig('Tabs', {});

  useEffect(() => {
    setSelectedTab(0);
  }, []);

  const handleTabsChange = (i: number) => {
    setSelectedTab(i);
    // Update analytics for session type and unavailable slots
    sessionType = tabData[i]?.name;
    // analytics.update({ sessionType: sessionType });
    // handleUnavailableTimes();
  };

  useEffect(() => {
    // analytics.update({ sessionType: sessionType });
    handleUnavailableTimes();
  }, []);

  useEffect(() => {
    handleUnavailableTimes();
  }, [getValues('date')]);

  const handleSession = useCallback(
    (content: any) => {
      setSelectedSession(content.time);
      setValue('menuId', '');
      onClick(content.time);
      // analytics.update({ arrivalTime: content.time });
    },
    [onClick]
  );

  const handleUnavailableTimes = () => {
    setSelectedSession('');
    setValue('time', '');
    // analytics.remove(['arrivalTime']);
    if (sessionType?.toLocaleLowerCase() === 'Breakfast'.toLocaleLowerCase()) {
      sessionType = 'breakFast';
    } else {
      sessionType = sessionType?.toLowerCase();
    }
    // const sessions: { available: boolean; time: string }[] = tabPanelData[sessionType];
    // const unavailableTimes = sessions
    //   ?.map(({ available, time }) => !available && time)
    //   .filter(Boolean);
    // if (unavailableTimes?.length || window?.analyticsData?.unavailableTimes?.length)
    //   analytics.update({
    //     unavailableTimes: unavailableTimes?.join(', '),
    //   });
  };

  const panelKeyMap: Record<TabName, string> = {
    Breakfast: 'breakFast',
    Lunch: 'lunch',
    Dinner: 'dinner',
  };

  const panelKeys = tabData.map((tab: { name: TabName }) => panelKeyMap[tab.name]);

  return (
    <>
      {tabLabel && (
        <Text
          data-testid={formatDataTestId(baseDataTestId, `${name}-session-label`)}
          {...labelStyle()}
          color={error ? 'error' : '#333333'}
        >
          {tabLabel}
        </Text>
      )}
      <Box sx={{ ...tabsStyles() }} __css={tabStyles.root}>
        <Box role="tablist" display="flex" __css={tabStyles.tablist}>
          {tabData?.map((tab: any, i: number) => (
            <Box
              w="full"
              as="button"
              type="button"
              role="tab"
              key={tab.name}
              id={`${tabsId}-tab-${i}`}
              aria-selected={selectedTab === i}
              aria-controls={`${tabsId}-panel-${i}`}
              tabIndex={selectedTab === i ? 0 : -1}
              aria-disabled={!tab.availablility}
              onClick={() => tab.availablility && handleTabsChange(i)}
              __css={tabStyles.tab}
              opacity={!tab.availablility ? 0.4 : 1}
              cursor={!tab.availablility ? 'not-allowed' : 'pointer'}
            >
              <Flex flexDir="column">
                <Text as="h4">{tab.name}</Text>
              </Flex>
            </Box>
          ))}
        </Box>
        <Box marginTop="var(--chakra-space-xl)">
          {tapLabel && tabPanelData && (
            <Text
              data-testid={formatDataTestId(baseDataTestId, `${name}-time-label`)}
              {...labelStyle()}
              color={error ? 'error' : '#333333'}
              _focus={{ color: error ? 'error' : '#511E62' }}
            >
              {tapLabel}
            </Text>
          )}
          <Box>
            {panelKeys.map((key: string, i: number) => {
              if (selectedTab !== i) return null;
              return (
                <Box
                  role="tabpanel"
                  key={key}
                  id={`${tabsId}-panel-${i}`}
                  aria-labelledby={`${tabsId}-tab-${i}`}
                  __css={tabStyles.tabpanel}
                >
                  {renderContent(tabPanelData[key])}
                </Box>
              );
            })}
          </Box>
        </Box>
      </Box>
    </>
  );

  function renderContent(arrayOfSession: any) {
    return (
      <Flex justify="space-between" direction="row" flexWrap="wrap">
        {arrayOfSession.map((content: any) => (
          <Button
            variant={variant}
            onClick={() => {
              handleSession(content);
            }}
            display="flex"
            alignItems="center"
            justifyContent="center"
            bg={content.time == selectedSession ? '#511E62' : 'transparent'}
            border={content.time == selectedSession ? '2px solid #696565' : '1px solid #696565'}
            fontWeight="semibold"
            width={{
              md: '30%',
              mobile: '32%',
            }}
            fontSize="10pt"
            height="50px"
            borderRadius="4px"
            color={content.time == selectedSession ? 'white' : 'black'}
            isDisabled={!content.available}
            marginBottom="12px"
            key={content.time}
            aria-label={content.time}
          >
            {content.time}
          </Button>
        ))}
      </Flex>
    );
  }
}
const tabsStyles = () => ({
  '.chakra-tabs__tablist': {
    w: '100%',
    justifyContent: 'space-between',
    border: 'none',
    flexWrap: 'wrap',
  },
  '.chakra-tabs__tab': {
    mb: '12px',
    fontWeight: 'semibold',
    borderRadius: '4px',
    width: {
      md: '30%',
      mobile: '32%',
    },
    border: '1px solid #696565',
    _selected: {
      bg: '#511E62',
      border: '2px solid #696565',
      color: 'white',
    },
  },

  '.chakra-tabs__tab-panel': {
    padding: '0',
  },

  '.chakra-tabs__tablist button[disabled]': {
    position: 'relative',
  },

  '.chakra-tabs__tablist button[disabled]::before, .chakra-button[disabled]::before': {
    content: '""',
    position: 'absolute',
    top: '50%',
    transform: 'rotate(16deg)',
    width: '100%',
    height: '1px',
    background: 'grey',
  },

  '.chakra-tabs__tablist button[disabled]::after, .chakra-button[disabled]::after': {
    content: '""',
    position: 'absolute',
    top: '50%',
    transform: 'rotate(163deg)',
    width: '100%',
    height: '1px',
    background: 'grey',
  },
});

const labelStyle = () => ({
  w: 'fit-content',
  fontSize: 'md',
  mb: 'var(--chakra-space-2)',
  fontWeight: 'bold',
  zIndex: '1',
});
