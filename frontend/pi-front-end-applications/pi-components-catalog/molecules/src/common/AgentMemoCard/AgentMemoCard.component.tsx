import { Box, Divider, Text } from '@chakra-ui/react';
import { Memo, MEMO_TYPE } from '@whitbread-eos/api';
import { Card, CollapseExpandText } from '@whitbread-eos/atoms';
import { useCustomLocale, formatDate } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React from 'react';

interface Props {
  memo: Memo;
}
export default function AgentMemoCard({
  memo: { modifiedOn, description, memoType },
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { language } = useCustomLocale();

  const updateDescriptionByMemoType = (memoType: string, description: string) => {
    let userName = '';
    // TODO: Replace with AEM labels when available: ccui.agentMemo.operaUser and ccui.agentMemo.system
    if (memoType === MEMO_TYPE.OPERA) {
      userName = language === 'en' ? 'Opera user' : 'Opera-Benutzer';
    } else if (memoType === MEMO_TYPE.SYSTEM) {
      userName = 'System';
    }

    if (userName.length) {
      return `${t('ccui.agentMemo.createdBy', { name: userName })}: ${description}`;
    }
    return description;
  };

  return (
    <Box data-testid="AgentMemoCard-Container">
      <Card display="grid" {...cardStyle(memoType)}>
        <Box data-testid="AgentMemoCard-Header" {...headerStyle}>
          <Text align="right" data-testid="AgentMemoCard-Date" {...createdOnStyle}>
            {formatDate(modifiedOn, 'dd/MM/yyyy - HH:mm', language)}
          </Text>
        </Box>
        <Divider {...deviderStyle} />
        <CollapseExpandText
          startingHeight={85}
          noOfLines={3}
          baseTestId="AgentMemoCard"
          expandButtonText={t('ccui.agentMemo.seeMore')}
          collapseButtonText={t('ccui.agentMemo.seeLess')}
          contentText={updateDescriptionByMemoType(memoType, description)}
        />
        <Divider {...deviderStyle} />
      </Card>
    </Box>
  );
}

const cardStyle = (memoType: string) => {
  return {
    backgroundColor:
      memoType === MEMO_TYPE.SYSTEM
        ? 'var(--chakra-colors-tooltipError)'
        : 'var(--chakra-colors-tooltipInfo)',
    border: '1px solid var(--chakra-colors-lightGrey2)',
    color: 'var(--chakra-colors-darkGrey1)',
    marginBottom: 'var(--chakra-space-xl)',
    padding: 'var(--chakra-space-xl)',
  };
};

const headerStyle = {
  marginBottom: 'var(--chakra-space-lg)',
};

const createdOnStyle = {
  fontSize: 'md',
  fontWeight: 'normal',
};

const deviderStyle = {
  color: 'var(--chakra-colors-lightGrey2)',
};
