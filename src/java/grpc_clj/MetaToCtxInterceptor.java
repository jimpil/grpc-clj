package grpc_clj;

import io.grpc.*;
import java.util.Map;

public class MetaToCtxInterceptor implements ServerInterceptor {

    private final Map<String, Context.Key<String>> ctx;

    public MetaToCtxInterceptor(Map<String, Context.Key<String>> m) {
        ctx = m;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            final Metadata meta,
            ServerCallHandler<ReqT, RespT> next) {

        Context curr = Context.current();

        for (String k : ctx.keySet()) {
            Metadata.Key<String> mk = Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER);
            curr = curr.withValue(ctx.get(k), meta.get(mk));
        }
        return Contexts.interceptCall(curr, call, meta, next);
    }
}
