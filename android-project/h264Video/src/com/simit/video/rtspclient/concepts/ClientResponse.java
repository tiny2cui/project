package com.simit.video.rtspclient.concepts;

import com.simit.video.rtspclient.concepts.Request.Method;

public interface ClientResponse {

	void response(Method method);
}
