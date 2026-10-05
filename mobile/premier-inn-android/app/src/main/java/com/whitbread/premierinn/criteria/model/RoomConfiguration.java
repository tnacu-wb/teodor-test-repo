package com.whitbread.premierinn.criteria.model;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.criteria.NumberSelectorMaxNumberInfo;
import com.whitbread.premierinn.criteria.view.CotState;
import com.whitbread.premierinn.criteria.view.NumberSelectorViewInput;
import com.whitbread.premierinn.domain.common.RoomType;

@AutoValue
public abstract class RoomConfiguration implements Parcelable {

    static final int DEFAULT_ROOM_NUMBER = 1;
    static final int MAX_NUMBER_OF_ADULTS_IN_ROOM = 2;
    static final int MIN_NUMBER_OF_ADULTS_IN_ROOM = 1;
    static final int MAX_NUMBER_OF_CHILDREN_IN_ROOM = 2;
    static final int MIN_NUMBER_OF_CHILDREN_IN_ROOM = 0;

    static final int MAX_NUMBER_OF_INFANTS_IN_ROOM = 1;
    static final int MIN_NUMBER_OF_INFANTS_IN_ROOM = 0;

    static final RoomType DEFAULT_ROOM_TYPE = RoomType.DOUBLE;

    public static NumberSelectorViewInput getAdultNumberSelectorViewInput(int newValue,
                                                                          @Nullable NumberSelectorMaxNumberInfo maxNumberInfo) {
        return getNumberSelector(newValue, MIN_NUMBER_OF_ADULTS_IN_ROOM, MAX_NUMBER_OF_ADULTS_IN_ROOM, maxNumberInfo);
    }

    public static NumberSelectorViewInput getChildrenNumberSelectorViewInput(int newValue,
                                                                             @Nullable NumberSelectorMaxNumberInfo maxNumberInfo) {
        return getNumberSelector(newValue, MIN_NUMBER_OF_CHILDREN_IN_ROOM, MAX_NUMBER_OF_CHILDREN_IN_ROOM, maxNumberInfo);
    }

    public static NumberSelectorViewInput getInfantsNumberSelectorViewInput(int newValue,
                                                                            @Nullable NumberSelectorMaxNumberInfo maxNumberInfo) {
        return getNumberSelector(newValue, MIN_NUMBER_OF_INFANTS_IN_ROOM, MAX_NUMBER_OF_INFANTS_IN_ROOM, maxNumberInfo);
    }

    private static NumberSelectorViewInput getNumberSelector(int newValue, int minCount, int maxCount,
                                                             @Nullable NumberSelectorMaxNumberInfo maxNumberInfo) {
        NumberSelectorViewInput numberSelectorViewInput;
        if (newValue > maxCount) {
            numberSelectorViewInput = NumberSelectorViewInput.builder()
                            .value(maxCount)
                            .maxNumberInfo(maxNumberInfo)
                            .minusEnabled(newValue > minCount).build();
        } else if (newValue == maxCount) {
            numberSelectorViewInput = NumberSelectorViewInput.builder()
                    .value(maxCount)
                    .minusEnabled(newValue > minCount).build();
        } else if (newValue <= minCount) {
            numberSelectorViewInput = NumberSelectorViewInput.builder()
                    .value(minCount)
                    .plusEnabled(true).build();
        } else {
            numberSelectorViewInput = NumberSelectorViewInput.builder()
                    .value(newValue)
                    .minusEnabled(true)
                    .plusEnabled(true).build();
        }
        return numberSelectorViewInput;
    }

    public abstract int number();

    public abstract NumberSelectorViewInput adults();

    public abstract NumberSelectorViewInput children();

    public abstract NumberSelectorViewInput infants();

    public abstract RoomType roomType();

    public abstract CotState cot();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract RoomConfiguration.Builder number(int number);

        public abstract RoomConfiguration.Builder adults(NumberSelectorViewInput adults);

        public abstract RoomConfiguration.Builder children(NumberSelectorViewInput children);

        public abstract RoomConfiguration.Builder infants(NumberSelectorViewInput infants);

        public abstract RoomConfiguration.Builder roomType(RoomType roomType);

        public abstract RoomConfiguration.Builder cot(CotState cot);

        public abstract RoomConfiguration autobuild();
    }

    public static RoomConfiguration.Builder builder() {
        return new AutoValue_RoomConfiguration.Builder();
    }

    public abstract RoomConfiguration.Builder toBuilder();

}
