package com.whitbread.premierinn.common.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class StringUtilsTest {

    @Test
    public void listOfBooleansToCommaSeparatedString() throws Exception {
        List<Boolean> booleans = Arrays.asList(true, false, true, true, false, false);
        assertEquals("true,false,true,true,false,false", StringUtils.toCommaSeparatedString(booleans));
    }

    @Test
    public void listOfStringsToCommaSeparatedString() throws Exception {
        List<String> strings = Arrays.asList("DB", "DBS", "MB", "DISS", "ST");
        assertEquals("DB,DBS,MB,DISS,ST", StringUtils.toCommaSeparatedString(strings));
    }

    @Test
    public void emptyListReturnEmptyString() throws Exception {
        List<String> strings = Collections.emptyList();

        assertThat(StringUtils.toCommaSeparatedString(strings), is(StringUtils.EMPTY_STRING));
    }

    @Test
    public void listOfIntegersToCommaSeparatedString() throws Exception {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        assertEquals("1,2,3,4,5", StringUtils.toCommaSeparatedString(integers));
    }
}