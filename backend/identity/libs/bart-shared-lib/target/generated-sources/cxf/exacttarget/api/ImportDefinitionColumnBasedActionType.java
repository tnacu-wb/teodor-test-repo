
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for ImportDefinitionColumnBasedActionType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="ImportDefinitionColumnBasedActionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AddAndUpdate"/&gt;
 *     &lt;enumeration value="AddButDoNotUpdate"/&gt;
 *     &lt;enumeration value="Delete"/&gt;
 *     &lt;enumeration value="Skip"/&gt;
 *     &lt;enumeration value="UpdateButDoNotAdd"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "ImportDefinitionColumnBasedActionType")
@XmlEnum
public enum ImportDefinitionColumnBasedActionType {

    @XmlEnumValue("AddAndUpdate")
    ADD_AND_UPDATE("AddAndUpdate"),
    @XmlEnumValue("AddButDoNotUpdate")
    ADD_BUT_DO_NOT_UPDATE("AddButDoNotUpdate"),
    @XmlEnumValue("Delete")
    DELETE("Delete"),
    @XmlEnumValue("Skip")
    SKIP("Skip"),
    @XmlEnumValue("UpdateButDoNotAdd")
    UPDATE_BUT_DO_NOT_ADD("UpdateButDoNotAdd");
    private final String value;

    ImportDefinitionColumnBasedActionType(String v) {
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
    public static ImportDefinitionColumnBasedActionType fromValue(String v) {
        for (ImportDefinitionColumnBasedActionType c: ImportDefinitionColumnBasedActionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
