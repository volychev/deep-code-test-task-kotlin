package ru.volychev.subscription

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SubscriptionApplication

fun main(args: Array<String>) {
	runApplication<SubscriptionApplication>(*args)
}
