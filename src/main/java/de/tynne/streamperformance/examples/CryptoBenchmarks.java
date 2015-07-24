package de.tynne.streamperformance.examples;

import de.tynne.streamperformance.Benchmark;
import de.tynne.streamperformance.BenchmarkProducer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author fury
 */
public class CryptoBenchmarks implements BenchmarkProducer {

    private final Random random = new Random();
    private final int times = 1000;

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
        Benchmark b = new Benchmark<>(supplier, new MessageDigestConsumer(digest), provider.getName()+" "+digest.getAlgorithm() + " " + data.length, times, data.length);
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
                    }
                }
            }
            p++;
        }
        
        return benchmarks;
    }
}
