package com.devmind.energy

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "energy")
class EnergyProperties(
    var baseUrl: String,
    var clientId: String,
    var duration: String,
    var secret: String,
    var oauthPath: String,
    var authorizeUrl: String,
    var subscribedServicesPath: String,
    var meteringPath: String,
    var applicationName: String
)
