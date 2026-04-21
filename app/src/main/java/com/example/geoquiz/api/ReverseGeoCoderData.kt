package com.example.geoquiz.api

import com.squareup.moshi.Json

data class ReverseGeoCoderData(
    @Json(name = "Feature")
    var features: List<Feature>? = null
) {

    data class Feature(
        @Json(name = "Property")
        val property: Property? = null,
    )

    class Property {
        @Json(name = "Address")
        var address: String? = null
        @Json(name = "AddressElement")
        var addressElement: List<AddressElement>? =null
    }

    class AddressElement {
        @Json(name = "Name")
        var name: String? = null
    }

    /**
     * 住所文字列を返す（1つ目のfeature）
     *
     * @return 住所文字列。取得できない時は空文字列
     */
    fun getAddress(): String {
        return if (!hasAddress()) {
            ""
        } else {
            (features?.first()?.property?.addressElement?.getOrNull(0)?.name ?: "") +
                    (features?.first()?.property?.addressElement?.getOrNull(1)?.name ?: "")
        }
    }

    /**
     * 住所文字列を持っているか判定（1つ目のfeatureだけで判定）
     *
     * @return 住所文字列が入っていればtrue
     */
    private fun hasAddress(): Boolean {
        if (features?.isEmpty() == true) {
            return false
        }

        val address = features?.first()?.property?.address ?: return false
        return address.isNotEmpty()
    }
}