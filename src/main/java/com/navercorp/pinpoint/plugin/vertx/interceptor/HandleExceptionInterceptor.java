/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.vertx.interceptor;

import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.SpanAsyncEventSimpleAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.util.StringUtils;
import com.navercorp.pinpoint.plugin.vertx.VertxConstants;
import com.navercorp.pinpoint.plugin.vertx.VertxHandleException;

/**
 * @author jaehong.kim
 */
public class HandleExceptionInterceptor extends SpanAsyncEventSimpleAroundInterceptor {
    public HandleExceptionInterceptor(MethodDescriptor methodDescriptor, TraceContext traceContext) {
        super(traceContext, methodDescriptor);
    }

    @Override
    public void doInBeforeTrace(SpanEventRecorder recorder, AsyncTraceId asyncTraceId, Object target, Object[] args) {
    }

    @Override
    public void doInAfterTrace(SpanEventRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
        recorder.recordApi(methodDescriptor);
        recorder.recordServiceType(VertxConstants.VERTX_INTERNAL);

        if (args != null && args.length >= 1 && args[0] instanceof Throwable) {
            final Throwable handleException = (Throwable) args[0];
            if (throwable != null) {
                if (handleException != null) {
                    // handle to two throwable(handle and catch).
                    final StringBuilder sb = new StringBuilder(256);
                    sb.append("handle=");
                    sb.append(StringUtils.drop(handleException.getMessage(), 120));
                    sb.append(", catch=");
                    sb.append(StringUtils.drop(throwable.getMessage(), 120));
                    recorder.recordException(new VertxHandleException(sb.toString()));
                } else {
                    // record catch exception.
                    recorder.recordException(throwable);
                }
            } else {
                if (handleException != null) {
                    // record handle exception.
                    recorder.recordException(handleException);
                }
            }
        }
    }
}
