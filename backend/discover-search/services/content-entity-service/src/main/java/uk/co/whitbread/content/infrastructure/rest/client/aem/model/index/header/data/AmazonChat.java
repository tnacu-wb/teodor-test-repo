package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonChat {

  private String amazonChatUrl;
  private String chatIcon;
  private String amazonAuthUrl;
  private String amazonChatId;
  private String amazonChatSnippetId;
  private String chatBotStatus;
  private List<String> webChatEnabledPages;
}
