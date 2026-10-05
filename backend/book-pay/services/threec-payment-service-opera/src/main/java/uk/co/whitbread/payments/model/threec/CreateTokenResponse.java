package uk.co.whitbread.payments.model.threec;

import lombok.Data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "Web2PayResultToken", namespace = "http://web2pay.com/5.0/2009/11/5.1.0/")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateTokenResponse {

  @XmlElement(name = "TokenNo", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
  private String token;

  @XmlElement(name = "CardTypeCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
  private String cardType;

  @XmlElement(name = "ReturnText", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
  private String returnText;

  @XmlElement(name = "ReturnCode", namespace = "http://sixcardsolutions.com/W2P/Front/Entity/2009/05/5.1.0")
  private int returnCode;
}
