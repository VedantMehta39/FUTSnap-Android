package com.example.futbinwatchernew.Database

import androidx.room.TypeConverter
import com.example.futbinwatchernew.Models.Platform

class PlatformTypeConverter {
    @TypeConverter
    fun fromPlatformToInt(platform: Platform) = platform.ordinal
    @TypeConverter
    fun fromIntToPlatform(platform:Int) = Platform.values()[platform]
}