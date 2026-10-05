package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonChatDto {
  private String amazonChatUrl;
  private String chatIcon;
  private String amazonAuthUrl;
  private String amazonChatId;
  private String amazonChatSnippetId;
  private String chatBotStatus;
  private List<String> webChatEnabledPages;
}