package com.inditex.sisuprice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application-level caching.
 * Enables Spring Cache abstraction with Caffeine as the provider.
 */
@Configuration
@EnableCaching
public class CacheConfig {}
