
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for RespondWhen&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="RespondWhen"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Never"/&gt;
 *     &lt;enumeration value="OnError"/&gt;
 *     &lt;enumeration value="Always"/&gt;
 *     &lt;enumeration value="OnConversationError"/&gt;
 *     &lt;enumeration value="OnConversationComplete"/&gt;
 *     &lt;enumeration value="OnCallComplete"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "RespondWhen")
@XmlEnum
public enum RespondWhen {

    @XmlEnumValue("Never")
    NEVER("Never"),
    @XmlEnumValue("OnError")
    ON_ERROR("OnError"),
    @XmlEnumValue("Always")
    ALWAYS("Always"),
    @XmlEnumValue("OnConversationError")
    ON_CONVERSATION_ERROR("OnConversationError"),
    @XmlEnumValue("OnConversationComplete")
    ON_CONVERSATION_COMPLETE("OnConversationComplete"),
    @XmlEnumValue("OnCallComplete")
    ON_CALL_COMPLETE("OnCallComplete");
    private final String value;

    RespondWhen(String v) {
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
    public static RespondWhen fromValue(String v) {
        for (RespondWhen c: RespondWhen.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
