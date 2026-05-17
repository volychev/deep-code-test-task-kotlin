package ru.volychev.subscription.v1.config

object ApiPaths {
    private const val VERSION = "v1"
    private const val BASE_PATH = "\${app.api.prefix}/$VERSION"

    const val USERS = "$BASE_PATH/users"
    const val SERVICES = "$BASE_PATH/services"
    const val SUBSCRIPTIONS = "$BASE_PATH/subscriptions"
}
