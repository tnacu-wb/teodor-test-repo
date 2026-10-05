import { Button, Center, Flex, Text } from '@chakra-ui/react';
import { dehydrate, QueryClient, useQueryClient } from '@tanstack/react-query';
import { PageName } from '@whitbread-eos/api';
import { Input } from '@whitbread-eos/atoms';
import { SEO as Seo } from '@whitbread-eos/molecules';
import { PISearchContainer as Search } from '@whitbread-eos/organisms';
import {
  ACCESS_TOKEN_COOKIE,
  axiosRequest,
  decodeIdToken,
  deleteCookie,
  EMAIL_COOKIE,
  getI18nLabels,
  getServerSideCustomLocale,
  ID_TOKEN_COOKIE,
  isStringValid,
  setCookie,
  useCustomLocale,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useState } from 'react';

interface Props {
  idTokenCookie: string | undefined | null;
}

export default function HomePage({ idTokenCookie }: Readonly<Props>) {
  const router = useRouter();
  const client = useQueryClient();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const { language, country } = useCustomLocale();

  const [showLoginPanel, setShowLoginPanel] = useState(false);
  const [loginToken, setLoginToken] = useState(idTokenCookie ?? '');

  const setIDTokenCookieHandler = () => {
    if (isStringValid(loginToken)) {
      setCookie(ID_TOKEN_COOKIE, loginToken, 10000);
    }
  };

  const clearIDTokenCookieHandler = () => {
    deleteCookie(ID_TOKEN_COOKIE);
    deleteCookie(ACCESS_TOKEN_COOKIE);
    deleteCookie(EMAIL_COOKIE);
    setLoginToken('');
  };

  return (
    <>
      <Seo page={PageName.HOME} />
      <Search
        queryClient={client}
        searchLocation={searchLocation?.toString()}
        ARRdd={Number(ARRdd)}
        ARRmm={Number(ARRmm)}
        ARRyyyy={Number(ARRyyyy)}
        NIGHTS={Number(NIGHTS)}
        ROOMS={Number(ROOMS)}
        isSummaryActive={false}
      />

      <Button onClick={() => setShowLoginPanel(!showLoginPanel)} size="sm" variant="primary" my="2">
        <Text>{!showLoginPanel ? 'Display' : 'Hide'} Login Panel</Text>
      </Button>

      {showLoginPanel && (
        <>
          <Flex direction="row" m="2" p="2" alignItems="center" w="100%">
            <Input
              name="reservationIdInput"
              onChange={(val) => setLoginToken(val)}
              type="text"
              label="Set login cookie for auth"
              placeholderText="Login Cookie"
              isAutoFocused
              value={loginToken}
            />
            <Button onClick={setIDTokenCookieHandler} size="md" variant="primary" ml="10px">
              <Text>Set Login Cookie</Text>
            </Button>
            <Button onClick={clearIDTokenCookieHandler} size="md" variant="secondary" ml="10px">
              <Text>Clear Login Cookie</Text>
            </Button>
          </Flex>
          <Text>Steps to LOG IN:</Text>
          <Text>1. Go to your QA Env and log in</Text>
          <Text>
            2. Search for <strong>id_token_cookie</strong> cookie and copy its value
          </Text>
          <Text>3. Paste the value in the above input</Text>
          <Text>
            <strong>If you want to LOG OUT, click on Clear Login Cookie</strong>
          </Text>
        </>
      )}

      <Center mt="25vh" data-testid="hub-london-kings-cross-hdp-link">
        <Link
          href={`/${country}/${language}/hotels/england/greater-london/london/hub-london-kings-cross.html?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI`}
        >
          Go to Hub London Kings Cross HDP &rarr;
        </Link>
      </Center>
      <Center mt="md" data-testid="london-kings-cross-hdp-link">
        <Link
          href={`/${country}/${language}/hotels/england/greater-london/london/london-kings-cross.html?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI`}
        >
          Go to London Kings Cross HDP &rarr;
        </Link>
      </Center>
      <Center mt="md" data-testid="london-euston-hdp-link">
        <Link
          href={`/${country}/${language}/hotels/england/greater-london/london/london-euston.html?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI`}
        >
          Go to London Euston HDP &rarr;
        </Link>
      </Center>
      <Center mt="md" data-testid="manchester-old-trafford-hdp-link">
        <Link
          href={`/${country}/${language}/hotels/england/greater-manchester/manchester/manchester-old-trafford.html?searchLocation=London&ARRdd=25&ARRmm=10&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI`}
        >
          Go to Manchester Old Trafford HDP &rarr;
        </Link>
      </Center>
      <Center mt="md" data-testid="dublin-city-centre-temple-bar-hdp-link">
        <Link
          href={`/${country}/${language}/hotels/republic-of-ireland/dublin/dublin/dublin-city-centre-temple-bar.html?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI`}
        >
          Go to Dublin City Centre Temple Bar HDP &rarr;
        </Link>
      </Center>
      <Center mt="md" data-testid="go-to-dashboard-link">
        <Link href={`/${country}/${language}/account/dashboard.html?bookingReference=AQPR431750`}>
          Go to Dashboard &rarr;
        </Link>
      </Center>
    </>
  );
}

export async function getServerSideProps({ locale = 'gb', req, res }: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');

  const cookies = new Cookies(req, res);
  const queryClient = new QueryClient();
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);
  if (idTokenCookie) {
    const { email } = decodeIdToken(idTokenCookie) || {};
    const axiosProps = {
      method: 'GET',
      url: `${process.env.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=false`,
      headers: {
        Authorization: `Bearer ${idTokenCookie}`,
        [WB_SESSION_ID]: sessionId || '',
      },
    };

    await queryClient.prefetchQuery({
      queryKey: ['userDetails', idTokenCookie],
      queryFn: () => axiosRequest(axiosProps),
    });
  }
  const { language } = getServerSideCustomLocale(locale);
  const labels = await getI18nLabels({
    language: language,
    queryClient,
  });

  logger.info({
    label: 'PI:MainPage',
    message: 'PageLoad',
  });

  return {
    props: {
      ...labels,
      dehydratedState: dehydrate(queryClient),
      idTokenCookie: idTokenCookie ?? null,
    },
  };
}
