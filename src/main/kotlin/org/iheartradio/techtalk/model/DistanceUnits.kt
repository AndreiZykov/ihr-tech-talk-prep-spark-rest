package org.iheartradio.techtalk.model

/** where:
 * 'M' is statute miles (default)
 * 'K' is kilometers
 * 'N' is nautical miles
 */
enum class DistanceUnits(val abbrv: String) {
    Miles("M"),
    Kilometers("K"),
    NauticalMiles("M")
}