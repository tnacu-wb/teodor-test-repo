import { Box, StyleProps } from '@chakra-ui/react';
import { type Announcement, Channel } from '@whitbread-eos/api';
import { Alert, Info, Notification } from '@whitbread-eos/atoms';
import { isStringValid, useSemanticTypography } from '@whitbread-eos/utils';
import { ReactElement } from 'react';

export const INFO_NOTIFICATION_TYPE = 'info';
export const WARNING_NOTIFICATION_TYPE = 'warning';

interface Props {
  announcement: Announcement | undefined;
  styles?: StyleProps;
  channel?: string;
  arrivalDate?: string;
  departureDate?: string;
}

export const AnnouncementNotification = ({
  announcement,
  styles,
  channel,
  arrivalDate = '',
  departureDate = '',
}: Readonly<Props>) => {
  const getTypographyProps = useSemanticTypography();
  const announcementDescriptionTypographyProps = getTypographyProps(
    announcementDescriptionLegacyTypography,
    announcementDescriptionSemanticTypography
  );
  const announcementDescriptionTextStyle =
    'textStyle' in announcementDescriptionTypographyProps
      ? (announcementDescriptionTypographyProps.textStyle as string)
      : undefined;

  if (
    !announcement ||
    announcement?.showAnnouncement !== 'true' ||
    !announcementOverlapsWithStay(arrivalDate, departureDate) ||
    !isStringValid(announcement.text)
  ) {
    return null;
  }

  return (
    <Box
      mt={{ base: 'md', lg: 'lg' }}
      data-testid={`announcement-notification-${announcement.type}`}
    >
      <Notification
        title={announcement.title}
        description={
          channel === Channel.Bb
            ? (announcement?.bbText?.replace(/href="\//g, 'href="https://www.premierinn.com/') ??
              '')
            : (announcement?.text?.replace(/href="\//g, 'href="https://www.premierinn.com/') ?? '')
        }
        {...getNotificationPropsByType()}
        {...{ ...notificationStyles, ...styles }}
        descriptionTextStyle={announcementDescriptionTextStyle}
        prefixDataTestId={`announcement-notification-${announcement.type}`}
        sx={{
          a: {
            color: 'darkGrey1',
            textDecoration: 'underline',
          },
        }}
        isInnerHTML
      />
    </Box>
  );

  function getNotificationPropsByType(): {
    variant: string;
    status: 'info' | 'error' | 'warning' | 'success' | undefined;
    svg: ReactElement;
  } {
    if (announcement?.type === INFO_NOTIFICATION_TYPE) {
      return {
        variant: 'info',
        status: 'info',
        svg: <Info />,
      };
    }
    if (announcement?.type === WARNING_NOTIFICATION_TYPE) {
      return {
        variant: 'alert',
        status: 'warning',
        svg: <Alert />,
      };
    }
    return {
      variant: 'info',
      status: 'info',
      svg: <Info />,
    };
  }

  function announcementOverlapsWithStay(arrivalDate: string, departureDate: string) {
    if (!announcement || !arrivalDate || !departureDate) return false;

    const [startDateDay, startDateMonth, startDateYear] =
      announcement?.startDate?.split('/')?.map((s: string): number => +s) ?? [];
    const [endDateDay, endDateMonth, endDateYear] =
      announcement?.endDate?.split('/')?.map((s: string): number => +s) ?? [];
    const startTime = new Date(startDateYear, startDateMonth - 1, startDateDay).getTime();
    const endTime = new Date(endDateYear, endDateMonth - 1, endDateDay).getTime();

    if (arrivalDate && departureDate) {
      const [arrivalYear, arrivalMonth, arrivalDay] =
        arrivalDate?.split('-')?.map((s: string): number => +s) ?? [];
      const [departureYear, departureMonth, departureDay] =
        departureDate?.split('-')?.map((s: string): number => +s) ?? [];
      const arrivalTime = new Date(arrivalYear, arrivalMonth - 1, arrivalDay).getTime();
      const departureTime = new Date(departureYear, departureMonth - 1, departureDay).getTime();
      return (
        (startTime <= arrivalTime && arrivalTime <= endTime) ||
        (startTime <= departureTime && departureTime <= endTime) ||
        (arrivalTime <= startTime && endTime <= departureTime)
      );
    }

    const currentTime = new Date().getTime();

    return startTime <= currentTime && currentTime <= endTime;
  }
};

const notificationStyles = {
  maxW: { base: 'full' },
  lineHeight: '2',
};

const announcementDescriptionLegacyTypography = {};

const announcementDescriptionSemanticTypography = {
  textStyle: 'body-s-regular',
};
