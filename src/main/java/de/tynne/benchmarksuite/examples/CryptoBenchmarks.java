package de.tynne.benchmarksuite.examples;

import de.tynne.benchmarksuite.Benchmark;
import de.tynne.benchmarksuite.BenchmarkProducer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author fury
 */
@Slf4j
public class CryptoBenchmarks implements BenchmarkProducer {

    private final Random random = new Random();

    class MessageDigestConsumer implements Consumer<byte[]> {

        private final MessageDigest messageDigest;

        public MessageDigestConsumer(MessageDigest messageDigest) {
            this.messageDigest = messageDigest;
        }

        @Override
        public void accept(byte[] t) {
            messageDigest.digest(t);
        }

    }

    private Benchmark bench(String id, Provider provider, MessageDigest digest, byte data[]) {
        Supplier<byte[]> supplier = () -> {
                random.nextBytes(data);
                return data;
        };
        log.debug("Creating id {} with provider {}, md {} and size {}", id, provider.getName(), digest.getAlgorithm(), data.length);
        Benchmark b = new Benchmark<>(supplier, new MessageDigestConsumer(digest), provider.getName()+" "+digest.getAlgorithm() + " " + data.length, data.length);
        b.setId(id);
        return b;
    }

    @Override
    public List<Benchmark> get() {
        List<Benchmark> benchmarks = new ArrayList<>();

        Provider providers[] = Security.getProviders();

        byte data[] = new byte[256*1024];
        char p = 'A';
        for (Provider provider : providers) {
            char s = 'L';
            for (Provider.Service service : provider.getServices()) {
                String type = service.getType();
                if (type.equals("MessageDigest")) {
                    try {
                        MessageDigest digest = MessageDigest.getInstance(service.getAlgorithm(), provider);
                        Benchmark benchmark = bench(p+""+s, provider, digest, data);
                        benchmarks.add(benchmark);
                        s++;
                    } catch (NoSuchAlgorithmException ex) {
                        ex.printStackTrace();
                        log.error("No such algo for "+service.getAlgorithm(), ex);
                    }
                }
            }
            p++;
        }
        
        log.debug("Benchmark count: {}", benchmarks.size());
        
        return benchmarks;
    }
}
