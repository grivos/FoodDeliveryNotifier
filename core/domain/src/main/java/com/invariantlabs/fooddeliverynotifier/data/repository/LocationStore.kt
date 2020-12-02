package com.invariantlabs.fooddeliverynotifier.data.repository

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.invariantlabs.fooddeliverynotifier.domain.R
import com.invariantlabs.fooddeliverynotifier.domain.model.StoredLocation
import com.invariantlabs.fooddeliverynotifier.domain.model.StoredLocationDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class LocationStore(context: Context) {

    private val dataStore: DataStore<StoredLocationDao> = context.createDataStore(
        fileName = "stored_location.pb",
        serializer = StoredLocationSerializer(context)
    )

    val storedLocationObservable: Observable<StoredLocation>
        get() = dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(
                    StoredLocationDao.getDefaultInstance()
                )
            } else {
                throw exception
            }
        }
            .map { dao ->
                StoredLocation(
                    dao.address,
                    dao.lat,
                    dao.lng
                )
            }
            .asObservable()
            .subscribeOn(Schedulers.io())

    suspend fun setStoredLocation(storedLocation: StoredLocation) {
        dataStore.updateData {
            StoredLocationDao.newBuilder()
                .setAddress(storedLocation.address)
                .setLat(storedLocation.lat)
                .setLng(storedLocation.lng)
                .build()
        }
    }
}

class StoredLocationSerializer(private val context: Context) : Serializer<StoredLocationDao> {

    override fun readFrom(input: InputStream): StoredLocationDao {
        try {
            return StoredLocationDao.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(
        t: StoredLocationDao,
        output: OutputStream
    ) = t.writeTo(output)

    override val defaultValue: StoredLocationDao
        get() = StoredLocationDao.newBuilder()
            .setAddress(context.getString(R.string.default_address_name))
            .setLat(context.getString(R.string.default_address_lat).toDouble())
            .setLng(context.getString(R.string.default_address_lng).toDouble())
            .build()
}
