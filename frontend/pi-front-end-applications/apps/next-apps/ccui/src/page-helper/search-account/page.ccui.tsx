import {
  BoxProps,
  Container,
  Grid,
  GridItem,
  GridItemProps,
  GridProps,
  Text,
  TextProps,
} from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  SearchAccountForm,
  SelectedRowType,
  PI,
  BOOKING_SUBCHANNEL,
  SearchAccountsType,
  SEARCH_ACCOUNT_ERROR_CODES,
  ErrorType,
} from '@whitbread-eos/api';
import { Error, Form, FormProps, Info, Notification } from '@whitbread-eos/atoms';
import { BackToPage } from '@whitbread-eos/molecules';
import {
  CCUISearchContainer as SearchContainer,
  SearchAccountResultsContainer,
} from '@whitbread-eos/organisms';
import {
  formatDataTestId,
  useCustomLocale,
  getLoggedInUserInfo,
  useRestQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import { SetStateAction, useCallback, useEffect, useState } from 'react';

import { searchAccountFormConfig } from './ccuiFormConfig/searchForm/searchAccountFormConfig';

interface Props {
  queryClient: QueryClient;
  accessToken: string;
}

const createGuestName = (title: string, firstName: string, lastName: string) => {
  if (!(lastName || firstName)) return '-';
  const guestName = `${title || ''} ${firstName} ${lastName}`;
  return guestName.length > 60 ? `${guestName.slice(0, 60)}...` : guestName;
};

const formatResults = (customerAccounts: SearchAccountsType[]) => {
  return customerAccounts.map((account: SearchAccountsType) => {
    return {
      cells: [
        {
          id: 'GuestName',
          value: createGuestName(
            account?.contactDetail?.title,
            account?.contactDetail?.firstName,
            account?.contactDetail?.lastName
          ),
        },
        {
          id: 'CompanyName',
          value: account?.contactDetail?.address?.companyName || '-',
        },
        {
          id: 'Email',
          value: account?.contactDetail?.email || '-',
        },
        {
          id: 'PostCodeHome',
          value: account?.contactDetail?.address?.postCode || '-',
        },
        {
          id: 'PostCodeCompany',
          value: '-',
        },
      ],
      AccountId: account?.customerAccountId,
    };
  });
};

const getDataForUpdateForm = (selectedId: string | undefined, guestAccounts: any) => {
  const guestFound = guestAccounts.find(
    (guest: { customerAccountId: string }) => guest.customerAccountId === selectedId
  );

  if (guestFound?.contactDetail) {
    return {
      title: guestFound.contactDetail.title,
      firstName: guestFound.contactDetail.firstName,
      lastName: guestFound.contactDetail.lastName,
      companyName: guestFound.contactDetail.address.companyName,
      email: guestFound.contactDetail.email,
      address: '',
      postalCode: guestFound.contactDetail.address.postCode,
      mobileNumber: guestFound.contactDetail.mobile,
      landlineNumber: guestFound.contactDetail.telephone,
    };
  } else {
    return {
      title: '',
      firstName: '',
      lastName: '',
      companyName: '',
      email: '',
      address: '',
      postalCode: '',
      mobileNumber: '',
      landlineNumber: '',
    };
  }
};

export default function SearchAccountPageCcui({ queryClient, accessToken }: Readonly<Props>) {
  const { t } = useTranslation();
  const { language } = useCustomLocale();
  const router = useRouter();

  const [resetForm, setResetForm] = useState(0);
  const [showResultsTable, setShowResultsTable] = useState<boolean>(false);
  const [clearPhoneFields, setClearPhoneFields] = useState<boolean>(false);
  const [guestAccounts, setGuestAccounts] = useState<SearchAccountsType[]>([]);
  const [isError, setIsError] = useState<string>('');
  const [searchInputs, setSearchInputs] = useState<any>(null);
  const [selectedRow, setSelectedRow] = useState<SelectedRowType>(null);
  const handleClickRow = (inputs: SelectedRowType) => {
    // => ResultRow - expandable logic
    if (inputs?.rowNr === selectedRow?.rowNr) {
      setSelectedRow(null);
    } else {
      setSelectedRow(inputs);
    }
  };

  const { publicRuntimeConfig = {} } = getConfig() || {};

  const { sessionId } = getLoggedInUserInfo(accessToken);

  const {
    data,
    isError: searchAccountsError,
    isLoading,
    error,
  } = useRestQueryRequest(
    ['SearchCustomerAccounts', searchInputs],
    'POST',
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/customers/hotels/search`,
    {
      'hotel-brand': PI,
      bookingChannel: BOOKING_SUBCHANNEL.WEB,
      'session-Id': sessionId,
      Authorization: `Bearer ${accessToken}`,
    },
    { cacheTime: 0, staleTime: 0, enabled: !!searchInputs },
    searchInputs
      ? {
          firstName: searchInputs.firstName || '',
          lastName: searchInputs.lastName || '',
          email: searchInputs.email || '',
          companyName: searchInputs.companyName || '',
          addressLine: searchInputs.address || '',
          postCode: searchInputs.postalCode || '',
          mobile: searchInputs.mobileNumber || '',
          telephone: searchInputs.landlineNumber || '',
        }
      : {
          firstName: '',
          lastName: '',
          email: '',
          companyName: '',
          addressLine: '',
          postCode: '',
          mobile: '',
          telephone: '',
        }
  );

  useEffect(() => {
    if (data) {
      if (data.length === 0) {
        setIsError('ccui.guestAccounts.error.not.found');
        setShowResultsTable(false);
      } else {
        setIsError('');
        setShowResultsTable(true);
        setGuestAccounts(data);
      }
    }
  }, [searchInputs, data]);

  useEffect(() => {
    if (error) {
      const {
        request: { status },
      } = error as unknown as ErrorType;
      if (status === SEARCH_ACCOUNT_ERROR_CODES.TOO_MANY_RESULTS) {
        const { response } = error as unknown as ErrorType;
        const {
          data: { details },
        } = response;
        const [TOO_MANY_RESULTS_ERROR_MESSAGE] = details;
        setIsError(TOO_MANY_RESULTS_ERROR_MESSAGE);
      } else {
        setIsError((error as unknown as Error)?.message);
      }
      setShowResultsTable(false);
    }
  }, [searchAccountsError, error]);

  const onSubmit = (inputs: SearchAccountForm) => {
    if (Object.values(inputs).filter((el) => el !== null).length > 1) {
      setSearchInputs(inputs);
      setSelectedRow(null);
    } else {
      setShowResultsTable(false);
      setGuestAccounts([]);
      setIsError('ccui.guestAccounts.error.minCriteria');
    }
  };

  const onAbort = () => {
    setShowResultsTable(false);
    setSelectedRow(null);
    queryClient.cancelQueries({ queryKey: ['SearchCustomerAccounts'] });
  };

  const onReset = () => {
    setResetForm((prev) => prev + 1);
    setClearPhoneFields(true);
    setShowResultsTable(false);
    setSelectedRow(null);
    setIsError('');
  };

  useEffect(() => {
    if (clearPhoneFields) {
      setClearPhoneFields(false);
    }
  }, [clearPhoneFields]);

  /**
   * New Form Params
   */
  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    firstName: '',
    lastName: '',
    companyName: '',
    email: '',
    address: '',
    postalCode: '',
    mobileNumber: '',
    landlineNumber: '',
  });

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );
  const baseDataTestId = 'SearchAccountPage';
  const formattedGuestAccounts = formatResults(guestAccounts);
  const dataForUpdateForm = getDataForUpdateForm(selectedRow?.accountId, guestAccounts);

  return (
    <Container {...containerStyles}>
      <Grid {...gridStyles} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
        <GridItem
          {...notificationStyles}
          data-testid={formatDataTestId(baseDataTestId, 'Notification')}
        >
          <Notification
            maxWidth="full"
            variant="info"
            status="info"
            title={''}
            description={language === 'en' ? 'Notification message' : 'Benachrichtigungsnachricht'}
            svg={<Info />}
          />
        </GridItem>
        <GridItem data-testid={formatDataTestId(baseDataTestId, 'Search')}>
          <SearchContainer queryClient={queryClient} isSummaryActive={false} />
        </GridItem>
        <GridItem>
          <Text {...mainHeaderStyles} data-testid={formatDataTestId(baseDataTestId, 'Header')}>
            {t('ccui.guestAccounts.header.title')}
          </Text>
          <Text
            {...descriptionStyles}
            data-testid={formatDataTestId(baseDataTestId, 'Description')}
          >
            {t('ccui.guestAccounts.header.subtitle')}
          </Text>
        </GridItem>

        <GridItem>
          <Form
            {...searchAccountFormConfig({
              getFormState,
              defaultValues,
              onSubmit,
              onAbort,
              onReset,
              baseDataTestId,
              resetForm,
              t,
              language,
              clearPhoneFields,
              submitBtnDisabled: isLoading,
            })}
          ></Form>
        </GridItem>

        {isError && (
          <GridItem>
            <Notification
              maxWidth="full"
              variant="error"
              status="error"
              title={''}
              description={t(isError)}
              svg={<Error />}
            />
          </GridItem>
        )}

        {showResultsTable && !!formattedGuestAccounts?.length && (
          <GridItem {...resultListWrapperStyle}>
            <SearchAccountResultsContainer
              handleClickRow={handleClickRow}
              selectedRow={selectedRow}
              dataForUpdateForm={dataForUpdateForm}
              baseDataTestId={baseDataTestId}
              resultsData={{
                isLoading,
                guests: formattedGuestAccounts,
              }}
              t={t}
            />
          </GridItem>
        )}

        <BackToPage goBack={router.back} linkText={t('ccui.guestAccounts.cta.cancelAndReturn')} />
      </Grid>
    </Container>
  );
}

const containerStyles = {
  maxW: '100%',
  padding: '0',
} as BoxProps;

const gridStyles = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  pb: {
    mobile: 'md',
    md: 'lg',
    lg: 'xl',
    xl: '5xl',
  },
} as GridProps;

const notificationStyles = {
  mt: '2xl',
  mb: 'lg',
} as GridItemProps;

const mainHeaderStyles = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
  lineHeight: '5',
  textAlign: 'left',
  color: 'baseBlack',
  mb: 'md',
} as TextProps;

const descriptionStyles = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
  textAlign: 'left',
  color: 'darkGrey1',
  mb: 'md',
} as TextProps;

const resultListWrapperStyle = {
  px: {
    mobile: 'md',
    md: 'lg',
    lg: '0',
  },
  pt: {
    mobile: 'lg',
    md: '2xl',
    lg: '0',
  },
} as GridItemProps;
