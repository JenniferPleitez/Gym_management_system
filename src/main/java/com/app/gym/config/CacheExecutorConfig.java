package com.app.gym.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.app.gym.servicios.cache.CacheMetricsService;

import io.micrometer.core.instrument.Timer.Sample;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CacheExecutorConfig {

    private final CacheMetricsService cacheMetricsService;

    public CacheExecutorConfig(CacheMetricsService cacheMetricsService){
        this.cacheMetricsService = cacheMetricsService;
    }

    @Bean(name = "cacheExecutor")
    public ThreadPoolTaskExecutor cacheExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    
        int corePool = Runtime.getRuntime().availableProcessors() / 2; 
        int maxPool = Runtime.getRuntime().availableProcessors() * 2;
        
        executor.setCorePoolSize(Math.max(2, corePool));  // 2-4 threads para operaciones normales
        executor.setMaxPoolSize(Math.max(4, maxPool));    // CPU cores * 2 para picos
        executor.setQueueCapacity(200);  // para absorber bursts
        
        executor.setThreadNamePrefix("cache-reload-");
        executor.setAllowCoreThreadTimeOut(true);  // Ahorra recursos en baja carga
        executor.setKeepAliveSeconds(60);  // Threads idle por 60 segundos
        
        executor.setRejectedExecutionHandler((r, exec) -> {
            log.warn("Cache executor queue full - task rejected, running in caller thread");
            cacheMetricsService.recordExecutorRejection();
            r.run();  // Ejecutar en hilo actual (CallerRunsPolicy)
        });
        
        // Hook para métricas
        executor.setTaskDecorator(task -> {
            //Metrics.counter("cache.executor.tasks.submitted").increment();
            Sample timerSample = cacheMetricsService.startExecutorTaskTimer();
            
            return () -> {
                try {
                    // Actualizamos el gauge del tamaño de cola antes de ejecutar
                    cacheMetricsService.setExecutorQueueSize(executor.getThreadPoolExecutor().getQueue().size());
                    task.run();
                    cacheMetricsService.stopExecutorTaskTimer(timerSample);
                } catch (Exception e) {
                    throw e;
                } finally {
                    // Actualizamos el gauge del tamaño de cola después de ejecutar
                    cacheMetricsService.setExecutorQueueSize(executor.getThreadPoolExecutor().getQueue().size());
                }
            };
        });
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        
        executor.initialize();
        
        log.info("Cache executor configurado: core={}, max={}, queue={}", 
                executor.getCorePoolSize(), 
                executor.getMaxPoolSize(), 
                executor.getQueueCapacity());
        
        return executor;
    }
}

