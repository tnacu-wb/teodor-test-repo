import { Box, Link } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { BOOKING_CHANNEL, BOOKING_SUBCHANNEL, type Claims, CREATE_MEMO } from '@whitbread-eos/api';
import { Card, Textarea } from '@whitbread-eos/atoms';
import { useCustomLocale, useMutationRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { ChangeEvent, useState } from 'react';

interface Props {
  basketReference: string;
  onSave: () => void;
  user: Claims | undefined;
}

export default function AddMemo({ basketReference, onSave, user }: Readonly<Props>) {
  const queryClient = useQueryClient();
  const [description, setDescription] = useState('');

  const { t } = useTranslation();
  const { language } = useCustomLocale();

  const handleOnChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
    const textAreaValue = event.target.value;
    setDescription(textAreaValue);
  };

  const { mutation, isLoading } = useMutationRequest(CREATE_MEMO);

  const onSubmit = () => {
    if (!isLoading && description.trim().length) {
      const createdByLabel = t('ccui.agentMemo.createdBy', { name: user?.name });
      const concatenatedDescription = `${createdByLabel}: ${description}`;
      const createMemoCriteria = {
        basketReference,
        description: concatenatedDescription,
        channel: BOOKING_CHANNEL.CCUI,
        subchannel: BOOKING_SUBCHANNEL.WEB,
        language,
      };
      mutation.mutate(createMemoCriteria, {
        onSuccess: (data: any) => {
          queryClient.setQueriesData({ queryKey: ['GetAgentMemos'] }, data);
          onSave();
        },
      });
    }
  };

  return (
    <Box data-testid="AddMemo-Container">
      <Card display="grid" {...cardStyle}>
        <Textarea
          data-testid="AddMemo-Description"
          placeholder={t('ccui.agentMemo.enterTextHere')}
          _placeholder={{ color: 'darkGrey1' }}
          resize="none"
          value={description}
          onChange={handleOnChange}
          isDisabled={isLoading}
          {...textareaStyle}
        ></Textarea>
        <Box {...linkWrapperStyle} data-testid="AddMemo-SaveAction">
          <Link {...linkStyle} onClick={onSubmit}>
            {t('ccui.agentMemo.save')}
          </Link>
        </Box>
      </Card>
    </Box>
  );
}

const textareaStyle = {
  variant: 'unstyled',
  color: 'darkGrey1',
  size: 'md',
};

const cardStyle = {
  border: '1px solid var(--chakra-colors-tertiary)',
  color: 'baseBlack',
};

const linkWrapperStyle = {
  display: 'grid',
  justifyContent: 'flex-end',
  marginTop: 'sm',
};

const linkStyle = {
  textDecoration: 'underline',
  color: 'tertiary',
  _focus: {
    outline: 'none',
  },
  fontSize: 'md',
  fontWeight: 'semibold',
};
