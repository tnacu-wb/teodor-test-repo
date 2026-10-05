package com.whitbread.premierinn.criteria.model;

import com.whitbread.premierinn.api.response.customer.RoomRequirements;
import com.whitbread.premierinn.criteria.NumberSelectorMaxNumberInfo;
import com.whitbread.premierinn.criteria.view.CotState;
import com.whitbread.premierinn.criteria.view.NumberSelectorViewInput;
import com.whitbread.premierinn.domain.common.RoomType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RoomConfigurationTest {

    @Test
    public void testDefaultConstructor() {
        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE.createDefault(false);
        assertEquals(RoomConfiguration.DEFAULT_ROOM_NUMBER, roomConfiguration.number());
        assertEquals(RoomConfiguration.MIN_NUMBER_OF_ADULTS_IN_ROOM, roomConfiguration.adults().value());
        assertEquals(RoomConfiguration.MIN_NUMBER_OF_CHILDREN_IN_ROOM, roomConfiguration.children().value());
        assertEquals(RoomType.DOUBLE.name(), roomConfiguration.roomType().name());
        assertNotSame(CotState.SELECTED, roomConfiguration.cot());
    }

    @Test
    public void testCreateNoErrorConstructor() {
        RoomConfiguration roomConfiguration = RoomConfiguration.builder()
                .number(2)
                .adults(NumberSelectorViewInput.createDefault(1))
                .children(NumberSelectorViewInput.createDefault(2))
                .infants(NumberSelectorViewInput.createDefault(0))
                .cot(CotState.SELECTED)
                .roomType(RoomType.DOUBLE)
                .autobuild();

        RoomConfiguration test = RoomConfigurationCreator.INSTANCE.createNoError(roomConfiguration, false);

        assertNull(test.adults().maxNumberInfo());
        assertNull(test.children().maxNumberInfo());
    }

    @Test
    public void test_createFromRoomRequirements_childrenAdultsPlusButtonDisabled() {
        int adultNumber = 2;
        int childrenNumber = 2;
        RoomType roomType = RoomType.FAMILY;

        RoomRequirements roomRequirements = RoomRequirements.builder()
                .roomNumber(0)
                .adults(adultNumber)
                .children(childrenNumber)
                .type(roomType.getCode())
                .cotRequired(true).build();

        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE.createFromRoomRequirements(roomRequirements, false);

        assertNotNull(roomConfiguration);

        assertEquals(adultNumber, roomConfiguration.adults().value());
        assertFalse(roomConfiguration.adults().plusEnabled());
        assertTrue(roomConfiguration.adults().minusEnabled());

        assertEquals(childrenNumber, roomConfiguration.children().value());
        assertFalse(roomConfiguration.children().plusEnabled());
        assertTrue(roomConfiguration.children().minusEnabled());

        assertEquals(roomType, roomConfiguration.roomType());

        assertSame(CotState.SELECTED, roomConfiguration.cot());
    }

    @Test
    public void test_createFromRoomRequirements_childrenAdultsMinusButtonDisabled() {
        int adultNumber = 1;
        int childrenNumber = 0;
        boolean cotRequired = true;
        RoomType roomType = RoomType.FAMILY;

        RoomRequirements roomRequirements = RoomRequirements.builder()
                .roomNumber(0)
                .adults(adultNumber)
                .children(childrenNumber)
                .type(roomType.getCode())
                .cotRequired(cotRequired).build();

        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE.createFromRoomRequirements(roomRequirements, false);

        assertNotNull(roomConfiguration);

        assertEquals(adultNumber, roomConfiguration.adults().value());
        assertTrue(roomConfiguration.adults().plusEnabled());
        assertFalse(roomConfiguration.adults().minusEnabled());

        assertEquals(childrenNumber, roomConfiguration.children().value());
        assertTrue(roomConfiguration.children().plusEnabled());
        assertFalse(roomConfiguration.children().minusEnabled());

        // Will default room to DOUBLE as there's no children and consequently you can't choose the FAMILY ROOM
        assertEquals(RoomType.DOUBLE.name(), roomConfiguration.roomType().name());

        assertEquals(CotState.SELECTED, roomConfiguration.cot());
    }

    @Test
    public void test_createFromAdultsChange_minusDisabled() {
        int adultNumber = 1;
        String tooManyAdultMessage = "Too many adult";
        RoomConfiguration roomConfigurationDefault = RoomConfigurationCreator.INSTANCE.createDefault(false);

        NumberSelectorMaxNumberInfo maxNumberInfo = new NumberSelectorMaxNumberInfo(tooManyAdultMessage, false, null);
        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE.createFromAdultChange(
                adultNumber, roomConfigurationDefault, maxNumberInfo, false);

        assertNotNull(roomConfiguration);
        assertEquals(adultNumber, roomConfiguration.adults().value());
        assertTrue(roomConfiguration.adults().plusEnabled());
        assertFalse(roomConfiguration.adults().minusEnabled());
        assertNull(roomConfiguration.adults().maxNumberInfo());
        assertEquals(roomConfigurationDefault.children(), roomConfiguration.children());
        assertEquals(roomConfigurationDefault.roomType(), roomConfiguration.roomType());
        assertEquals(roomConfigurationDefault.cot(), roomConfiguration.cot());
    }

    @Test
    public void test_createFromAdultsChange_plusDisabled_withMessage() {
        int adultNumber = 3;
        String tooManyAdultMessage = "Too many adult";
        RoomConfiguration roomConfigurationDefault = RoomConfigurationCreator.INSTANCE.createDefault(false);

        NumberSelectorMaxNumberInfo maxNumberInfo = new NumberSelectorMaxNumberInfo(tooManyAdultMessage, false, null);
        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE.createFromAdultChange(
                adultNumber, roomConfigurationDefault, maxNumberInfo, false);

        assertNotNull(roomConfiguration);

        assertEquals(RoomConfiguration.MAX_NUMBER_OF_ADULTS_IN_ROOM, roomConfiguration.adults().value());
        assertFalse(roomConfiguration.adults().plusEnabled());
        assertTrue(roomConfiguration.adults().minusEnabled());
        assertNotNull(roomConfiguration.adults().maxNumberInfo().getMessage());
        assertEquals(roomConfigurationDefault.children(), roomConfiguration.children());
        assertEquals(roomConfigurationDefault.roomType(), roomConfiguration.roomType());
        assertEquals(roomConfigurationDefault.cot(), roomConfiguration.cot());
    }

    @Test
    public void test_createFromChildrenChange_minusDisabled() {
        int childrenNumber = 1;
        String tooManyChildren = "Too many children";
        RoomConfiguration roomConfigurationDefault = RoomConfigurationCreator.INSTANCE.createDefault(false);

        NumberSelectorMaxNumberInfo maxNumberInfo = new NumberSelectorMaxNumberInfo(tooManyChildren, false, null);
        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE
                .createFromChildrenChange(childrenNumber, roomConfigurationDefault, maxNumberInfo, false);

        assertNotNull(roomConfiguration);
        assertEquals(roomConfigurationDefault.adults(), roomConfiguration.adults());

        assertEquals(childrenNumber, roomConfiguration.children().value());
        assertTrue(roomConfiguration.adults().plusEnabled());
        assertFalse(roomConfiguration.adults().minusEnabled());
        assertNull(roomConfiguration.children().maxNumberInfo());

        // As long as there's ate least one child, then the room type will be set to FAMILY
        assertEquals(RoomType.FAMILY.name(), roomConfiguration.roomType().name());
        assertEquals(roomConfigurationDefault.cot(), roomConfiguration.cot());
    }

    @Test
    public void test_createFromChildrenChange_plusDisabled_withMessage() {
        int childrenNumber = 3;
        String tooManyChildren = "Too many children";
        RoomConfiguration roomConfigurationDefault = RoomConfigurationCreator.INSTANCE.createDefault(false);

        NumberSelectorMaxNumberInfo maxNumberInfo = new NumberSelectorMaxNumberInfo(tooManyChildren, false, null);
        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE
                .createFromChildrenChange(childrenNumber, roomConfigurationDefault, maxNumberInfo, false);

        assertNotNull(roomConfiguration);

        assertEquals(RoomConfiguration.MAX_NUMBER_OF_CHILDREN_IN_ROOM, roomConfiguration.children().value());
        assertFalse(roomConfiguration.children().plusEnabled());
        assertTrue(roomConfiguration.children().minusEnabled());
        assertNotNull(roomConfiguration.children().maxNumberInfo().getMessage());

        assertEquals(roomConfigurationDefault.adults(), roomConfiguration.adults());
        // As long as there's ate least one child, then the room type will be set to FAMILY
        assertEquals(RoomType.FAMILY.name(), roomConfiguration.roomType().name());
        assertEquals(roomConfigurationDefault.cot(), roomConfiguration.cot());
    }

    @Test
    public void test_createFromCotChange() {
        boolean cotRequired = true;
        RoomConfiguration roomConfigurationDefault = RoomConfigurationCreator.INSTANCE.createDefault(false);

        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE.createFromCotChange(
                cotRequired, roomConfigurationDefault, false);

        assertNotNull(roomConfiguration);
        assertSame(CotState.SELECTED, roomConfiguration.cot());
        assertEquals(roomConfigurationDefault.adults(), roomConfiguration.adults());
        assertEquals(roomConfigurationDefault.children(), roomConfiguration.children());
        assertEquals(roomConfigurationDefault.roomType(), roomConfiguration.roomType());
    }

    @Test
    public void test_createFromRoomTypeChange() {
        RoomType roomType = RoomType.SINGLE;
        RoomConfiguration roomConfigurationDefault = RoomConfigurationCreator.INSTANCE.createDefault(false);

        RoomConfiguration roomConfiguration = RoomConfigurationCreator.INSTANCE
                .createFromRoomTypeChange(roomType, roomConfigurationDefault, false);

        assertNotNull(roomConfiguration);
        assertEquals(roomType, roomConfiguration.roomType());
        assertEquals(roomConfigurationDefault.adults(), roomConfiguration.adults());
        assertEquals(roomConfigurationDefault.children(), roomConfiguration.children());
        assertEquals(roomConfigurationDefault.cot(), roomConfiguration.cot());
    }
}
