package com.rr.whispers.translationservice.translations.exception

import com.rr.whispers.translationservice.translations.TranslationMonitor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class TranslationExceptionHandler @Autowired constructor(
    private val monitor: TranslationMonitor
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(LocalizeProjectsException::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = LocalizeProjectsException.DEFAULT_MESSAGE)
    fun handleLocalizeProjectsException(exception: LocalizeProjectsException) {
        logger.info(exception.message)
        monitor.incrementLocalizeProjectsExceptionCounter()
    }

    @ExceptionHandler(LocalizeProjectLanguagesException::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = LocalizeProjectLanguagesException.DEFAULT_MESSAGE)
    fun handleLocalizeProjectLanguagesException(exception: LocalizeProjectLanguagesException) {
        logger.info(exception.message)
        monitor.incrementLocalizeProjectLanguagesExceptionCounter(exception.projectId)
    }

    @ExceptionHandler(LocalizeProjectKeysException::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = LocalizeProjectKeysException.DEFAULT_MESSAGE)
    fun handleLocalizeProjectKeysException(exception: LocalizeProjectKeysException) {
        logger.info(exception.message)
        monitor.incrementLocalizeProjectKeysExceptionCounter(exception.projectId)
    }

    @ExceptionHandler(ProjectNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = ProjectNotFoundException.DEFAULT_MESSAGE)
    fun handleProjectNotFoundException(exception: ProjectNotFoundException) {
        logger.info(exception.message)
    }

    @ExceptionHandler(ProjectLanguageNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = ProjectLanguageNotFoundException.DEFAULT_MESSAGE)
    fun handleProjectLanguageNotFoundException(exception: ProjectLanguageNotFoundException) {
        logger.info(exception.message)
    }
}
