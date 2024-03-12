package org.tcelor.poc.banking.config;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import io.smallrye.mutiny.Uni;

public class ResourceConfig {

    public static <T> Uni<T> withCommonTimeout(Uni<T> uni) {
    return uni.ifNoItem()
             .after(Duration.ofMillis(ConstantConfig.TIMEOUT))
             .failWith(new TimeoutException("💥"));
    }

}