package com.example.practice1.url

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UrlExpiryService(
    private val urlMappingRepository: UrlMappingRepository
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var urlCheckJob: Job? = null

    init {
        startScheduler()
    }

    private fun startScheduler() {
        urlCheckJob = coroutineScope.launch {
            while (isActive) { // Loop until the job is cancelled
                deleteExpiredUrls()
                delay(24 * 60 * 60 * 1000L) // Wait for 24 hours (in milliseconds)
            }
        }
    }

    private suspend fun deleteExpiredUrls() {
        val now = LocalDateTime.now()
        val expiredUrls = urlMappingRepository.findAll()
            .filter { it.expiryDate?.isBefore(now) == true }.toList()

        expiredUrls.forEach { urlMapping ->
            urlMapping.id?.let { urlMappingRepository.deleteById(it) }
        }
    }

    fun stopScheduler() {
        urlCheckJob?.cancel() // Cancel the job when needed
    }
}
