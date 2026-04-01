package com.romullo.pereira.expensensecontrol.adapters.inbound.rest.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)
