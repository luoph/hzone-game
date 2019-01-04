package com.hzone.server.http.coder;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.hzone.server.http.HttpResponseMessage;

public class HttpServerProtocolCodecFactory extends  
        DemuxingProtocolCodecFactory {
    public HttpServerProtocolCodecFactory() {  
        super.addMessageDecoder(HttpRequestDecoder.class);  
        super.addMessageEncoder(HttpResponseMessage.class,  
                HttpResponseEncoder.class);  
    }
  
}  