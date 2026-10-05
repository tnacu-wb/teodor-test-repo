import {
  Box,
  ButtonProps,
  Drawer,
  DrawerBody,
  DrawerCloseButton,
  DrawerContent,
  DrawerHeader,
  DrawerOverlay,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
  ModalOverlay,
} from '@chakra-ui/react';
import { MemoModalVariants, type MemosResponse } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms';
import { AddMemo, AgentMemoCard } from '@whitbread-eos/molecules';
import { formatDataTestId, useAgentMemo } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useState } from 'react';

export default function AgentMemo() {
  const { t } = useTranslation();
  const {
    isAgentMemoOpen,
    closeAgentMemo,
    agentMemoState: { reservationId, variant },
    agentMemoData,
    agentMemoCount,
    user,
  } = useAgentMemo();
  const isPageVariant = variant === MemoModalVariants.PAGE;
  const isCardVariant = variant === MemoModalVariants.CARD;
  const baseDataTestId = 'AgentMemo';
  const [showAddMemo, setShowAddMemo] = useState(false);

  const handleCloseAgentMemo = () => {
    setShowAddMemo(false);
    closeAgentMemo();
  };

  const addMemo = (extraStyles?: any) => {
    return showAddMemo ? (
      <AddMemo basketReference={reservationId} user={user} onSave={() => setShowAddMemo(false)} />
    ) : (
      <Button
        size="full"
        variant="tertiary"
        onClick={() => setShowAddMemo(true)}
        data-testid={formatDataTestId(baseDataTestId, 'AddMemoButton')}
        {...(extraStyles || {})}
      >
        {t('ccui.agentMemo.addMemo')}
      </Button>
    );
  };

  const content = (data: MemosResponse) => {
    if (data?.memos) {
      return (
        <Box {...contentStyle} data-testid={formatDataTestId(baseDataTestId, 'Content')}>
          {data.memos.map((memo) => (
            <AgentMemoCard memo={memo} key={`AgentMemoCard-${memo.ids[0].memoIds.join('')}`} />
          ))}
        </Box>
      );
    }
    return null;
  };

  return (
    <>
      {isPageVariant && (
        <Drawer
          isOpen={isAgentMemoOpen}
          placement="right"
          size="md"
          autoFocus={false}
          preserveScrollBarGap={true}
          returnFocusOnClose={false}
          trapFocus={false}
          onClose={handleCloseAgentMemo}
        >
          <DrawerOverlay />
          <Box
            data-testid={formatDataTestId(baseDataTestId, 'PageVariant')}
            {...dialogContentStyle}
          >
            <DrawerContent {...drawerContentStyle}>
              <DrawerCloseButton />
              <DrawerHeader>
                {agentMemoCount
                  ? t('ccui.agentMemo.agentMemoCount', { count: agentMemoCount })
                  : t('ccui.agentMemo.agentMemo')}
              </DrawerHeader>

              <DrawerBody>
                {addMemo()}
                {content(agentMemoData as MemosResponse)}
              </DrawerBody>
            </DrawerContent>
          </Box>
        </Drawer>
      )}
      {isCardVariant && (
        <Modal
          preserveScrollBarGap={true}
          isCentered={false}
          scrollBehavior="inside"
          isOpen={isAgentMemoOpen}
          onClose={handleCloseAgentMemo}
          size="md"
        >
          <ModalOverlay />
          <ModalContent
            data-testid={formatDataTestId(baseDataTestId, 'CardVariant')}
            {...modalContentStyle}
          >
            <ModalHeader
              {...modalHeaderStyle}
              data-testid={formatDataTestId(baseDataTestId, 'CardVariant-ModalHeader')}
            >
              {agentMemoCount
                ? t('ccui.agentMemo.agentMemoCount', { count: agentMemoCount })
                : t('ccui.agentMemo.agentMemo')}
              <ModalCloseButton
                data-testid={formatDataTestId(baseDataTestId, 'CardVariant-ModalCloseButton')}
                {...modalCloseButtonStyle}
              ></ModalCloseButton>
            </ModalHeader>
            <ModalBody
              data-testid={formatDataTestId(baseDataTestId, 'CardVariant-ModalBody')}
              {...modalBodyStyle}
            >
              <Box overflow="auto" maxH="calc(100vh - 4rem)">
                {addMemo(addMemoButtonStyle)}
              </Box>
              {content(agentMemoData as MemosResponse)}
            </ModalBody>
          </ModalContent>
        </Modal>
      )}
    </>
  );
}

const dialogContentStyle = {
  position: 'absolute',
  top: '72px',
} as const;

const drawerContentStyle = {
  pb: 0,
};

const modalBodyStyle = {
  overflow: 'auto',
  my: 'var(--chakra-space-3xl)',
  p: 0,
};

const modalContentStyle = {
  position: 'absolute',
  top: '46px',
  maxW: 'auto',
  maxH: '100vh',
  overflow: 'auto',
  width: { sm: 'var(--chakra-space-breakpoint-sm)' },
  height: { sm: 'auto' },
  borderRadius: { sm: 0 },
  boxShadow: '0 0.3rem 0.3rem 0 var(--chakra-colors-darkGrey2)',
  my: 0,
  p: 'var(--chakra-space-lg)',
  pb: 0,
} as const;

const modalHeaderStyle = {
  minH: 'var(--chakra-space-xxl)',
  display: 'grid',
  gridTemplateColumns: '1fr auto',
  alignItems: 'center',
  p: 0,
  fontWeight: 'var(--chakra-fontWeights-bold)',
};

const addMemoButtonStyle = {
  my: 0,
};

const modalCloseButtonStyle = {
  h: 'var(--chakra-space-xl)',
  w: 'var(--chakra-space-xl)',
  position: 'static',
  alignSelf: 'flex-end',
  fontSize: 'xs',
  lineHeight: '3',
  fontWeight: 'var(--chakra-fontWeights-normal)',
  color: 'var(--chakra-colors-darkGrey1)',
  _focus: {
    boxShadow: 'none',
  },
  _hover: {
    bgColor: 'transparent',
  },
  _active: {
    bgColor: 'transparent',
  },
} as ButtonProps;

const contentStyle = {
  marginTop: '3xl',
};
