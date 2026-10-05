'use client';

import {
  Drawer,
  DrawerTrigger,
  DrawerContent,
  DrawerTitle,
  DrawerDescription,
} from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { getEmployeesCSV } from '@whitbread-eos/utils/server';

import { DownloadButtonContent } from './download-button-content';

type Props = {
  className?: string;
  mobile?: boolean;
  testId?: string;
  token?: string;
  companyId?: string;
  icons: Record<string, string>;
};

export const DownloadButton = ({ className, mobile, testId, token, companyId, icons }: Props) => {
  const { t } = useTranslation('users');
  const handleDownloadCSV = async () => await getEmployeesCSV(token, companyId);

  const renderDrawer = (children: React.ReactNode) => {
    return (
      <Drawer>
        <DrawerTrigger asChild>{children}</DrawerTrigger>
        <DrawerContent>
          <DrawerTitle className={drawerTitleStyle}>
            {t('userMgmt.manageEmployees.downloadCta.label')}
          </DrawerTitle>
          <DrawerDescription className={drawerTitleStyle}>
            {t('userMgmt.manageEmployees.downloadCta.label')} (.csv)
          </DrawerDescription>
        </DrawerContent>
      </Drawer>
    );
  };

  const renderContent = () => {
    return (
      <DownloadButtonContent
        handleDownloadCSV={handleDownloadCSV}
        altText={t('download.label')}
        buttonText={t('userMgmt.manageEmployees.downloadCta.label')}
        testId={testId}
        className={className}
        icons={icons}
      />
    );
  };

  if (mobile) {
    return renderDrawer(renderContent());
  }

  return renderContent();
};

const drawerTitleStyle = 'hidden';
