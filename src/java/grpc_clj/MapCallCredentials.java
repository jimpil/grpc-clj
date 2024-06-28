package grpc_clj;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;

import java.util.Map;
import java.util.concurrent.Executor;

public class MapCallCredentials extends CallCredentials {

    private final Map<String, String> meta;

    public MapCallCredentials(Map<String, String> m){
        meta = m;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        executor.execute(() -> {
            try {
                Metadata metadata = new Metadata();
                for (String k : meta.keySet()){
                    Metadata.Key<String> mk = Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER);
                    metadata.put(mk, meta.get(k));
                }
                metadataApplier.apply(metadata);
            } catch (Throwable e) {
                metadataApplier.fail(Status.INVALID_ARGUMENT.withCause(e));
            }
        });
    }
//    @Override
//    public void thisUsesUnstableApi() {
//    }
}
