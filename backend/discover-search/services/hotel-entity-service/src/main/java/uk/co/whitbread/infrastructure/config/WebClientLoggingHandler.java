package uk.co.whitbread.infrastructure.config;

import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;


@ChannelHandler.Sharable
@Slf4j
public class WebClientLoggingHandler extends ChannelDuplexHandler {

  @Override
  public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) {
    if (message instanceof ByteBufHolder holder) {
      var request = holder.content().toString(StandardCharsets.UTF_8);
      if (!request.isBlank()) {
        log.info("WebClient Request: body = {}", request);
      }
    }
    context.write(message, promise);
  }
}
