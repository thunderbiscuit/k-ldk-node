package me.thunderbiscuit.kldk.node

import org.ldk.enums.ConfirmationTarget
import org.ldk.structs.FeeEstimator

// to create a FeeEstimator we need to provide an object that implement the FeeEstimatorInterface
// which has 1 function: get_est_sat_per_1000_weight(conf_target: ConfirmationTarget?): Int
object KldkFeeEstimator : FeeEstimator.FeeEstimatorInterface {
    override fun get_est_sat_per_1000_weight(confirmation_target: ConfirmationTarget?): Int {
        // we don't actually use the confirmation_target parameter and simply return 25_000 no matter what
        return 25_000
    }
}
