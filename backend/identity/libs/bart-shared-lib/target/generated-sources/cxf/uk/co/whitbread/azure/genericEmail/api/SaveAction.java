
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for SaveAction&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="SaveAction"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AddOnly"/&gt;
 *     &lt;enumeration value="Default"/&gt;
 *     &lt;enumeration value="Nothing"/&gt;
 *     &lt;enumeration value="UpdateAdd"/&gt;
 *     &lt;enumeration value="UpdateOnly"/&gt;
 *     &lt;enumeration value="Delete"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "SaveAction")
@XmlEnum
public enum SaveAction {

    @XmlEnumValue("AddOnly")
    ADD_ONLY("AddOnly"),
    @XmlEnumValue("Default")
    DEFAULT("Default"),
    @XmlEnumValue("Nothing")
    NOTHING("Nothing"),
    @XmlEnumValue("UpdateAdd")
    UPDATE_ADD("UpdateAdd"),
    @XmlEnumValue("UpdateOnly")
    UPDATE_ONLY("UpdateOnly"),
    @XmlEnumValue("Delete")
    DELETE("Delete");
    private final String value;

    SaveAction(String v) {
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
    public static SaveAction fromValue(String v) {
        for (SaveAction c: SaveAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
