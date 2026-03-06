package com.example.chatapplication.data.local.converter

import androidx.room.TypeConverter
import com.example.chatapplication.data.model.Gender

class GenderConverter {
    @TypeConverter
    fun fromGender(gender: Gender): Int {    //转换成Int
        return gender.ordinal
    }

    @TypeConverter
    fun toGender(value: Int): Gender {    //转换成Gender
        return Gender.values().getOrElse(value) { Gender.UNKNOWN }
    }

}