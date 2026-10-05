import { Box, List, ListItem, Text } from '@chakra-ui/react';
import { usePromoTranslation } from '@whitbread-eos/utils';
import React from 'react';

type PromoNotesProps = {
  isBackground?: boolean;
  notes: string[];
};

const PromoNotes = ({ isBackground = true, notes }: PromoNotesProps) => {
  const t = usePromoTranslation();

  return (
    <Box {...notesWrapperStyles} {...(!isBackground && { background: 'none' })}>
      <Text {...notesTextStyles}>{t.notesTitle}</Text>
      <List {...notesListWrapper}>
        {notes && notes?.map((item, i) => <ListItem key={i}>{item}</ListItem>)}
      </List>
    </Box>
  );
};

const notesWrapperStyles = {
  width: '100%',
  padding: '1.375rem',
  borderRadius: '0 0 8px 8px',
  background: '#EAF2F3',
  fontSize: '0.875rem',
  color: 'var(--Greys-Greys---Base-Black, #000)',
  fontStyle: 'normal',
  lineHeight: '1.3125rem',
};

const notesTextStyles = {
  fontWeight: '600',
  marginBottom: '0.5rem',
};

const notesListWrapper = {
  styleType: 'disc',
  paddingLeft: 3,
  fontWeight: '400',
  lineHeight: '1.3125rem',
};

export default PromoNotes;
