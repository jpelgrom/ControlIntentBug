package com.example.controlintentbug

import android.app.PendingIntent
import android.content.Intent
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.actions.ControlAction
import java.util.concurrent.Flow
import java.util.function.Consumer

class TestControlsProviderService : ControlsProviderService() {
    private val ids = listOf(1, 2, 3, 4, 5, 6)
    private val intentFlags = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE

    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        return Flow.Publisher { subscriber ->
            ids.forEach { id ->
                subscriber.onNext(
                    Control.StatelessBuilder(
                        "$id",
                        PendingIntent.getActivity(
                            baseContext,
                            1,
                            Intent(baseContext, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
                            intentFlags
                        )
                    )
                        .setTitle("Control $id")
                        .build()
                )
            }
            subscriber.onComplete()
        }
    }

    override fun createPublisherFor(controlIds: MutableList<String>): Flow.Publisher<Control> {
        return Flow.Publisher { subscriber ->
            subscriber.onSubscribe(object : Flow.Subscription {
                override fun request(n: Long) {
                    controlIds.forEach { id ->
                        subscriber.onNext(
                            Control.StatefulBuilder(
                                id,
                                PendingIntent.getActivity(
                                    baseContext,
                                    1,
                                    Intent(baseContext, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
                                    intentFlags
                                )
                            )
                                .setTitle("Control $id")
                                .setStatus(Control.STATUS_OK)
                                .build()
                        )
                    }
                }

                override fun cancel() {
                    // Not needed
                }
            })
        }
    }

    override fun performControlAction(
        controlId: String,
        action: ControlAction,
        consumer: Consumer<Int>
    ) {
        consumer.accept(ControlAction.RESPONSE_OK)
    }
}