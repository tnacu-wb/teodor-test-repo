
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for MessageDeliveryTypeEnum&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="MessageDeliveryTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Standard"/&gt;
 *     &lt;enumeration value="DelayedDeliveryByMTAQueue"/&gt;
 *     &lt;enumeration value="DelayedDeliveryByOMMQueue"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "MessageDeliveryTypeEnum")
@XmlEnum
public enum MessageDeliveryTypeEnum {

    @XmlEnumValue("Standard")
    STANDARD("Standard"),
    @XmlEnumValue("DelayedDeliveryByMTAQueue")
    DELAYED_DELIVERY_BY_MTA_QUEUE("DelayedDeliveryByMTAQueue"),
    @XmlEnumValue("DelayedDeliveryByOMMQueue")
    DELAYED_DELIVERY_BY_OMM_QUEUE("DelayedDeliveryByOMMQueue");
    private final String value;

    MessageDeliveryTypeEnum(String v) {
        value = v;
    }

    /**
     * Gets the value associated to the enum constant.
     * 
     * @return
     *     The value linked to the enum.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the enum associated to the value passed as parameter.
     * 
     * @param v
     *     The value to get the enum from.
     * @return
     *     The enum which corresponds to the value, if it exists.
     * @throws IllegalArgumentException
     *     If no value matches in the enum declaration.
     */
    public static MessageDeliveryTypeEnum fromValue(String v) {
        for (MessageDeliveryTypeEnum c: MessageDeliveryTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
