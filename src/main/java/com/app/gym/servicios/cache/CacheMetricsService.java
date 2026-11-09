package com.app.gym.servicios.cache;

import org.springframework.stereotype.Service;

import com.app.gym.modelos.enums.CacheErrorKind;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CacheMetricsService {
    private final MeterRegistry meterRegistry;
    
    // ---- Métricas principales de operaciones de cache ----
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter cacheEvictionCounter;
    private final Counter cacheWriteCounter;
    private final Counter cacheUpdateCounter;
    private final Timer cacheLoadTimer;
    private final AtomicLong cacheWritesValue = new AtomicLong(0);
    private final AtomicLong cacheEvictionsValue = new AtomicLong(0);
    
    // ---- Métricas del Executor ----
    private final AtomicInteger executorQueueSize;
    private final Counter executorRejectionCounter; 
    private final Timer executorTaskTimer;
    
    public CacheMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Contadores
        this.cacheHitCounter = Counter.builder("cache.operation.hits")
                .description("Total successful cache retrievals")
                .tag("component", "account_cache")
                .register(meterRegistry);
                
        this.cacheMissCounter = Counter.builder("cache.operation.misses")
                .description("Total cache misses")
                .tag("component", "account_cache")
                .register(meterRegistry);

        this.cacheWriteCounter = Counter.builder("cache.operation.writes")
                .description("Total cache write")
                .tag("component", "account_cache")
                .register(meterRegistry);
        
        this.cacheUpdateCounter = Counter.builder("cache.operation.updates")
                .description("Total cache update")
                .tag("component", "account_cache")
                .register(meterRegistry);
                
        this.cacheEvictionCounter = Counter.builder("cache.operation.evictions")
                .description("Total invalidated cache entries")
                .tag("component", "account_cache")
                .register(meterRegistry);
                
        
        // Es para llevar un estimado de las cuentas activas (cuentas cacheadas - eliminadas). No es preciso, pero nos da una idea.
        Gauge.builder("cache.account.active.estimated", this, CacheMetricsService::estimatedCacheSize)
            .description("Estimated number of active cached accounts")
            .tag("component", "account_cache")
            .register(meterRegistry);
        
        this.cacheLoadTimer = Timer.builder("cache.load.duration")
                .description("Time taken to load or recache data")
                .tag("component", "account_cache")
                .publishPercentileHistogram()
                .sla(
                    Duration.ofMillis(5),
                    Duration.ofMillis(10),
                    Duration.ofMillis(50),
                    Duration.ofMillis(100),
                    Duration.ofMillis(250),
                    Duration.ofMillis(500),
                    Duration.ofSeconds(1)
                )
                .register(meterRegistry);
        
        // Métricas del Executor usando Gauge
        this.executorQueueSize = new AtomicInteger(0);
        Gauge.builder("cache.executor.queue.size", executorQueueSize, AtomicInteger::get)
                .description("Size of the cache executor queue")
                .tag("component", "account_cache_executor")
                .register(meterRegistry);
                
        this.executorRejectionCounter = Counter.builder("cache.executor.rejections")
                .description("Total rejected tasks")
                .tag("component", "account_cache_executor")
                .register(meterRegistry);

        this.executorTaskTimer = Timer.builder("cache.executor.task.duration")
                .description("Time taken to execute cache tasks")
                .tag("component", "account_cache_executor")
                .publishPercentileHistogram()
                .sla(
                    Duration.ofMillis(5),
                    Duration.ofMillis(10),
                    Duration.ofMillis(50),
                    Duration.ofMillis(100),
                    Duration.ofMillis(250),
                    Duration.ofMillis(500),
                    Duration.ofSeconds(1)
                )
                .register(meterRegistry);
    }

    // ---- Para AccountCacheService ----
    public void recordCacheHit() {
        cacheHitCounter.increment();
    }
    
    public void recordCacheMiss() {
        cacheMissCounter.increment();
    }
    
    public void recordCacheEviction() {
        cacheEvictionCounter.increment();
        cacheEvictionsValue.incrementAndGet();
    }
    
    public void recordCacheError(CacheErrorKind kind) {
        meterRegistry.counter("cache.operation.errors",
                "component", "account_cache",
                "errorType", kind == null ? "UNKNOWN" : kind.name()
            )
            .increment();
    }
    
    public Sample startCacheLoadTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void stopCacheLoadTimer(Sample sample) {
        sample.stop(cacheLoadTimer);
    }

    public void recordCacheWrite() {
        cacheWriteCounter.increment();
        cacheWritesValue.incrementAndGet();
    };

    public void recordCacheUpdate() {
        cacheUpdateCounter.increment();
    };

    private double estimatedCacheSize() {
        return Math.max(0, cacheWritesValue.get() - cacheEvictionsValue.get());
    }
    
    // ---- Para CacheExecutor ----
    public void setExecutorQueueSize(int size) {
        executorQueueSize.set(size);
    }
    
    public void recordExecutorRejection() {
        executorRejectionCounter.increment();
    }
    
    public Sample startExecutorTaskTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void stopExecutorTaskTimer(Sample sample) {
        sample.stop(executorTaskTimer);
    }
}
