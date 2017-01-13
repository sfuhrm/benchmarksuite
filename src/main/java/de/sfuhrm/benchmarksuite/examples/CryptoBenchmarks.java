/*
 * Copyright 2015 Stephan Fuhrmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.sfuhrm.benchmarksuite.examples;

import de.sfuhrm.benchmarksuite.Benchmark;
import de.sfuhrm.benchmarksuite.BenchmarkProducer;
import de.sfuhrm.benchmarksuite.BenchmarkSuite;
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
 * @author Stephan Fuhrmann
 */
@Slf4j
@BenchmarkSuite(name = "MessageDigests")
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
