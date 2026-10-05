package com.whitbread.premierinn.common;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ValidatorTest {

    @Test
    public void testCorrectEmailAddress() {
        assertTrue(Validator.isEmailValid("a@b.com"));
        assertTrue(Validator.isEmailValid("atgheri@giouahf.aeriuh"));
        assertTrue(Validator.isEmailValid("p@q.rz"));
        assertTrue(Validator.isEmailValid("blah_de_blah@time.com"));
        assertTrue(Validator.isEmailValid(".!#$%&'*+-/=?^_`{|}~@te.com"));
        assertTrue(Validator.isEmailValid("hello@somequitelongdomainnname1234578874.com"));
        assertTrue(Validator.isEmailValid("hello@hi.somequitelongtldabcdefghijklm"));
    }

    @Test
    public void testIncorrectEmailAddresses() {
        assertFalse(Validator.isEmailValid("abc.com"));
        assertFalse(Validator.isEmailValid("a@bcom"));
        assertFalse(Validator.isEmailValid(""));
        assertFalse(Validator.isEmailValid("a\"b(c)d,e:f;g<h>i[j\\k]l@example.com"));
    }

    @Test
    public void testIsFirstNameValid() {
        assertTrue(Validator.isFirstNameValid("Mark William"));
        assertTrue(Validator.isFirstNameValid("O' Meara"));
        assertTrue(Validator.isFirstNameValid("O'Meara Smith"));
        assertTrue(Validator.isFirstNameValid("Abc^&%^&%&^)("));
        assertTrue(Validator.isFirstNameValid("Exclamation mark!"));
        assertTrue(Validator.isFirstNameValid("abcdefghijklmnopqrst")); // 20 characters

        assertFalse(Validator.isFirstNameValid(""));
        assertFalse(Validator.isFirstNameValid("     "));
        assertFalse(Validator.isFirstNameValid("Mark 123"));
        assertFalse(Validator.isFirstNameValid("Mark ~"));
        assertFalse(Validator.isFirstNameValid("Mark ;"));
        assertFalse(Validator.isFirstNameValid("Mark :"));
        assertFalse(Validator.isFirstNameValid("abcdefghijklmnopqrstu")); // 21 characters
    }

    @Test
    public void testIsLastNameValid() {
        assertTrue(Validator.isLastNameValid("Mark William"));
        assertTrue(Validator.isFirstNameValid("O' Meara"));
        assertTrue(Validator.isFirstNameValid("O'Meara Smith"));
        assertTrue(Validator.isLastNameValid("Abc^&%^&%&^)("));
        assertTrue(Validator.isFirstNameValid("Exclamation mark!"));
        assertTrue(Validator.isLastNameValid("abcdefghijklmnopqrstuvwxyzabcd")); // 30 characters

        assertFalse(Validator.isLastNameValid(""));
        assertFalse(Validator.isLastNameValid("     "));
        assertFalse(Validator.isLastNameValid("Mark123"));
        assertFalse(Validator.isFirstNameValid("Mark ~"));
        assertFalse(Validator.isFirstNameValid("Mark ;"));
        assertFalse(Validator.isFirstNameValid("Mark :"));
        assertFalse(Validator.isLastNameValid("abcdefghijklmnopqrstuvwxyzabcde")); // 31 characters
    }

    @Test
    public void testIsPasswordValid() {
        assertTrue(Validator.isPasswordValid("Password1"));
        assertTrue(Validator.isPasswordValid("12345678Qa"));

        assertFalse(Validator.isPasswordValid("Password1!"));
        assertFalse(Validator.isPasswordValid("12345678"));
        assertFalse(Validator.isPasswordValid("12345678995"));
        assertFalse(Validator.isPasswordValid("asdasdasdasdsa"));
        assertFalse(Validator.isPasswordValid("1234567 "));
        assertFalse(Validator.isPasswordValid("aasd?asd!FFFas"));
        assertFalse(Validator.isPasswordValid("JJJJJasdf"));
        assertFalse(Validator.isPasswordValid("         "));
        assertFalse(Validator.isPasswordValid("JJJJJ asdf"));
        assertFalse(Validator.isPasswordValid("~1234567"));
        assertFalse(Validator.isPasswordValid("123~4567"));
        assertFalse(Validator.isPasswordValid("123:4567"));
        assertFalse(Validator.isPasswordValid("123:~4567"));
        assertFalse(Validator.isPasswordValid(":1234567"));
        assertFalse(Validator.isPasswordValid("1234567:"));
        assertFalse(Validator.isPasswordValid("1234567:"));
        assertFalse(Validator.isPasswordValid("123456s7~"));
        assertFalse(Validator.isPasswordValid("~passwordasd"));
        assertFalse(Validator.isPasswordValid("12:34~567~8"));
        assertFalse(Validator.isPasswordValid("12:34~567~8"));
    }

    @Test
    public void testIsPasswordEightChardValid() {
        assertTrue(Validator.isPasswordEightChardValid("Password1!"));
        assertTrue(Validator.isPasswordEightChardValid("12345678"));
        assertTrue(Validator.isPasswordEightChardValid("12345678995"));
        assertTrue(Validator.isPasswordEightChardValid("asdasdasdasdsa"));
        assertTrue(Validator.isPasswordEightChardValid("1234567 "));
        assertTrue(Validator.isPasswordEightChardValid("aasd?asd!FFFas"));
        assertTrue(Validator.isPasswordEightChardValid("JJJJJasdf"));
        assertTrue(Validator.isPasswordEightChardValid("         "));
        assertTrue(Validator.isPasswordEightChardValid("JJJJJ asdf"));
        assertTrue(Validator.isPasswordEightChardValid("~1234567"));
        assertTrue(Validator.isPasswordEightChardValid("123~4567"));
        assertTrue(Validator.isPasswordEightChardValid("123:4567"));
        assertTrue(Validator.isPasswordEightChardValid("123:~4567"));
        assertTrue(Validator.isPasswordEightChardValid(":1234567"));
        assertTrue(Validator.isPasswordEightChardValid("1234567:"));
        assertTrue(Validator.isPasswordEightChardValid("1234567:"));
        assertTrue(Validator.isPasswordEightChardValid("123456s7~"));
        assertTrue(Validator.isPasswordEightChardValid("~passwordasd"));
        assertTrue(Validator.isPasswordEightChardValid("12:34~567~8"));
        assertTrue(Validator.isPasswordEightChardValid("12:34~567~8"));

        assertFalse(Validator.isPasswordEightChardValid("1234567"));
        assertFalse(Validator.isPasswordEightChardValid("!!!!!!!"));
        assertFalse(Validator.isPasswordEightChardValid("1h3jkfi"));
        assertFalse(Validator.isPasswordEightChardValid("AbAb12K"));
        assertFalse(Validator.isPasswordEightChardValid("12:34~5"));
        assertFalse(Validator.isPasswordEightChardValid("67~8"));
    }

    @Test
    public void testDoPasswordsMatch() {
        Validator.isPasswordValid("Ab123456");
        assertTrue(Validator.doPasswordsMatch("Ab123456"));

        assertFalse(Validator.doPasswordsMatch("Ab1234567"));
        assertFalse(Validator.doPasswordsMatch("Ab12345"));
    }

}