package com.mng.rpc.codec;

import com.mng.rpc.playload.DubboContent;
import com.mng.rpc.playload.DubboMessage;
import com.mng.rpc.playload.DubboObject;
import com.mng.rpc.playload.FullDubboMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;

public class DubboObjectAggregator extends
    MessageAggregator<DubboObject, DubboMessage, DubboContent, FullDubboMessage> {

  public DubboObjectAggregator(int maxContentLength) {
    super(maxContentLength);
  }

  @Override
  protected boolean isStartMessage(DubboObject msg) throws Exception {
    return false;
  }

  @Override
  protected boolean isContentMessage(DubboObject msg) throws Exception {
    return false;
  }

  @Override
  protected boolean isLastContentMessage(DubboContent msg) throws Exception {
    return false;
  }

  @Override
  protected boolean isAggregated(DubboObject msg) throws Exception {
    return false;
  }

  @Override
  protected boolean isContentLengthInvalid(DubboMessage start, int maxContentLength) throws Exception {
    return false;
  }

  @Override
  protected Object newContinueResponse(DubboMessage start, int maxContentLength, ChannelPipeline pipeline)
      throws Exception {
    return null;
  }

  @Override
  protected boolean closeAfterContinueResponse(Object msg) throws Exception {
    return false;
  }

  @Override
  protected boolean ignoreContentAfterContinueResponse(Object msg) throws Exception {
    return false;
  }

  @Override
  protected FullDubboMessage beginAggregation(DubboMessage start, ByteBuf content) throws Exception {
    return null;
  }

}
